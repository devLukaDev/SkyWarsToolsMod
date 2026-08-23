package org.devlukadev.skywarstoolsmod.features.autododge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.MCName;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayersDodge {

    public static CompletableFuture<Boolean> shouldDodgePlayerInTab() {
        NetHandlerPlayClient nh = Minecraft.getMinecraft().thePlayer.sendQueue;
        Collection<NetworkPlayerInfo> players = nh.getPlayerInfoMap();

        List<CompletableFuture<Boolean>> checks = new ArrayList<>();
        for (NetworkPlayerInfo p : players) {
            String originalName = p.getGameProfile().getName();
            ChatLib.chat("Checking " + originalName);
            if (originalName.equals(MCName.getName())) continue; // Don't dodge yourself (;

            CompletableFuture<SkyWarsResponse> respFuture;
            if (NickDetector.isLikelyNicked(p)) {
                if (NickDetector.isMythical(p)) {
                    respFuture = SkyWarsRequestCache.getStatsAsync(originalName);
                } else {
                    respFuture = CompletableFuture.completedFuture(null);
                }
            } else {
                respFuture = SkyWarsRequestCache.getStatsAsync(p.getGameProfile().getId());
            }

            checks.add(respFuture.thenApply(resp -> {
                boolean dodge = responseDodge(resp);
                if (dodge) {
                    ChatLib.chat("High stats detected: " + originalName);
                }
                return dodge;
            }));
        }

        // claude
        return CompletableFuture.allOf(checks.toArray(new CompletableFuture[0]))
                .thenApply(v -> checks.stream().anyMatch(CompletableFuture::join));
    }

    private static boolean responseDodge(SkyWarsResponse resp) {
        if (resp == null || resp.stats == null) return false;
        double thresholdKD = SkyWarsToolsMod.config.autododgePlayersKD;
        double thresholdWL = SkyWarsToolsMod.config.autododgePlayersWL;
        double KD = ratio(resp.stats.kills, resp.stats.deaths);
        double WL = ratio(resp.stats.wins, resp.stats.losses);
        if (KD >= thresholdKD) return true;
        if (WL >= thresholdWL) return true;
        return false;
    }

    private static double ratio(long numerator, long denominator) {
        return (denominator == 0 ? numerator : (double) numerator / denominator);
    }

    public static CompletableFuture<Boolean> checkPlayer(String playerName) {
        if (playerName.equalsIgnoreCase(MCName.getName())) return CompletableFuture.completedFuture(false);

        return SkyWarsRequestCache.getStatsAsync(playerName)
                .thenApply(PlayersDodge::responseDodge);
    }

}