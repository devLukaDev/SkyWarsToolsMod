package org.devlukadev.skywarstoolsmod.updater;

// Original from Alexdoru MWE
// https://github.com/Alexdoru/MWE

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

import net.minecraftforge.fml.common.versioning.ComparableVersion;
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

        ComparableVersion current = new ComparableVersion(this.currentV);
        ComparableVersion latest = this.latestVersion;
        System.out.println("SkyWarsToolsMod Versions: current=" + currentV + ", latest=" + latest);

        if (this.updateInfo != null) {
            final String releaseLink = "https://github.com/devLukaDev/SkyWarsToolsMod/releases";

            if (current.compareTo(latest) > 0) {
                // current is newer than latest
                ChatLib.chat("&6&m--------------------------------------------------------", false);
                ChatLib.chat("&e&lYou are running a private beta of SkyWarsToolsMod.", false);
                ChatLib.chat("&7Currently installed:&f " + current, false);
                ChatLib.chat("&7Latest public release:&f " + latestVersion, false);
                ChatLib.chat("&6Thanks for testing!", false);
                ChatLib.chat("&6&m--------------------------------------------------------", false);
            } else if (current.compareTo(latest) < 0) {

                ChatLib.chat("&6&m--------------------------------------------------------", false);
                ChatLib.chat("&c&lA new version is available for SkyWarsToolsMod!", false);
                ChatLib.chat("&7Current:&f " + current, false);
                ChatLib.chat("&7Latest:&f " + latest, false);
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(
                                "Click here to view the changelog & download page.")
                                .setChatStyle(new ChatStyle()
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, releaseLink))
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                new ChatComponentText(releaseLink)))));

                if (this.isFeatherClient) {
                    ChatLib.chat("&c✘ The automatic updater is disabled on Feather.", false);
                } else if (this.downloadSuccess) {
                    ChatLib.chat("&a✔ Update has been downloaded and will be installed automatically when closing the game.", false);
                }
                ChatLib.chat("&6&m--------------------------------------------------------", false);

            }

        } else {
            if (current.equals(latest)){
                ChatLib.chat("&aYou are using the latest version of SkyWarsToolsMod! (&f" + current + "&a)");
            }
        }
    }

}