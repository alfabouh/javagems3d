package javagems3d.mapping;

public class GameMap implements IGameMap {
    private final String name;

    public GameMap(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
