package flowerforce.view.game;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

/**
 * Models a generic FlowerForceScene.
 */
public abstract class AbstractFlowerForceScene implements FlowerForceScene {

    private Scene scene;

    /**
     * 
     * @param fxmlPath the path of the FXML file to load
     * @param controller the scene's controller to pass to the FXML Loader
     */
    protected AbstractFlowerForceScene(final String fxmlPath, final Object controller) {
        try {
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClassLoader.getSystemResource(fxmlPath));
            loader.setController(controller);
            final AnchorPane root = loader.load();
            this.scene = FlowerForceApplication.getScaledScene(root);
        } catch (IOException e) {
            Platform.exit();
        } 
    }

    /**
     * {@inheritDoc}
     */
    @SuppressFBWarnings(
        value = "EI",
        justification = "The scene returned was created to be requested by other classes"
    )
    @Override
    public Scene getScene() {
        return this.scene;
    }

}
