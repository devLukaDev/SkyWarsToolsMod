package org.devlukadev.skywarstoolsmod.events;

import cc.polyfrost.oneconfig.events.event.LocrawEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;

import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.MessagePattern;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LastGameEXPEvents {

    private static final Pattern XP_PATTERN =
            Pattern.compile("\\+(\\d+) SkyWars Experience");

    private static final Pattern SKYWARS_XP_MULTIPLIER =
            Pattern.compile("^x(\\d+(?:\\.\\d+)?) SkyWars Experience! (.+)$");

    private static float lastXP = 0;

    public static float getLastXP() {
        return lastXP;
    }

    @SubscribeEvent
    public void onAdditionChat(ClientChatReceivedEvent event) {
        if (!MessagePattern.isValidExperienceAddMessage(event.message)) return;

        Matcher matcher = XP_PATTERN.matcher(event.message.getUnformattedText());
        if (matcher.find()) {
            lastXP += Integer.parseInt(matcher.group(1));
        }
    }

    @SubscribeEvent
    public void onMultiplicationChat(ClientChatReceivedEvent event) {
        if (!MessagePattern.isValidExperienceMultMessage(event.message)) return;
        // TODO requires real world testing
        Matcher matcher = SKYWARS_XP_MULTIPLIER.matcher(event.message.getUnformattedText());
        if (matcher.matches()) {
            float multiplier = Float.parseFloat(matcher.group(1)); // 1.2
            lastXP *= multiplier;
        }
    }

    @SubscribeEvent
    public void onGameEndChat(ClientChatReceivedEvent event) {
        if (MessagePattern.isGameEndMessage(event.message)) {
            if (SkyWarsToolsMod.config.experienceShowTemp) SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(true);
        }
    }

    @SubscribeEvent
    public void onResetChat(ClientChatReceivedEvent event) {
        if (MessagePattern.isValidResetMessage(event.message)) {
            lastXP = 0;
            if (SkyWarsToolsMod.config.experienceShowTemp) SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(false);
        }
    }

    @Subscribe
    public void onLocraw(LocrawEvent event) {
        // TODO maybe replace with a LocationClientPacket from Hypixel ModAPI?
        if (!HypixelUtils.INSTANCE.isHypixel()) return;

        LocrawInfo.GameType type = event.info.getGameType();

        System.out.println("Now in " + type);

        if (type.equals(LocrawInfo.GameType.SKYWARS)) {
            SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(true);
        } else {
            SkyWarsToolsMod.config.lastGameEXPHud.setVisibility(false);
        }
    }
}