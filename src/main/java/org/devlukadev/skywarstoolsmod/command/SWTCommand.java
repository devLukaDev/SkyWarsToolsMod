package org.devlukadev.skywarstoolsmod.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.*;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.command.swt.StatsPrint;
import org.devlukadev.skywarstoolsmod.features.autododge.AutododgeScreen;
import org.devlukadev.skywarstoolsmod.features.sessions.SessionManager;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.Fetch;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.NamesResponse;
import org.devlukadev.skywarstoolsmod.utils.fetchutils.responses.OverallResponse;

@Command(value = "swt", description = "SkyWarsTools command", aliases = {"skywarstools"})
public class SWTCommand {

    @Main
    private void Main() {
        SkyWarsToolsMod.config.openGui();
    }

    @SubCommandGroup(value = "sessions", aliases = {"session"})
    private static class SessionsCommandGroup {

        @Main
        private void Main() {
            ChatLib.chat("Usage: /swt sessions <sync|reset>");
        }

        @SubCommand(description = "Re-syncs session stats against Hypixel API")
        private void sync() {
            SessionManager.getInstance().sync(Minecraft.getMinecraft().thePlayer.getName());
        }

        @SubCommand(description = "Resets the current session and starts a new one")
        private void reset() {
            SessionManager.getInstance().startSession(Minecraft.getMinecraft().thePlayer.getName());
        }

    }

    @SubCommandGroup(value = "autododge")
    private static class AutododgeCommandGroup {
        @Main
        private void Main() {
            GuiUtils.displayScreen(new AutododgeScreen());
        }
    }

    @SubCommandGroup(value = "stats")
    private static class StatsCommandGroup {
        @Main
        private void Main() {
            ChatLib.chat("Usage: /swt stats <overall|names|mining>");
        }

        @SubCommand(description = "Get overall stats")
        private void overall(GameProfile player) {
            String url = SkyWarsToolsMod.SWT_API + "/overall?player=" + player.getName();
            System.out.println("fetching " + url);
            Fetch.getJsonAsync(url, OverallResponse.class)
                    .thenAccept(response -> {
                        Minecraft.getMinecraft().addScheduledTask(() -> {
                            if (response != null) {
                                StatsPrint.formatOverallData(response);
                            } else {
                                ChatLib.chat("&cCould not get those stats...");
                            }
                        });

                    })
                    .exceptionally(ex -> {
                        System.err.println("Fetch failed: " + ex.getMessage());
                        return null;
                    });


        }

        @SubCommand(description = "Fetches past usernames of the player")
        private void names(GameProfile player) {
            String url = SkyWarsToolsMod.SWT_API + "/snapshotKeys?player=" + player.getName();
            System.out.println("fetching " + url);
            Fetch.getJsonAsync(url, NamesResponse.class)
                    .thenAccept(response -> Minecraft.getMinecraft().addScheduledTask(() -> {
                        if (response == null || response.player == null) {
                            ChatLib.chat("&cPlayer does not exist or is nicked.");
                            return;
                        }
                        StatsPrint.formatNamesData(response);
                    }))
                    .exceptionally(ex -> {
                        System.err.println("Fetch failed: " + ex.getMessage());
                        return null;
                    });
        }

        @SubCommand(description = "Fetches mining risk of the player")
        private void mining(GameProfile player) {
            String url = SkyWarsToolsMod.SWT_API + "/overall?player=" + player.getName();
            System.out.println("fetching " + url);
            Fetch.getJsonAsync(url, OverallResponse.class)
                    .thenAccept(response -> Minecraft.getMinecraft().addScheduledTask(() -> {
                        if (response == null || response.player == null) {
                            ChatLib.chat("&cPlayer does not exist or is nicked.");
                            return;
                        }
                        StatsPrint.formatMiningData(response);
                    }))
                    .exceptionally(ex -> {
                        System.err.println("Fetch failed: " + ex.getMessage());
                        return null;
                    });
        }
    }


}