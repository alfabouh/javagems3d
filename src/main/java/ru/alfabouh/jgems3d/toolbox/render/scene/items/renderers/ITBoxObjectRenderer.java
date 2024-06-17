package ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers;

import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.TBoxScene3DObject;

public interface ITBoxObjectRenderer {
    void onRender(TBoxScene3DObject tBoxScene3DObject, double deltaTime);
    void preRender(TBoxScene3DObject tBoxScene3DObject);
    void postRender(TBoxScene3DObject tBoxScene3DObject);
}
