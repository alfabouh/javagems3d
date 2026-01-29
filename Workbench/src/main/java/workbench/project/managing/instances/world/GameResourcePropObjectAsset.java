package workbench.project.managing.instances.world;

import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;

public class GameResourcePropObjectAsset extends GameResourceWorldObjectAsset {
    public GameResourcePropObjectAsset(String ID, String modelAssetRelativePath, TagsContainer tagsContainer, TranslationConstraints axisConstraints) {
        super(ID, modelAssetRelativePath, tagsContainer, axisConstraints);
    }
}