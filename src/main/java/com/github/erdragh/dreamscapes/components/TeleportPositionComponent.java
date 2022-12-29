package com.github.erdragh.dreamscapes.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface TeleportPositionComponent extends Component {
    Vec3d getPosition();
    boolean hasTeleported();
    Identifier getWorldIdentifier();

    void setPosition(Vec3d pos);
    void setTeleported();
    void setWorldIdentifier(Identifier identifier);
}
