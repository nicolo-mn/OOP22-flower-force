package flowerforce.model.game;

import flowerforce.model.entities.plants.Plant;
import flowerforce.model.entities.plants.ShootingPlantFactory;
import flowerforce.model.entities.plants.SunflowerFactory;
import flowerforce.model.entities.plants.Wallnut;
import flowerforce.model.entities.zombies.Zombie;
import flowerforce.model.entities.zombies.ZombieFactory;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Models world levels, it contains level information.
 */
public final class LevelInfo {

    private static final List<Function<Point2D, Zombie>> AVAILABLE_ZOMBIES = List.of(
            ZombieFactory::basic,
            ZombieFactory::conehead,
            ZombieFactory::runner,
            ZombieFactory::newspaper,
            ZombieFactory::buckethead,
            ZombieFactory::quarterback
    );
    private static final List<Function<Point2D, Plant>> AVAILABLE_PLANTS = List.of(
            SunflowerFactory::createCommonSunflower,
            ShootingPlantFactory::createPeaShooter,
            ShootingPlantFactory::createSnowShooter,
            Wallnut::new,
            ShootingPlantFactory::createFastShooter,
            ShootingPlantFactory::createFireShooter
    );
    private static final int COINS = 100;
    private static final List<Integer> ZOMBIE_LEVEL = List.of(34, 51, 68, 68, 68, 85, 85);
    private static final Function<Point2D, Zombie> ZOMBIE_BOSS = ZombieFactory::gargantuar;

    private LevelInfo() { }

    /**
     * @param id of the level
     * @return the coins you get if you pass the level
     */
    public static int getLevelCoins(final int id) {
        return COINS * id;
    }

    /**
     * @param id of the level
     * @return plants available on that level
     */
    public static List<Function<Point2D, Plant>> getPlantsInfo(final int id) {
        return AVAILABLE_PLANTS.subList(0, Math.min(1 + id, AVAILABLE_ZOMBIES.size()));
    }

    /**
     * @param id of the level
     * @return the available zombies on that level
     */
    public static List<Function<Point2D, Zombie>> getZombiesInfo(final int id) {
        return AVAILABLE_ZOMBIES.subList(0, Math.min(1 + id, AVAILABLE_ZOMBIES.size()));
    }

    /**
     * @param id of the level
     * @return the zombie to spawn on that level
     */
    public static Integer getTotalZombies(final int id) {
        return ZOMBIE_LEVEL.get(Math.min(id - 1, ZOMBIE_LEVEL.size() - 1));
    }

    /**
     * @param id of the level
     * @return if it is present, the level boss
     */
    public static Optional<Function<Point2D, Zombie>> getBossId(final int id) {
        return 1 + id > AVAILABLE_ZOMBIES.size() ? Optional.of(ZOMBIE_BOSS) : Optional.empty();
    }
    /**
     * @return the last level ID
     */
    public static int getLastLevelId() {
        return AVAILABLE_ZOMBIES.size();
    }
}
