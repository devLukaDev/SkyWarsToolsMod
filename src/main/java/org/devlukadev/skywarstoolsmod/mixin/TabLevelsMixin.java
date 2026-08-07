package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;


@Mixin(GuiPlayerTabOverlay.class)
    public class TabLevelsMixin {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void modifyPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (!LocationUtil.getCurrentLocation().getMap().isPresent()) return; // In a lobby

        UUID uuid = networkPlayerInfoIn.getGameProfile().getId();
        if (NickDetector.isLikelyNicked(networkPlayerInfoIn)) {
            cir.setReturnValue("§c[?] " + cir.getReturnValue());
            return;
        }
        cir.setReturnValue(SkyWarsRequestCache.getPrefix(uuid) + cir.getReturnValue());
    }
}
