package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.container;

import ru.alfabouh.jgems3d.toolbox.render.scene.container.MapProperties;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveObject;

import java.util.HashSet;
import java.util.Set;

public final class SaveContainer {
    private final MapProperties mapProperties;
    private Set<SaveObject> saveObjectsSet;

    public SaveContainer(MapProperties mapProperties) {
        this.mapProperties = mapProperties;
        this.saveObjectsSet = new HashSet<>();
    }

    public void addSaveObject(SaveObject saveObject) {
        this.getSaveObjectsSet().add(saveObject);
    }

    public void setSaveObjectSet(Set<SaveObject> saveObjectSet) {
        this.saveObjectsSet = saveObjectSet;
    }

    public Set<SaveObject> getSaveObjectsSet() {
        return this.saveObjectsSet;
    }

    public MapProperties getSaveMapProperties() {
        return this.mapProperties;
    }
}
