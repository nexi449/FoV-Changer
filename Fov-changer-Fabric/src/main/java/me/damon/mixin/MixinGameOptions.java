package me.damon.mixin;

import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(GameOptions.class)
public class MixinGameOptions {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 110))
    private int maxFovValueHook(int constant) {
        return 200;
    }
}