package org.devlukadev.skywarstoolsmod.features.lastgameexp;


import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

import java.util.regex.Matcher;

import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.*;

public class LastGameEXPEvents {

    private static float lastXP = 0;

    public static float getLastXP() {
        return lastXP;
    }

    @SubscribeEvent
    public void onAdditionChat(ClientChatReceivedEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.experienceMasterSwitch) {
            SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(false);
        };

        String message = event.message.getFormattedText();

        Matcher matcher;

        matcher = SKYWARS_XP_MULTIPLIER.matcher(message);
        if (matcher.matches()) {
            float multiplier = Float.parseFloat(matcher.group(1)); // 1.2
            System.out.println(multiplier);
            lastXP *= multiplier;
            return;
        }

        matcher = XP_PATTERN.matcher(message);
        if (matcher.find()) {
            lastXP += Integer.parseInt(matcher.group(1));
            return;
        }

        if (YOU_DIED_YOU_WON.matcher(message).matches()) {
            if (SkyWarsToolsMod.config.experienceShowTemp) {
                SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(true);
            }
            return;
        }

        if (GAME_START.matcher(message).matches()) {
            lastXP = 0;
            if (SkyWarsToolsMod.config.experienceShowTemp) {
                SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(false);
            }
            return;
        }
    }


    public void onLocationReceived(ClientboundLocationPacket packet) {
        if (!SkyWarsToolsMod.config.experienceMasterSwitch) {
            SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(false);
            return;
        }
        if (LocationUtil.isInSkyWars()) {
            SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(true);
        } else {
            SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(false);
        }
    }
}