package org.devlukadev.skywarstoolsmod.features.sessions;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.platform.Platform;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

import java.util.ArrayList;
import java.util.List;

public class SessionHUD extends BasicHud {
    protected transient List<String> lines = new ArrayList<>();

    @Color(
            name = "First Column Color"
    )
    protected OneColor titleColor = new OneColor(255, 255, 255);

    @Color(
            name = "Value Color"
    )
    protected OneColor valueColor = new OneColor(255, 255, 255);

    @Color(
            name = "Second Column Color"
    )
    protected OneColor divColor = new OneColor(255, 255, 255);

    @Dropdown(
            name = "Text Type",
            options = {"No Shadow", "Shadow", "Full Shadow"}
    )
    protected int textType = 0;

    public SessionHUD() {
        super(true, 50, 50, 1, true,
                false, 0, 5, 5, new OneColor(10, 10, 10, 100),
                false, 0, new OneColor(10, 10, 10, 100));
    }

    @Override
    protected void preRender(boolean example) {
        lines.clear();

        SessionData data = SessionManager.getInstance().getData();
        SessionData.BaselineSnapshot baseline = SessionManager.getInstance().getCurrentStats();
        lines.add("Wins: §" + baseline.wins + "§ | W/L: §" +
                (baseline.losses == 0 ? "∞" : String.format("%.2f", (double) baseline.wins / baseline.losses)));

        lines.add("Kills: §" + baseline.kills + "§ | K/D: §" +
                (baseline.deaths == 0 ? "∞" : String.format("%.2f", (double) baseline.kills / baseline.deaths)));

        lines.add("Session Wins: §" + data.wins + "§ | W/L: §" +
                (data.losses == 0 ? "∞" : String.format("%.2f", (double) data.wins / data.losses)));

        lines.add("Session Kills: §" + data.kills + "§ | K/D: §" +
                (data.deaths == 0 ? "∞" : String.format("%.2f", (double) data.kills / data.deaths)));

        lines.add("Session EXP: §" + data.xpGained + "§ | EXP/H: §" +
                (data.time_played == 0 ? "∞" : Math.round(data.xpGained / (data.time_played / 3600.0))));

        lines.add("Session Time: §" + timeSince(data.sessionStartMillis));

        lines.add("Session Playtime: §" + formatTimestamp(data.time_played));
    }


    @Override
    public void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        if (lines == null || lines.isEmpty()) return;

        float textY = y;

        for (String line : lines) {
            drawLine(line, x, textY, scale);
            textY += (12 * scale);
        }
    }

    /**
     * Function that can be overwritten to implement different behavior easily
     *
     * @param line  The line
     * @param x     The X coordinate
     * @param y     The Y coordinate
     * @param scale The scale
     */
    private void drawLine(String line, float x, float y, float scale) {
        String[] parts = line.split("§", -1);

        String title = parts[0];
        String value = parts.length > 1 ? parts[1] : "";
        String divider = parts.length > 2 ? parts[2] : "";
        String value2 = parts.length > 3 ? parts[3] : "";

        float currentX = x;

        TextRenderer.drawScaledString(
                title,
                currentX,
                y,
                titleColor.getRGB(),
                TextRenderer.TextType.toType(textType),
                scale
        );

        currentX += Platform.getGLPlatform().getStringWidth(title) * scale;

        TextRenderer.drawScaledString(
                value,
                currentX,
                y,
                valueColor.getRGB(),
                TextRenderer.TextType.toType(textType),
                scale
        );

        currentX += Platform.getGLPlatform().getStringWidth(value) * scale;

        TextRenderer.drawScaledString(
                divider,
                currentX,
                y,
                divColor.getRGB(),
                TextRenderer.TextType.toType(textType),
                scale
        );

        currentX += Platform.getGLPlatform().getStringWidth(divider) * scale;

        TextRenderer.drawScaledString(
                value2,
                currentX,
                y,
                valueColor.getRGB(),
                TextRenderer.TextType.toType(textType),
                scale
        );
    }

    /**
     * Function that can be overwritten to implement different behavior easily
     *
     * @param line The line
     * @return The width of the line (scaled accordingly)
     */
    protected float getLineWidth(String line, float scale) {
        String[] parts = line.split("§", -1);

        float width = 0;
        for (String part : parts) {
            width += Platform.getGLPlatform().getStringWidth(part);
        }

        return width * scale;
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        if (lines == null) return 0;

        float width = 0;
        for (String line : lines) {
            width = Math.max(width, getLineWidth(line, scale));
        }

        return width;
    }

    @Override
    public boolean shouldShow() {
        return super.shouldShow() && LocationUtil.isInSkyWars() && SkyWarsToolsMod.config.sessionsEnabled;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return lines == null ? 0 : (lines.size() * 12 - 4) * scale;
    }

    @Override
    public boolean shouldDrawBackground() {
        return super.shouldDrawBackground() && lines != null && !lines.isEmpty();
    }


    private String timeSince(long timestamp) {
        long seconds = (System.currentTimeMillis() - timestamp) / 1000;

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    private String formatTimestamp(long timestamp) {
        long hours = timestamp / 3600;
        long minutes = (timestamp % 3600) / 60;
        long secs = timestamp % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);

    }

}