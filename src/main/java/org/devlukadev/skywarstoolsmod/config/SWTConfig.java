package org.devlukadev.skywarstoolsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.autododge.AutododgeScreen;
import org.devlukadev.skywarstoolsmod.features.lastgameexp.LastGameEXPHud;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.org/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class SWTConfig extends Config {
    // ==== About ====
    @Info(
            text = "Version: @VER@",
            type = InfoType.INFO
    )
    public static boolean ignoredb; // Useless. Java limitations with @annotation.
    @Info(
            text = "By devLukaDev",
            type = InfoType.INFO
    )
    public static boolean adjhsa;
    // ==== EXP Display ====
    @Switch(
            name = "Enable EXP Display",
            description = "Master switch for enabling/disabling the entire Last Game Experience feature.",
            category = "LastGameEXP", subcategory = "Settings"
    )
    public boolean experienceMasterSwitch = true;
    @Info(
            text = "Use this instead of the HUD enable/disable switch.",
            type = InfoType.WARNING, // Types are: INFO, WARNING, ERROR, SUCCESS
            category = "LastGameEXP", subcategory = "Settings"
    )
    public static boolean asji; // Useless. Java limitations with @annotation.


    @Switch(
            name = "Show Only On Death/Win",
            description = "Only show the display when you are no longer alive, or have won",
            category = "LastGameEXP", subcategory = "Settings"
    )
    public boolean experienceShowTemp = false;
    @Info(
            type = InfoType.WARNING,
            text = "Want to disable the HUD? Do so above!",
            category = "LastGameEXP", subcategory = "HUD"
    )
    public static boolean ignored2; // Useless. Java limitations with @annotation.
    @HUD(
            name = "SkyWars EXP Display",
            category = "LastGameEXP", subcategory = "HUD"
    )
    public LastGameEXPHud lastGameEXPHud = new LastGameEXPHud();

    // ==== Autododge ====

    @Switch(
            name = "Enable Autododge",
            description = "Whether to enable SkyWars Autododge",
            category = "Autododge"
    )
    public boolean autododgeEnabled = true;

    @Button(
            name = "Maps to Dodge",
            text = "Open GUI",
            category = "Autododge"
    )
    Runnable runnable = () -> {
        GuiUtils.displayScreen(new AutododgeScreen());
    };

    @Switch(
            name = "Enable Autododge Sound",
            description = "Whether to play sounds when dodging",
            category = "Autododge"
    )
    public boolean autododgeSoundEnabled = true;

    @Switch(
            name = "Lobby Last Resort",
            description = "If Hypixel prevents you from dodging (Please don't spam the command!), go to the lobby instead",
            category = "Autododge"
    )
    public boolean autododgeLobby = true;

    // ==== SkyWars Levels ====

    @Switch(
            name = "Enable SkyWars Levels",
            description = "Enable automatic SkyWars levels to be shown when joining a game",
            category = "SkyWars Levels"
    )
    public boolean levelsEnabled = true;

    // ==== Enhanced Who ====

    @Switch(
            name = "Enable Enhanced Who",
            description = "On /who, tells you where the other teams are relative to you",
            category = "Enhanced Who"
    )
    public boolean islandFinderEnabled = true;

    @Switch(
            name = "Render Island Beacon",
            description = "On /who, renders a beacon on your island",
            category = "Enhanced Who"
    )
    public boolean islandFinderBeacon = true;

    @Switch(
            name = "Auto-Who On Game Start",
            description = "Automatically send /who when the game starts",
            category = "Enhanced Who"
    )
    public boolean islandFinderAutoWho = false;
    // === Item Cooldowns ===
    @Switch(
            name = "Enable Item Cooldowns HUD",
            description = "Enables a HUD around the crosshair that shows relevant information on item cooldowns",
            category = "CooldownsHUD"
    )
    public boolean cooldownsHUDEnabled = true;

    @Slider(
            name = "Icon size",
            category = "CooldownsHUD",
            min = 1,
            max = 200
    )
    public int cooldownsHUDSize = 50;

    @Slider(
            name = "Distance from crosshair",
            category = "CooldownsHUD",
            min = 1,
            max = 200
    )
    public int cooldownsHUDDistance = 50;


    // ==== Fixes ====
    @Switch(
            name = "Kit Select Fix",
            description = "Attempts to fix kit selecting",
            category = "Fixes"
    )
    public boolean kitSelectFix = true;
    @Switch(
            name = "Prevent Taking Lapis from Etable",
            description = "Makes sure you cannot accidentally take lapis from the etable",
            category = "Fixes"
    )
    public boolean etableFix = true;



    public SWTConfig() {
        super(new Mod(SkyWarsToolsMod.NAME, ModType.UTIL_QOL, "/logo-480.png"), SkyWarsToolsMod.MODID + ".json");
        initialize();


        addDependency("autododgeSoundEnabled", "autododgeEnabled");
        addDependency("autododgeLobby", "autododgeEnabled");
        addDependency("autododgeMaps", "autododgeEnabled");
        addDependency("experienceShowTemp", "experienceMasterSwitch");
        addDependency("lastGameEXPHud", "experienceMasterSwitch");

        addDependency("islandFinderAutoWho", "islandFinderEnabled");
        addDependency("islandFinderBeacon", "islandFinderEnabled");
    }
}