package org.devlukadev.skywarstoolsmod.utils.scheduler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientScheduler {
    private static final List<DelayedTask> tasks = new ArrayList<>();

    public static void schedule(int delayTicks, Runnable task) {
        tasks.add(new DelayedTask(delayTicks, task));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tasks.removeIf(DelayedTask::tick);
    }
}

