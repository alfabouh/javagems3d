package javagems3d.system.resources.assets.models.mesh.vertex.attributes;

import javagems3d.help.JGemsUtils;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;

public final class FloatVertexAttribute extends VertexAttribute<Float> {
    private FloatBuffer floatBuffer;

    public FloatVertexAttribute(RenderAttributePointer attributePointer) {
        super(attributePointer);
    }

    @Override
    public void pushGLBuffer() {
        this.floatBuffer = MemoryUtil.memAllocFloat(this.getValues().size());
        this.floatBuffer.put(JGemsUtils.convertFloatsArray(this.getValues())).flip();
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, (FloatBuffer) this.getBuffer(), GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(this.floatBuffer);
    }

    public FloatVertexAttribute putArray(float[] i) {
        for (float a : i) {
            this.getValues().add(a);
        }
        return this;
    }

    @Override
    public Buffer getBuffer() {
        return this.floatBuffer;
    }

    @Override
    public int attributeType() {
        return GL46.GL_FLOAT;
    }
}
