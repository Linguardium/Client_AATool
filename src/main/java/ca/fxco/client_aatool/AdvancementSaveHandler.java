package ca.fxco.client_aatool;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ca.fxco.client_aatool.ClientAATool.CLIENT_AATOOL_PATH;
import static ca.fxco.client_aatool.FileUtils.createFileIfMissing;

public class AdvancementSaveHandler {
    private static final Codec<Map<Identifier, AdvancementProgress>> ADVANCEMENT_PROGRESS_CODEC =  Codec.unboundedMap(Identifier.CODEC, AdvancementProgress.CODEC);
    private static final Path advancementsPath = CLIENT_AATOOL_PATH.resolve("advancements");

    public static void handleAdvancementsSave(MinecraftClient client, Map<AdvancementEntry, AdvancementProgress> advancementProgresses) {
        if (ClientAATool.shouldLog && !client.isIntegratedServerRunning()) {
            String uuid = client.player != null ? client.player.getUuidAsString() : "advancements";
            Path advancementFile = advancementsPath.resolve(uuid+".json");
            if (!createFileIfMissing(advancementFile)) return;
            save(advancementFile, advancementProgresses);
        }
    }


    private static void save(Path filePath, Map<AdvancementEntry, AdvancementProgress> advancementProgresses) {
        try {
            CompletableFuture<?> future = DataProvider.writeCodecToPath(
                    DataWriter.UNCACHED,
                    ADVANCEMENT_PROGRESS_CODEC,
                    advancementProgresses
                            .entrySet()
                            .stream()
                            .filter(e->e.getValue().isAnyObtained())
                            .collect(Collectors.toMap(e->e.getKey().id(), Map.Entry::getValue)),
                    filePath);
            future.getNow(null);
            if (future.isCompletedExceptionally()) throw new IOException();
        } catch (IOException err) {
            ClientAATool.LOGGER.error("Couldn't save player advancements to {}\n{}", filePath, err);
        }
    }
}
