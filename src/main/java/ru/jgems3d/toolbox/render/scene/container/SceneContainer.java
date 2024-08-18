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

package ru.jgems3d.toolbox.render.scene.container;

import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;
import ru.jgems3d.toolbox.map_sys.save.objects.map_prop.FogProp;
import ru.jgems3d.toolbox.map_sys.save.objects.map_prop.SkyProp;
import ru.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SceneContainer {
    private final Set<TBoxScene3DObject> tBoxScene3DObjects;
    private MapProperties mapProperties;

    public SceneContainer() {
        this.tBoxScene3DObjects = new TreeSet<>(Comparator.comparingInt(TBoxScene3DObject::getId));
        this.createMapProperties();
    }

    public void createMapProperties() {
        this.mapProperties = new MapProperties("default_map", new SkyProp(), new FogProp());
    }

    public void removeObject(TBoxScene3DObject scene3DObject) {
        this.getSceneObjects().remove(scene3DObject);
        this.objectPostRender(scene3DObject);
    }

    public void addObject(TBoxScene3DObject scene3DObject) {
        this.getSceneObjects().add(scene3DObject);
        this.objectPreRender(scene3DObject);
    }

    public void render(float deltaTime) {
        this.getSceneObjects().forEach(e -> e.getRenderData().getObjectRenderer().onRender(this.getMapProperties(), e, deltaTime));
    }

    public void clear() {
        this.getSceneObjects().forEach(this::objectPostRender);
        this.getSceneObjects().clear();
    }

    private void objectPreRender(TBoxScene3DObject scene3DObject) {
        scene3DObject.getRenderData().getObjectRenderer().preRender(scene3DObject);
        SystemLogging.get().getLogManager().log("Object " + scene3DObject + " - Pre-Render!");
    }

    private void objectPostRender(TBoxScene3DObject scene3DObject) {
        scene3DObject.getRenderData().getObjectRenderer().preRender(scene3DObject);
        SystemLogging.get().getLogManager().log("Object " + scene3DObject + " - Post-Render!");
    }

    public <T extends TBoxScene3DObject> Set<T> getObjectsFromContainer(Class<T> clazz) {
        return this.getSceneObjects().stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toSet());
    }

    public MapProperties getMapProperties() {
        return this.mapProperties;
    }

    public void setMapProperties(MapProperties mapProperties) {
        this.mapProperties = mapProperties;
    }

    public Set<TBoxScene3DObject> getSceneObjects() {
        return tBoxScene3DObjects;
    }
}
