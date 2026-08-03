package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(GuiPlayerTabOverlay.class)
    public class MinecraftMixin {

        @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
        private void modifyPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
            cir.setReturnValue(cir.getReturnValue() + " TEST!");
        }
    }
