package org.devlukadev.skywarstoolsmod.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.ConfigUtils;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.config.elements.BasicOption;
import cc.polyfrost.oneconfig.config.elements.OptionPage;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.config.options.*;
import org.devlukadev.skywarstoolsmod.config.pages.CommandsPage;
import org.devlukadev.skywarstoolsmod.features.autododge.AutododgeScreen;
import org.devlukadev.skywarstoolsmod.features.lastgameexp.LastGameEXPHud;
import org.devlukadev.skywarstoolsmod.features.sessions.SessionHUD;
import org.devlukadev.skywarstoolsmod.features.sessions.SessionManager;
import org.devlukadev.skywarstoolsmod.config.options.TabPreviewOption;

import java.lang.reflect.Field;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.org/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class SWTConfig extends Config {
    // ==== About ====

    @CustomOption(id = "introductionOption")
    public boolean yesa = true;

    @Page(
            name = "All commands",
            location = PageLocation.BOTTOM,
            // optional description that is also displayed on the page button
            description = "See all commands than can be run!"
    )
    public static CommandsPage commandsPage = new CommandsPage();


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

    @EnglishWarningOptionAnnotation(category = "LastGameEXP", subcategory = "Settings")
    public static boolean skywarsEnglishWarninga = true;

    // ==== Autododge ====
    @Switch(
            name = "Enable Autododge",
            description = "Whether to enable SkyWars Autododge",
            category = "Autododge"
    )
    public boolean autododgeEnabled = true;

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

    @Switch(
            name = "Invert List",
            description = "Instead of dodging the maps on your list, dodge all maps except those!",
            category = "Autododge"
    )
    public boolean autododgeInverted = false;

    @Button(
            name = "Maps to Dodge",
            text = "Open GUI",
            category = "Autododge",
            size = 2
    )
    Runnable runnable = () -> {
        GuiUtils.displayScreen(new AutododgeScreen());
    };

    @Switch(
            name = "Dodge High-stat Players",
            description = "Automatically dodge players with high stats",
            category = "Autododge",
            subcategory = "Player dodge"
    )
    public boolean autododgePlayersEnabled = false;

    @Slider(
            name = "K/D To Dodge",
            min = 0.0F,
            max = 15.0F,
            description = "Minimum K/D to automatically dodge",
            category = "Autododge",
            subcategory = "Player dodge")
    public float autododgePlayersKD = 5;

    @Slider(
            name = "W/L To Dodge",
            min = 0.0F,
            max = 15.0F,
            description = "Minimum W/L to automatically dodge",
            category = "Autododge",
            subcategory = "Player dodge")
    public float autododgePlayersWL = 1.5F;

    @Switch(
            name = "Dodge Tagged Players",
            description = "Automatically dodge players that you've tagged",
            category = "Autododge",
            subcategory = "Player dodge"
    )
    public boolean autododgeTagsEnabled = true;

    @Text(name = "Exception tag",
            category = "Autododge",
            subcategory = "Player dodge",
            description = "Players with this tag will not be dodged",
            size = 2)
    public String autododgeTagsExceptionText = "donotdodge";

    @EnglishWarningOptionAnnotation(category = "Autododge")
    public static boolean skywarsEnglishWarningb = true;

    // ==== SkyWars Levels ====
    @Switch(
            name = "Enable SkyWars Levels",
            description = "Enable automatic SkyWars levels to be shown when joining a game",
            category = "SkyWars Levels",
            size = 2
    )
    public boolean levelsEnabled = true;

    @Slider(
            name = "Gutter Minimum Space",
            description = "How big the alignment whitespace should be",
            category = "SkyWars Levels",
            min = 0,
            max = 15,
            step = 1
    )
    public int levelsGutter = 3;


    @Info(
            category = "SkyWars Levels",
            size = 2,
            text = "You can use \"%default%\", \"%level%\", \"%wl%\", \"%kd%\", \"%kills%\", \"%wins%\", \"%deaths%\", " +
                    "\"%losses%\", \"%exp%\", and & for colour codes.", type = InfoType.INFO)
    public boolean bs = true;

    @Text(
            name = "Tab levels formatting",
            secure = false,
            category = "SkyWars Levels",
            size = 2
    )
    public String levelsText = "%level% %default%";

    @CustomOption(id = "tabPreviewOption")
    public boolean yes = true;


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


    @EnglishWarningOptionAnnotation(category = "Enhanced Who")
    public static boolean skywarsEnglishWarninge = true;

    // == Sessions ==
    @Switch(
            name = "Enable Session Tracker",
            description = "HyStats-esque session tracker, makes estimated stats based on chat events",
            category = "Sessions",
            size = 2
    )
    public boolean sessionsEnabled = true;

    @Button(
            category = "Sessions",
            name = "Sync with Hypixel API",
            text = "Sync"
    )
    Runnable runnable4 = () -> {
        SessionManager.getInstance().sync(Minecraft.getMinecraft().thePlayer.getName());
    };

    @Button(
            category = "Sessions",
            name = "Reset/Start session",
            text = "Reset"
    )
    Runnable runnable5 = () -> {
        SessionManager.getInstance().startSession(Minecraft.getMinecraft().thePlayer.getName());
    };

    @EnglishWarningOptionAnnotation(category = "Sessions")
    public static boolean skywarsEnglishWarningh = true;


    @HUD(
            category = "Sessions",
            name = "Sessions HUD"
    )
    public SessionHUD sessionHUD = new SessionHUD();


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
            name = "Kit Select Bug Warning",
            description = "Warns the user when a wrong kit is selected",
            category = "Fixes"
    )
    public boolean kitSelectFix = true;
    @Switch(
            name = "Prevent Taking Lapis while Enchanting",
            description = "Makes sure you cannot accidentally take lapis from the enchanting table",
            category = "Fixes"
    )
    public boolean etableFix = true;


    // Non-editable properties
    public String mostRecentNick;


    public SWTConfig() {
        super(new Mod(SkyWarsToolsMod.NAME, ModType.UTIL_QOL, "/logo-480.png"), SkyWarsToolsMod.MODID + ".json");
        initialize();


        addDependency("autododgeSoundEnabled", "autododgeEnabled");
        addDependency("autododgeLobby", "autododgeEnabled");
        addDependency("autododgeMaps", "autododgeEnabled");

        addDependency("experienceShowTemp", "experienceMasterSwitch");
        addDependency("lastGameEXPHud", "experienceMasterSwitch");

        addDependency("levelsGutter", "levelsEnabled");
        addDependency("levelsText", "levelsEnabled");

        addDependency("islandFinderAutoWho", "islandFinderEnabled");
        addDependency("islandFinderBeacon", "islandFinderEnabled");
    }

    @Override
    protected BasicOption getCustomOption(Field field, CustomOption annotation, OptionPage page, Mod mod, boolean migrate) {
        BasicOption option = null;
        switch (annotation.id()) {
            case "tabPreviewOption":
                option = new TabPreviewOption(
                        field,
                        null,               // parent — null since there's no bound variable, like the category example
                        "Tablist preview",  // name
                        "Preview a name in tab", // description
                        "SkyWars Levels",         // category
                        "", 0                // size: 0 = single column, 1 = double
                );
                ConfigUtils.getSubCategory(page, option.category, option.subcategory).options.add(option);
                break;
            case "introductionOption":
                option = new IntroductionOption(
                        field,
                        null,               // parent — null since there's no bound variable, like the category example
                        "General information",  // name
                        "General information", // description
                        "General",         // category
                        "", 1                // size: 0 = single column, 1 = double
                );
                ConfigUtils.getSubCategory(page, option.category, option.subcategory).options.add(option);
                break;
            case "englishWarningOption":
                EnglishWarningOptionAnnotation englishWarning = ConfigUtils.findAnnotation(field, EnglishWarningOptionAnnotation.class);
                option = new EnglishWarningOption(
                        field,
                        null,
                        "Warning English",
                        "Some features might work, but this one does not",
                        englishWarning.category(),
                        englishWarning.subcategory(), 2
                );
                ConfigUtils.getSubCategory(page, option.category, option.subcategory).options.add(option);
                break;
            case "empty":
                EmptyOptionAnnotation emptyOptionAnnotation = ConfigUtils.findAnnotation(field, EmptyOptionAnnotation.class);
                option = new EmptyOption(
                        field,
                        this,
                        emptyOptionAnnotation.name(),
                        emptyOptionAnnotation.description(),
                        emptyOptionAnnotation.category(),
                        emptyOptionAnnotation.subcategory(),
                        emptyOptionAnnotation.size());
                ConfigUtils.getSubCategory(page, emptyOptionAnnotation.category(), emptyOptionAnnotation.subcategory()).options.add(option);
                break;
        }
        return option;
    }
}