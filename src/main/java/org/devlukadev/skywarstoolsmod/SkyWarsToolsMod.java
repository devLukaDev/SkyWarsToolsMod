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
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.devlukadev.skywarstoolsmod.command.SWLevel;
import org.devlukadev.skywarstoolsmod.command.SWTCommand;
import org.devlukadev.skywarstoolsmod.config.SWTConfig;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.devlukadev.skywarstoolsmod.features.autododge.TagManager;
import org.devlukadev.skywarstoolsmod.features.enhancedwho.EnhancedWho;
import org.devlukadev.skywarstoolsmod.features.autododge.AutododgeEvents;
import org.devlukadev.skywarstoolsmod.features.kitselectorfix.KitSelectorFixEvent;
import org.devlukadev.skywarstoolsmod.features.lastgameexp.LastGameEXPEvents;
import org.devlukadev.skywarstoolsmod.features.sessions.SessionTracker;
import org.devlukadev.skywarstoolsmod.features.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.features.tablevels.TabRowRenderContext;
import org.devlukadev.skywarstoolsmod.features.usagetimer.TimeWarpPearlTracker;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerHUD;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerManager;
import org.devlukadev.skywarstoolsmod.updater.SWTUpdater;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.MCName;
import org.devlukadev.skywarstoolsmod.utils.scheduler.ClientScheduler;

import java.io.File;

import org.apache.logging.log4j.Logger;

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

    public static final String SWT_API = "https://api.skywarstools.com/api";
    @Mod.Instance(MODID)
    public static SkyWarsToolsMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static SWTConfig config;
    public static final Logger logger = LogManager.getLogger("@NAME@");

    private File cacheFolder;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.cacheFolder = new File(event.getModConfigurationDirectory(), "swt");

        new SWTUpdater(event.getSourceFile()).start();
    }

    public File getCacheFolder() {
        return cacheFolder;
    }

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new SWTConfig();
        HypixelUtils.INSTANCE.initialize();
        CommandManager.INSTANCE.registerCommand(new SWLevel());
        MinecraftForge.EVENT_BUS.register(new ClientScheduler());

        MinecraftForge.EVENT_BUS.register(this); // For all @Subscribe events in this class

        // Nick tracker
        MinecraftForge.EVENT_BUS.register(new MCName());

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

        TagManager.loadData(getCacheFolder());

        // Levels
        LocationUtil.addListener(TabRowRenderContext::onLocationReceived);
        MinecraftForge.EVENT_BUS.register(new TabRowRenderContext());

        // Enhanced Who
        MinecraftForge.EVENT_BUS.register(new EnhancedWho());

        // Kit Select Fix
        MinecraftForge.EVENT_BUS.register(new KitSelectorFixEvent());

        // Item Cooldown hud
        EventManager.INSTANCE.register(new UsageTimerHUD());
        MinecraftForge.EVENT_BUS.register(new UsageTimerManager());
        MinecraftForge.EVENT_BUS.register(new TimeWarpPearlTracker());

        //Sessions
        SessionTracker sessionTracker = new SessionTracker();
        LocationUtil.addListener(sessionTracker::onLocationReceived);
        MinecraftForge.EVENT_BUS.register(sessionTracker);


        // Commands
        CommandManager.INSTANCE.registerCommand(new SWTCommand());
        CommandManager.INSTANCE.registerCommand(new SWLevel());

    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        TagManager.onShutdown();
    }

    @SubscribeEvent
    public void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            event.left.add("");
            event.left.add("SkyWarsToolsMod @VER@");
            event.left.add("ResponseCache: " + SkyWarsRequestCache.getCacheSize());
            event.left.add("HypixelName: " + MCName.getName());

        }
    }
}
