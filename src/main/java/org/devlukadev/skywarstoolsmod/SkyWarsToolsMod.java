package org.devlukadev.skywarstoolsmod;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.command.ExampleCommand;
import org.devlukadev.skywarstoolsmod.features.autododge.OpenAutododgeGUI;
import org.devlukadev.skywarstoolsmod.config.SWTConfig;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.devlukadev.skywarstoolsmod.features.enhancedwho.EnhancedWho;
import org.devlukadev.skywarstoolsmod.features.autododge.AutododgeEvents;
import org.devlukadev.skywarstoolsmod.features.kitselectorfix.KitSelectorFixEvent;
import org.devlukadev.skywarstoolsmod.features.lastgameexp.LastGameEXPEvents;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerHUD;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerInventory;
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
        LocationUtil.addListener(lastGameEXPEvents::onLocationReceived);

        // Autododge
        AutododgeEvents autododge = new AutododgeEvents();

        LocationUtil.addListener(autododge::onLocationReceived);
        MinecraftForge.EVENT_BUS.register(autododge);

        CommandManager.INSTANCE.registerCommand(new OpenAutododgeGUI());

        // Enhanced Who
        MinecraftForge.EVENT_BUS.register(new EnhancedWho());

        // Kit Select Fix
        MinecraftForge.EVENT_BUS.register(new KitSelectorFixEvent());

        // Item Cooldown hud
        EventManager.INSTANCE.register(new UsageTimerHUD());

        MinecraftForge.EVENT_BUS.register(this); // For event below

    }

    @SubscribeEvent
    public void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            event.left.add("");
            event.left.add("SkyWarsTools");
            event.left.add("hasCorruptedPearl: " + String.valueOf(UsageTimerInventory.hasCorruptedPearl));
            event.left.add("hasEchoClock: " + String.valueOf(UsageTimerInventory.hasEchoClock));
            event.left.add("hasEndlordPearl: " + String.valueOf(UsageTimerInventory.hasEndlordPearl));

        }
    }
}
