package org.devlukadev.skywarstoolsmod.features.usagetimer.huds;

import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.asset.Image;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerInventory;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerManager;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

public class CorruptedHud extends BasicHud {

    // cache images once instead of re-instantiating every frame
    private static Image CORRUPTED = new Image("/corrupted.png", 32);

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
//        if (!LocationUtil.isInSkyWars()) return;

//        if (!UsageTimerInventory.hasCorruptedPearl) {
        drawIconWithTimer(CORRUPTED, x, y, 20, "corrupted_pearl");
//        }
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return 30;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return 30;
    }

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


        NanoVGHelper.INSTANCE.setupAndDraw( true, (vg) -> {
            NanoVGHelper.INSTANCE.drawImage(vg, image, imgX, imgY, iconSize, iconSize);
            if (onCooldown) {
                NanoVGHelper.INSTANCE.drawImage(vg, "/timeout.png", imgX, imgY, iconSize, iconSize, SkyWarsToolsMod.class);
            }
            NanoVGHelper.INSTANCE.drawText(vg, text, textX, textY, -1, fontSize, Fonts.MINECRAFT_REGULAR);
        });
    }
}



