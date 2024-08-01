package ru.jgems3d.engine.graphics.opengl.rendering.items.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.jgems3d.engine.physics.entities.properties.controller.IControllable;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.exceptions.JGemsException;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSceneEntity implements IModeledSceneObject, IWorldObject, IWorldTicked {
    private final List<Light> lightList;
    private final SceneWorld sceneWorld;
    private final WorldItem worldItem;
    private final RenderEntityData renderData;
    protected Model<Format3D> model;
    protected Vector3f prevRenderPosition;
    protected Vector3f prevRenderRotation;
    protected Vector3f renderPosition;
    protected Vector3f renderRotation;
    private InterpolationPoints currentPositionInterpolation;
    private InterpolationPoints currentRotationInterpolation;
    private boolean isVisible;
    private boolean isDead;

    public AbstractSceneEntity(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull RenderEntityData renderData) {
        this.lightList = new ArrayList<>();

        this.worldItem = worldItem;
        this.renderPosition = new Vector3f(worldItem.getPosition());
        this.renderRotation = new Vector3f(worldItem.getRotation());
        this.prevRenderPosition = new Vector3f(this.renderPosition);
        this.prevRenderRotation = new Vector3f(this.renderRotation);
        this.sceneWorld = sceneWorld;
        this.renderData = renderData;
        this.isVisible = true;
        this.currentPositionInterpolation = new InterpolationPoints(this.getPrevRenderPosition(), this.getFixedPosition());
        this.currentRotationInterpolation = new InterpolationPoints(this.getPrevRenderRotation(), this.getFixedRotation());
    }

    public void setModel(Model<Format3D> model) {
        this.model = model;
    }

    protected void initModel() {
        this.setModel(new Model<>(new Format3D(), this.getRenderData().getMeshDataGroup()));
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        JGemsHelper.getLogger().log("[ " + this.getWorldItem().toString() + " ]" + " - PreRender");
        if (this.hasRender()) {
            if (this.getRenderData().getEntityModelConstructor() != null) {
                this.setModel(new Model<>(new Format3D(), this.getRenderData().getEntityModelConstructor().constructMeshDataGroup(this.getWorldItem())));
            } else {
                this.initModel();
            }
            this.renderFabric().onStartRender(this);
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        JGemsHelper.getLogger().log("[ " + this.getWorldItem().toString() + " ]" + " - PostRender");
        if (this.hasRender()) {
            this.renderFabric().onStopRender(this);
        }
        this.clearLights();
    }

    public void clearLights() {
        Iterator<Light> lightIterator = this.getLightsList().iterator();
        while (lightIterator.hasNext()) {
            Light l = lightIterator.next();
            l.stop();
            this.onRemoveLight(l);
            lightIterator.remove();
        }
    }

    public void addLight(Light light) {
        this.getLightsList().add(light);
        light.start();
        this.onAddLight(light);
    }

    public void removeLight(Light light) {
        if (!this.getLightsList().contains(light)) {
            throw new JGemsException("Couldn't remove light. Entity doesn't keep it. " + this);
        }
        this.getLightsList().remove(light);
        light.stop();
        this.onRemoveLight(light);
    }

    protected void onAddLight(Light light) {
        JGemsHelper.getLogger().log("Add light to: " + this.getWorldItem().getItemName());
    }

    protected void onRemoveLight(Light light) {
        JGemsHelper.getLogger().log("Removed light from: " + this.getWorldItem().getItemName());
    }

    public void setDead() {
        this.isDead = true;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getWorldItem().isDead()) {
            this.setDead();
        }
    }

    public RenderSphere calcRenderSphere() {
        if (!this.hasRender() || !this.hasModel()) {
            return null;
        }
        return new RenderSphere(JGemsHelper.calcDistanceToMostFarPoint(this.getModel().getMeshDataGroup(), this.getScale()), this.getRenderPosition());
    }

    @Override
    public boolean canBeCulled() {
        return true;
    }

    public void updateRenderTranslation() {
        if (this.hasModel()) {
            Model<Format3D> model = this.getModel();
            model.getFormat().setScaling(new Vector3f(this.getScale()));
            model.getFormat().setPosition(this.getRenderPosition());
            model.getFormat().setRotation(this.getRenderRotation());
        }
    }

    public void updateRenderPos(float partialTicks) {
        Vector3f pos = this.getFixedPosition();
        Vector3f rot = this.getFixedRotation();
        if (this.getMeshRenderData().getRenderAttributes().isShouldInterpolateMovement()) {
            this.renderPosition.set(this.getCurrentPosState().interpolatedPoint(partialTicks));
            if (this.isEntityUnderUserControl()) {
                this.renderRotation.set(rot);
            } else {
                Vector3f newRotation = new Vector3f();
                Quaternionf result = this.getQuaternionInterpolated(partialTicks);
                result.getEulerAnglesXYZ(newRotation);
                this.renderRotation.set(new Vector3f(newRotation.x, newRotation.y, newRotation.z));
            }
        } else {
            this.renderPosition.set(pos);
            this.renderRotation.set(rot);
        }
        this.adjustLightsTranslation(this.getRenderPosition(), new Vector3f(0.0f));
    }

    private Quaternionf getQuaternionInterpolated(float partialTicks) {
        Quaternionf start = new Quaternionf();
        Quaternionf end = new Quaternionf();

        start.rotateXYZ(this.getCurrentRotState().getStartPoint().x, this.getCurrentRotState().getStartPoint().y, this.getCurrentRotState().getStartPoint().z);
        end.rotateXYZ(this.getCurrentRotState().getEndPoint().x, this.getCurrentRotState().getEndPoint().y, this.getCurrentRotState().getEndPoint().z);

        Quaternionf res = new Quaternionf();
        end.slerp(start, partialTicks, res);
        return res;
    }

    public void refreshInterpolatingState() {
        this.currentPositionInterpolation = new InterpolationPoints(this.getWorldItem().getPosition(), this.currentPositionInterpolation.getStartPoint());
        this.currentRotationInterpolation = new InterpolationPoints(this.getWorldItem().getRotation(), this.currentRotationInterpolation.getStartPoint());
    }

    public void setPrevPos(Vector3f vector3f) {
        this.prevRenderPosition.set(new Vector3f(vector3f));
    }

    public void setPrevRot(Vector3f vector3f) {
        this.prevRenderRotation.set(new Vector3f(vector3f));
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isEntityUnderUserControl() {
        return this.getWorldItem() instanceof IControllable && ((IControllable) this.getWorldItem()).isValidController();
    }

    public List<Light> getLightsList() {
        return this.lightList;
    }

    public JGemsShaderManager getShaderManager() {
        return this.getMeshRenderData().getShaderManager();
    }

    public Vector3f getScale() {
        return this.getWorldItem().getScaling();
    }

    private InterpolationPoints getCurrentPosState() {
        return this.currentPositionInterpolation;
    }

    private InterpolationPoints getCurrentRotState() {
        return this.currentRotationInterpolation;
    }

    protected Vector3f getFixedPosition() {
        return this.getWorldItem().getPosition();
    }

    protected Vector3f getFixedRotation() {
        return this.getWorldItem().getRotation();
    }

    public Vector3f getPrevRenderRotation() {
        return new Vector3f(this.prevRenderRotation);
    }

    public Vector3f getPrevRenderPosition() {
        return new Vector3f(this.prevRenderPosition);
    }

    public Vector3f getRenderPosition() {
        return new Vector3f(this.renderPosition);
    }

    public Vector3f getRenderRotation() {
        return new Vector3f(this.renderRotation);
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    @Override
    public MeshRenderData getMeshRenderData() {
        return this.getRenderData().getMeshRenderData();
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public IRenderObjectFabric renderFabric() {
        return this.getRenderData().getRenderFabric();
    }

    public RenderEntityData getRenderData() {
        return this.renderData;
    }

    public WorldItem getWorldItem() {
        return this.worldItem;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public static final class InterpolationPoints {
        private final Vector3f startPoint;
        private final Vector3f endPoint;

        public InterpolationPoints(Vector3f startPoint, Vector3f endPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public Vector3f interpolatedPoint(float partialTicks) {
            Vector3f newP = new Vector3f(this.getEndPoint());
            return newP.lerp(this.getStartPoint(), partialTicks);
        }

        public Vector3f getStartPoint() {
            return this.startPoint;
        }

        public Vector3f getEndPoint() {
            return this.endPoint;
        }
    }
}