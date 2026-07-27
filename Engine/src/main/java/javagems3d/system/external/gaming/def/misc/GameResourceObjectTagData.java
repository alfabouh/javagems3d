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

package javagems3d.system.external.gaming.def.misc;
import javagems3d.system.external.mapping.tags.TagsContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.system.external.gaming.def.IAsset;

public class GameResourceObjectTagData implements IAsset {
    private final String name;
    private TagsContainer tagsContainer;

    public GameResourceObjectTagData(@NotNull String name, @Nullable TagsContainer tagsContainer) {
        this.tagsContainer = tagsContainer;
        this.name = name;
    }

    public GameResourceObjectTagData setTagContainer(TagsContainer tagItem) {
        this.tagsContainer = tagItem;
        return this;
    }

    public TagsContainer getTagContainer() {
        return this.tagsContainer;
    }

    @Override
    public String name() {
        return this.name;
    }
}
