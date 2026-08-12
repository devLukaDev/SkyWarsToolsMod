package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class TakeItemFromGUIMixin {

    @Inject(method = "canTakeStack", at = @At("RETURN"), cancellable = true)
    private void onCanTakeStack(EntityPlayer playerIn, CallbackInfoReturnable<Boolean> cir) {
        if (!SkyWarsToolsMod.config.etableFix) return;
        if (!LocationUtil.isInSkyWars()) return;

        Slot self = (Slot) (Object) this;
        if (self.getSlotIndex() != 1) return;
        if (!self.getHasStack()) return;

        ItemStack stack = self.getStack();
        if (stack.getItem() == Items.dye && stack.getMetadata() == EnumDyeColor.BLUE.getDyeDamage()) {
            // condition for blocking removal — e.g. some custom lock state
            ChatLib.chat("Lapis removed, cancelled!");
            cir.setReturnValue(false);
        }
    }
}
