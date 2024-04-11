package ca.fxco.client_aatool;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientAATool implements ClientModInitializer {
    public static final Path CLIENT_AATOOL_PATH = FabricLoader.getInstance().getGameDir().resolve("client_aatool").resolve("server");

    public static boolean shouldLog = false;

    public static final Map<String,Boolean> servers = new HashMap<>();

    public static final Logger LOGGER = LogManager.getLogger("ClientAATool");

    @Override
    public void onInitializeClient() {
        if (Files.notExists(CLIENT_AATOOL_PATH)) {
            try {
                Files.createDirectories(CLIENT_AATOOL_PATH);
            } catch (IOException e) {
                LOGGER.error("Unable to create Client AATool folder {}: {}", CLIENT_AATOOL_PATH, e);
            }
        }
    }
}
