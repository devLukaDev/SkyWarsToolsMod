package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
@Mixin(GuiPlayerTabOverlay.class)
public class MinecraftMixin {

    // TODO get rid of redirect because https://mixins.microcontrollers.dev/default/redirect/
    @Redirect(
            method = "renderPlayerlist",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;getPlayerName(Lnet/minecraft/client/network/NetworkPlayerInfo;)Ljava/lang/String;"
            )
    )
    private String redirectGetPlayerName(GuiPlayerTabOverlay instance, NetworkPlayerInfo info) {
        return instance.getPlayerName(info) + " TEST!";
    }
}
