package org.devlukadev.skywarstoolsmod.utils;

import net.minecraft.client.gui.FontRenderer;

public class TextAlignUtil {
    public static String padToWidth(FontRenderer fr, String text, int targetPxWidth) {
        int spaceWidth = fr.getCharWidth(' ');
        int current = fr.getStringWidth(text);
        StringBuilder sb = new StringBuilder(text);
        while (current + spaceWidth <= targetPxWidth) {
            sb.append(' ');
            current += spaceWidth;
        }
        return sb.toString();
    }
}