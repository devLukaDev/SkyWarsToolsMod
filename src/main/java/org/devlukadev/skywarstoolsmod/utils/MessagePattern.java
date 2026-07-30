package org.devlukadev.skywarstoolsmod.utils;

import net.minecraft.util.IChatComponent;

import java.util.regex.Pattern;

public final class MessagePattern {


    private static final Pattern SKYWARS_XP =
            Pattern.compile("^\\+\\d+ SkyWars Experience! (Win|Kill|Assist)$");

    private static final Pattern RESET_XP = Pattern.compile("^§r§eCages opened! §r§cFIGHT!§r$");

    private static final Pattern GAME_END = Pattern.compile(
            "^You won! Want to play again\\? Click here!|You died! Want to play again\\? Click here!$"
    );

    public static final Pattern SKYWARS_XP_MULT = Pattern.compile(
            "^x\\d*\\.?\\d* SkyWars Experience! Win (.*)$");

    private MessagePattern() {
    }

    public static boolean isValidResetMessage(IChatComponent message) {
        String text = message.getFormattedText();
        return RESET_XP.matcher(text).matches();
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