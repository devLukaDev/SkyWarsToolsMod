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
    @HUD(
            name = "SkyWars EXP Display",
            category = "LastGameEXP", subcategory = "HUD"
    )
    public LastGameEXPHud lastGameEXPHud = new LastGameEXPHud();

    @Switch(
            name = "Enable Experience Display",
            description = "Show the SkyWars EXP you earned this game",
            category = "LastGameEXP", subcategory = "Settings"
    )
    public static boolean experienceEnabled = true;

    @Text(
            name = "Display String",
            description = "Customize the string that is displayed for the EXP display. Use &d{exp}&r to insert the amount of EXP.",
            placeholder = "&6EXP This Game: &d{exp}",
            category = "LastGameEXP", subcategory = "Settings"
    )
    public static String experienceDisplayString = "&6EXP This Game: &d{exp}";

    @Switch(
            name = "Show Only On Death/Win",
            description = "Only show the display when you die or win a game, instead of always, might take a bit to take effect",
            category = "LastGameEXP", subcategory = "Settings"
    )
    public static boolean experienceShowTemp = false;

    // ==== Autododge ====

    @Switch(
            name = "Enable Autododge",
            description = "Whether to enable SkyWars Autododge",
            category = "Autododge"
    )
    public static boolean autododgeEnabled = true;

    @Button(
            name = "Edit Autododge List",
            description = "Use /autododge to add/remove maps from the list",
            text = "Edit",
            category = "Autododge"
    )
    public Runnable autododgeListButton = () -> {
        // Opens the map-editing UI. Replace with your own command/chat utility -
        // OneConfig doesn't have a ChatLib equivalent baked in.
        // e.g. ClientCommandHandler / your mod's command dispatcher:
        // CommandUtils.runClientCommand("autododge");
        ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, "autododge");

        // Close this config GUI so the book/edit screen isn't obscured.
        // OneConfig manages its own GUI stack; if you need to explicitly close it,
        // use cc.polyfrost.oneconfig.gui.OneConfigGui.INSTANCE (or the relevant close call).


        // Play a sound - use Minecraft's own sound manager since World.playSound (CT) isn't available:
        Minecraft.getMinecraft().thePlayer.playSound("random.click", 1.0F, 1.0F);
    };

    @Switch(
            name = "Enable Autododge Sound",
            description = "Whether to play a sound when dodging",
            category = "Autododge"
    )
    public static boolean autododgeSoundEnabled = true;

    @Switch(
            name = "Lobby Last Resort",
            description = "If Hypixel prevents you from dodging (Please don't spam the command!), go to the lobby instead",
            category = "Autododge"
    )
    public static boolean autododgeLobby = true;

    // ==== SkyWars Levels ====

    @Switch(
            name = "Enable SkyWars Levels",
            description = "Enable automatic SkyWars levels to be shown when joining a game",
            category = "SkyWars Levels"
    )
    public static boolean levelsEnabled = true;

    // ==== Enhanced Who ====

    @Switch(
            name = "Enable Enhanced Who",
            description = "On /who, tells you where the other teams are relative to you",
            category = "Enhanced Who"
    )
    public static boolean islandFinderEnabled = true;

    @Switch(
            name = "Render Island Beacon",
            description = "On /who, renders a beacon on your island",
            category = "Enhanced Who"
    )
    public static boolean islandFinderBeacon = true;

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
    public static boolean aboutInfo;

    public SWTConfig() {
        super(new Mod(SkyWarsToolsMod.NAME, ModType.UTIL_QOL), SkyWarsToolsMod.MODID + ".json");
        initialize();


        addDependency("autododgeSoundEnabled", "autododgeEnabled");
        addDependency("autododgeLobby", "autododgeEnabled");
    }
}