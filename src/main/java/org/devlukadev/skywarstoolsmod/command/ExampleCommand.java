package org.devlukadev.skywarstoolsmod.command;

import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in ExampleMod.java with `CommandManager.INSTANCE.registerCommand(new ExampleCommand());`
 *
 * @see Command
 * @see Main
 * @see SkyWarsToolsMod
 */
@Command(value = SkyWarsToolsMod.MODID, description = "Access the " + SkyWarsToolsMod.NAME + " GUI.")
public class ExampleCommand {
    @Main
    private void handle() {
        SkyWarsToolsMod.INSTANCE.config.openGui();
    }
}