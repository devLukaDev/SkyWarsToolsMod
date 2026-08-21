package org.devlukadev.skywarstoolsmod.config.options;

import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.asset.Image;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;

import java.lang.reflect.Field;

public class EnglishWarningOption extends BasicOption {
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
    public EnglishWarningOption(Field field, Object parent, String name, String description, String category, String subcategory, int size) {
        super(field, parent, name, description, category, subcategory, size);
    }

    @Override
    public int getHeight() {
        return 45;
    }

    // padding
    private int cursorY;

    private static final Image warning = new Image("/warning.png");

    public void draw(long vg, int x, int y, InputHandler inputHandler) {
        // padding
        NanoVGHelper.INSTANCE.drawImage(vg, warning, x, y, 45, 45);

        int tx = x + 65;
        cursorY = y + 12;

        drawLine(vg, tx, "Warning", 18, 0xFFEE2255);
        drawLine(vg, tx, "Feature only works if the Hypixel language is set to English!", 14, -1);

    }


    private void drawLine(long vg, int x, String text, int fontSize, int color) {
        NanoVGHelper.INSTANCE.drawText(vg, text, x, cursorY, color, fontSize, Fonts.MEDIUM);
        cursorY += fontSize + 4;
    }
}
