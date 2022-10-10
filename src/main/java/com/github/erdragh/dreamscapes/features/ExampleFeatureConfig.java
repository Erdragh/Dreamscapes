package com.github.erdragh.dreamscapes.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;

public record ExampleFeatureConfig(int number, Identifier blockID) implements FeatureConfig {
  public static Codec<ExampleFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
          Codecs.POSITIVE_INT.fieldOf("number").forGetter(ExampleFeatureConfig::number),
          Identifier.CODEC.fieldOf("blockID").forGetter(ExampleFeatureConfig::blockID)
  ).apply(instance, ExampleFeatureConfig::new));

  public ExampleFeatureConfig(int number, Identifier blockID) {
    this.blockID = blockID;
    this.number = number;
  }

  public int number() {
    return number;
  }

  public Identifier blockID() {
    return blockID;
  }
}
