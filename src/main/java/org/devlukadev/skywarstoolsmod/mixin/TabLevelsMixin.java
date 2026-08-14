package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabStringConstructor;
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

        String originalName = cir.getReturnValue();

        if (NickDetector.isLikelyNicked(networkPlayerInfoIn)) {
            if (NickDetector.isMythical(networkPlayerInfoIn)) {
                // Could still be nicked, but we can't know for sure - fetch anyway
                SkyWarsResponse cached = SkyWarsRequestCache.getStats(networkPlayerInfoIn.getGameProfile().getName());
                cir.setReturnValue(TabStringConstructor.build(cached, originalName, false));
            } else {
                cir.setReturnValue(TabStringConstructor.build(null, originalName, true));
            }
            return;
        }

        UUID uuid = networkPlayerInfoIn.getGameProfile().getId();
        SkyWarsResponse cached = SkyWarsRequestCache.getStats(uuid);
        cir.setReturnValue(TabStringConstructor.build(cached, originalName, false));
    }
}