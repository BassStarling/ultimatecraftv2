package com.bassstarling.ultimatecraftv2.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "ultimatecraftv2");

    public static final RegistryObject<SoundEvent> PIPE_HIT = SOUND_EVENTS.register("pipe_hit",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("ultimatecraftv2", "pipe_hit")));
}