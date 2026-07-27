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

import javagems3d.system.external.gaming.def.IAsset;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import org.joml.Vector3f;

public class GameResourceMarkerObjectAsset implements IAsset, IWBenchAssetWithTranslationConstraints {
    private final String ID;
    private String modelAssetRelativePath;
    private TagsContainer tagsContainer;
    private TranslationConstraints axisConstraints;
    private Vector3f color;
    private boolean transparent;

    public GameResourceMarkerObjectAsset(String ID, String modelAssetRelativePath, TagsContainer tagsContainer, TranslationConstraints axisConstraints, Vector3f color, boolean transparent) {
        this.ID = ID;
        this.axisConstraints = axisConstraints;
        this.modelAssetRelativePath = modelAssetRelativePath;
        this.tagsContainer = tagsContainer;
        this.color = color;
        this.transparent = transparent;
    }

    public String getID() {
        return this.ID;
    }

    public String getModelAssetRelativePath() {
        return this.modelAssetRelativePath;
    }

    public GameResourceMarkerObjectAsset setModelAssetRelativePath(String modelAssetRelativePath) {
        this.modelAssetRelativePath = modelAssetRelativePath;
        return this;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public GameResourceMarkerObjectAsset setTagsContainer(TagsContainer tagsContainer) {
        this.tagsContainer = tagsContainer;
        return this;
    }

    public TranslationConstraints getAxisConstraints() {
        return this.axisConstraints;
    }

    public GameResourceMarkerObjectAsset setAxisConstraints(TranslationConstraints axisConstraints) {
        this.axisConstraints = axisConstraints;
        return this;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public GameResourceMarkerObjectAsset setColor(Vector3f color) {
        this.color = color;
        return this;
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public GameResourceMarkerObjectAsset setTransparent(boolean transparent) {
        this.transparent = transparent;
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