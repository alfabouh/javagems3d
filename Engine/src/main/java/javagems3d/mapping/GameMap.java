package javagems3d.mapping;

import javagems3d.physics.entities.kinematic.player.IPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GameMap implements IGameMap {
    private final String name;
    private final String information;
    private final IPlayer player;

    public GameMap(@Nullable IPlayer player, String name, @Nullable String information) {
        this.name = name;
        this.information = information == null ? "***" : information;
        this.player = player;
    }

    @Override
    public @Nullable IPlayer getCurrentPlayer() {
        return this.player;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull String getInformation() {
        return this.information;
    }
}
