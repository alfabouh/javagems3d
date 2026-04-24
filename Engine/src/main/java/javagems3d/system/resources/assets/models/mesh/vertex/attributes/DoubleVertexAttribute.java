package javagems3d.system.resources.assets.models.mesh.vertex.attributes;

import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.DoubleBuffer;

public final class DoubleVertexAttribute extends VertexAttribute<Double> {
    private DoubleBuffer doubleBuffer;

    public DoubleVertexAttribute(RenderAttributePointer attributePointer) {
        super(attributePointer);
    }

    @Override
    public void pushGLBuffer() {
        this.doubleBuffer = MemoryUtil.memAllocDouble(this.getValues().size());
        this.doubleBuffer.put(JGemsHelper.Math.convertDoublesArray(this.getValues())).flip();
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, (DoubleBuffer) this.getBuffer(), GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(this.doubleBuffer);
    }

    public void putArray(double[] i) {
        for (double a : i) {
            this.getValues().add(a);
        }
    }

    @Override
    public Buffer getBuffer() {
        return this.doubleBuffer;
    }

    @Override
    public int attributeType() {
        return GL46.GL_DOUBLE;
    }
}
