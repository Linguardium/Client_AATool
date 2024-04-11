package ca.fxco.client_aatool.mixin;

import ca.fxco.client_aatool.StatsSaveHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandler_saveMixin extends ClientCommonNetworkHandler {

    protected ClientPlayNetworkHandler_saveMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
        // Ignored constructor needed to extend target class parent
        // could just as easily use MinecraftClient.getInstance()
    }

    @Inject(
            method = "onStatistics(Lnet/minecraft/network/packet/s2c/play/StatisticsS2CPacket;)V",
            at = @At("RETURN")
    )
    public void onStatisticsSave(StatisticsS2CPacket packet, CallbackInfo ci) {
            StatsSaveHandler.handleStatsSave(this.client);
    }
}
