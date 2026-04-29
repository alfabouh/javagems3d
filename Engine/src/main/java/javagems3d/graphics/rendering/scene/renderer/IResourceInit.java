package javagems3d.graphics.rendering.scene.renderer;

public interface IResourceInit {
    void createResources();
    void destroyResources();

    default void recreateResources() {
        this.destroyResources();
        this.createResources();
    }
}
