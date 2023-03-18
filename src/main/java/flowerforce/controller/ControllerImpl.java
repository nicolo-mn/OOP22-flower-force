package flowerforce.controller;

import flowerforce.common.WorldSavingManager;
import flowerforce.model.game.Game;
import flowerforce.model.game.World;
import flowerforce.view.game.GameEngine;

import javafx.geometry.Dimension2D;

/**
 * This is an implementation of {@link Controller}.
 */
public final class ControllerImpl implements Controller {

    private GameEngine gameEngine;
    private final World world;

    private Game game;

    /**
     * Create a new instance of Controller.
     */
    public ControllerImpl() throws InstantiationException{
        this.world = WorldSavingManager.load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGameEngine(final GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPlayerCoins() {
        return this.world.getPlayer().getCoins();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSunCounter() {
        if(this.game != null) {

            return this.game.getSun();
        }
        //return this.game.getSun();

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLastUnlockedLevelId() {
        return this.world.getPlayer().getLastUnlockedLevelId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void placePlant(final int plantId, final int row, final int col) {
        //this.world.placePlant(row, col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewLevelGame(final int levelId) {
        this.game = this.world.createLevelGame(1);
        final GameLoop gameLoop = new GameLoopImpl(this.gameEngine, this.game); //TODO: update
        new Thread((Runnable) gameLoop).start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewInfiniteGame() {
        //final GameLoop gameLoop = new GameLoopImpl(GameEngine, this.world.StartNewGame());
        //new Thread((Runnable) gameLoop).start();
    }
}
