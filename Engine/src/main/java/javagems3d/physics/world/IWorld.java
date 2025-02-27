package javagems3d.physics.world;

public interface IWorld {
    void onWorldStart();
    void onWorldUpdate();
    void onWorldEnd();
    int getTicks();
}