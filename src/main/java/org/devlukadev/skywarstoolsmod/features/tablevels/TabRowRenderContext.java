package org.devlukadev.skywarstoolsmod.features.tablevels;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

import java.util.List;
import java.util.regex.Matcher;

import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.TEAMS_CAGE_TP;

public class TabRowRenderContext {
    public static List<String> lastSegments;   // resolved, unpadded pieces for the row about to be drawn
    public static int[] lastColWidths;          // TabColumnWidths.get() snapshot
    public static String lastBuiltString;       // exact string getPlayerName returned, for verification
    public static int max;

    public static void onLocationReceived(ClientboundLocationPacket packet) {
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (!LocationUtil.isInSkyWars()) return;

        // Reset to 0 so a max can be found again when going into a new game
        max = 0;

    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!SkyWarsToolsMod.config.levelsEnabled) return;
        if (!LocationUtil.isInSkyWars()) return;
        Matcher matcher = TEAMS_CAGE_TP.matcher(event.message.getFormattedText());
        if (matcher.find()) max = 0;
    }

}

