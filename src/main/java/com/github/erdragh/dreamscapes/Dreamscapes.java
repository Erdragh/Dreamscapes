package com.github.erdragh.dreamscapes;

import com.github.erdragh.dreamscapes.effects.DreamStatusEffect;
import com.github.erdragh.dreamscapes.features.ExampleFeatureConfig;
import com.github.erdragh.dreamscapes.features.PillarFeature;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.Arrays;
import java.util.List;

public class Dreamscapes implements ModInitializer {

  public static final String MODID = "dreamscapes";

  public static final Identifier PILLAR_FEATURE_ID = new Identifier(MODID, "pillar_feature");
  public static final StatusEffect DREAM_EFFECT = new DreamStatusEffect();
  private static final ConfiguredFeature<?, ?> OVERWORLD_WOOL_ORE_CONFIGURED_FEATURE =
          new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, Blocks.WHITE_WOOL.getDefaultState(), 9));
  public static Feature<ExampleFeatureConfig> PILLAR_FEATURE = new PillarFeature(ExampleFeatureConfig.CODEC);
  public static ConfiguredFeature<ExampleFeatureConfig, PillarFeature> PILLAR_FEATURE_CONFIGURED = new ConfiguredFeature<>(
          (PillarFeature) PILLAR_FEATURE,
          new ExampleFeatureConfig(10, new Identifier("minecraft", "netherite_block"))
  );
  public static PlacedFeature PILLAR_FEATURE_PLACED = new PlacedFeature(
          RegistryEntry.of(PILLAR_FEATURE_CONFIGURED),
          List.of(SquarePlacementModifier.of())
  );
  public static PlacedFeature OVERWORLD_WOOL_ORE_PLACED_FEATURE = new PlacedFeature(
          RegistryEntry.of(OVERWORLD_WOOL_ORE_CONFIGURED_FEATURE),
          Arrays.asList(
                  CountPlacementModifier.of(20),
                  SquarePlacementModifier.of(),
                  HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(64))
          )
  );

  @Override
  public void onInitialize() {

    Registry.register(Registry.FEATURE, PILLAR_FEATURE_ID, PILLAR_FEATURE);
    Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, PILLAR_FEATURE_ID, PILLAR_FEATURE_CONFIGURED);
    Registry.register(BuiltinRegistries.PLACED_FEATURE, PILLAR_FEATURE_ID, PILLAR_FEATURE_PLACED);

    BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Feature.VEGETAL_DECORATION,
            RegistryKey.of(Registry.PLACED_FEATURE_KEY, PILLAR_FEATURE_ID)
    );

    Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(MODID, "overworld_wool_ore"), OVERWORLD_WOOL_ORE_CONFIGURED_FEATURE);
    Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(MODID, "overworld_wool_ore"), OVERWORLD_WOOL_ORE_PLACED_FEATURE);

    BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(MODID, "overworld_wool_ore")));

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
