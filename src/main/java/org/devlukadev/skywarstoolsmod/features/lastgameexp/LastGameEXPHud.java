package org.devlukadev.skywarstoolsmod.features.lastgameexp;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LastGameEXPHud extends SingleTextHud {

    public LastGameEXPHud() {
        super("&6EXP Last Game", true);
    }

    public void setVisibility(boolean visible){
        this.enabled = visible;
    }

    @Override
    protected String getText(boolean example) {
        double lastXP = LastGameEXPEvents.getLastXP();

        return "§f" + BigDecimal.valueOf(lastXP)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    @Override
    public boolean shouldShow() {
        return super.shouldShow() && LocationUtil.isInSkyWars();
    }
}
