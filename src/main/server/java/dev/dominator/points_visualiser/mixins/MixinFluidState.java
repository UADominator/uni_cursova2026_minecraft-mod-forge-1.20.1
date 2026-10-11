package dev.dominator.points_visualiser.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Fluids spread in the scheduled tick, and lava sets fire in the random tick
@Mixin(FluidState.class)
public class MixinFluidState {

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void onTick(Level level, BlockPos pos, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	private void onRandomTick(Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		ci.cancel();
	}
}
