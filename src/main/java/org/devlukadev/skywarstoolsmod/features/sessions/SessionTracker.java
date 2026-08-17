package org.devlukadev.skywarstoolsmod.features.sessions;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.MessagePattern;

import java.util.regex.Matcher;

public class SessionTracker {

    boolean playerDied = false;
    boolean playerWonWhileDead = false;
    boolean gameStarted = false;

    // TODO Class responsible for tracking kills, wins and experience gain
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.sessionsEnabled) return;

        String message = event.message.getFormattedText();

        Matcher killWinMatcher = MessagePattern.GENERAL_XP_EVENT_GROUP.matcher(message);
        if (killWinMatcher.find()) {
            String reason = killWinMatcher.group(0);
            switch (reason) {
                case "Kill":
                    SessionManager.getInstance().addKill();
                    break;
                case "Win":
                    SessionManager.getInstance().addWin();
                    if (playerDied) {
                        playerWonWhileDead = true;
                    }
                    break;
                default:
                    break;
            }
        }

        Matcher deathMatcher = MessagePattern.YOU_DIED.matcher(message);
        if (deathMatcher.matches()){
            playerDied = true; // Player might still win (teams)
            SessionManager.getInstance().addDeath();
        }

        Matcher startMatcher = MessagePattern.GAME_START.matcher(message);
        if (startMatcher.matches()) {
            gameStarted = true;
        }

    }

    public void onLocationReceived(ClientboundLocationPacket clientboundLocationPacket) {
        // Cases for when we receive this packet

        // Start of game ->

        if (playerWonWhileDead){
            // TODO do stuff

        } else {
            // Treat as logout if player not dead / won?
        }


        //Reset
        playerDied = false;
        playerWonWhileDead = false;
    }
}