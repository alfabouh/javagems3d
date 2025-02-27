package toolbox.render.scene.items.renderers;

import javagems3d.temp.map_sys.save.objects.MapProperties;
import toolbox.render.scene.items.objects.base.TBoxAbstractObject;

public interface ITBoxObjectRenderer {
    void onRender(MapProperties properties, TBoxAbstractObject tBoxAbstractObject, float deltaTime);

    void preRender(TBoxAbstractObject tBoxAbstractObject);

    void postRender(TBoxAbstractObject tBoxAbstractObject);
}
