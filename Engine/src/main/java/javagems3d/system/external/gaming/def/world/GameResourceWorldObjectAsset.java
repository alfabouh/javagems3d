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

package javagems3d.system.external.gaming.def.world;

import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.gaming.def.IAsset;

public abstract class GameResourceWorldObjectAsset implements IAsset, IWBenchAssetWithTranslationConstraints {
    private final String ID;
    private String modelAssetRelativePath;
    private TagsContainer tagsContainer;
    private TranslationConstraints axisConstraints;
    private RenderProperties renderProperties;

    public GameResourceWorldObjectAsset(String ID, String modelAssetRelativePath, TagsContainer tagsContainer, RenderProperties renderProperties, TranslationConstraints axisConstraints) {
        this.ID = ID;
        this.modelAssetRelativePath = modelAssetRelativePath;
        this.tagsContainer = tagsContainer;
        this.renderProperties = renderProperties;
        this.axisConstraints = axisConstraints;
    }

    public String getID() {
        return this.ID;
    }

    public String getModelAssetRelativePath() {
        return this.modelAssetRelativePath;
    }

    public GameResourceWorldObjectAsset setModelAssetRelativePath(String modelAssetRelativePath) {
        this.modelAssetRelativePath = modelAssetRelativePath;
        return this;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public GameResourceWorldObjectAsset setTagsContainer(TagsContainer tagsContainer) {
        this.tagsContainer = tagsContainer;
        return this;
    }

    public TranslationConstraints getAxisConstraints() {
        return this.axisConstraints;
    }

    public GameResourceWorldObjectAsset setAxisConstraints(TranslationConstraints axisConstraints) {
        this.axisConstraints = axisConstraints;
        return this;
    }

    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }

    public GameResourceWorldObjectAsset setRenderProperties(RenderProperties renderProperties) {
        this.renderProperties = renderProperties;
        return this;
    }

    @Override
    public String toString() {
        return this.getID();
    }

    @Override
    public String name() {
        return this.getID();
    }
}