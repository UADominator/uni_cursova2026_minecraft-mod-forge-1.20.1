package dev.dominator.points_visualiser.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Every redstone signal is read through these two methods, so nothing gets powered
@Mixin(BlockBehaviour.BlockStateBase.class)
public class MixinBlockStateBase {

	@Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
	private void onGetSignal(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(0);
	}

	@Inject(method = "getDirectSignal", at = @At("HEAD"), cancellable = true)
	private void onGetDirectSignal(BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(0);
	}
}
