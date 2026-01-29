package workbench.project.managing.instances.world;

import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;

public class GameResourceEntityObjectAsset extends GameResourceWorldObjectAsset {
    public GameResourceEntityObjectAsset(String ID, String modelAssetRelativePath, TagsContainer tagsContainer, TranslationConstraints axisConstraints) {
        super(ID, modelAssetRelativePath, tagsContainer, axisConstraints);
    }
}
