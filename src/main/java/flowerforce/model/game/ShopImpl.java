package flowerforce.model.game;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import flowerforce.model.entities.plants.CherryBomb;
import flowerforce.model.entities.plants.Plant;
import flowerforce.model.entities.plants.PlantInfo;
import flowerforce.model.entities.plants.PlantInfoImpl;
import flowerforce.model.entities.plants.ShootingPlantFactory;
import flowerforce.model.entities.plants.SunflowerFactory;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;

/**
 * This is an implementation of {@link Shop}.
 */
public final class ShopImpl implements Shop {
    private static final List<Pair<Function<Point2D, Plant>, Integer>> SHOP_PLANTS = List.of(
            new Pair<Function<Point2D, Plant>, Integer>(SunflowerFactory::createDoubleSunflower, 600),
            new Pair<Function<Point2D, Plant>, Integer>(CherryBomb::new, 1000),
            new Pair<Function<Point2D, Plant>, Integer>(ShootingPlantFactory::createStrongShooter, 1200)
    );
    private final Player player;

    private final List<PlantInfo> plants = new ArrayList<>();

    /**
     * This is a constructor for a new shop instance.
     * @param player The player to add bought plants to
     */
    @SuppressFBWarnings (
            value = {
                "EI_EXPOSE_REP2"
            },
            justification = "I need to access and modify the exact instance of the Player that the world has"
    )
    public ShopImpl(final Player player) {
        this.player = player;

        //Adding all plants in the map
        final Point2D samplePoint = new Point2D(0, 0);
        SHOP_PLANTS.forEach(p -> this.plants.add(
                new PlantInfoImpl(p.getKey().apply(samplePoint).getName(), p.getValue())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<PlantInfo, Boolean> getPlants() {
        final Map<PlantInfo, Boolean> outputMap = new HashMap<>();
        final var playerPlants = this.getPlayerBoughtPlants();
        this.plants.forEach(p -> outputMap.put(p, !playerPlants.contains(p) && this.player.getCoins() >= p.getCost()));
        return outputMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean buyPlant(final PlantInfo plantInfo) {
        if (this.plants.contains(plantInfo)
                && this.player.subtractCoins(plantInfo.getCost())) {
            this.player.addPlant(getKeyIndex(plantInfo));
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Function<Point2D, Plant>> getBoughtPlantsFunctions() {
        return this.player.getPlantsIds().stream()
                .map(id -> SHOP_PLANTS.get(id).getKey())
                .collect(Collectors.toSet());
    }

    private Set<PlantInfo> getPlayerBoughtPlants() {
        return this.player.getPlantsIds().stream()
                .map(this.plants::get)
                .collect(Collectors.toSet());
    }
    private int getKeyIndex(final PlantInfo plantInfo) {
        return this.plants.indexOf(plantInfo);
    }
}
