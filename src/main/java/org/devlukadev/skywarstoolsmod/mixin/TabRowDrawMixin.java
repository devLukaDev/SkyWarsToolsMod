package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.EnumChatFormatting;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabColumnWidths;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabRowRenderContext;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public class TabRowDrawMixin {

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    //TODO look at alternative for Redirect
    private int redirectRowDraw(FontRenderer fr, String text, float x, float y, int color) {
        if (LocationUtil.isInLobby() || !SkyWarsToolsMod.config.levelsEnabled || !LocationUtil.isInSkyWars()) {
            return fr.drawStringWithShadow(text,x,y,color);
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

        int prevDrawnPosX = 0;
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
}

