package flowerforce.model.entities;

import javafx.geometry.Point2D;

/**
 * Model a factory of different zombies.
 */
public interface ZombieFactory {

    /**
     * @param position where it is initially placed
     * @return a basic zombie
     */
    Zombie basic(Point2D position);

    /** 
     * @param position where it is initially placed
     * @return a medium-resistance zombie
     */
    Zombie conehead(Point2D position);

    /**
     * @param position where it is initially placed 
     * @return a high-resistance zombie
     */
    Zombie buckethead(Point2D position);

    /**
     * @param position where it is initially placed
     * @return a running zombie
     */
    Zombie running(Point2D position);

    /** 
     * @param position where it is initially placed
     * @return a running and high-resistance zombie
     */
    Zombie quarterback(Point2D position);

}
