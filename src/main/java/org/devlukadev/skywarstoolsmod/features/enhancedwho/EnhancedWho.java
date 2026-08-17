package org.devlukadev.skywarstoolsmod.features.enhancedwho;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.RenderUtils;
import org.devlukadev.skywarstoolsmod.utils.scheduler.ClientScheduler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.YOU_DIED_YOU_WON;
import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.GAME_START;

public class EnhancedWho {

    private static final Pattern TEAM_LINE = Pattern.compile("^§rTeam #(\\d{1,2}):((?: §r§.[^§]+§r,?)+)$");
    private static final Pattern PLAYER_ENTRY = Pattern.compile("§r(§.([^§]+))§r");

    // Ticks to keep the initial capture window open. Teams are static once the
    // game starts, so after this window the roster is frozen for the whole game.
    private static final int CAPTURE_WINDOW_TICKS = 20;

    BlockPos playerStartPosition = null;
    boolean shouldRenderBeacon = false;
    boolean playerDied = false;
    private String mode = null;
    int playerTeam = -1;

    // Frozen once, right after game start. Never mutated after the capture window closes.
    private final Map<Integer, WhoTeam> teamRoster = new LinkedHashMap<>();

    // True only during the brief window right after our own auto-triggered /who.
    private boolean isCapturingRoster = false;

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.islandFinderEnabled) return;
        if (!LocationUtil.getCurrentLocation().getMap().isPresent()) return; // In a lobby

        String message = event.message.getFormattedText();

        if (YOU_DIED_YOU_WON.matcher(message).matches()) {
            playerDied = true;
            return;
        }

        if (GAME_START.matcher(message).matches()) {
            onGameStart();
            return;
        }

        Matcher matcher = TEAM_LINE.matcher(message);
        if (!matcher.matches()) return;

        WhoTeam parsedTeam = parseTeamLine(matcher);

        if (isCapturingRoster) {
            handleRosterCaptureLine(parsedTeam, event);
        } else {
            handleLiveWhoLine(parsedTeam, event);
        }
    }

    private void onGameStart() {
        playerDied = false;
        playerTeam = -1;
        shouldRenderBeacon = false;
        teamRoster.clear();

        playerStartPosition = Minecraft.getMinecraft().thePlayer.getPosition();

        if (LocationUtil.getCurrentLocation().getMode().isPresent()) {
            mode = LocationUtil.getCurrentLocation().getMode().get();
        }

        // Trigger our own /who while everyone is still alive, to capture the
        // full, static team roster once. All resulting team lines get caught
        // and cancelled by handleRosterCaptureLine below.
        isCapturingRoster = true;
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/who");

        ClientScheduler.schedule(CAPTURE_WINDOW_TICKS, () -> {
            isCapturingRoster = false;
        });
    }

    private WhoTeam parseTeamLine(Matcher teamLineMatcher) {
        int teamNumber = Integer.parseInt(teamLineMatcher.group(1));
        String playersPart = teamLineMatcher.group(2).trim();

        Matcher playerMatcher = PLAYER_ENTRY.matcher(playersPart);
        String clientPlayerName = Minecraft.getMinecraft().thePlayer.getName();

        java.util.List<WhoTeam.Player> teamPlayers = new java.util.ArrayList<>();
        while (playerMatcher.find()) {
            teamPlayers.add(new WhoTeam.Player(
                    playerMatcher.group(1), // §aPlayer1
                    playerMatcher.group(2)  // Player1
            ));
            if (playerMatcher.group(2).equals(clientPlayerName)) {
                playerTeam = teamNumber;
            }
        }

        return new WhoTeam(teamNumber, teamPlayers);
    }

    // During the capture window: every team line belongs to the initial roster build.
    // Always cancel — the player shouldn't see our auto-triggered /who spam.
    private void handleRosterCaptureLine(WhoTeam parsedTeam, ClientChatReceivedEvent event) {
        teamRoster.put(parsedTeam.getTeamNumber(), parsedTeam);
        if (!SkyWarsToolsMod.config.islandFinderAutoWho) event.setCanceled(true);
    }

    // Outside the capture window: this is a /who the player triggered themselves later
    // in the game. Only some teams may show up (others are fully dead). Update the
    // roster entry for teams that still exist, print our enhanced version, and cancel
    // the raw line. If a team number somehow isn't in our roster (shouldn't normally
    // happen since teams are static), leave the message uncancelled as a fallback.
    private void handleLiveWhoLine(WhoTeam parsedTeam, ClientChatReceivedEvent event) {
        int teamNumber = parsedTeam.getTeamNumber();

        if (!teamRoster.containsKey(teamNumber)) {
            return; // unknown team, don't touch the message
        }

        teamRoster.put(teamNumber, parsedTeam); // update with currently-alive members
        event.setCanceled(true);

        printTeamLine(parsedTeam);
        shouldRenderBeacon = true;
    }

    private void printTeamLine(WhoTeam whoTeam) {
        StringBuilder playersSB = new StringBuilder();
        for (WhoTeam.Player player : whoTeam.getPlayers()) {
            playersSB.append(player.getFormattedName());
            playersSB.append(", ");
        }
        String playerString = playersSB.length() >= 2
                ? playersSB.substring(0, playersSB.length() - 2)
                : playersSB.toString();

        String relativeString = getRelative(whoTeam, mode);
        ChatLib.chat("Team #" + whoTeam.getTeamNumber() + ": " + playerString + relativeString, false);
    }

    private String getRelative(WhoTeam whoTeam, String mode) {
        int teamNum = whoTeam.getTeamNumber();

        if (teamNum == playerTeam) {
            return " (You)";
        }
        if (playerTeam == -1) {
            return "";
        }

        int islands = 12;
        if (mode != null) {
            if (mode.contains("mini")) islands = 4;
            if (mode.contains("mega")) islands = 20; //TODO check also for mega 100p?
        }

        int offset = teamNum - playerTeam;
        String direction = offset > 0 ? "right" : "left";
        offset = Math.abs(offset);

        if (offset > islands / 2) {
            offset = islands - offset;
            direction = direction.equals("right") ? "left" : "right";
        }

        return "&f (" + offset + " islands " + direction + " of you)";
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!shouldRenderBeacon) return;
        if (!SkyWarsToolsMod.config.islandFinderEnabled) return;
        if (!SkyWarsToolsMod.config.islandFinderBeacon) return;
        if (playerStartPosition == null) return;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        RenderUtils.renderBeaconBeam(playerStartPosition.getX() - px, 0, playerStartPosition.getZ() - pz,
                0x023431, 1.0f, event.partialTicks);
    }
}