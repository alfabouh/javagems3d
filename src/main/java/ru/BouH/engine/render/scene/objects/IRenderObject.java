package ru.BouH.engine.render.scene.objects;

import ru.BouH.engine.render.scene.fabric.render.base.IRenderFabric;

public interface IRenderObject {
    IRenderFabric renderFabric();

    boolean hasRender();
}
