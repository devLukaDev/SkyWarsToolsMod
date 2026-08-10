package org.devlukadev.skywarstoolsmod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import org.devlukadev.skywarstoolsmod.utils.ChatLib;
import org.devlukadev.skywarstoolsmod.utils.LocationUtil;
import org.devlukadev.skywarstoolsmod.utils.scheduler.ClientScheduler;

import java.util.Collection;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in ExampleMod.java with `CommandManager.INSTANCE.registerCommand(new ExampleCommand());`
 *
 * @see Command
 * @see Main
 * @see SkyWarsToolsMod
 */
@Command(value = "swtexample", description = "Access the " + SkyWarsToolsMod.NAME + " GUI.", aliases = {"swtexample"})
public class ExampleCommand {
    @Main
    private void handle() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        ItemStack stack = player.getHeldItem();
        if (stack == null) {
            player.addChatMessage(new ChatComponentText("§cNo item in hand."));
            return;
        }

        if (!stack.hasTagCompound()) {
            player.addChatMessage(new ChatComponentText("§cHeld item has no NBT tag."));
            return;
        }

        NBTTagCompound tag = stack.getTagCompound();
        player.addChatMessage(new ChatComponentText("§eFull NBT: §f" + tag.toString()));

        if (tag.hasKey("ExtraAttributes")) {
            NBTTagCompound extraAttributes = tag.getCompoundTag("ExtraAttributes");
            player.addChatMessage(new ChatComponentText("§aExtraAttributes keys:"));
            for (String key : extraAttributes.getKeySet()) {
                NBTBase value = extraAttributes.getTag(key);
                player.addChatMessage(new ChatComponentText("  §b" + key + "§7: §f" + value.toString()));
            }
        } else {
            player.addChatMessage(new ChatComponentText("§cNo ExtraAttributes tag on this item."));
        }
    }
}