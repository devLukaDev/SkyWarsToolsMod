package org.devlukadev.skywarstoolsmod.features.autododge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.tablevels.SkyWarsRequestCache;
import org.devlukadev.skywarstoolsmod.features.tags.Tag;
import org.devlukadev.skywarstoolsmod.features.tags.TagManager;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.MCName;
import org.devlukadev.skywarstoolsmod.utils.NickDetector;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.SkyWarsResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayersDodge {

    public static CompletableFuture<Boolean> shouldDodgePlayerInTab() {
        NetHandlerPlayClient nh = Minecraft.getMinecraft().thePlayer.sendQueue;
        Collection<NetworkPlayerInfo> players = nh.getPlayerInfoMap();

        List<CompletableFuture<Boolean>> checks = new ArrayList<>();
        for (NetworkPlayerInfo p : players) {
            String originalName = p.getGameProfile().getName();
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
                Tag tag = TagManager.checkForTags(p.getGameProfile().getId());
                boolean hasDoNotDodge = tag != null && tag.getReasons().contains("donotdodge");
                boolean tagDodge =
                        tag != null &&
                                SkyWarsToolsMod.config.autododgeTagsEnabled &&
                                !hasDoNotDodge;
                boolean statsDodge = responseDodge(resp);
                boolean dodge = tagDodge || statsDodge;

                if (tag != null) {
                    String reason = "&cTagged: " + tag.getReasons().get(0);
                    ChatLib.chat("&e" + originalName + " &7(" + reason + "&7)" + (hasDoNotDodge ? " &8[dodge disabled]" : ""), true);
                }

                if (dodge) {
                    ChatLib.showTitle("§cDodging §6" + originalName, "HOLD SNEAK TO CANCEL", 10, 20, 10);
                    ChatLib.chat("&cDodging &e" + originalName, true);
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

        UUID uuid = resolveOnlineUuid(playerName);
        Tag tag = TagManager.checkForTags(uuid);
        boolean hasDoNotDodge = tag != null && tag.getReasons().contains(SkyWarsToolsMod.config.autododgeTagsExceptionText);
        boolean tagDodge =
                tag != null &&
                        SkyWarsToolsMod.config.autododgeTagsEnabled &&
                        !hasDoNotDodge;

        if (tag != null) {
            String reason = "&cTagged: " + tag.getReasons().get(0);
            ChatLib.chat("&e" + playerName + " &7(" + reason + "&7)" + (hasDoNotDodge ? " &8[dodge disabled]" : ""), true);
        }

        return SkyWarsRequestCache.getStatsAsync(playerName)
                .thenApply(resp -> tagDodge || responseDodge(resp));
    }

    private static UUID resolveOnlineUuid(String playerName) {
        NetHandlerPlayClient nh = Minecraft.getMinecraft().thePlayer.sendQueue;
        for (NetworkPlayerInfo p : nh.getPlayerInfoMap()) {
            if (p.getGameProfile().getName().equalsIgnoreCase(playerName)) {
                return p.getGameProfile().getId();
            }
        }
        return null;
    }

}