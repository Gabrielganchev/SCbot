package org.example;



import bwapi.*;

public class MacroManager {
    private final Game game;
    private final Player self;
    private final int dronesOnMinerals = 12;// Drones on minerals
    private final int dronesOnGas = 3;// gas drones
    private final int dronesToBuild = 2; // Number of Drones to use for building
    private boolean hatcheryBuilt = false; // Track if an additional Hatchery has been built

    public MacroManager(Game game, Player self) {
        this.game = game;
        this.self = self;
    }

    // Manages economy by training new workers and keeping track of supply
    public void manageEconomy() {
        // Build Overlords if the supply is near the limit
        if (self.supplyUsed() >= self.supplyTotal() - 2 && self.supplyTotal() < 200) {
            buildOverlords();
        }


        // Build one additional Hatchery if we haven't built one yet
        buildHatcheries();

        //Build Extractors if needed
        buildExtractors();
        // Assign Drones to collect minerals and gas
        collectResources();

        // Train Drones if resources allow and there are idle Hatcheries
        trainDrones();
    }

    // Method to assign Drones to collect minerals and gas
    public void collectResources() {
        // Collect minerals first
        int mineralDronesCount = 0;
        for (Unit drone : self.getUnits()) {
            if (drone.getType() == UnitType.Zerg_Drone) {
                // Allow Drones to gather minerals if they're idle
                if (drone.isIdle()) {
                    Unit closestMineral = findClosestMineral(drone);
                    if (closestMineral != null) {
                        drone.gather(closestMineral);

                        mineralDronesCount++;
                        System.out.println("Drone assigned to collect minerals.");


                    }
                }
            }


        }

        if (mineralDronesCount < dronesOnMinerals){
            for(Unit drone : self.getUnits()){
                if(drone.getType() == UnitType.Zerg_Drone && drone.isIdle()){
                    Unit closestMineral = findClosestMineral(drone);
                    drone.gather(closestMineral);
                    System.out.println("Additional Drone assigned to collect minerals.");
                }
            }
        }


        // After minerals, assign some Drones to gather gas
        int gasDronesCount = 0; // Keep track of how many Drones are assigned to gas
        for (Unit drone : self.getUnits()) {
            if (drone.getType() == UnitType.Zerg_Drone) {
                // Check if the Drone is idle and we need more gas gatherers
                if (drone.isIdle() && gasDronesCount < 3) { // Adjust the number of gas Drones as needed
                    Unit closestExtractor = findClosestExtractor(drone);
                    if (closestExtractor != null) {
                        drone.gather(closestExtractor);
                        gasDronesCount++;
                        System.out.println("Drone assigned to collect gas.");
                    }
                }
            }
        }
    }

    // Find the closest mineral patch for the Drone
    private Unit findClosestMineral(Unit drone) {
        Unit closestMineral = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Unit neutralUnit : game.getNeutralUnits()) {
            if (neutralUnit.getType().isMineralField()) {
                int distance = drone.getDistance(neutralUnit);
                if (distance < closestDistance) {
                    closestMineral = neutralUnit;
                    closestDistance = distance;
                }
            }
        }
        return closestMineral;
    }

    // Find the closest Extractor for the Drone
    private Unit findClosestExtractor(Unit drone) {
        Unit closestExtractor = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Unit extractor : self.getUnits()) {
            if (extractor.getType() == UnitType.Zerg_Extractor) {
                int distance = drone.getDistance(extractor);
                if (distance < closestDistance) {
                    closestExtractor = extractor;
                    closestDistance = distance;
                }
            }
        }
        return closestExtractor;
    }

    // Count how many Drones are currently assigned to building tasks
    private int countDronesAssignedToBuild() {
        int count = 0;
        for (Unit drone : self.getUnits()) {
            if (drone.getType() == UnitType.Zerg_Drone && drone.isConstructing()) {
                count++;
            }
        }
        return count;
    }

    // Builds one additional Hatchery if there are enough resources and available Drones
    public void buildHatcheries() {
       if(!hatcheryBuilt && self.minerals() >= 300 && self.supplyUsed() < self.supplyTotal() - 2 ){
           for (Unit drone : self.getUnits()){
               if(drone.getType() == UnitType.Zerg_Drone && drone.isIdle() && countDronesAssignedToBuild() < dronesToBuild){
                   TilePosition buildTile = getBuildTile(drone , UnitType.Zerg_Hatchery, self.getStartLocation());
                   if (buildTile!=null){
                       drone.build(UnitType.Zerg_Hatchery ,buildTile);
                       hatcheryBuilt = true;
                       System.out.println("Building additional Hatchery");
                       break;// only build  One Hatchery at a time

                   }
               }
           }

       }
    }


    public void buildExtractors(){
        for (Unit drone : self.getUnits()){
            if(drone.getType() == UnitType.Zerg_Drone && drone.isIdle()){
                TilePosition extractorTile = getBuildTile(drone, UnitType.Zerg_Extractor , self.getStartLocation());
                if (extractorTile !=null && self.minerals() >= 75){
                    drone.build(UnitType.Zerg_Extractor , extractorTile);
                    System.out.println("Building EXtractor.");
                    break;
                }
            }
        }

    }

    // Train new Drones if resources allow and there are idle Hatcheries
    private void trainDrones() {
        for (Unit hatchery : self.getUnits()) {
            if (hatchery.getType() == UnitType.Zerg_Hatchery || hatchery.getType() == UnitType.Zerg_Lair || hatchery.getType() == UnitType.Zerg_Hive) {
                // Only train Drones if we have enough minerals and supply
                if (hatchery.isIdle() && self.minerals() >= 50 && self.supplyTotal() < 200) {
                    hatchery.train(UnitType.Zerg_Drone);
                    System.out.println("Training new Drone.");
                }
            }
        }
    }

    // Builds Overlords to increase supply when nearing the limit
    public void buildOverlords() {
        for (Unit hatchery : self.getUnits()) {
            if (hatchery.getType() == UnitType.Zerg_Hatchery || hatchery.getType() == UnitType.Zerg_Lair || hatchery.getType() == UnitType.Zerg_Hive) {
                if (hatchery.isIdle() && self.minerals() >= 100) {
                    hatchery.train(UnitType.Zerg_Overlord);
                    System.out.println("Building Overlord to increase supply.");
                }
            }
        }
    }

    // Find a tile where the building can be placed
    private TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
        int maxDist = 10;
        int stopDist = 40;

        // Search for a tile in a spiral around the given location
        for (int dist = 2; dist < stopDist; dist += 2) {
            for (int i = -dist; i <= dist; i++) {
                for (int j = -dist; j <= dist; j++) {
                    TilePosition tile = new TilePosition(aroundTile.getX() + i, aroundTile.getY() + j);
                    if (game.canBuildHere(tile, buildingType, builder, true)) {
                        return tile;
                    }
                }
            }
        }
        return null; // No suitable tile found
    }
}
