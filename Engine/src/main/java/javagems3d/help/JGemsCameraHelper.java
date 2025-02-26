package javagems3d.help;

import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.controller.base.IController;
import org.joml.Vector3f;

public abstract class JGemsCameraHelper {
    public static ICamera getCurrentCamera() {
        return JGemsCoreHelper.getScreen().getCamera();
    }

    public static void setCurrentCamera(ICamera camera) {
        JGemsCoreHelper.getScreen().getScene().setCamera(camera);
    }

    public static void enableFreeCamera(IController controller, Vector3f pos, Vector3f rot) {
        JGemsCoreHelper.getScreen().getScene().setCamera(new ControlledCamera(controller, pos, rot));
    }

    public static void enableAttachedCamera(WorldItem worldItem) {
        JGemsCoreHelper.getScreen().getScene().setCamera(JGemsCoreHelper.getSceneWorld().createAttachedCamera(worldItem));
    }

    public static void enableAttachedCamera(SceneEntity abstractSceneEntity) {
        JGemsCoreHelper.getScreen().getScene().setCamera(JGemsCoreHelper.getSceneWorld().createAttachedCamera(abstractSceneEntity));
    }
}
