package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.container;

import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveMapProperties;
import ru.alfabouh.jgems3d.proxy.mapsys.toolbox.file.save.objects.SaveModeledObject;

import java.util.HashSet;
import java.util.Set;

public final class SaveContainer {
    private final SaveMapProperties saveMapProperties;
    private Set<SaveModeledObject> saveObjectSet;

    public SaveContainer(SaveMapProperties saveMapProperties) {
        this.saveMapProperties = saveMapProperties;
        this.saveObjectSet = new HashSet<>();
    }

    public void addSaveObject(SaveModeledObject saveObject) {
        this.getSaveObjectSet().add(saveObject);
    }

    public void setSaveObjectSet(Set<SaveModeledObject> saveObjectSet) {
        this.saveObjectSet = saveObjectSet;
    }

    public Set<SaveModeledObject> getSaveObjectSet() {
        return this.saveObjectSet;
    }

    public SaveMapProperties getSaveMapProperties() {
        return this.saveMapProperties;
    }
}
