/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.controller.binding;

import javagems3d.system.controller.components.Key;

import java.util.HashSet;
import java.util.Set;

public abstract class BindingManager {
    private final Set<Binding> bindingSet;

    public BindingManager() {
        this.bindingSet = new HashSet<>();
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
