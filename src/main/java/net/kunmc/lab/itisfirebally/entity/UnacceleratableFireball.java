package net.kunmc.lab.itisfirebally.entity;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftVector;
import org.bukkit.util.Vector;

public class UnacceleratableFireball extends EntityLargeFireball {
    private final Vec3D velocity;

    public UnacceleratableFireball(Location location, Vector velocity) {
        super(EntityTypes.FIREBALL, ((CraftWorld) location.getWorld()).getHandle());
        this.velocity = CraftVector.toNMS(velocity);

        setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        setMot(this.velocity);
    }

    @Override
    public void tick() {
        boolean shouldSetMot = getMot() == velocity;
        super.tick();

        if (shouldSetMot) {
            this.setMot(velocity);
            this.velocityChanged = true;
        }

        MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, this::a);
        if (movingobjectposition != null && movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS) {
            this.preOnHit(movingobjectposition);
        }
    }
}
