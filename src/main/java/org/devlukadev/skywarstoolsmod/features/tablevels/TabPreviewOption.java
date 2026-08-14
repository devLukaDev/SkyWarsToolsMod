package org.devlukadev.skywarstoolsmod.features.tablevels;

import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;
import cc.polyfrost.oneconfig.utils.color.ColorUtils;
import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabPreviewOption extends BasicOption {
    /**
     * Initialize option
     *
     * @param field       variable attached to option (null for category)
     * @param parent      the parent object of the field, used for getting and setting the variable
     * @param name        name of option
     * @param description The description
     * @param category    The category
     * @param subcategory The subcategory
     * @param size        size of option, 0 for single column, 1 for double.
     */
    public TabPreviewOption(Field field, Object parent, String name, String description, String category, String subcategory, int size) {
        super(field, parent, name, description, category, subcategory, size);
    }

    @Override
    public int getHeight() {
        return 80;
    }

    // Standard Minecraft color codes -> RGBA ints via your ColorUtils
    private static final Map<Character, Integer> COLOR_CODES = new HashMap<>();

    static {
        COLOR_CODES.put('0', ColorUtils.getColor(0, 0, 0, 255));
        COLOR_CODES.put('1', ColorUtils.getColor(0, 0, 170, 255));
        COLOR_CODES.put('2', ColorUtils.getColor(0, 170, 0, 255));
        COLOR_CODES.put('3', ColorUtils.getColor(0, 170, 170, 255));
        COLOR_CODES.put('4', ColorUtils.getColor(170, 0, 0, 255));
        COLOR_CODES.put('5', ColorUtils.getColor(170, 0, 170, 255));
        COLOR_CODES.put('6', ColorUtils.getColor(255, 170, 0, 255));
        COLOR_CODES.put('7', ColorUtils.getColor(170, 170, 170, 255));
        COLOR_CODES.put('8', ColorUtils.getColor(85, 85, 85, 255));
        COLOR_CODES.put('9', ColorUtils.getColor(85, 85, 255, 255));
        COLOR_CODES.put('a', ColorUtils.getColor(85, 255, 85, 255));
        COLOR_CODES.put('b', ColorUtils.getColor(85, 255, 255, 255));
        COLOR_CODES.put('c', ColorUtils.getColor(255, 85, 85, 255));
        COLOR_CODES.put('d', ColorUtils.getColor(255, 85, 255, 255));
        COLOR_CODES.put('e', ColorUtils.getColor(255, 255, 85, 255));
        COLOR_CODES.put('f', ColorUtils.getColor(255, 255, 255, 255));
        COLOR_CODES.put('r', ColorUtils.getColor(255, 255, 255, 255));
    }

    private static final int DEFAULT_COLOR = ColorUtils.getColor(255, 255, 255, 255);

    public void draw(long vg, int x, int y, InputHandler inputHandler) {
        NanoVGHelper.INSTANCE.drawRect(
                vg,
                x, y, x + 630, 80,
                ColorUtils.getColor(20, 20, 20, 100)
        );

        String example = applyPlaceholders(SkyWarsToolsMod.config.tabLevelText);
        drawColoredCenteredText(vg, example, x + 480, y + 40);
    }

    // Swap % tokens for sample values
    private String applyPlaceholders(String text) {
        text = text.replace("%default%",
                Minecraft.getMinecraft().thePlayer.getName());
        text = text.replace("%level%", "&4[&4&l3&5&l9&5&l6&czz_zz&c]&r");
        text = text.replace("%wl%", "0.75");
        text = text.replace("%kd%", "2.10");
        text = text.replace("%kills%", "12000");
        text = text.replace("%wins%", "5000");
        text = text.replace("%deaths%", "1600");
        text = text.replace("%losses%", "1500");
        text = text.replace("%exp%", "1912940");
        return text;
    }

    // Split on & codes, then draw segments side by side, centered as a whole
    private void drawColoredCenteredText(long vg, String text, float centerX, float centerY) {
        List<TextSegment> segments = parseColorCodes(text);
        float size = 38;

        float totalWidth = 0f;
        for (TextSegment seg : segments) {
            totalWidth += NanoVGHelper.INSTANCE.getTextWidth(vg, seg.text, (float) size, Fonts.MINECRAFT_REGULAR);
        }

        float cx = centerX - totalWidth / 2f;
        for (TextSegment seg : segments) {
            NanoVGHelper.INSTANCE.drawText(vg, seg.text, cx, centerY, seg.color, (float) size, Fonts.MINECRAFT_REGULAR);
            cx += NanoVGHelper.INSTANCE.getTextWidth(vg, seg.text, (float) size, Fonts.MINECRAFT_REGULAR);
        }
    }

    private List<TextSegment> parseColorCodes(String text) {
        List<TextSegment> segments = new ArrayList<>();
        int currentColor = DEFAULT_COLOR;
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '&' && i + 1 < chars.length) {
                if (sb.length() > 0) {
                    segments.add(new TextSegment(sb.toString(), currentColor));
                    sb.setLength(0);
                }
                if (COLOR_CODES.containsKey(Character.toLowerCase(chars[i + 1]))) {
                    currentColor = COLOR_CODES.get(Character.toLowerCase(chars[i + 1]));

                }
                i++; // skip the code char
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            segments.add(new TextSegment(sb.toString(), currentColor));
        }
        return segments;
    }

    private static class TextSegment {
        final String text;
        final int color;

        TextSegment(String text, int color) {
            this.text = text;
            this.color = color;
        }
    }

}
