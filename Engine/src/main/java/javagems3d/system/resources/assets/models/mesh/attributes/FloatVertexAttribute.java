package javagems3d.system.resources.assets.models.mesh.attributes;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.attributes.pointer.AttributePointer;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;

public final class FloatVertexAttribute extends VertexAttribute<Float> {
    private FloatBuffer floatBuffer;

    public FloatVertexAttribute(AttributePointer attributePointer) {
        super(attributePointer);
    }

    @Override
    public void pushGLBuffer() {
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, (FloatBuffer) this.getBuffer(), GL30.GL_STATIC_DRAW);
    }

    public void putArray(float[] i) {
        for (float a : i) {
            this.getValues().add(a);
        }
    }

    @Override
    public void bake() {
        this.floatBuffer = MemoryUtil.memAllocFloat(this.getValues().size());
        this.floatBuffer.put(JGemsHelper.UTILS.convertFloatsArray(this.getValues())).flip();
    }

    @Override
    public Buffer getBuffer() {
        return this.floatBuffer;
    }

    @Override
    public int attributeType() {
        return GL30.GL_FLOAT;
    }
}
