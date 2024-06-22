package ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers;

import ru.alfabouh.jgems3d.toolbox.render.scene.container.MapProperties;
import ru.alfabouh.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;

public interface ITBoxObjectRenderer {
    void onRender(MapProperties properties, TBoxScene3DObject tBoxScene3DObject, double deltaTime);
    void preRender(TBoxScene3DObject tBoxScene3DObject);
    void postRender(TBoxScene3DObject tBoxScene3DObject);
}
