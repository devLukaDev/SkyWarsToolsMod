package org.devlukadev.skywarstoolsmod.features.usagetimer;

import cc.polyfrost.oneconfig.events.event.HudRenderEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

public class UsageTimerHUD {

    @Subscribe
    private void onHudRender(HudRenderEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.cooldownsHUDEnabled) return;

        String label = getActiveItemLabel();
        if (label == null) return; // player doesn't have a tracked item, draw nothing

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int centerX = sr.getScaledWidth() / 2;
        int centerY = sr.getScaledHeight() / 2;

        int yOffset = 20; // distance below the crosshair

        NanoVGHelper.INSTANCE.setupAndDraw((vg) -> {
            NanoVGHelper.INSTANCE.drawText(
                    vg,
                    label,
                    centerX,
                    centerY + yOffset,
                    -1,          // white, ARGB "merged int" color
                    16,          // font size
                    Fonts.REGULAR
            );
        });
    }

    /**
     * Returns display text for whichever tracked item is active, or null if none.
     */
    private static String getActiveItemLabel() {
        if (UsageTimerInventory.hasCorruptedPearl) return "Corrupted Pearl";
        if (UsageTimerInventory.hasEchoClock) return "Echo Clock";
        if (UsageTimerInventory.hasEndlordPearl) return "EndLord Pearl";
        return null;
    }
}
