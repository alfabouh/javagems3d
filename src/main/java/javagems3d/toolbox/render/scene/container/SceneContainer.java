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

package javagems3d.toolbox.render.scene.container;

import javagems3d.engine.system.resources.assets.shaders.RenderPass;
import javagems3d.logger.SystemLogging;
import javagems3d.toolbox.map_sys.save.objects.MapProperties;
import javagems3d.toolbox.map_sys.save.objects.map_prop.FogProp;
import javagems3d.toolbox.map_sys.save.objects.map_prop.SkyProp;
import javagems3d.toolbox.render.scene.items.objects.base.TBoxAbstractObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SceneContainer {
    private final List<TBoxAbstractObject> tBoxAbstractObjects;
    private MapProperties mapProperties;

    public SceneContainer() {
        this.tBoxAbstractObjects = new ArrayList<>();
        this.createMapProperties();
    }

    public void createMapProperties() {
        this.mapProperties = new MapProperties("default_map", new SkyProp(), new FogProp());
    }

    public void removeObject(TBoxAbstractObject scene3DObject) {
        this.getSceneObjects().remove(scene3DObject);
        this.objectPostRender(scene3DObject);
    }

    public void addObject(TBoxAbstractObject scene3DObject) {
        this.getSceneObjects().add(scene3DObject);
        this.objectPreRender(scene3DObject);

        this.getSceneObjects().sort(Comparator.comparingInt(TBoxAbstractObject::getId));
    }

    public void renderForward(float deltaTime) {
        this.getSceneObjects().stream().filter(e -> e.getRenderData().getShaderManager().checkShaderRenderPass(RenderPass.FORWARD)).forEach(e -> e.getRenderData().getObjectRenderer().onRender(this.getMapProperties(), e, deltaTime));
    }

    public void renderTransparent(float deltaTime) {
        this.getSceneObjects().stream().filter(e -> e.getRenderData().getShaderManager().checkShaderRenderPass(RenderPass.TRANSPARENCY)).forEach(e -> e.getRenderData().getObjectRenderer().onRender(this.getMapProperties(), e, deltaTime));
    }

    public void clear() {
        this.getSceneObjects().forEach(this::objectPostRender);
        this.getSceneObjects().clear();
    }

    private void objectPreRender(TBoxAbstractObject scene3DObject) {
        scene3DObject.getRenderData().getObjectRenderer().preRender(scene3DObject);
        SystemLogging.get().getLogManager().log("Object " + scene3DObject + " - Pre-Render!");
    }

    private void objectPostRender(TBoxAbstractObject scene3DObject) {
        scene3DObject.getRenderData().getObjectRenderer().preRender(scene3DObject);
        SystemLogging.get().getLogManager().log("Object " + scene3DObject + " - Post-Render!");
    }

    public <T extends TBoxAbstractObject> Set<T> getObjectsFromContainer(Class<T> clazz) {
        return this.getSceneObjects().stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toSet());
    }

    public MapProperties getMapProperties() {
        return this.mapProperties;
    }

    public void setMapProperties(MapProperties mapProperties) {
        this.mapProperties = mapProperties;
    }

    public List<TBoxAbstractObject> getSceneObjects() {
        return this.tBoxAbstractObjects;
    }
}
