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

package javagems3d.system.controller.binding;

import javagems3d.system.controller.components.Key;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class BindingManager {
    private final Set<Binding> bindingSet;

    public BindingManager() {
        this.bindingSet = new LinkedHashSet<>();
    }

    public abstract Key keyMoveLeft();

    public abstract Key keyMoveRight();

    public abstract Key keyMoveForward();

    public abstract Key keyMoveBackward();

    public abstract Key keyMoveUp();

    public abstract Key keyMoveDown();

    @SuppressWarnings("all")
    public void removeBinding(Key key) {
        this.getBindingSet().remove(key);
    }

    @SuppressWarnings("all")
    public void removeBinding(int keyCode) {
        this.getBindingSet().remove(keyCode);
    }

    public void addBinding(Key key) {
        this.addBinding(Binding.createBinding(key, ""));
    }

    public void addBinding(Key key, String description) {
        this.addBinding(Binding.createBinding(key, description));
    }

    public void addBinding(Binding binding) {
        this.getBindingSet().add(binding);
    }

    public Set<Binding> getBindingSet() {
        return this.bindingSet;
    }
}
