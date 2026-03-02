package javagems3d.system.external.mapping;

import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IGameMap {
    @NotNull String getName();
    @NotNull String getInformation();
    @Nullable IPlayer getCurrentPlayer();

    @FunctionalInterface
    interface IPlayerConstructor {
        Pair<@NotNull IPlayer, @Nullable EntityRenderData> constructPlayer(PhysicsWorld world);
    }
}
