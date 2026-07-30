package org.devlukadev.skywarstoolsmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.ChatComponentText;

public class ChatLib {

    private static final String prefix = "&bSkyWarsTools&f >§r ";

    public static void chat(String formattedMessage, boolean appendPrefix) {
        if (appendPrefix) formattedMessage = prefix + formattedMessage;
        String colored = formattedMessage.replace('&', '§');

        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(colored));
    }

    public static void chat(ChatComponentText message, boolean appendPrefix) {
        if (appendPrefix) {
            ChatComponentText prefixComponent = new ChatComponentText(prefix);
            Minecraft.getMinecraft().thePlayer.addChatMessage(prefixComponent.appendSibling(message));
        } else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(message);
        }

    }

    public static void showTitle(String title, String subtitle, int fadeIn, int display, int fadeOut) {
        GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
        gui.setDefaultTitlesTimes();
        gui.displayTitle(null, null, fadeIn, display, fadeOut);
        gui.displayTitle(title, null, fadeIn, display, fadeOut);
        gui.displayTitle(null, subtitle, fadeIn, display, fadeOut);
    }
}
