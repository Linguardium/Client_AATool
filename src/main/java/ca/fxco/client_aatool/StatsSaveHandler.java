package ca.fxco.client_aatool;

import ca.fxco.client_aatool.mixin.StatHandlerAccessor;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static ca.fxco.client_aatool.ClientAATool.CLIENT_AATOOL_PATH;
import static ca.fxco.client_aatool.FileUtils.createFileIfMissing;

public class StatsSaveHandler {
    private static final Codec<Map<Identifier, Map<Identifier,Integer>>> STATS_TYPE_ENTRIES = Codec.unboundedMap(Identifier.CODEC, Codec.unboundedMap(Identifier.CODEC, Codec.INT));
    private static final Path statisticsPath = CLIENT_AATOOL_PATH.resolve("stats");

    public static void handleStatsSave(MinecraftClient client) {
        if (ClientAATool.shouldLog && !client.isIntegratedServerRunning() && client.player != null) {
            String uuid = client.player.getUuidAsString();
            Path statisticsFile = statisticsPath.resolve(uuid + ".json");
            if (!createFileIfMissing(statisticsFile)) return;
            save(statisticsFile, client.player);
        }
    }

    private static void save(Path filePath, ClientPlayerEntity player) {

        Map<Identifier, Map<Identifier, Integer>> map2 = new HashMap<>();
        StatHandlerAccessor accessor = (StatHandlerAccessor)player.getStatHandler();
        for (Object2IntMap.Entry<Stat<?>> statEntry : accessor.getStatMap().object2IntEntrySet()) {
            Identifier statTypeId = Registries.STAT_TYPE.getId(statEntry.getKey().getType());
            Identifier statId = getStatId(statEntry.getKey());
            map2.computeIfAbsent(statTypeId, k -> new HashMap<>())
                    .put(statId, statEntry.getIntValue());
        }

        try {
            CompletableFuture<?> future = DataProvider.writeCodecToPath(
                    DataWriter.UNCACHED,
                    STATS_TYPE_ENTRIES,
                    map2,
                    filePath);
            future.getNow(null);
            if (future.isCompletedExceptionally()) throw new IOException();
        } catch (IOException err) {
            ClientAATool.LOGGER.error("Couldn't save stats", err);
        }
    }

        private static <T> Identifier getStatId(Stat<T> stat) {
        return stat.getType().getRegistry().getId(stat.getValue());
    }
}
