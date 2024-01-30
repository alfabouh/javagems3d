package ru.BouH.engine.game.resources.assets.models.mesh;

import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.game.resources.cache.ICached;

import java.util.ArrayList;
import java.util.List;

public class MeshDataGroup implements ICached {
    private final List<ModelNode> modelNodeList;

    public MeshDataGroup() {
        this.modelNodeList = new ArrayList<>();
    }

    public MeshDataGroup(ModelNode modelNode) {
        this.modelNodeList = new ArrayList<>();
        this.putNode(modelNode);
    }

    public MeshDataGroup(Mesh mesh) {
        this(new ModelNode(mesh));
    }

    public void putNode(ModelNode modelNode) {
        this.modelNodeList.add(modelNode);
    }

    public List<ModelNode> getModelNodeList() {
        return this.modelNodeList;
    }

    @Override
    public void onCleaningCache(GameCache gameCache) {
        this.getModelNodeList().forEach(ModelNode::cleanMesh);
    }
}
