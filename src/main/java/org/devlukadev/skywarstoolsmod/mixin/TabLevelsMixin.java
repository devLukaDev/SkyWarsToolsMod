package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabColumnWidths;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabRowRenderContext;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabStringConstructor;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public class TabLevelsMixin {

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void modifyPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (!LocationUtil.getCurrentLocation().getMap().isPresent()) return;

        String originalName = cir.getReturnValue();
        SkyWarsResponse resp;
        boolean nicked;

        if (NickDetector.isLikelyNicked(networkPlayerInfoIn)) {
            if (NickDetector.isMythical(networkPlayerInfoIn)) {
                resp = SkyWarsRequestCache.getStats(networkPlayerInfoIn.getGameProfile().getName());
                nicked = false;
            } else {
                resp = null;
                nicked = true;
            }
        } else {
            resp = SkyWarsRequestCache.getStats(networkPlayerInfoIn.getGameProfile().getId());
            nicked = false;
        }

        List<String> segments = TabStringConstructor.resolveSegments(resp, originalName, nicked);
        String plain = String.join("", segments);

        // stash for the upcoming drawStringWithShadow call
        TabRowRenderContext.lastSegments = segments;
        TabRowRenderContext.lastColWidths = TabColumnWidths.getWidths();
        TabRowRenderContext.lastBuiltString = plain;

        cir.setReturnValue(plain); // used for width-scan sizing + fallback
    }
}