package org.devlukadev.skywarstoolsmod.utils;

import java.util.regex.Pattern;

public final class MessagePattern {

    /**
     * Regex pattern for <pre>+2 SkyWars Experience! (Win)</pre>
     */
    public static final Pattern XP_PATTERN =
            Pattern.compile("§r§d\\+(\\d+) SkyWars Experience");

    /**
     * Regex pattern for <pre>x1.2 SkyWars Experience! Win (...)</pre>
     */
    public static final Pattern SKYWARS_XP_MULTIPLIER =
            Pattern.compile("^(?:§.)*x(\\d+(?:\\.\\d+)?) SkyWars Experience! (.+)$"); // TODO check if triggers with manual chat
    /**
     * Regex pattern for <pre>Cages opened! FIGHT!</pre>
     */
    public static final Pattern GAME_START = Pattern.compile("^§r§eCages opened! §r§cFIGHT!§r$");

    /**
     * Regex pattern for game ending: <pre>You won! Want to play again? Click here!</pre> or <pre>You died! Want to play again? Click here!</pre>
     */
    public static final Pattern YOU_DIED_YOU_WON = Pattern.compile(
            "^§[ac](?:You won|You died)! §r§eWant to play again\\?§r§b§l Click here! §r$"
    );

    /**
     * Regex pattern for <pre>SkyWarsTools has joined (1/12)!</pre>
     */
    public static final Pattern JOIN_PATTERN = Pattern.compile(
            "^§r§e§r(?:§.)?(?<player>\\w+)§r§r§r§e has joined \\(§r§b(?<current>\\d+)§r§r§r§e/§r§b(?<max>\\d+)§r§r§r§e\\)!§r§e§r$"
    );

    /**
     * Regex pattern for <pre>+2 SkyWars Experience! (Kill)</pre>
     */
    public static final Pattern GENERAL_XP_EVENT_GROUP =
            Pattern.compile("§r§d\\+3 SkyWars Experience! (.*?)§r");


    /**
     * Regex pattern for DYING: <pre>You died! Want to play again? Click here!</pre> // TODO check if teams also says this
     */
    public static final Pattern YOU_DIED = Pattern.compile(
            "^§cYou died! §r§eWant to play again\\?§r§b§l Click here! §r$"
    );



}