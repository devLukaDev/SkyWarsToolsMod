package org.devlukadev.skywarstoolsmod.tablevels;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;
import org.devlukadev.skywarstoolsmod.utils.scheduler.ClientScheduler;

import java.util.*;
import java.util.regex.Matcher;

import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.GAME_START;

public class TabLevels {
    
    @SubscribeEvent
    public void onPlayerChat(ClientChatReceivedEvent event){
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (!LocationUtil.getCurrentLocation().getMap().isPresent()) return; // In a lobby

        String message = event.message.getFormattedText();
        Matcher gameStartMatcher = GAME_START.matcher(message);
        if (!gameStartMatcher.matches()) {
            return;
        }

        ClientScheduler.schedule(20, () -> {
            NetHandlerPlayClient netHandler = Minecraft.getMinecraft().thePlayer.sendQueue;
            Collection<NetworkPlayerInfo> players = netHandler.getPlayerInfoMap();

            List<SkyWarsResponse> responses = new ArrayList<>();
            List<String> nicked = new ArrayList<>();
            for (NetworkPlayerInfo player : players) {
                UUID uuid = player.getGameProfile().getId();
                if (NickDetector.isLikelyNicked(uuid)) {
                    String name = player.getGameProfile().getName();
                    nicked.add(name);
                    continue;
                }
                SkyWarsResponse response = SkyWarsRequestCache.getBare(uuid);
                if (response != null) {
                    responses.add(response);
                } else {
                    // not cached yet — kick off a fetch so it's available next time
                    SkyWarsRequestCache.getBare(uuid);
                }
            }

            responses.sort(Comparator.comparingDouble(a -> a.exp)); // descending by exp

            for (SkyWarsResponse response : responses) {
                ChatLib.chat(response.display.levelFormatted + " - " + response.player);
            }
            for (String nickedPlayer: nicked){
                ChatLib.chat("§c[?] " + nickedPlayer);
            }
        });




    }

}
