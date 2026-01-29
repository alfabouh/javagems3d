package api.scripting.classes.world.objects;

import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.help.JGemsHelper;
import org.jetbrains.annotations.NotNull;

@JSTypeDoc(description = "Prop in scenes world", priority = JSTypeDoc.Priority.MED)
public final class BackgroundPropJS extends PropJS {
    public BackgroundPropJS(@NotNull SceneProp sceneProp) {
        super(sceneProp);
    }

    @Override
    public void remove() {
        JGemsHelper.world().getEnvironment().getSkyBox().getBackground().removeObject(this.getSceneObject());
    }
}