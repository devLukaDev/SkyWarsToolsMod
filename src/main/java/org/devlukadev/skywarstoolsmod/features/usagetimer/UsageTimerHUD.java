package org.devlukadev.skywarstoolsmod.features.usagetimer;

import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.asset.Image;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;


public class UsageTimerHUD {

    public static void drawIconWithTimer(Image image, float imgX, float imgY, float scale, String key) {
        float textX = imgX + ((scale * 30) / 4);
        float textY = imgY + ((scale * 30) / 2);
        boolean onCooldown = UsageTimerManager.isOnCooldown(key);

        String text;
        int fontSize;
        if (onCooldown) {
            text = String.valueOf(UsageTimerManager.getRemainingSeconds(key));
            fontSize = (int) ((scale * 30) / text.length());
        } else {
            fontSize = 20;
            text = "";
        }
        NanoVGHelper.INSTANCE.setupAndDraw(true, (vg) -> {
            NanoVGHelper.INSTANCE.drawImage(vg, image, imgX, imgY, scale * 30, scale * 30);
            if (onCooldown) {
                NanoVGHelper.INSTANCE.drawImage(vg, "/timeout.png", imgX, imgY, scale * 30, scale * 30, SkyWarsToolsMod.class);
                NanoVGHelper.INSTANCE.drawText(vg, text, textX, textY, -1, fontSize, Fonts.MINECRAFT_REGULAR);
            }
        });
    }

}