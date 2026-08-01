package org.devlukadev.skywarstoolsmod.tablevels;

import com.mojang.authlib.GameProfile;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.Fetch;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;
import org.devlukadev.skywarstoolsmod.utils.scheduler.ClientScheduler;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.GAME_START;
import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.JOIN_PATTERN;

public class TabLevels {

    boolean readingJoinMessages = false;
    public void onLocationReceived(ClientboundLocationPacket packet) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (!LocationUtil.getCurrentLocation().getMap().isPresent()) return; // In a lobby
        // We have joined a game
        readingJoinMessages = true;

        ClientScheduler.schedule(5, () -> {
            NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
            Collection<NetworkPlayerInfo> playerInfoList = netHandler.getPlayerInfoMap();
            ChatLib.chat("TabListPlayers:");
            for (NetworkPlayerInfo info : playerInfoList) {
                GameProfile profile = info.getGameProfile();
                String name = profile.getName();
                int ping = info.getResponseTime();
                ChatLib.chat(name + " (" + profile.getId() + ") ping: " + ping);

            }
        });
    }

    @SubscribeEvent
    public void onPlayerChat(ClientChatReceivedEvent event){
        if (!readingJoinMessages) return;
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (!LocationUtil.getCurrentLocation().getMap().isPresent()) return; // In a lobby

        String message = event.message.getFormattedText();
        System.out.println(message);
        Matcher joinMatcher = JOIN_PATTERN.matcher(message);
        Matcher gameStartMatcher = GAME_START.matcher(message);
        if (gameStartMatcher.matches()) {
            readingJoinMessages = false;
            return;
        }

        if (!joinMatcher.matches()) return;
        // This is a SW game join message!
        String player = joinMatcher.group("player");

        Fetch.getJsonAsync("https://api.skywarstools.com/api/skywars?player=" + player, SkyWarsResponse.class)
                .thenAccept(response -> {
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        // safe to touch Minecraft objects here
                        ChatLib.chat(response.player + ": " + response.display.levelFormattedWithBrackets, true);
                    });
                })
                .exceptionally(ex -> {
                    System.err.println("Fetch failed: " + ex.getMessage());
                    return null;
                });

    }

}
