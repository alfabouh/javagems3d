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

import javagems3d.system.service.files.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class JGemsPathSource implements ISource {
    private final JGemsPath jGemsPath;
    private final ISource.Source source;

    public JGemsPathSource(JGemsStringSource stringSource) {
        this.jGemsPath = new JGemsPath(stringSource.toString());
        this.source = stringSource.getSource();
    }

    public JGemsPathSource(@NotNull String fullPath, @NotNull ISource.Source source) {
        this.source = source;
        this.jGemsPath = new JGemsPath(fullPath);
    }
    public JGemsPathSource(@NotNull JGemsPath jGemsPath, @NotNull ISource.Source source) {
        this.source = source;
        this.jGemsPath = jGemsPath;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JGemsPathSource that)) {
            return false;
        }
        return that.getSource().equals(this.getSource()) && that.getPath().equals(this.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPath(), this.source);
    }

    @Override
    public String toString() {
        return this.getPath().toString();
    }

    public JGemsPath getPath() {
        return this.jGemsPath;
    }

    public ISource.Source getSource() {
        return this.source;
    }
}
