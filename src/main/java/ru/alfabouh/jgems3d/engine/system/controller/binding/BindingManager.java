package ru.alfabouh.jgems3d.engine.system.controller.binding;

import ru.alfabouh.jgems3d.engine.system.controller.components.Key;

import java.util.HashSet;
import java.util.Set;

public abstract class BindingManager {
    private final Set<Binding> bindingSet;

    public BindingManager() {
        this.bindingSet = new HashSet<>();
    }

    public void createBinding(Key key, String description) {
        this.createBinding(Binding.createBinding(key, description));
    }

    public void createBinding(Binding binding) {
        this.getBindingSet().add(binding);
    }

    public Set<Binding> getBindingSet() {
        return this.bindingSet;
    }
}
