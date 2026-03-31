package javagems3d.system.external.gaming.def.world;

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;

public class GameResourceEntityObjectAsset extends GameResourceWorldObjectAsset {
    public GameResourceEntityObjectAsset(String ID, String modelAssetRelativePath, TagsContainer tagsContainer, RenderProperties renderProperties, TranslationConstraints axisConstraints) {
        super(ID, modelAssetRelativePath, tagsContainer, renderProperties, axisConstraints);
    }
}
