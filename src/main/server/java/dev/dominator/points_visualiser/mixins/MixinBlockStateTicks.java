package dev.dominator.points_visualiser.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class MixinBlockStateTicks {

	@Inject(
		method = "tick",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(
		method = "randomTick",
		at = @At("HEAD"),
		cancellable = true
	)
	private void onRandomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		ci.cancel();
	}
}
