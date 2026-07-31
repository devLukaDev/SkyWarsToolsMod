package org.devlukadev.skywarstoolsmod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TileEntityBeacon.class)
public interface MinecraftMixin {

    @Accessor("beamSegments")
    List<TileEntityBeacon.BeamSegment> getBeamSegments();

    @Accessor("beamSegments")
    void setBeamSegments(List<TileEntityBeacon.BeamSegment> segments);


}

