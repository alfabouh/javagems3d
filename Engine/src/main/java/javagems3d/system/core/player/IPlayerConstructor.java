package javagems3d.system.core.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.service.collections.Pair;

@FunctionalInterface
public interface IPlayerConstructor {
    Pair<@NotNull IPlayer, @Nullable EntityRenderData> constructPlayer(PhysicsWorld world, Vector3f startPos, Vector3f startRot);
}
