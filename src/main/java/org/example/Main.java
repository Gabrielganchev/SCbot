package org.example;
import bwapi.*;
import bwapi.BWClient;

public class Main implements BWEventListener {
    private Game game;
    private Player self;
    private MacroManager macroManager;
    private AttackManager attackManager;
    private BWClient bwClient;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        // Initialize the BWClient, which connects the bot to the game.
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    @Override
    public void onStart() {
        game = bwClient.getGame();
        self = game.self();

        // Initialize macro and attack managers
        macroManager = new MacroManager(game, self);
        attackManager = new AttackManager(game, self);

        System.out.println("Zerg Macro Bot has started!");
    }

    @Override
    public void onFrame() {
        // Instead of using isInGame(), we directly perform actions every frame
        // Macro management
        macroManager.manageEconomy();
       // macroManager.trainUnits();


        // Attack management
        attackManager.attackEnemy();
    }

    @Override
    public void onUnitCreate(Unit unit) {
        System.out.println("New unit created: " + unit.getType());
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        System.out.println("Unit destroyed: " + unit.getType());
    }

    @Override
    public void onEnd(boolean isWinner) {
        System.out.println(isWinner ? "You won!" : "You lost.");
    }

    // Required by the BWEventListener interface
    @Override
    public void onPlayerDropped(Player player) {
        System.out.println("Player dropped: " + player.getName());
    }

    // Override the remaining abstract methods
    @Override
    public void onUnitMorph(Unit unit) {
        // Handle unit morphing (e.g. when a Drone morphs into a building)
    }

    @Override
    public void onUnitComplete(Unit unit) {
        // Handle when a unit completes its construction or morph
    }

    @Override
    public void onUnitShow(Unit unit) {
        // Handle when a unit becomes visible on the map
    }

    @Override
    public void onUnitHide(Unit unit) {
        // Handle when a unit becomes hidden (e.g. Fog of War)
    }

    @Override
    public void onUnitRenegade(Unit unit) {
        // Handle when a unit changes ownership (e.g. Mind Control)
    }

    @Override
    public void onSaveGame(String gameName) {
        // Handle game save events
    }
    @Override
    public void onUnitEvade(Unit unit){
        System.out.println("Unit evaded " + unit.getType());

    }
    @Override
    public void onUnitDiscover(Unit unit){
        System.out.println("Unit discorverd " + unit.getType());
    }

    @Override
    public void onPlayerLeft(Player player){
        System.out.println("Player has left "+ player.getName());

    }
    @Override
    public void onNukeDetect(Position position) {
        if (position != null) {
            System.out.println("Nuclear launch detected at: " + position);
        } else {
            System.out.println("Nuclear launch detected, but the position is unknown!");
        }
    }
    @Override
    public void onReceiveText(Player player,String text){
        System.out.println("received message from " + player.getName() + ": " + text);
    }
    @Override
    public void onSendText(String text){
        System.out.println("Bot send message ");
    }
}