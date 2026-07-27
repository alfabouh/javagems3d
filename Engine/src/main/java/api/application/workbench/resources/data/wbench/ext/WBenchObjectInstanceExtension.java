/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
