package org.devlukadev.skywarstoolsmod;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.devlukadev.skywarstoolsmod.command.ExampleCommand;
import org.devlukadev.skywarstoolsmod.command.OpenAutododgeGUI;
import org.devlukadev.skywarstoolsmod.config.SWTConfig;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.devlukadev.skywarstoolsmod.enhancedwho.EnhancedWho;
import org.devlukadev.skywarstoolsmod.events.AutododgeEvents;
import org.devlukadev.skywarstoolsmod.events.LastGameEXPEvents;
import org.devlukadev.skywarstoolsmod.updater.SWTUpdater;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.scheduler.ClientScheduler;

import java.io.File;

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


    private File cacheFolder;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.cacheFolder = new File(event.getModConfigurationDirectory(), "swt");

        new SWTUpdater(event.getSourceFile()).start();
    }

    public File getCacheFolder(){
        return cacheFolder;
    }

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new SWTConfig();
        HypixelUtils.INSTANCE.initialize();
        CommandManager.INSTANCE.registerCommand(new ExampleCommand());
        MinecraftForge.EVENT_BUS.register(new ClientScheduler());

        // Hypixel Mod API
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, LocationUtil::onLocationReceived);

        // Last Game EXP
        LastGameEXPEvents lastGameEXPEvents = new LastGameEXPEvents();

        MinecraftForge.EVENT_BUS.register(lastGameEXPEvents);
        EventManager.INSTANCE.register(lastGameEXPEvents);

        // Autododge
        AutododgeEvents autododge = new AutododgeEvents();

        LocationUtil.addListener(autododge::onLocationReceived);
        MinecraftForge.EVENT_BUS.register(autododge);

        CommandManager.INSTANCE.registerCommand(new OpenAutododgeGUI());

        // Enhanced Who
        MinecraftForge.EVENT_BUS.register(new EnhancedWho());

        // TabLevels
//        TabLevels tabLevels = new TabLevels();
//        LocationUtil.addListener(tabLevels::onGameJoin);
//        MinecraftForge.EVENT_BUS.register(tabLevels);


    }
}
