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

package javagems3d.system.external.mapping.tags;

import javagems3d.system.external.mapping.tags.items.TagItem;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;

public final class Tag <T extends TagItem> implements ICopyable<Tag<?>> {
    private final T item;
    private final TagID tagID;

    public Tag(@NotNull TagID tagID, @NotNull T defaultItem) {
        this.item = defaultItem;
        this.tagID = tagID;
    }

    public static <E extends TagItem> Tag<E> create(TagID tagID, @NotNull E defaultItem) {
        return new Tag<>(tagID, defaultItem);
    }

    @SuppressWarnings("all")
    public <R extends TagItem> R getTagItemUnsafeCast() {
        return (R) this.getTagItem();
    }

    public boolean check(Class<? extends TagItem> clazz) {
        return this.getTagItem().getClass().isAssignableFrom(clazz);
    }

    public TagID getTagID() {
        return this.tagID;
    }

    public T getTagItem() {
        return this.item;
    }

    @Override
    @SuppressWarnings("all")
    public Tag<T> copy() {
        return new Tag<T>(this.getTagID(), (T) this.getTagItem().copy());
    }
}
