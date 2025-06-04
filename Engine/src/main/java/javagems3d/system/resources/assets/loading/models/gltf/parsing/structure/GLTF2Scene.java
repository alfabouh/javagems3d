package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Animations;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Skin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class GLTF2Scene {
    private final String name;
    private final List<GLTF2Material> materials;
    private final List<GLTF2Skin> skins;
    private final List<GLTF2Animations> animations;
    private final List<GLTF2Node> nodes;

    public GLTF2Scene(String name, List<GLTF2Material> materials, @Nullable List<GLTF2Skin> skins, @Nullable List<GLTF2Animations> animations, List<GLTF2Node> nodes) {
        this.name = name;
        this.materials = materials;
        this.skins = skins;
        this.animations = animations;
        this.nodes = nodes;
    }

    public String getName() {
        return this.name;
    }

    public List<GLTF2Material> getMaterials() {
        return this.materials;
    }

    public List<GLTF2Skin> getSkins() {
        return this.skins;
    }

    public List<GLTF2Animations> getAnimations() {
        return this.animations;
    }

    public List<GLTF2Node> getNodes() {
        return this.nodes;
    }
}