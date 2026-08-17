package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabColumnWidths;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabRowRenderContext;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabStringConstructor;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public class TabLevelsMixin {

    @Unique
    private static long skyWarsToolsMod$lastRecompute = 0;

    // This is responsible for setting the playerName to whatever the user has specified when it comes to segments
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void modifyPlayerName(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> cir) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (LocationUtil.isInLobby()) return;

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

    // Responsible for injecting on the rendering of the text, which is used to vertically align all segments
    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int redirectRowDraw(FontRenderer fr, String text, float x, float y, int color) {
        if (LocationUtil.isInLobby() || !SkyWarsToolsMod.config.levelsEnabled || !LocationUtil.isInSkyWars()) {
            return fr.drawStringWithShadow(text, x, y, color);
        }

        List<String> segments = TabRowRenderContext.lastSegments;
        String expected = TabRowRenderContext.lastBuiltString;

        // Guard: this signature is ALSO used for header/footer text, so only
        // intercept if this call's text matches the row we just resolved
        // (accounting for the spectator italic prefix vanilla may have added).
        boolean isOurRow = expected != null && segments != null &&
                (text.equals(expected) || text.equals(EnumChatFormatting.ITALIC + expected));

        if (!isOurRow) {
            return fr.drawStringWithShadow(text, x, y, color);
        }

        boolean italic = text.startsWith(EnumChatFormatting.ITALIC.toString());
        int[] widths = TabRowRenderContext.lastColWidths;
        float cursorX = x;
        int total = 0;

        int prevDrawnPosX = (int) cursorX;
        for (int i = 0; i < segments.size(); i++) {

            String seg = italic ? EnumChatFormatting.ITALIC + segments.get(i) : segments.get(i);

            int drawnPosX = fr.drawStringWithShadow(seg, cursorX, y, color);
            int colWidth = (widths != null && i < widths.length) ? widths[i] : drawnPosX - prevDrawnPosX;
            prevDrawnPosX = drawnPosX;
            cursorX += colWidth;
            total += colWidth;
        }

        // consume so a later header/footer draw with the same text (unlikely) doesn't re-trigger
        TabRowRenderContext.lastSegments = null;
        TabRowRenderContext.lastBuiltString = null;
        if (total > TabRowRenderContext.max) TabRowRenderContext.max = total;
        return total;
    }


    // Making sure we get the latest information on which players are on Tab
    @Inject(method = "updatePlayerList", at = @At("HEAD"))
    private void onUpdatePlayerList(boolean willBeRendered, CallbackInfo ci) {
        if (!LocationUtil.isInSkyWars() || LocationUtil.isInLobby() || !SkyWarsToolsMod.config.levelsEnabled) {
            return;
        }
        if (!willBeRendered) return;
        long now = Minecraft.getSystemTime();
        if (now - skyWarsToolsMod$lastRecompute < 200) return;
        skyWarsToolsMod$lastRecompute = now;

        NetHandlerPlayClient nh = Minecraft.getMinecraft().thePlayer.sendQueue;
        TabColumnWidths.recompute(
                Minecraft.getMinecraft().fontRendererObj,
                nh.getPlayerInfoMap()
        );
    }

    // Setting the width to fit
    @ModifyVariable(method = "renderPlayerlist", at = @At("STORE"), ordinal = 1)
    private int modifyTabListWidth(int j) {
        if (TabRowRenderContext.max == 0 || !LocationUtil.isInSkyWars() || LocationUtil.isInLobby()) {
            TabRowRenderContext.max = 0;
            // Reset to zero so any value is bigger again, so it can be incrementally increased to fit new players
            return j;
        }
        return TabRowRenderContext.max;
    }

}