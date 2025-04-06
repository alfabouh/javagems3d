package javagems3d.mapping.tags.items;

import com.google.gson.reflect.TypeToken;

public class TagCheckBoolean extends TagItem {
    public static final String TYPE_STRING = "TagCheckBoolean";

    private boolean flag;

    public TagCheckBoolean(boolean flag) {
        super(TagCheckBoolean.TYPE_STRING);
        this.flag = flag;
    }

    public TagCheckBoolean setFlag(boolean flag) {
        this.flag = flag;
        return this;
    }

    public boolean isFlag() {
        return this.flag;
    }

    @Override
    public TagItem copy() {
        return new TagCheckBoolean(this.isFlag());
    }
}
