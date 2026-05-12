package api.application.workbench.resources.data.wbench.ext;

import api.application.workbench.resources.data.wbench.WBenchData;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.external.gaming.def.misc.GameResourceModelAsset;
import javagems3d.system.external.gaming.def.misc.GameResourceSoundAsset;
import javagems3d.system.external.gaming.def.misc.GameResourceTextureAsset;
import javagems3d.system.external.gaming.def.misc.set.GameResourcesSet;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class WBenchObjectInstanceExtension {
    private final ContextData contextData;

    public WBenchObjectInstanceExtension(@NotNull ContextData contextData) {
        this.contextData = contextData;
    }

    public abstract Vector3f textInMenuColor();
    public abstract int orderInItemsList();

    public abstract void onSpawnExt(IRenderWorld world);
    public abstract void onDestroyExt(IRenderWorld world);
    public abstract void onUpdateExt(IRenderWorld world);

    public void onApplySnapshot(TagsContainer tagsContainer, SceneProp sceneObject) {
    }

    public void onObjectCloned(TagsContainer tagsContainer, SceneProp sceneObject) {
    }

    public void onTagsContainerAnyTagModified(TagsContainer tagsContainer, SceneProp sceneObject) {
    }

    public boolean shouldMarkerBeFullLighted() {
        return false;
    }

    public @Nullable Vector3f returnNewMarkerColor() {
        return null;
    }

    public String overrideItemName(String original) {
        return original;
    }

    public ContextData getContextData() {
        return this.contextData;
    }

    public record ContextData(WBenchData.ObjectType objectType,
                              TranslationConstraints translationConstraints,
                              Supplier<TagsContainer> tagsContainer,
                              SceneProp sceneProp,
                              boolean isEnabledBackgroundScene,
                              DebugLinesDrawer debugLinesDrawer,
                              UtilityFunctions utilityFunctions) {
    }

    public record UtilityFunctions(GameResourcesSet gameResourcesSet,
                                   Function<String, GameResourceTextureAsset> extractTextureFromLocalCache,
                                   Function<String, GameResourceModelAsset> extractModelFromLocalCache,
                                   Function<String, GameResourceSoundAsset> extractSoundFromLocalCache) {
    }
}
