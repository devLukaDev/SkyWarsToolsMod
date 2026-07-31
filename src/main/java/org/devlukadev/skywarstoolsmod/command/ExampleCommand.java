package org.devlukadev.skywarstoolsmod.command;

import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.scheduler.ClientScheduler;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in ExampleMod.java with `CommandManager.INSTANCE.registerCommand(new ExampleCommand());`
 *
 * @see Command
 * @see Main
 * @see SkyWarsToolsMod
 */
@Command(value = "swtexample", description = "Access the " + SkyWarsToolsMod.NAME + " GUI.", aliases = {"swtexample"})
public class ExampleCommand {
    @Main
    private void handle() {
        ClientScheduler.schedule(40, () -> Minecraft.getMinecraft().thePlayer.playSound("random.orb", 1.0F, 1.0F));
    }
}