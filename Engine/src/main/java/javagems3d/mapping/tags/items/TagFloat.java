package javagems3d.mapping.tags.items;

import com.google.gson.reflect.TypeToken;

public class TagFloat extends TagItem {
    public static final String TYPE_STRING = "TagFloat";

    private float value;
    private final float min;
    private final float max;

    public TagFloat(float value, float min, float max) {
        super(TagFloat.TYPE_STRING);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public TagFloat setValue(float value) {
        this.value = value;
        return this;
    }

    public float getValue() {
        return this.value;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagFloat(this.getValue(), this.getMin(), this.getMax());
    }
}
