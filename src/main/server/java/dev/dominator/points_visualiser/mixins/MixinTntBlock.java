package dev.dominator.points_visualiser.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntBlock.class)
public class MixinTntBlock {

	/// Every ignition path (redstone, fire, flint and steel, projectiles) ends here
	@Inject(
		method = "explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void onExplode(Level level, BlockPos pos, LivingEntity entity, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "wasExploded", at = @At("HEAD"), cancellable = true)
	private void onWasExploded(Level level, BlockPos pos, Explosion explosion, CallbackInfo ci) {
		ci.cancel();
	}
}
