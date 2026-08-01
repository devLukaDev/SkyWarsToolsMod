package org.devlukadev.skywarstoolsmod.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.hud.AutododgeScreen;
import org.devlukadev.skywarstoolsmod.utils.scheduler.ClientScheduler;

@Command(value = "swt autododge", description = "Open the autododge edit screen")
public class OpenAutododgeGUI {
    private void handle() {
        GuiUtils.displayScreen(new AutododgeScreen());
    }
}
