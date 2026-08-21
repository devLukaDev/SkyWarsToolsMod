package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Adapted from Alexdoru
// https://github.com/Alexdoru/MWE/blob/master/src/main/java/fr/alexdoru/mwe/asm/hooks/mc/gui/GuiScreenBookHook.java#L57

@Mixin(GuiScreenBook.class)
public class NickBookMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onBookInit(EntityPlayer entityPlayer, ItemStack itemStack, boolean bl, CallbackInfo ci) {
        skyWarsToolsMod$onBookInit(itemStack);
    }

    @Unique
    private static final Pattern skyWarsToolsMod$nickSuccessPagePattern = Pattern.compile("§[0-9a-f](\\w+)§r");

    @Unique
    private static void skyWarsToolsMod$onBookInit(ItemStack book) {
        try {
            if (book.hasTagCompound()) {
                final NBTTagCompound nbttagcompound = book.getTagCompound();
                NBTTagList bookPages = nbttagcompound.getTagList("pages", 8);
                if (bookPages != null) {
                    bookPages = (NBTTagList) bookPages.copy();
                    int bookTotalPages = bookPages.tagCount();
                    if (bookTotalPages < 1) {
                        bookTotalPages = 1;
                    }
                    for (int i = 0; i < bookTotalPages; i++) {
                        final String nickLine = IChatComponent.Serializer.jsonToComponent(bookPages.getStringTagAt(i)).
                                getUnformattedText().replace("\n", "");
                        Matcher matcher = skyWarsToolsMod$nickSuccessPagePattern.matcher(nickLine);
                        if (matcher.find()) {
                            final String newNick = matcher.group(1);
                            if (newNick != null && !newNick.isEmpty()) {
                                ChatLib.chat("Nick has been set to " + newNick);
                                SkyWarsToolsMod.config.mostRecentNick = newNick;
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}


