package com.github.erdragh.dreamscapes.components;

import com.github.erdragh.dreamscapes.Dreamscapes;
import com.github.erdragh.dreamscapes.events.TeleportEventHandler;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class PlayerComponents implements EntityComponentInitializer {

    private static final ComponentKey<TeleportPositionComponent> ORIGIN_POSITION = ComponentRegistry.getOrCreate(new Identifier(Dreamscapes.MODID, "player_origin_position"), TeleportPositionComponent.class);
    private static final ComponentKey<TeleportPositionComponent> DREAM_POSITION = ComponentRegistry.getOrCreate(new Identifier(Dreamscapes.MODID, "player_dream_position"), TeleportPositionComponent.class);
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ORIGIN_POSITION, player -> new PlayerTeleportComponent(player, player.world.getRegistryKey().getValue()), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(DREAM_POSITION, player -> new PlayerTeleportComponent(player, TeleportEventHandler.DREAM_REGISTRY_KEY.getValue()), RespawnCopyStrategy.ALWAYS_COPY);
    }

    public static TeleportPositionComponent useOriginPosition(Entity provider) {
        return ORIGIN_POSITION.get(provider);
    }
    public static TeleportPositionComponent useDreamPosition(Entity provider) {
        return DREAM_POSITION.get(provider);
    }
}
