package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

public final class GLTF2AccessorData {
    private final int bufferView;
    private final int count;
    private final int componentType;
    private final String typeStr;

    public GLTF2AccessorData(int bufferView, int count, int componentType, String typeStr) {
        this.bufferView = bufferView;
        this.count = count;
        this.componentType = componentType;
        this.typeStr = typeStr;
    }

    public int getBufferView() {
        return this.bufferView;
    }

    public int getCount() {
        return this.count;
    }

    public int getComponentType() {
        return this.componentType;
    }

    public String getTypeStr() {
        return this.typeStr;
    }
}
