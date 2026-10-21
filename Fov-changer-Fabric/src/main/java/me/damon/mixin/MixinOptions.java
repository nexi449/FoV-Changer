package me.damon.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "net.minecraft.client.Options", remap = false)
public class MixinOptions {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 110))
	private int maxFovValueHook(int constant) {
		return 200;
	}
}
