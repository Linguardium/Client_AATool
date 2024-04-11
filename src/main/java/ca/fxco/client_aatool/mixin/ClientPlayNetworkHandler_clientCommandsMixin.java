package ca.fxco.client_aatool.mixin;

import ca.fxco.client_aatool.ClientCommands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandler_clientCommandsMixin {

    @Shadow
    private CommandDispatcher<ServerCommandSource> commandDispatcher;


    @Inject(
            at = @At("RETURN"),
            method = "<init>"
    )
    public void onInit(MinecraftClient client,
                       ClientConnection clientConnection,
                       ClientConnectionState clientConnectionState,
                       CallbackInfo ci) {
        ClientCommands.registerCommands(this.commandDispatcher);
    }


    @Inject(
            method = "onCommandTree",
            at = @At("TAIL")
    )
    public void onOnCommandTree(CommandTreeS2CPacket packet, CallbackInfo ci) {
        ClientCommands.registerCommands(this.commandDispatcher);
    }

    @Inject(
            at = @At("HEAD"),
            method = "sendChatCommand",
            cancellable = true
    )
    private void onSendCommand(String command, CallbackInfo ci) {
        StringReader reader = new StringReader(command);
        int cursor = reader.getCursor();
        reader.setCursor(cursor);
        if (ClientCommands.isClientSideCommand(command.split(Pattern.quote(" ")))) {
            ClientCommands.executeCommand(reader);
            ci.cancel();
        }
    }

}
