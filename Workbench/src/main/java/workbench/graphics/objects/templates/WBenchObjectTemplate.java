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

package workbench.graphics.objects.templates;

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.graphics.objects.WBenchCommonObject;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchObjectTemplate extends WBenchTemplate {
    protected transient String modelDef;
    protected MeshGroup meshGroup;
    protected RenderAttributes renderAttributes;
    protected TagsContainer tagsContainer;
    protected TranslationConstraints translationConstraints;

    public WBenchObjectTemplate(@NotNull WBenchObject.ID objectId, MeshGroup meshGroup, RenderAttributes renderAttributes, TagsContainer tagsContainer, TranslationConstraints translationConstraints) {
        super(objectId);
        this.meshGroup = meshGroup;
        this.renderAttributes = renderAttributes;
        this.tagsContainer = tagsContainer;
        this.translationConstraints = translationConstraints;
        this.modelDef = "NULL";
    }

    public WBenchObject<?> createObject(@NotNull WBenchWorld world, @Nullable TagsContainer overridedTagsContainer) {
        return new WBenchCommonObject(world, this, overridedTagsContainer);
    }

    public String getModelDef() {
        return this.modelDef;
    }

    public WBenchObjectTemplate setModelDef(String modelDef) {
        this.modelDef = modelDef;
        return this;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public MeshGroup getMeshGroup() {
        return this.meshGroup == null ? ResourceManager.DEFAULT_CUBE_MESHGROUP() : this.meshGroup;
    }

    public RenderAttributes getRenderAttributes() {
        return this.renderAttributes;
    }
}