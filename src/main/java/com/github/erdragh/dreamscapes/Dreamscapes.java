package com.github.erdragh.dreamscapes;

import com.github.erdragh.dreamscapes.effects.DreamStatusEffect;
import com.github.erdragh.dreamscapes.events.TeleportEventHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Dreamscapes implements ModInitializer {

  public static final String MODID = "dreamscapes";

  public static final StatusEffect DREAM_EFFECT = new DreamStatusEffect();

  @Override
  public void onInitialize() {

    Registry.register(Registries.STATUS_EFFECT, new Identifier(MODID, "can_dream"), DREAM_EFFECT);

    EntitySleepEvents.START_SLEEPING.register(TeleportEventHandler::handleSleep);

    ServerLivingEntityEvents.ALLOW_DAMAGE.register(TeleportEventHandler::handleDamage);
  }
}
