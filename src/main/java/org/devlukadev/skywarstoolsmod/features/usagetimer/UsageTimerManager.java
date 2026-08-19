package org.devlukadev.skywarstoolsmod.features.usagetimer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import static org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerInventory.getExtraAttributeKey;
import static org.devlukadev.skywarstoolsmod.utils.MessagePattern.GAME_START;

public class UsageTimerManager {

    public static class ItemCooldown {
        public final String key;
        public final int gameStartSeconds;
        public final int usageSeconds; // 0 = no retrigger-on-use cooldown


        public ItemCooldown(String key, int gameStartSeconds, int usageSeconds) {
            this.key = key;
            this.gameStartSeconds = gameStartSeconds;
            this.usageSeconds = usageSeconds;
        }
    }

    public static boolean wasTimeWarpPearlJustThrown = false;
    // fill in your real durations here
    private static final Map<String, ItemCooldown> DEFINITIONS = new HashMap<>();

    static {
        DEFINITIONS.put("echo_clock", new ItemCooldown("echo_clock", 9, 39));
        DEFINITIONS.put("endlord_pearl", new ItemCooldown("endlord_pearl", 30, 0));
        DEFINITIONS.put("corrupted_pearl", new ItemCooldown("corrupted_pearl", 30, 0));
        DEFINITIONS.put("cyro_bridge_egg", new ItemCooldown("cyro_bridge_egg", 29, 0));
    }

    private static final Map<String, Long> cooldownEndTimes = new HashMap<>();

    @SubscribeEvent
    public void onGameStart(ClientChatReceivedEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.cooldownsHUDEnabled) return;

        String message = event.message.getFormattedText();

        if (GAME_START.matcher(message).matches()) {
            // Game starts
            long now = System.currentTimeMillis();
            ChatLib.chat("Game started at " + now);
            for (ItemCooldown def : DEFINITIONS.values()) {
                cooldownEndTimes.put(def.key, now + def.gameStartSeconds * 1000L);
            }
        }
    }

    /**
     * Call when an item is actually used (right-clicked). Only restarts cooldown if the item has a usage cooldown.
     */
    public static void onItemUsed(String key) {
        ItemCooldown def = DEFINITIONS.get(key);

        if (def == null || def.usageSeconds <= 0) return;
        if (System.currentTimeMillis() <= cooldownEndTimes.get(key)) return; // The item cannot be used yet

        cooldownEndTimes.put(key, System.currentTimeMillis() + def.usageSeconds * 1000L);
    }

    /**
     * For special cases like Time Warp Pearl's landing timer, where the duration isn't from DEFINITIONS.
     */
    public static void startCustom(String key, int seconds) {
        cooldownEndTimes.put(key, System.currentTimeMillis() + seconds * 1000L);
    }

    public static boolean isOnCooldown(String key) {
        Long end = cooldownEndTimes.get(key);
        return end != null && end > System.currentTimeMillis();
    }

    public static long getRemainingSeconds(String key) {
        Long end = cooldownEndTimes.get(key);
        if (end == null) return 0;
        return Math.max(0, (end - System.currentTimeMillis()) / 1000 + 1);
    }

    public static void reset() {
        cooldownEndTimes.clear();
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!SkyWarsToolsMod.config.cooldownsHUDEnabled) return;
        if (event.entityPlayer != Minecraft.getMinecraft().thePlayer) return;
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR
                && event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        ItemStack stack = event.entityPlayer.getHeldItem();
        String key = getExtraAttributeKey(stack); // see note below
        if (key == null) return;

        if (key.equalsIgnoreCase("endlord_pearl")) {
            if (isOnCooldown("endlord_pearl")) return;
            wasTimeWarpPearlJustThrown = true;
        }
        if (key.equalsIgnoreCase("echo_clock")) {
            if (isOnCooldown("echo_clock")) return;
            if (isVoidBelow(Minecraft.getMinecraft().thePlayer)) return; // Clock cant be used, ignored
        }

        UsageTimerManager.onItemUsed(key);
    }

    public boolean isVoidBelow(EntityPlayer player) {
        World world = player.worldObj;
        int x = MathHelper.floor_double(player.posX);
        int z = MathHelper.floor_double(player.posZ);
        int startY = MathHelper.floor_double(player.posY) - 1;

        for (int y = startY; y >= 0; y--) {
            Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            if (block != Blocks.air) {
                return false;
            }
        }
        return true;
    }


}
