package org.devlukadev.skywarstoolsmod.features.autododge;

import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;

import java.util.Arrays;

public class AutododgeEvents {

    public static boolean dodgingEngaged = false;

    // -1 = not scheduled, 0 = fire this tick, >0 = ticks remaining
    private static int dodgeTicksLeft = -1;

    // Static because called elsewhere by hypixel mod api handler
    public void onLocationReceived(ClientboundLocationPacket packet) {

        if (!packet.getMap().isPresent()) return; // In a lobby
        if (!SkyWarsToolsMod.config.autododgeEnabled) return;

        final String map = packet.getMap().get();
        final String[] dodgeMaps = AutododgeStorage.load().toArray(new String[0]);
        if (dodgeMaps.length == 0) {
            ChatLib.chat("&cYour Autododge config has no maps. Is it malformed?", true);
            return;
        }

        if (!SkyWarsToolsMod.config.autododgeInverted) {
            if (!Arrays.asList(dodgeMaps).contains(map)) return;
        } else {
            if (Arrays.asList(dodgeMaps).contains(map)) return;
        }


        // We are in a map that needs to be dodged!
        ChatLib.chat("&aMap &e" + map + "&a is on dodge list! Dodging in &e5&a seconds...", true);
        ChatLib.chat("&cHOLD SNEAK TO CANCEL DODGING!", true);

        ChatLib.showTitle("§cDodging", "HOLD SNEAK TO CANCEL", 10, 100, 10);
        dodgingEngaged = true;
        dodgeTicksLeft = 100; // 5 seconds * 20 ticks
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        // If you go into another world while dodging was engaged, cancel it
        if (!SkyWarsToolsMod.config.autododgeEnabled) return;
        // TODO do we need this? might be fucking things up -- Yup, this says true a couple times while actually dodging
        //  Solution: debounce this and only listen to the first one? or just delay, only listen to last one
        //  its some weird race condition - needs testing to see what best way to deal with
        
        System.out.println(dodgingEngaged);
        if (dodgingEngaged) {
            cancelDodge();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // For the countdown
        if (!SkyWarsToolsMod.config.autododgeEnabled) return;
        if (event.phase != TickEvent.Phase.END) return;

        // --- dodge timeout firing ---
        if (dodgeTicksLeft > 0) {
            dodgeTicksLeft--;
            if (dodgeTicksLeft % 20 == 0)
                Minecraft.getMinecraft().thePlayer.playSound("random.orb", 1.0F, (-0.005F * dodgeTicksLeft) + 1);
            // NPE when logging out in the middle of this? idk if Tick fires in menu, if so then player is null?

        } else if (dodgeTicksLeft == 0) {
            dodgeTicksLeft = -1;
            dodgingEngaged = false;
            performDodge();
        }

        // Cancel on sneaking
        if (dodgingEngaged
                && Minecraft.getMinecraft().thePlayer != null
                && Minecraft.getMinecraft().thePlayer.isSneaking()
                && dodgeTicksLeft <= 90) {
            System.out.println("Cancelled here");
            cancelDodge();
            ChatLib.chat("&cDodging cancelled!", true);

            IChatComponent prefix = new ChatComponentText(
                    EnumChatFormatting.GREEN + "Want to remove this map from the dodge list? "
            );

            ChatComponentText link = new ChatComponentText("Open the config here.");
            ChatStyle style = new ChatStyle();
            style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/autododge"));
            style.setUnderlined(true);
            style.setColor(EnumChatFormatting.AQUA);
            link.setChatStyle(style);

            prefix.appendSibling(link);

            ChatLib.showTitle("§aCancelled dodge", "Have fun!", 10, 10, 10);
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!SkyWarsToolsMod.config.autododgeEnabled) return;
        String msg = event.message.getFormattedText();

        final String GAME_STARTS_SOON = "§r§e§r§eThe game starts in §r§a§r§c1§r§e second!§r§e§r";
        final String GAME_START = "§r§eCages opened! §r§cFIGHT!§r";

        if (msg.equals(GAME_STARTS_SOON)) {
            if (dodgingEngaged) {
                cancelDodge();

                if (SkyWarsToolsMod.config.autododgeLobby) {
                    ChatLib.chat("&cCould not queue yet, went to lobby as a last resort! " +
                            "Wanted to play anyway? Change this setting in the config.", true);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/lobby");
                }
            }
        } else if (msg.equals(GAME_START)) {
            if (dodgingEngaged) {
                cancelDodge();
                ChatLib.chat("&cGame started too quickly... Could not dodge in time, sorry about that!", true);
            }
        }
    }

    private static void cancelDodge() {
        dodgeTicksLeft = -1;
        dodgingEngaged = false;
        ChatLib.showTitle("Cancelled", null, 10, 20, 10);
    }

    private static void performDodge() {
        dodgingEngaged = false;
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/requeue");
    }
}