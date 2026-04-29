package javagems3d.system.resources.assets.models.mesh.vertex.attributes;

import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.IntBuffer;

public final class IntegerVertexAttribute extends VertexAttribute<Integer> {
    private IntBuffer intBuffer;

    public IntegerVertexAttribute(RenderAttributePointer attributePointer) {
        super(attributePointer);
    }

    @Override
    public void pushGLBuffer() {
        this.intBuffer = MemoryUtil.memAllocInt(this.getValues().size());
        this.intBuffer.put(JGemsHelper.Math.convertIntsArray(this.getValues())).flip();
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, (IntBuffer) this.getBuffer(), GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(this.intBuffer);
    }

    public void clearData() {
        super.clearData();
        this.intBuffer = null;
    }

    public void putArray(int[] i) {
        for (int a : i) {
            this.getValues().add(a);
        }
    }

    @Override
    public Buffer getBuffer() {
        return this.intBuffer;
    }

    @Override
    public int attributeType() {
        return GL46.GL_FLOAT;
    }
}
