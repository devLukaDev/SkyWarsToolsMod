package org.devlukadev.skywarstoolsmod.features.sessions;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.MessagePattern;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.SKYWARS_XP_MULTIPLIER;
import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.XP_PATTERN;

public class SessionTracker {

    boolean playerDied = false;
    boolean gameStarted = false;
    boolean gameWon = false;
    double xpThisGamePrePotion = 0;
    long playtimeStart = -1;
    long playtimeEnd = -1;
    Map<String, Double> frequency = new HashMap<>();

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (LocationUtil.isInLobby()) return;
        if (!SkyWarsToolsMod.config.sessionsEnabled) return;

        String message = event.message.getFormattedText();
        System.out.println(message);

        Matcher killWinMatcher = MessagePattern.GENERAL_XP_EVENT_GROUP.matcher(message);
        if (killWinMatcher.find()) {
            String reason = killWinMatcher.group(1);
            switch (reason) {
                case "Kill":
                    SessionManager.getInstance().addKill();
                    frequency.merge("kill", 1d, Double::sum);
                    break;
                case "Win":
                    SessionManager.getInstance().addWin();
                    frequency.merge("win", 1d, Double::sum);
                    gameWon = true;
                    playtimeEnd = System.currentTimeMillis() / 1000;
                    break;
                default:
                    break;
            }
        }

        Matcher matcher = SKYWARS_XP_MULTIPLIER.matcher(message);
        if (matcher.matches()) {
            double multiplier = Double.parseDouble(matcher.group(1)); // 1.2
            // This is at the end of a game, when the player has won
            double diff = xpThisGamePrePotion * multiplier - xpThisGamePrePotion;
            System.out.println(xpThisGamePrePotion + " * " + multiplier + " - " + xpThisGamePrePotion);
            SessionManager.getInstance().addXp(diff);
            frequency.put("xp_mult_add", diff);

        }

        matcher = XP_PATTERN.matcher(message);
        if (matcher.find()) {
            double xp = Integer.parseInt(matcher.group(1));
            SessionManager.getInstance().addXp(xp);
            frequency.merge("xp", xp, Double::sum);
            xpThisGamePrePotion += xp;

        }

        Matcher deathMatcher = MessagePattern.YOU_DIED.matcher(message);
        if (deathMatcher.matches()) {
            playerDied = true; // Player might still win (teams), so no less yet
            playtimeEnd = System.currentTimeMillis() / 1000;
            SessionManager.getInstance().addDeath();
            frequency.merge("death", 1d, Double::sum);

        }

        Matcher startMatcher = MessagePattern.GAME_START.matcher(message);
        if (startMatcher.matches()) {
            gameStarted = true;
            playtimeStart = System.currentTimeMillis() / 1000;
        }

    }

    public void onLocationReceived(ClientboundLocationPacket clientboundLocationPacket) {
        if (!SkyWarsToolsMod.config.sessionsEnabled) return;
        if (!LocationUtil.isInSkyWars()) return;

        if (SessionManager.getInstance().getCurrentStats() == null) {
            ChatLib.chat("&cWARNING: &eYou have no session started yet. Run &b/swt sessions start");
            Minecraft.getMinecraft().thePlayer.playSound("mob.villager.no", 1F, 1F);
            return;
        }
        // Cases for when we receive this packet
        if (gameStarted) {
            // Previous game was a thing!
            if (!gameWon) {
                // Lost somehow
                SessionManager.getInstance().addLoss();
                frequency.merge("loss", 1d, Double::sum);
                if (!playerDied) {
                    // No game won event seen, nor a death event? -> Player logged out -> count death + loss
                    playtimeEnd = System.currentTimeMillis() / 1000;
                    SessionManager.getInstance().addDeath();
                    frequency.merge("death", 1d, Double::sum);
                }

            }

            long playTimeLastGame = playtimeEnd - playtimeStart;
            SessionManager.getInstance().addPlaytime(playTimeLastGame);
            frequency.put("time_played", (double) playTimeLastGame);

        }

        if (!frequency.isEmpty()) {
            ChatLib.chat("Previous game stats:");
            frequency.forEach((result, count) ->
                    ChatLib.chat(result + ": " + count));
            ChatLib.chat("Not correct? Report this with replay file please!");
        }

        //Reset
        frequency = new HashMap<>();
        playerDied = false;
        gameStarted = false;
        gameWon = false;
    }

    @SubscribeEvent
    public void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            frequency.forEach((result, count) ->
                    event.left.add(result + ": " + count));

        }
    }

}