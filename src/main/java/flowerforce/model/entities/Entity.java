package flowerforce.model.entities;

import javafx.geometry.Point2D;

/**
 * Models a in-game entity.
 */
public interface Entity {

    /**
     * 
     * @return the entity's position
     */
    Point2D getPosition();

    /**
     * 
     * @return true if the entity doesn't have to be in game anymore
     */
    boolean isOver();

    /**
     * 
     * @return the name of the entity (this is not an identifier, there could be two or more entities with the same name)
     */
    String getEntityName();

}
