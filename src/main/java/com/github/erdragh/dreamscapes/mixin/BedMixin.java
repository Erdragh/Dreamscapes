package com.github.erdragh.dreamscapes.mixin;

import com.github.erdragh.dreamscapes.Dreamscapes;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedMixin {

    @Inject(at = @At("RETURN"), method = "onUse", cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) {

        } else {
            var res = cir.getReturnValue();
            if (res == ActionResult.SUCCESS) {
                player.trySleep(pos).ifLeft((reason) -> {
                    if (reason.getMessage() != null) {
                        player.sendMessage(reason.getMessage(), true);
                        var dreamWorld = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(Dreamscapes.MODID, "dream")));
                        ((ServerPlayerEntity) player).teleport(dreamWorld, player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    }
                });
            }
        }
    }
}
