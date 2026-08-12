package org.devlukadev.skywarstoolsmod.features.usagetimer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.DumpFields;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

import java.util.List;

public class TimeWarpPearlTracker {

    private static EntityEnderPearl trackedPearl = null;

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (!(event.entity instanceof EntityEnderPearl)) return;
        EntityEnderPearl pearl = (EntityEnderPearl) event.entity;

        // Only track if the player's held item at throw time was flagged as the Time Warp Pearl.
        // (You'll likely want ItemUsageListener to set a "pendingThrow" flag right before this fires,
        // since by the time the entity joins, the stack may have already decremented/changed.)
        if (!UsageTimerManager.wasTimeWarpPearlJustThrown) return;
        UsageTimerManager.wasTimeWarpPearlJustThrown = false;
        trackedPearl = pearl;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!LocationUtil.isInSkyWars()) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (trackedPearl == null) return;

        if (trackedPearl.isDead || !trackedPearl.worldObj.loadedEntityList.contains(trackedPearl)) {
            // pearl has impacted / been removed — landing detected
            UsageTimerManager.startCustom("endlord_pearl", 3);
            trackedPearl = null;
        }
    }
}
