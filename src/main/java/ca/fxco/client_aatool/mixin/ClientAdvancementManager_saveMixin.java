package ca.fxco.client_aatool.mixin;

import ca.fxco.client_aatool.AdvancementSaveHandler;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManager_saveMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow @Final private Map<AdvancementEntry, AdvancementProgress> advancementProgresses;

    @Inject(
            method = "onAdvancements(Lnet/minecraft/network/packet/s2c/play/AdvancementUpdateS2CPacket;)V",
            at = @At("RETURN")
    )
    public void onAdvancementsSave(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
        AdvancementSaveHandler.handleAdvancementsSave(this.client,this.advancementProgresses);
    }

}
