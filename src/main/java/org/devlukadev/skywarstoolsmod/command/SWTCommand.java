package org.devlukadev.skywarstoolsmod.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommandGroup;
import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.features.sessions.SessionManager;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;

@Command(value = "swt", description = "Main command", aliases = {"skywarstools"})
public class SWTCommand {

    @Main
    private void Main() {
        ChatLib.chat("SkyWarsTools - try /swt help for a list of commands. ");
    }

    @SubCommandGroup(value = "sessions", aliases = {"session"})
    private static class SessionsCommandGroup {

        @Main
        private void Main() {
            ChatLib.chat("Usage: /swt sessions <start|sync|reset>");
        }

        @SubCommand(description = "Starts a new session and syncs your baseline stats")
        private void start() {
            SessionManager.getInstance().startSession(Minecraft.getMinecraft().thePlayer.getName());
            ChatLib.chat("Session started, syncing baseline stats...");
        }

        @SubCommand(description = "Re-syncs session stats against Hypixel API")
        private void sync() {
            ChatLib.chat("Syncing session stats...");
            SessionManager.getInstance().sync(Minecraft.getMinecraft().thePlayer.getName());
        }

        @SubCommand(description = "Resets the current session")
        private void reset() {
            SessionManager.getInstance().resetSession();
            ChatLib.chat("Session reset.");
        }

    }
}