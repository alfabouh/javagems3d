package ru.alfabouh.engine.render.scene.objects;

import ru.alfabouh.engine.render.scene.fabric.render.base.IRenderFabric;

public interface IRenderObject {
    IRenderFabric renderFabric();

    boolean hasRender();
}
