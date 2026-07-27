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

package javagems3d.system.service.files.source;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class JGemsStringSource implements ISource {
    private final String string;
    private final ISource.Source source;

    public JGemsStringSource(JGemsPathSource pathSource) {
        this.string = pathSource.toString();
        this.source = pathSource.getSource();
    }

    public JGemsStringSource(@NotNull String string, @NotNull ISource.Source source) {
        this.string = string;
        this.source = source;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JGemsStringSource that)) {
            return false;
        }
        return that.getSource().equals(this.getSource()) && that.getString().equals(this.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.string, this.source);
    }

    @Override
    public String toString() {
        return this.getString();
    }

    public String getString() {
        return this.string;
    }

    public ISource.Source getSource() {
        return this.source;
    }
}