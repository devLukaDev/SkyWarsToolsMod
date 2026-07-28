package org.devlukadev.skywarstoolsmod;

import org.devlukadev.skywarstoolsmod.command.ExampleCommand;
import org.devlukadev.skywarstoolsmod.config.SWTConfig;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * The entrypoint of the Example Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = SkyWarsToolsMod.MODID, name = SkyWarsToolsMod.NAME, version = SkyWarsToolsMod.VERSION)
public class SkyWarsToolsMod {

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @Mod.Instance(MODID)
    public static SkyWarsToolsMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static SWTConfig config;

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new SWTConfig();
        CommandManager.INSTANCE.registerCommand(new ExampleCommand());
    }
}
