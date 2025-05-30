package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

public final class GLTF2Asset {
    private final String generator;
    private final String version;

    public GLTF2Asset(String generator, String version) {
        this.generator = generator;
        this.version = version;
    }

    public String getGenerator() {
        return this.generator;
    }

    public String getVersion() {
        return this.version;
    }
}
