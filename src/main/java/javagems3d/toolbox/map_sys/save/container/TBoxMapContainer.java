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

package javagems3d.toolbox.map_sys.save.container;

import javagems3d.toolbox.map_sys.save.objects.MapProperties;
import javagems3d.toolbox.map_sys.save.objects.SaveObject;

import java.util.HashSet;
import java.util.Set;

public final class TBoxMapContainer {
    private final MapProperties mapProperties;
    private Set<SaveObject> saveObjectsSet;

    public TBoxMapContainer(MapProperties mapProperties) {
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
