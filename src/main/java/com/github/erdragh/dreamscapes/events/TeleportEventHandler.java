package com.github.erdragh.dreamscapes.events;

import com.github.erdragh.dreamscapes.Dreamscapes;
import com.github.erdragh.dreamscapes.components.PlayerComponents;
import com.github.erdragh.dreamscapes.effects.DreamStatusEffect;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TeleportEventHandler {

    public static final RegistryKey<World> DREAM_REGISTRY_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(Dreamscapes.MODID, "dream"));

    public static void handleSleep(LivingEntity entity, BlockPos pos) {

        if (entity instanceof ServerPlayerEntity && !entity.getWorld().getRegistryKey().equals(DREAM_REGISTRY_KEY)) {
            var player = ((ServerPlayerEntity) entity);
            if (player.getStatusEffects().stream().anyMatch((statusEffectInstance -> {
                if (statusEffectInstance.getEffectType().getClass().equals(DreamStatusEffect.class)) {
                    System.out.println("Matching Status Effect found: " + statusEffectInstance.getEffectType().getName());
                    return true;
                }
                return false;
            }))) {
                System.out.println("Matching Status effect, player is dreaming");
            } else {
                return;
            }
            teleport(true, player, pos);
        }
    }

    public static boolean handleDamage(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        var world = entity.getWorld();
        if ((entity instanceof ServerPlayerEntity) && entity.getWorld().getRegistryKey().equals(DREAM_REGISTRY_KEY)) {
            System.out.println("Hey, a player got damaged!");
            var player = (ServerPlayerEntity) entity;
            player.setHealth(player.getMaxHealth());

            System.out.println(player.getWorld().isClient);

            teleport(false, player, player.getBlockPos());
        }
        return true;
    }

    private static void teleport(boolean toDream, ServerPlayerEntity player, BlockPos actionPos) {
        var sourcePositionComponent = toDream ? PlayerComponents.useOriginPosition(player) : PlayerComponents.useDreamPosition(player);
        var destinationPositionComponent = toDream ? PlayerComponents.useDreamPosition(player) : PlayerComponents.useOriginPosition(player);

        var sourceWorld = player.getWorld();

        sourcePositionComponent.setPosition(player.getPos());
        sourcePositionComponent.setWorldIdentifier(sourceWorld.getRegistryKey().getValue());

        var destinationWorld = player.getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, destinationPositionComponent.getWorldIdentifier()));

        if (toDream) {
            sourceWorld.setBlockState(actionPos, sourceWorld.getBlockState(actionPos).with(BooleanProperty.of("occupied"), false));
        }

        Vec3d teleportPosition = null;

        if (destinationPositionComponent.hasTeleported()) {
            teleportPosition = destinationPositionComponent.getPosition();
        }

        int teleportHeight = 500;
        for (int i = -64; i < 319; i++) {
            if (destinationWorld.isTopSolid((teleportPosition == null ? actionPos.withY(i) : new BlockPos(teleportPosition.getX(), i, teleportPosition.getZ())), player)) {
                teleportHeight = i;
            }
        }
        if (teleportHeight > 319) {
            teleportHeight = 320;
        }
        teleportHeight++;

        if (teleportPosition == null) {
            teleportPosition = new Vec3d(player.getX(), teleportHeight, player.getZ());
        }

        player.teleport(destinationWorld, teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ(), player.getYaw(), player.getPitch());
        if (teleportHeight > 319 && destinationWorld.isTopSolid(new BlockPos((int) teleportPosition.getX(), (int) teleportPosition.getY(), (int) teleportPosition.getZ()), player)) {
            destinationWorld.setBlockState(actionPos.withY(--teleportHeight), Blocks.STONE.getDefaultState());
        }
        sourcePositionComponent.setTeleported();
    }
}
