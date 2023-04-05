package flowerforce.controller;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import flowerforce.controller.utilities.CardGenerator;
import flowerforce.controller.utilities.EntityConverter;
import flowerforce.controller.utilities.WorldSavingManager;
import flowerforce.model.entities.EntityInfo;
import flowerforce.model.entities.PlantInfo;
import flowerforce.model.game.Game;
import flowerforce.model.game.World;
import flowerforce.controller.utilities.EntityConverterImpl;
import flowerforce.view.entities.CardView;
import flowerforce.view.entities.EntityView;
import flowerforce.view.game.GameEngine;

/**
 * This is an implementation of {@link Controller}.
 */
public final class ControllerImpl implements Controller {

    private Optional<GameEngine> gameEngine = Optional.empty();
    private final World world;
    private EntityConverter entityConverter;
    private Optional<Game> game;
    private final Map<CardView, PlantInfo> cards = new HashMap<>();
    private final Map<EntityInfo, EntityView> previousPlant = new HashMap<>();
    private final Map<EntityInfo, EntityView> previousZombie = new HashMap<>();
    private final Map<EntityInfo, EntityView> previousBullet = new HashMap<>();
    private final Map<CardView, PlantInfo> purchasablePlants = new HashMap<>();

    /**
     * Create a new instance of Controller.
     */
    public ControllerImpl() throws InstantiationException {
        this.world = WorldSavingManager.load();
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
    public int getPlayerScoreRecord() {
        return this.world.getPlayer().getScoreRecord();
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
    public void setGameEngine(final GameEngine gameEngine) {
        this.gameEngine = Optional.ofNullable(gameEngine);
        this.checkGameEngine();
        this.entityConverter = new EntityConverterImpl(this.world.getYardDimension(), this.gameEngine.get().getYardDimension());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSunCounter() {
        this.checkGame();
        return this.game.get().getSun();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScore() {
        this.checkGame();
        return this.game.get().getScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getProgressState() {
        this.checkGame();
        return this.game.get().getProgressState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean placePlant(final CardView cardView, final int row, final int col) {
        this.checkGame();
        return this.game.get().placePlant(this.cards.get(cardView), row, col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removePlant(final int row, final int col) {
        this.checkGame();
        return this.game.get().removePlant(row, col);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean buyPlant(final CardView cardView) {
        final boolean isBought = this.world.getShop().buyPlant(this.purchasablePlants.get(cardView));
        this.save();
        return isBought;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewLevelGame(final int levelId) {
        this.resetGame();
        this.game = Optional.of(this.world.createLevelGame(levelId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewInfiniteGame() {
        this.resetGame();
        this.game = Optional.of(this.world.createInfiniteGame());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<EntityView> getPlacedPlants() {
        this.checkGame();
        final Set<EntityInfo> plants = this.game.get().getPlacedPlants();

        //region Plants
        final Set<EntityInfo> plantsToRemove = new HashSet<>();
        //Remove the entities that are no longer there
        this.previousPlant.keySet().forEach(p -> {
            if (!plants.contains(p)) {
                plantsToRemove.add(p);
            }
        });
        plantsToRemove.forEach(p -> this.previousPlant.remove(p));
        //Create the plant EntityView if plant not already present
        plants.forEach(p -> {
            if (!this.previousPlant.containsKey(p)) {
                this.previousPlant.put(p, this.entityConverter.getPlantView(p));
            }
        });
        //endregion

        return new HashSet<>(this.previousPlant.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<EntityView> getPlacedZombies() {
        this.checkGame();
        final Set<EntityInfo> zombies = this.game.get().getPlacedZombies();

        //region Zombies
        final Set<EntityInfo> zombiesToRemove = new HashSet<>();
        this.previousZombie.keySet().forEach(z -> {
            if (!zombies.contains(z)) {
                zombiesToRemove.add(z);
            }
        });
        zombiesToRemove.forEach(z -> this.previousZombie.remove(z));
        zombies.forEach(z -> {
            if (this.previousZombie.containsKey(z)) {
                this.entityConverter.changeZombieViewPosition(this.previousZombie.get(z), z.getPosition());
            } else {
                this.previousZombie.put(z, this.entityConverter.getZombieView(z));
            }
        });
        //endregion

        return new HashSet<>(this.previousZombie.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<EntityView> getPlacedBullets() {
        this.checkGame();
        final Set<EntityInfo> bullets = this.game.get().getPlacedBullet();

        //region Bullets
        final Set<EntityInfo> bulletToRemove = new HashSet<>();
        this.previousBullet.keySet().forEach(b -> {
            if (!bullets.contains(b)) {
                bulletToRemove.add(b);
            }
        });
        bulletToRemove.forEach(b -> this.previousBullet.remove(b));
        bullets.forEach(b -> {
            if (this.previousBullet.containsKey(b)) {
                this.entityConverter.changeBulletViewPosition(this.previousBullet.get(b), b.getPosition());
            } else {
                this.previousBullet.put(b, this.entityConverter.getBulletView(b));
            }
        });
        //endregion

        return new HashSet<>(this.previousBullet.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<EntityView> getDamagedEntities() {
        checkGame();
        final var damagedEntities = this.game.get().getDamagedEntity();
        final Set<EntityView> output = new HashSet<>();
        this.previousPlant.entrySet().stream()
                .filter(e -> damagedEntities.contains(e.getKey()))
                .forEach(e -> output.add(e.getValue()));
        this.previousZombie.entrySet().stream()
                .filter(e -> damagedEntities.contains(e.getKey()))
                .forEach(e -> output.add(e.getValue()));
        return output;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<CardView> getEnabledCards() {
        this.checkGame();
        final Set<PlantInfo> enabledPlants = this.game.get().getEnabledPlants();
        return this.cards.entrySet().stream()
                .filter(e -> enabledPlants.contains(e.getValue())) //Removed not available cardviews
                .map(Map.Entry::getKey) //Map to get just keys
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<CardView, Boolean> getPurchasablePlants() {
        final Map<PlantInfo, Boolean> shopPlants = this.world.getShop().getPlants();
        final Map<CardView, Boolean> toReturn = new HashMap<>();
        this.purchasablePlants.clear();
        shopPlants.keySet().stream()
                .forEach(p -> {
                    final CardView card = CardGenerator.getCardView(p);
                    this.purchasablePlants.put(card, p);
                    toReturn.put(card, shopPlants.get(p));
                });
        return Collections.unmodifiableMap(toReturn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalRows() {
        return this.world.getRowsNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalColumns() {
        return this.world.getColsNum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save() {
        WorldSavingManager.save(this.world);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameLoop getGameLoop() {
        checkGame();
        checkGameEngine();
        this.gameEngine.get().loadCards(this.getCards());
        return new GameLoop(this.gameEngine.get(), this.game.get(), this.world.getRenderingInformations());
    }

    private List<CardView> getCards() {
        this.checkGame();
        this.game.get().getPlaceablePlant()
                .forEach(p -> cards.put(CardGenerator.getCardView(p), p));
        return cards.keySet().stream().toList();
    }

    private void checkGameEngine() {
        if (this.gameEngine.isEmpty()) {
            throw new NoSuchElementException("GameEngine has not been set.");
        }
    }

    private void checkGame() {
        if (this.game.isEmpty()) {
            throw new NoSuchElementException("Game has not been started.");
        }
    }

    private void resetGame() {
        this.cards.clear();
        this.previousPlant.clear();
        this.previousZombie.clear();
        this.previousBullet.clear();
        this.purchasablePlants.clear();
    }
}
