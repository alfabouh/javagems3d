package javagems3d.system.resources.assets.models.mesh.attributes;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.attributes.pointer.AttributePointer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.DoubleBuffer;

public final class DoubleVertexAttribute extends VertexAttribute<Double> {
    private DoubleBuffer doubleBuffer;

    public DoubleVertexAttribute(AttributePointer attributePointer) {
        super(attributePointer);
    }

    @Override
    public void pushGLBuffer() {
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, (DoubleBuffer) this.getBuffer(), GL30.GL_STATIC_DRAW);
    }

    public void putArray(double[] i) {
        for (double a : i) {
            this.getValues().add(a);
        }
    }

    @Override
    public void bake() {
        this.doubleBuffer = MemoryUtil.memAllocDouble(this.getValues().size());
        this.doubleBuffer.put(JGemsHelper.UTILS.convertDoublesArray(this.getValues())).flip();
    }

    @Override
    public Buffer getBuffer() {
        return this.doubleBuffer;
    }

    @Override
    public int attributeType() {
        return GL30.GL_DOUBLE;
    }
}
