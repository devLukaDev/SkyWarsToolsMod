package org.devlukadev.skywarstoolsmod.utils;

import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.config.SWTConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MCName {

    // Responsible for providing the apparent name of a player on hypixel, either nicked or not

    static boolean currentlyNicked = false;
    private static final Pattern nickedPattern = Pattern.compile("§cNICKED");
    private static final Pattern nickResetPattern = Pattern.compile("§r§aYour nick has been reset!§r");

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (!HypixelUtils.INSTANCE.isHypixel()) return;
        if (!LocationUtil.isInLobby()) return;
        if (event.type == 2) {

            Matcher matcher = nickedPattern.matcher(event.message.getFormattedText());
            if (!matcher.find()) return;
            currentlyNicked = true;
        } else {
            // Normal chat - look for nick reset

            Matcher matcher = nickResetPattern.matcher(event.message.getFormattedText());
            if (!matcher.find()) return;
            currentlyNicked = false;

        }


    }

    public static String getName() {
        if (currentlyNicked) {
            if (SkyWarsToolsMod.config.mostRecentNick == null) {
                ChatLib.chat("&cWe could not get your nickname! Please /nick reset and re-nick to make " +
                        "sure all features work as intended.");
                return Minecraft.getMinecraft().thePlayer.getName();
            }
            return SkyWarsToolsMod.config.mostRecentNick;
        }
        return Minecraft.getMinecraft().thePlayer.getName();

    }
}
