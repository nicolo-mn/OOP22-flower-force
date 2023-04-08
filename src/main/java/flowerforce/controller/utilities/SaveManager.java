package flowerforce.controller.utilities;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.File;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import com.google.gson.Gson;
import flowerforce.common.ResourceFinder;

/**
 * A utility class that manages saving information to a JSON file or loading information from it.
 * @param <T> The type of the file to save on the file or to load from it
 */
public final class SaveManager<T> {
    private static final String SAVING_FILES_EXTENSION = ".json";
    private static final Gson GSON = new Gson(); //Instance to json text converter
    private final Class<T> genericClass; //class of the type to deserialize
    private final URL savingFileURL; //path of the savingFile

    /**
     * Create a new instance of the game saving manager.
     * @param genericClass the class of the generic type
     * @param fileName the name for the saving file
     */
    public SaveManager(final Class<T> genericClass, final String fileName) {
        this.genericClass = genericClass;
        this.savingFileURL = ResourceFinder.getSavingFilePath(fileName + SAVING_FILES_EXTENSION);
    }

    /**
     * Save the player's information to a JSON format save file.
     * @param p The player to be saved to file.
     * @return True if the save operation was successful, false otherwise.
     */
    public boolean save(final T p) {
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(new File(this.savingFileURL.toURI())), "UTF-8")) {
            fw.write(GSON.toJson(p));
            return true;
        } catch (URISyntaxException | IOException e) {
            return false;
        }
    }

    /**
     * Reads the player's information save file, if it exists and returns an optional with the object of the generic type.
     * @return An optional containing the instance of the saved player read from file,
     *  empty if the file does not exist or in case of an error during the read operation.
     */
    public Optional<T> load() {
        final File file;
        try {
            file = new File(this.savingFileURL.toURI());
            if (!file.exists()) {
                return Optional.empty();
            }

            final Reader fr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            return Optional.of(GSON.fromJson(fr, genericClass));

        } catch (URISyntaxException | IOException e) {
            return Optional.empty();
        }
    }

}
