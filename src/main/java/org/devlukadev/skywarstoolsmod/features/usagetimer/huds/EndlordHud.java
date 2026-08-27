package org.devlukadev.skywarstoolsmod.features.usagetimer.huds;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.asset.Image;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerInventory;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.lwjgl.opengl.Display;

import static org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerHUD.drawIconWithTimer;

public class EndlordHud extends BasicHud {

    // cache images once instead of re-instantiating every frame
    private static Image ENDLORD = new Image("/endlord_pearl.png", 32);

    public EndlordHud() {
        super(true, (float) (Display.getWidth() / 2) + 15, 20, 1, false, false, 0,
                0, 0, new OneColor(0, 0, 0, 0), false, 0, new OneColor(0, 0, 0, 0));
    }

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        if (example) drawIconWithTimer(ENDLORD, x, y, scale, "endlord_pearl");
        if (!LocationUtil.isInSkyWars()) return;

        if (UsageTimerInventory.hasEndlordPearl) {
            drawIconWithTimer(ENDLORD, x, y, scale, "endlord_pearl");
        }
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return 30 * scale;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return 30 * scale;
    }

}



