package com.github.erdragh.dreamscapes.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PlayerTeleportComponent implements TeleportPositionComponent {

    public PlayerTeleportComponent(PlayerEntity player, Identifier worldIdentifier) {
        this.position = player.getPos();
        this.worldIdentifier = worldIdentifier;
    }

    private Vec3d position;
    private boolean hasTeleported;
    private Identifier worldIdentifier;

    @Override
    public void readFromNbt(NbtCompound tag) {
        var x = tag.getDouble("x");
        var y = tag.getDouble("y");
        var z = tag.getDouble("z");

        this.hasTeleported = tag.getBoolean("has_teleported");

        this.position = new Vec3d(x, y, z);

        this.worldIdentifier = Identifier.tryParse(tag.getString("world_identifier"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        System.out.println(position);
        System.out.println(worldIdentifier);

        tag.putDouble("x", this.position.getX());
        tag.putDouble("y", this.position.getY());
        tag.putDouble("z", this.position.getZ());

        tag.putBoolean("has_teleported", hasTeleported);

        tag.putString("world_identifier", this.worldIdentifier.toString());
    }

    @Override
    public Vec3d getPosition() {
        return this.position;
    }

    @Override
    public boolean hasTeleported() {
        return this.hasTeleported;
    }

    @Override
    public Identifier getWorldIdentifier() {
        return this.worldIdentifier;
    }

    @Override
    public void setPosition(Vec3d pos) {
        this.position = pos;
    }

    @Override
    public void setTeleported() {
        this.hasTeleported = true;
    }

    @Override
    public void setWorldIdentifier(Identifier identifier) {
        this.worldIdentifier = identifier;
    }
}
