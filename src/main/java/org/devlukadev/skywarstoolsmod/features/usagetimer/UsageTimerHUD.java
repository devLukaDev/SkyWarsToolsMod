package org.devlukadev.skywarstoolsmod.features.usagetimer;

import cc.polyfrost.oneconfig.events.event.HudRenderEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.asset.Image;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.lwjgl.opengl.Display;

public class UsageTimerHUD {

    // cache images once instead of re-instantiating every frame
    private static final Image CORRUPTED = new Image("/corrupted.png", 32);
    private static final Image ECHO = new Image("/echo.png", 32);
    private static final Image ENDLORD = new Image("/endlord_pearl.png", 32);
    private static final Image EGG = new Image("/egg.png", 32);

    @Subscribe
    private void onHudRender(HudRenderEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.cooldownsHUDEnabled) return;

        int centerX = Display.getWidth() / 2;
        int centerY = Display.getHeight() / 2;

        int iconSize = SkyWarsToolsMod.config.cooldownsHUDSize;
        int crossHairDistance = SkyWarsToolsMod.config.cooldownsHUDDistance;

        if (UsageTimerInventory.hasCorruptedPearl) {
            float imgX = (centerX - crossHairDistance) - ((float) iconSize / 2);
            float imgY = centerY - ((float) iconSize / 2);
            drawIconWithTimer(CORRUPTED, imgX, imgY, iconSize, "corrupted_pearl");
        }

        if (UsageTimerInventory.hasEchoClock) {
            float imgX = centerX - ((float) iconSize / 2);
            float imgY = (centerY - crossHairDistance) - ((float) iconSize / 2);
            drawIconWithTimer(ECHO, imgX, imgY, iconSize, "echo_clock");
        }

        if (UsageTimerInventory.hasEndlordPearl) {
            float imgX = (centerX + crossHairDistance) - ((float) iconSize / 2);
            float imgY = centerY - ((float) iconSize / 2);
            drawIconWithTimer(ENDLORD, imgX, imgY, iconSize, "endlord_pearl");
        }

        if (UsageTimerInventory.hasCryoBridgeEgg) {
            float imgX = centerX - ((float) iconSize / 2);
            float imgY = (centerY - crossHairDistance) - ((float) iconSize / 2);
            drawIconWithTimer(EGG, imgX, imgY, iconSize, "cyro_bridge_egg");
        }
    }

    /**
     * Draws an icon at (imgX, imgY) with size iconSize, plus its countdown text
     * offset consistently the same way the Echo Clock block originally was
     * (the only one that lined up correctly).
     */
    private static void drawIconWithTimer(Image image, float imgX, float imgY, int iconSize, String key) {
        float textX = imgX + ((float) iconSize / 4);
        float textY = imgY + ((float) iconSize / 2);
        boolean onCooldown = UsageTimerManager.isOnCooldown(key);

        String text;
        int fontSize;
        if (onCooldown) {
            text = String.valueOf(UsageTimerManager.getRemainingSeconds(key));
            fontSize = iconSize / text.length();
        } else {
            fontSize = 20;
            text = "";
        }


        NanoVGHelper.INSTANCE.setupAndDraw((vg) -> {
            NanoVGHelper.INSTANCE.drawImage(vg, image, imgX, imgY, iconSize, iconSize);
            if (onCooldown) {
                NanoVGHelper.INSTANCE.drawImage(vg, "/timeout.png", imgX, imgY, iconSize, iconSize, SkyWarsToolsMod.class);
            }
            NanoVGHelper.INSTANCE.drawText(vg, text, textX, textY, -1, fontSize, Fonts.MINECRAFT_REGULAR);
        });
    }
}