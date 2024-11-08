package ru.expensive.asm.mixins.accessors;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InputUtil.Type.class)
public interface TypeAccessor {
    @Accessor("map")
    Int2ObjectMap<InputUtil.Key> getMap();
}