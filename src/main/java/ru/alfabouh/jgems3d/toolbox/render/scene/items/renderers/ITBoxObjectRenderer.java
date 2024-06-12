package ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers;

import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.ITBoxScene3DObject;

public interface ITBoxObjectRenderer {
    void onRender(ITBoxScene3DObject itBoxScene3DObject, double deltaTime);
    void preRender(ITBoxScene3DObject itBoxScene3DObject);
    void postRender(ITBoxScene3DObject itBoxScene3DObject);
}
