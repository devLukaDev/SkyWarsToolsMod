package org.devlukadev.skywarstoolsmod.utils;

import net.minecraft.util.IChatComponent;

import java.util.regex.Pattern;

public final class MessagePattern {

    /**
     * Regex pattern for <pre>+2 SkyWars Experience! (Win)</pre>
     */
    public static final Pattern SKYWARS_XP =
            Pattern.compile("^\\+\\d+ SkyWars Experience! (Win|Kill|Assist)$");
    /**
     * Regex pattern for <pre>Cages opened! FIGHT!</pre>
     */
    public static final Pattern GAME_START = Pattern.compile("^§r§eCages opened! §r§cFIGHT!§r$");

    /**
     * Regex pattern for game ending: <pre>You won! Want to play again? Click here!</pre> or <pre>You died! Want to play again? Click here!</pre>
     */
    public static final Pattern GAME_END = Pattern.compile(
            "^You won! Want to play again\\? Click here!|You died! Want to play again\\? Click here!$"
    ); //TODO check does this fire in teams? in a diff way maybe?

    /**
     * Regex pattern for <pre>x1.2 SkyWars Experience! Win (...)</pre>
     */
    public static final Pattern SKYWARS_XP_MULT = Pattern.compile(
            "^x\\d*\\.?\\d* SkyWars Experience! Win (.*)$");

    private MessagePattern() {
    }

    public static boolean isValidResetMessage(IChatComponent message) {
        String text = message.getFormattedText();
        return GAME_START.matcher(text).matches();
    }

    public static boolean isValidExperienceAddMessage(IChatComponent message) {

        String text = message.getUnformattedText();
        return SKYWARS_XP.matcher(text).matches();

    }

    public static boolean isValidExperienceMultMessage(IChatComponent message){

        String text= message.getUnformattedText();
        return SKYWARS_XP_MULT.matcher(text).matches();
    }

    public static boolean isGameEndMessage(IChatComponent message) {
        String text = message.getUnformattedText();
        return GAME_END.matcher(text).matches();
    }



}