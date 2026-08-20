package org.devlukadev.skywarstoolsmod.command;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import org.devlukadev.skywarstoolsmod.SkyWarsToolsMod;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

@Command(value = "swlevel", description = "Quick stats lookup")
public class SWLevel {
    @Main
    private void handle(GameProfile player) {
        ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, "/swt stats overall " + player.getName());
    }
}