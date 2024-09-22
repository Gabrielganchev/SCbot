package org.example;
import bwapi.*;

public class AttackManager {
    private Game game;
    private Player self;

    public AttackManager(Game game, Player self) {
        this.game = game;
        this.self = self;
    }

    // Sends idle Zerglings to attack the closest enemy unit
    public void attackEnemy() {
        for (Unit zergling : self.getUnits()) {
            if (zergling.getType() == UnitType.Zerg_Zergling && zergling.isIdle()) {
                Unit closestEnemy = findClosestEnemy(zergling);
                if (closestEnemy != null) {
                    zergling.attack(closestEnemy);
                }
            }
        }
    }

    // Finds the closest enemy unit to attack
    private Unit findClosestEnemy(Unit unit) {
        Unit closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;

        for (Unit enemy : game.enemy().getUnits()) {
            double distance = unit.getDistance(enemy);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEnemy = enemy;
            }
        }
        return closestEnemy;
    }
}
