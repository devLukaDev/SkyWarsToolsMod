package org.devlukadev.skywarstoolsmod.utils.scheduler;

public class DelayedTask {
    private int ticksRemaining;
    private final Runnable task;

    public DelayedTask(int delayTicks, Runnable task) {
        this.ticksRemaining = delayTicks;
        this.task = task;
    }

    // returns true when done, so it can be removed
    public boolean tick() {
        if (--ticksRemaining <= 0) {
            task.run();
            return true;
        }
        return false;
    }
}

