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

import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.WBenchObject;
import javagems3d.system.service.files.VirtualObjectsFolder;

public abstract class WBenchTemplate implements VirtualObjectsFolder.ObjectWithName {
    protected final WBenchObject.ID objectId;

    public WBenchTemplate(@NotNull WBenchObject.ID objectId) {
        this.objectId = objectId;
    }

    public WBenchObject.ID getObjectId() {
        return this.objectId;
    }

    @Override
    public String name() {
        return this.getObjectId().nameId();
    }
}
