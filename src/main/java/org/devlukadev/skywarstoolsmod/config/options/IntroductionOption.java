package org.devlukadev.skywarstoolsmod.config.options;

import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.asset.Image;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;

import java.lang.reflect.Field;

public class IntroductionOption extends BasicOption {
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
    public IntroductionOption(Field field, Object parent, String name, String description, String category, String subcategory, int size) {
        super(field, parent, name, description, category, subcategory, size);
    }

    @Override
    public int getHeight() {
        return 370;
    }

    // padding
    private int cursorY;

    private static final Image logo = new Image("/logo-480.png", 32);

    public void draw(long vg, int x, int y, InputHandler inputHandler) {
        // padding
        int tx = x + 16;
        cursorY = y + 16;

        drawLine(vg, tx, "SkyWarsToolsMod", 26, -1);
        drawLine(vg, tx, "SkyWars Utility mod by devLukaDev - v@VER@", 12, -1);
        drawLine(vg, tx, "", 20, -1);
        drawLine(vg, tx, "A mod bringing essentials to your SkyWars playing experience! Such as:", 14, -1);
        drawLine(vg, tx, "- Automatically dodge maps", 14, -1);
        drawLine(vg, tx, "- HyStats-esque session tracking", 14, -1);
        drawLine(vg, tx, "- Tablist player information", 14, -1);
        drawLine(vg, tx, "- UI enhancements for certain kits", 14, -1);
        drawLine(vg, tx, "- Better /who", 14, -1);
        drawLine(vg, tx, "- Various small fixes!", 14, -1);
        drawLine(vg, tx, "", 20, -1);
        drawLine(vg, tx, "Have questions/comments or found a bug?", 14, -1);
        drawLine(vg, tx, "Go to SkyWarsTools.com and join the discord.", 14, -1);
        drawLine(vg, tx, "", 20, -1);
        drawLine(vg, tx, "Credits", 12, -1);
        drawLine(vg, tx, "Main developer: devLukaDev", 12, -1);
        drawLine(vg, tx, "Attributions:", 12, -1);
        drawLine(vg, tx, "Alexdoru - Updater, Nick-detect", 12, -1);
        drawLine(vg, tx, "Yedelo - Launch Tweaker", 12, -1);

        NanoVGHelper.INSTANCE.drawImage(vg, logo, x + 700, y + 10, 300, 300);


    }


    private void drawLine(long vg, int x, String text, int fontSize, int color) {
        NanoVGHelper.INSTANCE.drawText(vg, text, x, cursorY, color, fontSize, Fonts.MEDIUM);
        cursorY += fontSize + 4;
    }
}
