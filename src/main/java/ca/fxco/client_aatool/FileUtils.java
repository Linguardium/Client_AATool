package ca.fxco.client_aatool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static boolean createFileIfMissing(Path filePath) {
        if (Files.notExists(filePath)) {
            try {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            } catch (IOException err) {
                ClientAATool.LOGGER.error("Couldn't create stat file", err);
                return false;
            }
        }
        return true;
    }

}
