package ru.expensive.asm.mixins;

import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Input.class)
public class InputMixin {
    @Shadow
    public boolean pressingRight;
    @Shadow
    public boolean pressingLeft;
    @Shadow
    public boolean pressingBack;
    @Shadow
    public boolean pressingForward;
    @Shadow
    public float movementForward;
    @Shadow
    public float movementSideways;
    @Shadow
    public boolean jumping;
    @Shadow
    public boolean sneaking;
}
