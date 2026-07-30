package org.devlukadev.skywarstoolsmod.utils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DelayedTask {
    // TODO this entire class is untested, its from MWE DelayedTask.java - I did crash when I used it once but might have been coincidence

    private final Runnable runnable;
    private int counter;

    public DelayedTask(Runnable task) {
        this(task, 0);
    }

    public DelayedTask(Runnable task, int ticks) {
        this.runnable = task;
        this.counter = ticks;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (this.counter <= 0) {
                MinecraftForge.EVENT_BUS.unregister(this);
                this.runnable.run();
            }
            this.counter--;
        }
    }

}
