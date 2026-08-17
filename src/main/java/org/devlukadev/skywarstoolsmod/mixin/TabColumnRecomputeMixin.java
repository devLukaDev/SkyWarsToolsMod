package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabColumnWidths;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiPlayerTabOverlay.class)
public class TabColumnRecomputeMixin {
    @Unique
    private static long skyWarsToolsMod$lastRecompute = 0;

    @Inject(method = "updatePlayerList", at = @At("HEAD"))
    private void onUpdatePlayerList(boolean willBeRendered, CallbackInfo ci) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (!LocationUtil.getCurrentLocation().getMap().isPresent()) return; // In a lobby
        if (!SkyWarsToolsMod.config.levelsAlignment) return;

        if (!willBeRendered) return;
        long now = Minecraft.getSystemTime();
        if (now - skyWarsToolsMod$lastRecompute < 200) return;
        skyWarsToolsMod$lastRecompute = now;

        NetHandlerPlayClient nh = Minecraft.getMinecraft().thePlayer.sendQueue;
        TabColumnWidths.recompute(
                Minecraft.getMinecraft().fontRendererObj,
                nh.getPlayerInfoMap(),
                p -> SkyWarsRequestCache.getStats(p.getGameProfile().getId()),
                NickDetector::isLikelyNicked

        );
    }
}
