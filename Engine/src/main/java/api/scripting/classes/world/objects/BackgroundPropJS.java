package api.scripting.classes.world.objects;

import api.scripting.classes.util.Vec3f;
import api.scripting.classes.world.ObjectJS;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSTypeDoc(description = "Prop in scene world", priority = JSTypeDoc.Priority.MED)
public final class BackgroundPropJS extends PropJS {
    public BackgroundPropJS(@NotNull SceneProp sceneProp) {
        super(sceneProp);
    }

    @Override
    public void remove() {
        JGemsHelper.world().getEnvironment().getSkyBox().getBackground().removeObject(this.getSceneObject());
    }
}