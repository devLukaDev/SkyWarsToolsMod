package org.devlukadev.skywarstoolsmod.features.usagetimer.huds;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.asset.Image;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerInventory;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerManager;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.lwjgl.opengl.Display;

import static org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerHUD.drawIconWithTimer;

public class EchoHud extends BasicHud {

    // cache images once instead of re-instantiating every frame
    private static Image ECHO = new Image("/echo.png", 32);

    public EchoHud() {
        super(true, (float) (Display.getWidth() / 2) - 40, 20, 1, false, false, 0,
                0, 0, new OneColor(0, 0, 0, 0), false, 0, new OneColor(0, 0, 0, 0));
    }

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        if (example) drawIconWithTimer(ECHO, x, y, scale, "echo_clock");
        if (!LocationUtil.isInSkyWars()) return;

        if (UsageTimerInventory.hasEchoClock) {
            drawIconWithTimer(ECHO, x, y, scale, "echo_clock");
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



