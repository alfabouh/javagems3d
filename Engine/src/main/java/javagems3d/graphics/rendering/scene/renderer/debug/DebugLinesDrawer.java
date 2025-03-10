package javagems3d.graphics.rendering.scene.renderer.debug;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class DebugLinesDrawer {
    private static final Object monitor = new Object();

    private final Set<Request> requests;
    private final JGemsShaderManager drawerShader;
    private int vao;
    private int vbo;
    private int ebo;

    public DebugLinesDrawer(JGemsShaderManager drawerShader) {
        this.requests = new HashSet<>();
        this.drawerShader = drawerShader;
    }

    public void setup() {
        this.vao = GL46.glGenVertexArrays();
        this.vbo = GL46.glGenBuffers();
        this.ebo = GL46.glGenBuffers();
        GL46.glBindVertexArray(this.vao);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.vbo);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, 1024L, GL46.GL_STREAM_DRAW);

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, 1024L, GL46.GL_STREAM_DRAW);

        GL46.glEnableVertexAttribArray(0);
        GL46.glVertexAttribPointer(0, 3, GL46.GL_FLOAT, false, 0, 0);

        GL46.glBindVertexArray(0);
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
    }

    public void render() {
        Iterator<Request> requestIterator = this.getRequests().iterator();
        while (requestIterator.hasNext()) {
            Request request = requestIterator.next();
            if (request.getPreRender() != null) {
                request.getPreRender().accept(this.getDrawerShader());
            }

            GL46.glBindVertexArray(this.getVao());

            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.getVbo());
            GL46.glBufferSubData(GL46.GL_ARRAY_BUFFER, 0L, request.getVectorsToDraw());

            GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, this.getEbo());
            GL46.glBufferSubData(GL46.GL_ELEMENT_ARRAY_BUFFER, 0L, request.getIndexes());

            MemoryUtil.memFree(request.getVectorsToDraw());
            MemoryUtil.memFree(request.getIndexes());

            this.getDrawerShader().beginShading();
            this.getDrawerShader().performUniform(new UniformString("color"), UniformFunctions.VEC4F(new Vector4f(request.getColor(), 1.0f)));
            this.getDrawerShader().performPerspectiveMatrix(new UniformString("projection_matrix"), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
            this.getDrawerShader().performViewMatrix(new UniformString("view_matrix"), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
            GL46.glEnableVertexAttribArray(0);
            GL46.glDrawElements(GL46.GL_LINES, request.indexes.remaining(), GL46.GL_UNSIGNED_INT, 0);
            GL46.glDisableVertexAttribArray(0);
            this.getDrawerShader().endShading();
            GL46.glBindVertexArray(0);

            if (request.getPostRender() != null) {
                request.getPostRender().accept(this.getDrawerShader());
            }
            requestIterator.remove();
        }
    }

    public static Consumer<JGemsShaderManager> noDepth() {
        return (e) -> GL46.glDisable(GL46.GL_DEPTH_TEST);
    }

    public static Consumer<JGemsShaderManager> Depth() {
        return (e) -> GL46.glEnable(GL46.GL_DEPTH_TEST);
    }

    public static Request LineRequest(Vector3f start, Vector3f stop, Vector3f color, @Nullable Consumer<JGemsShaderManager> preRender, @Nullable Consumer<JGemsShaderManager> postRender) {
        FloatBuffer vertexes = MemoryUtil.memAllocFloat(2 * 3);
        IntBuffer indexes = MemoryUtil.memAllocInt(2);

        vertexes.put(new float[]{start.x, start.y, start.z});
        vertexes.put(new float[]{stop.x, stop.y, stop.z});
        vertexes.flip();

        indexes.put(new int[]{0, 1});
        indexes.flip();

        return new Request(color, vertexes, indexes, preRender, postRender);
    }

    public static Request BoxRequest(Vector3f min, Vector3f max, Vector3f color, @Nullable Consumer<JGemsShaderManager> preRender, @Nullable Consumer<JGemsShaderManager> postRender) {
        FloatBuffer vertexes = MemoryUtil.memAllocFloat(8 * 3);
        IntBuffer indexes = MemoryUtil.memAllocInt(24);

        vertexes.put(new float[]{min.x, min.y, min.z});
        vertexes.put(new float[]{max.x, min.y, min.z});
        vertexes.put(new float[]{min.x, max.y, min.z});
        vertexes.put(new float[]{max.x, max.y, min.z});
        vertexes.put(new float[]{min.x, min.y, max.z});
        vertexes.put(new float[]{max.x, min.y, max.z});
        vertexes.put(new float[]{min.x, max.y, max.z});
        vertexes.put(new float[]{max.x, max.y, max.z});
        vertexes.flip();

        indexes.put(new int[]{0, 1, 0, 2, 0, 4,
                1, 3, 1, 5,
                2, 3, 2, 6,
                3, 7,
                4, 5, 4, 6,
                5, 7,
                6, 7});
        indexes.flip();

        return new Request(color, vertexes, indexes, preRender, postRender);
    }

    public void clear() {
        this.getRequests().clear();
        GL46.glDeleteBuffers(this.vbo);
        GL46.glDeleteBuffers(this.ebo);
        GL46.glDeleteVertexArrays(this.vao);
    }

    public void addRequest(Request request) {
        synchronized (DebugLinesDrawer.monitor) {
            this.requests.add(request);
        }
    }

    public Set<Request> getRequests() {
        synchronized (DebugLinesDrawer.monitor) {
            return this.requests;
        }
    }

    public JGemsShaderManager getDrawerShader() {
        return this.drawerShader;
    }

    public int getEbo() {
        return this.ebo;
    }

    public int getVao() {
        return this.vao;
    }

    public int getVbo() {
        return this.vbo;
    }

    public static class Request {
        private final Consumer<JGemsShaderManager> preRender;
        private final Consumer<JGemsShaderManager> postRender;

        private final FloatBuffer vectorsToDraw;
        private final IntBuffer indexes;
        private final Vector3f color;

        public Request(@NotNull Vector3f color, @NotNull FloatBuffer vectorsToDraw, @NotNull IntBuffer indexes, @Nullable Consumer<JGemsShaderManager> preRender, @Nullable Consumer<JGemsShaderManager> postRender) {
            this.vectorsToDraw = vectorsToDraw;
            this.indexes = indexes;
            this.color = color;

            this.preRender = preRender;
            this.postRender = postRender;
        }

        public Consumer<JGemsShaderManager> getPreRender() {
            return this.preRender;
        }

        public Consumer<JGemsShaderManager> getPostRender() {
            return this.postRender;
        }

        public IntBuffer getIndexes() {
            return this.indexes;
        }

        public FloatBuffer getVectorsToDraw() {
            return this.vectorsToDraw;
        }

        public Vector3f getColor() {
            return this.color;
        }
    }
}
