package org.devlukadev.skywarstoolsmod.config.pages;

import cc.polyfrost.oneconfig.gui.pages.Page;
import cc.polyfrost.oneconfig.utils.InputHandler;

public class InfoPage extends Page {
    public InfoPage() {
        super("My Page"); // set the name of the page
    }


    @Override
    public void draw(long vg, int x, int y, InputHandler inputHandler) {

    }

    public int getMaxScrollHeight() {
        return 696; // No scrolling!
    }
}
