package javagems3d.mapping.tags.items;

import com.google.gson.reflect.TypeToken;
import javagems3d.mapping.tags.base.ColorMode;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class TagColor extends TagItem {
    public static final String TYPE_STRING = "TagColor";

    private final Vector4f colorVector;
    private final ColorMode colorMode;

    public TagColor(ColorMode colorMode, @NotNull Vector4f colorVector) {
        super(TagColor.TYPE_STRING);
        this.colorVector = colorVector;
        this.colorMode = colorMode;
    }

    public ColorMode getColorMode() {
        return this.colorMode;
    }

    public void setColor(Vector4f color) {
        this.getColorVector().set(color);
    }

    public Vector4f getColorVector() {
        return this.colorVector;
    }

    @Override
    public TagItem copy() {
        return new TagColor(this.getColorMode(), new Vector4f(this.getColorVector()));
    }
}
