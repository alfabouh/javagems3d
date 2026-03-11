package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Animations;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Skin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GLTF2Scene(String name, List<GLTF2Material> materials, List<GLTF2Skin> skins,
                         List<GLTF2Animations> animations, List<GLTF2Node> nodes) {
    public GLTF2Scene(String name, List<GLTF2Material> materials, @Nullable List<GLTF2Skin> skins, @Nullable List<GLTF2Animations> animations, List<GLTF2Node> nodes) {
        this.name = name;
        this.materials = materials;
        this.skins = skins;
        this.animations = animations;
        this.nodes = nodes;
    }
}