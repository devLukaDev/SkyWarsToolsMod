package org.devlukadev.skywarstoolsmod.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommandGroup;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.features.autododge.AutododgeScreen;
import org.devlukadev.skywarstoolsmod.features.sessions.SessionManager;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;

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
        private void Main(){
            GuiUtils.displayScreen(new AutododgeScreen());
        }
    }
}