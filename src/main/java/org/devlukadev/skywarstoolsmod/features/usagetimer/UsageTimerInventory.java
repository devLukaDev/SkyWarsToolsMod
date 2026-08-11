package org.devlukadev.skywarstoolsmod.features.usagetimer;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class UsageTimerInventory {

    public static boolean hasCorruptedPearl = false;
    public static boolean hasEchoClock = false;
    public static boolean hasEndlordPearl = false;
    public static boolean hasCryoBridgeEgg = false;

    private static final ItemStack[] hotbarCache = new ItemStack[9];

    // maps the ExtraAttributes NBT key -> a setter for the corresponding flag
    private static final Map<String, Consumer<Boolean>> TRACKED_ATTRIBUTES = new HashMap<>();

    static {
        TRACKED_ATTRIBUTES.put("corrupted_pearl", v -> hasCorruptedPearl = v);
        TRACKED_ATTRIBUTES.put("echo_clock", v -> hasEchoClock = v);
        TRACKED_ATTRIBUTES.put("endlord_pearl", v -> hasEndlordPearl = v);
        TRACKED_ATTRIBUTES.put("cyro_bridge_egg", v -> hasCryoBridgeEgg = v);
    }

    public static void onHotbarSlotChanged(int slot, ItemStack stack) {
        if (slot < 0 || slot > 8) return;
        hotbarCache[slot] = stack;
        recalculateFlags();
    }

    private static void recalculateFlags() {
        // reset all tracked flags to false, then re-derive from current hotbar state
        Set<String> found = new HashSet<>();

        for (ItemStack stack : hotbarCache) {
            String key = getExtraAttributeKey(stack);
            if (key != null) {
                found.add(key);
            }
        }

        for (Map.Entry<String, Consumer<Boolean>> entry : TRACKED_ATTRIBUTES.entrySet()) {
            entry.getValue().accept(found.contains(entry.getKey()));
        }
    }

    /**
     * Returns the first tracked ExtraAttributes key present on this stack with a truthy byte value, or null.
     */
    public static String getExtraAttributeKey(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) return null;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("ExtraAttributes")) return null;

        NBTTagCompound extraAttributes = tag.getCompoundTag("ExtraAttributes");
        for (String key : TRACKED_ATTRIBUTES.keySet()) {
            if (extraAttributes.hasKey(key) && extraAttributes.getBoolean(key)) {
                return key;
            }
        }
        return null;
    }
}
