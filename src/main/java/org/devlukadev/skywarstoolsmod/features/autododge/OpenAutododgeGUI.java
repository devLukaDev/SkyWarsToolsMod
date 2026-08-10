package org.devlukadev.skywarstoolsmod.features.autododge;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;

@Command(value = "swt autododge", description = "Open the autododge edit screen")
public class OpenAutododgeGUI {
    private void handle() {
        GuiUtils.displayScreen(new AutododgeScreen());
    }
}
