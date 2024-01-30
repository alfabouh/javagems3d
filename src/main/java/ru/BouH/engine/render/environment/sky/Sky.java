package ru.BouH.engine.render.environment.sky;


import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.models.mesh.Mesh;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;

public class Sky implements IWorldDynamic {
    private static final float[] skyboxPos = {
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f
    };
    private static final int[] skyboxInd = new int[]{
            0, 1, 3, 3, 1, 2,
            4, 0, 3, 5, 4, 3,
            3, 2, 7, 5, 3, 7,
            6, 1, 0, 6, 0, 4,
            2, 1, 6, 2, 6, 7,
            7, 6, 4, 7, 4, 5
    };
    private final Model<Format3D> model3D;
    private final CubeMapProgram cubeMapProgram;
    private Vector3f sunAngle;
    private Matrix4d lightProjectionMatrix;

    public Sky(CubeMapProgram cubeMapProgram, Vector3f sunAngle) {
        this.cubeMapProgram = cubeMapProgram;
        this.model3D = this.makeMesh();
        this.setSunAngle(sunAngle);
        this.calcLightProjectionMatrix();
    }

    protected void calcLightProjectionMatrix() {
        this.lightProjectionMatrix = RenderManager.instance.getLookAtMatrix(new Vector3d(this.getSunAngle()), new Vector3d(0.0d, 1.0d, 0.0d), new Vector3d(0.0d));
    }

    private Model<Format3D> makeMesh() {
        Mesh mesh = new Mesh();
        mesh.putPositionValues(Sky.skyboxPos);
        mesh.putIndexValues(Sky.skyboxInd);
        mesh.bakeMesh();
        return new Model<>(new Format3D(), mesh);
    }

    public CubeMapProgram getCubeMapProgram() {
        return this.cubeMapProgram;
    }

    public Vector3f getSunAngle() {
        return new Vector3f(this.sunAngle);
    }

    public void setSunAngle(Vector3f sunAngle) {
        this.sunAngle = new Vector3f(sunAngle).normalize();
    }

    public float getSunBrightness() {
        Vector3f nv = this.getSunAngle().mul(1, 0, 1);
        float angle1 = nv.angle(this.getSunAngle());
        float factor = MathHelper.sin(Math.toDegrees(angle1));
        factor += (factor * 0.2f);
        return 1.0f;
    }

    public Model<Format3D> getModel3D() {
        return this.model3D;
    }

    public Matrix4d getLightProjectionMatrix() {
        return new Matrix4d(this.lightProjectionMatrix);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.calcLightProjectionMatrix();
    }
}
