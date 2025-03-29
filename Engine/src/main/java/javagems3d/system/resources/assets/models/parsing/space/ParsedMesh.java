package javagems3d.system.resources.assets.models.parsing.space;

import javagems3d.system.resources.assets.models.animation.Animation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ParsedMesh {
    private final Map<String, List<ParsedVertexData>> map;
    private List<ParsedMaterialData> materialsData;
    private List<Animation> animations;

    public ParsedMesh() {
        this.map = new HashMap<>();
        this.materialsData = new ArrayList<>();
        this.animations = null;
    }

    public void createRow(String name) {
        this.getMap().put(name, new ArrayList<>());
    }

    public void add(String name, List<ParsedVertexData> data) {
        this.getMap().get(name).addAll(data);
    }

    public void setMaterialsData(@NotNull List<ParsedMaterialData> materialsData) {
        this.materialsData = materialsData;
    }

    public List<Animation> getAnimations() {
        return this.animations;
    }

    public void setAnimations(List<Animation> animations) {
        this.animations = animations;
    }

    public List<ParsedMaterialData> getMaterialsData() {
        return this.materialsData;
    }

    public Collection<List<ParsedVertexData>> values() {
        return this.getMap().values();
    }

    public Map<String, List<ParsedVertexData>> getMap() {
        return this.map;
    }
}
