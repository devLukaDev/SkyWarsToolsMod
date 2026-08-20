package org.devlukadev.skywarstoolsmod.command.swt;

import net.minecraft.client.Minecraft;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.NamesResponse;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.OverallResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatsPrint {

    private static final List<String> MINING_KITS = Arrays.asList(
            "kit_advanced_solo_enchanter",
            "kit_supporting_team_enchanter",
            "kit_advanced_solo_enderman",
            "kit_attacking_team_enderman",
            "kit_basic_solo_speleologist",
            "kit_mining_team_speleologist"
    );


    public static void formatOverallData(OverallResponse response) {
        ChatLib.chat("§6§m-----------------------------------------------------", false);
        ChatLib.chat("§c§l" + response.player, false);

        IChatComponent level = new ChatComponentText("§eLevel: §b" + response.display.levelFormattedWithBrackets);
        level.setChatStyle(new ChatStyle().setChatHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§a" + response.display.active_scheme))
        ));
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(level);

        ChatLib.chat("§eWins: §b" + response.stats.wins, false);
        ChatLib.chat("§eLosses: §b" + response.stats.losses, false);
        ChatLib.chat("§eWLR: §b" + String.format("%.2f",
                (double) response.stats.wins / (response.stats.losses == 0 ? 1 : response.stats.losses)), false);
        ChatLib.chat("§eKills: §b" + response.stats.kills, false);
        ChatLib.chat("§eDeaths: §b" + response.stats.deaths, false);
        ChatLib.chat("§eKDR: §b" + String.format("%.2f",
                (double) response.stats.kills / (response.stats.deaths == 0 ? 1 : response.stats.deaths)), false);
        ChatLib.chat("§eMax Prestige Kits: §b" + response.stats.customs_kitsMaxPrestige, false);
        ChatLib.chat("§6§m-----------------------------------------------------", false);
    }

    public static void formatNamesData(NamesResponse response) {
        List<String> uniquePlayers = response.data.stream()
                .map(entry -> entry.player)
                .distinct()
                .collect(Collectors.toList());

        String namesString = "§b" + String.join("§8,§b ", uniquePlayers);

        ChatLib.chat("§6§m-----------------------------------------------------", false);
        ChatLib.chat("§ePlayer: §b" + response.player, false);
        ChatLib.chat("§eUnique Names: §b" + uniquePlayers.size(), false);
        ChatLib.chat(namesString, false);
        ChatLib.chat("§eTotal Snapshots: §b" + response.data.size(), false);
        ChatLib.chat("§6§m-----------------------------------------------------", false);
    }

    public static void formatMiningData(OverallResponse response) {
        ChatLib.chat("§6§m-----------------------------------------------------", false);
        ChatLib.chat("§c§l" + response.player + " §7- §bMining Stats", false);
        ChatLib.chat("§8Can be used to estimate how likely a player is to be mining.", false);

        OverallResponse.Stats stats = response.stats;
        List<String> activeKits = Arrays.asList(stats.activeKit_SOLO, stats.activeKit_TEAM, stats.activeKit_MINI);

        double blocksBrokenRatio = (double) stats.blocks_broken / stats.blocks_placed;
        double killWinRatio = (double) stats.kills_solo / (stats.wins_solo == 0 ? 1 : stats.wins_solo);
        boolean hasMiningPerks = stats.perkslot != null && stats.perkslot.normal.containsValue("solo_mining_expertise");
        double survivedPlayersRatio = (double) stats.survived_players / stats.kills;

        String bbSeverity = blocksBrokenRatio > 0.1 ? "§c(High Risk)" : blocksBrokenRatio > 0.05 ? "§e(Medium Risk)" : "§a(Low Risk)";
        ChatLib.chat("§eBlocks Broken Ratio: §b" + String.format("%.2f", blocksBrokenRatio) + " " + bbSeverity, false);

        String kwrSeverity = killWinRatio > 6 ? "§c(High Risk)" : killWinRatio > 5.5 ? "§e(Medium Risk)" : "§a(Low Risk)";
        ChatLib.chat("§eKill/Win Ratio: §b" + String.format("%.2f", killWinRatio) + " " + kwrSeverity, false);

        String sprSeverity = survivedPlayersRatio > 5 ? "§c(High Risk)" : survivedPlayersRatio > 4 ? "§e(Medium Risk)" : "§a(Low Risk)";
        ChatLib.chat("§eSurvived/Kills Ratio: §b" + String.format("%.2f", survivedPlayersRatio) + " " + sprSeverity, false);

        String miningPerksText = hasMiningPerks ? "§bYes §c(High Risk)" : "§bNo §a(Low Risk)";
        ChatLib.chat("§eHas Mining Perks: §b" + miningPerksText, false);

        List<String> matchedKits = activeKits.stream().filter(MINING_KITS::contains).collect(Collectors.toList());
        boolean hasMiningKits = !matchedKits.isEmpty();
        String miningKitsText = hasMiningKits ? "§bYes §c(High Risk)" : "§bNo §a(Low Risk)";

        IChatComponent activeKitsComponent = new ChatComponentText("§eActive Mining Kits: §b" + miningKitsText);
        activeKitsComponent.setChatStyle(new ChatStyle().setChatHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText("§a" + (matchedKits.isEmpty() ? "None" : String.join(", ", matchedKits))))
        ));
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(activeKitsComponent);

        ChatLib.chat("§6§m-----------------------------------------------------", false);
    }
}