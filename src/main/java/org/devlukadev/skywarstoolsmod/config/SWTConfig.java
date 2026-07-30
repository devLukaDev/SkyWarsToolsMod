package org.devlukadev.skywarstoolsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.hud.LastGameEXPHud;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.org/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class SWTConfig extends Config {

    // ==== EXP Display ====
    @Switch(
            name = "Show Only On Death/Win",
            description = "Only show the display when you are no longer alive, or have won",
            category = "LastGameEXP", subcategory = "Settings"
    )
    public boolean experienceShowTemp = false;

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

    @Text(
            name = "Maps to Autododge",
            description = "Put maps here, divided with commas",
            placeholder = "Chronos,Firelink Shrine,Aegis",
            category = "Autododge", multiline = true
    )
    public String autododgeMaps = "";


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

    // ==== About ====

    @Info(
            text = "See README.md in the mod's resources for full details.",
            type = InfoType.INFO,
            category = "About"
    )
    public boolean aboutInfo;

    public SWTConfig() {
        super(new Mod(SkyWarsToolsMod.NAME, ModType.UTIL_QOL), SkyWarsToolsMod.MODID + ".json");
        initialize();


        addDependency("autododgeSoundEnabled", "autododgeEnabled");
        addDependency("autododgeLobby", "autododgeEnabled");
    }
}