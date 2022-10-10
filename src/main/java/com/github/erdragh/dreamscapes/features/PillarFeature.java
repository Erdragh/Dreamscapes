package com.github.erdragh.dreamscapes.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class PillarFeature extends Feature<ExampleFeatureConfig> {
  public PillarFeature(Codec<ExampleFeatureConfig> configCodec) {
    super(configCodec);
  }

  @Override
  public boolean generate(FeatureContext<ExampleFeatureConfig> context) {
    StructureWorldAccess world = context.getWorld();
    BlockPos origin = context.getOrigin();

    Random random = context.getRandom();
    ExampleFeatureConfig config = context.getConfig();

    int number = config.number();
    Identifier blockID = config.blockID();

    BlockState blockState = Registry.BLOCK.get(blockID).getDefaultState();

    if (blockState == null) throw new IllegalStateException(blockID + " could not be parsed to a valid block identifier!");

    BlockPos testPos = new BlockPos(origin);

    for (int y = 0; y < world.getHeight(); y++) {
      testPos = testPos.up();

      if (world.getBlockState(testPos).isIn(BlockTags.DIRT)) {
        if (world.getBlockState(testPos.up()).isOf(Blocks.AIR)) {
          for (int i = 0; i < number; i++) {
            world.setBlockState(testPos, blockState, 0x10);
            testPos = testPos.up();

            if (testPos.getY() >= world.getTopY()) break;
          }
          return true;
        }
      }
    }

    return false;
  }
}

