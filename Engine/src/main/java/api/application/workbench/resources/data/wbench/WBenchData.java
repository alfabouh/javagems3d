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

package api.application.workbench.resources.data.wbench;

import api.application.workbench.resources.data.wbench.ext.WBenchObjectInstanceExtension;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.mapping.tags.items.TagItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class WBenchData {
    private final TagsContainer tagsContainer;
    private final TranslationConstraints translationConstraints;

    public WBenchData(TagsContainer tagsContainer, TranslationConstraints translationConstraints) {
        this.tagsContainer = tagsContainer;
        this.translationConstraints = translationConstraints;
    }
    @SuppressWarnings("all")
    public WBenchData addTags(Tag<? extends TagItem>... tags) {
        for (Tag<? extends TagItem> tag : tags) {
            this.addTag(tag);
        }
        return this;
    }

    public WBenchData addTag(Tag<? extends TagItem> tag) {
        this.getTagsContainer().addTag(tag);
        return this;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }

    public enum ObjectType {
        MARKER,
        PROP,
        ENTITY
    }
}
