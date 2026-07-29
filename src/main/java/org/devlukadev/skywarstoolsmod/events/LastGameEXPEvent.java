package org.devlukadev.skywarstoolsmod.events;

import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.MessagePattern;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LastGameEXPEvent {

    private static final Pattern XP_PATTERN =
            Pattern.compile("\\+(\\d+) SkyWars Experience");

    private static int lastXP = 0;

    public static int getLastXP() {
        return lastXP;
    }

    @SubscribeEvent
    public void onAdditionChat(ClientChatReceivedEvent event) {
        if (!MessagePattern.isValidExperienceMessage(event.message)) return;

        Matcher matcher = XP_PATTERN.matcher(event.message.getUnformattedText());
        if (matcher.find()) {
            lastXP += Integer.parseInt(matcher.group(1));
        }
    }

    @SubscribeEvent
    public void onGameEndChat(ClientChatReceivedEvent event) {
        if (MessagePattern.isGameEndMessage(event.message)) {
            SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(false);
        }
    }

    @SubscribeEvent
    public void onResetChat(ClientChatReceivedEvent event) {
        if (MessagePattern.isValidResetMessage(event.message)) {
            lastXP = 0;
            SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(true);
        }
    }
}