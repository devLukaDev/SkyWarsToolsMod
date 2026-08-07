package org.devlukadev.skywarstoolsmod.updater;

// Original from Alexdory MWE
// https://github.com/Alexdoru/MWE

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;

import java.io.File;


public final class SWTUpdater extends ModUpdater {

    public SWTUpdater(File modJarFile) {
        super(
                modJarFile,
                "@NAME@",
                "@VER@",
                true
        );
    }

    @Override
    protected String getApiEndpoint() {
        return "https://api.github.com/repos/devLukaDev/SkyWarsToolsMod/releases/latest";
    }

    @Override
    protected void printChatNotification() {
        if (this.updateInfo != null) {
            final String releaseLink = "https://github.com/devLukaDev/SkyWarsToolsMod/releases";
            ChatLib.chat("&8--------");
            ChatLib.chat("&4&l    MWE &6v" + this.updateInfo.version + " &2is available!");

            Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(
                            "    Click here to view the changelog & download page.")
                            .setChatStyle(new ChatStyle()
                                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, releaseLink))
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ChatComponentText(releaseLink))))
            );

            if (this.automaticUpdate) {
                ChatLib.chat("");
                if (this.isFeatherClient) {
                    ChatLib.chat("&c✘ The automatic updater is disabled on Feather.");
                } else if (this.downloadSuccess) {
                    ChatLib.chat("&a✔ Update has been downloaded and will be installed automatically when closing the game.");
                }
            }

            ChatLib.chat("&8--------");
        }
    }

}