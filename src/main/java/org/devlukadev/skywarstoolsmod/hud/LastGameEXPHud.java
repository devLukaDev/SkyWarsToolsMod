package org.devlukadev.skywarstoolsmod.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import org.devlukadev.skywarstoolsmod.events.LastGameEXPEvents;

public class LastGameEXPHud extends SingleTextHud {

    public LastGameEXPHud() {
        super("§6EXP Last Game", true);
    }

    public void setVisibility(boolean visible){
        this.enabled = visible;
    }

    @Override
    protected String getText(boolean example) {
        float lastXP = LastGameEXPEvents.getLastXP();

        if (lastXP == 0.0f) return "§f" + 0;

        float rounded = Math.round(lastXP * 100) / 100.0f;

        if (rounded == Math.floor(rounded)) {
            return "§f" + (int) rounded;
        }
        return "§f" + rounded;
    }
}
