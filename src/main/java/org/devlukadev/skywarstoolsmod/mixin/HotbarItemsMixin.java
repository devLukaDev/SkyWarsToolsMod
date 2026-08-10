package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.devlukadev.skywarstoolsmod.features.usagetimer.UsageTimerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public class HotbarItemsMixin {

    @Inject(method = "setInventorySlotContents", at = @At("RETURN"))
    private void onSetSlot(int slot, ItemStack stack, CallbackInfo ci) {
        UsageTimerInventory.onHotbarSlotChanged(slot, stack);
    }
}
