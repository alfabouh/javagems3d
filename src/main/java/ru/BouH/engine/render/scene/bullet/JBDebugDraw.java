package ru.BouH.engine.render.scene.bullet;

import org.bytedeco.bullet.LinearMath.btIDebugDraw;
import org.bytedeco.bullet.LinearMath.btVector3;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.math.Pair;
import ru.BouH.engine.render.scene.scene_render.groups.DebugRender;

public class JBDebugDraw extends btIDebugDraw {
    private int debugMode;

    public void drawLine(btVector3 from, btVector3 to, btVector3 color) {
        DebugRender.objectWires.add(new Pair<>(MeshHelper.generateVector3DModel(MathHelper.convert(from), MathHelper.convert(to)), MathHelper.convert(color)));
    }

    @Override
    public void setDebugMode(int debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public int getDebugMode() {
        return this.debugMode;
    }
}