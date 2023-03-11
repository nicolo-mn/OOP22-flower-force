package flowerforce.controller;

import flowerforce.view.game.GameEngine;
public interface Controller {

    void setGameEngine(GameEngine gameEngine);
    int getPlayerCoins();

    int getSunCounter();

    int updateSunCounter();

    void placePlant(int row, int col);

    void StartNewLevelGame(final int levelId);

    void startNewInfiniteGame();
}
