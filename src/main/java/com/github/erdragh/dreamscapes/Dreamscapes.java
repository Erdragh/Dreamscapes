package com.github.erdragh.dreamscapes;

import com.github.erdragh.dreamscapes.effects.DreamStatusEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.SleepManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class Dreamscapes implements ModInitializer {

    public static final String MODID = "dreamscapes";

    public static final StatusEffect DREAM_EFFECT = new DreamStatusEffect();

    @Override
    public void onInitialize() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(MODID, "can_dream"), DREAM_EFFECT);

        EntitySleepEvents.START_SLEEPING.register((entity, pos) -> {

            var dreamRegistryKey = RegistryKey.of(Registry.WORLD_KEY, new Identifier(Dreamscapes.MODID, "dream"));

            if (entity instanceof ServerPlayerEntity && !entity.getWorld().getRegistryKey().equals(dreamRegistryKey)) {
                var originWorld = entity.getWorld();
                var dreamWorld = entity.getServer().getWorld(dreamRegistryKey);
                System.out.println(dreamWorld.getRegistryKey().getValue());
                originWorld.setBlockState(pos, originWorld.getBlockState(pos).with(BooleanProperty.of("occupied"), false));
                int teleportHeight = 500;
                for (int i = -64; i < 319; i++) {
                    var blockState = dreamWorld.getBlockState(pos.withY(i));
                    if (blockState.isFullCube(dreamWorld, pos.withY(i))) {
                        teleportHeight = i;
                    }
                }
                if (teleportHeight > 319) {
                    teleportHeight = 320;
                }
                teleportHeight++;
                ((ServerPlayerEntity) entity).teleport(dreamWorld, entity.getX(), teleportHeight, entity.getZ(), entity.getYaw(), entity.getPitch());
                if (teleportHeight > 319) {
                    dreamWorld.setBlockState(pos.withY(--teleportHeight), Blocks.STONE.getDefaultState());
                }
            }
        });
    }
}
