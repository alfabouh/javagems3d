package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

public final class GLTF2ImageTexture {
    private final String name;
    private final String uri;

    public GLTF2ImageTexture(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return this.name;
    }

    public String getUri() {
        return this.uri;
    }
}
