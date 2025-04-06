package javagems3d.mapping.tags.items;

import com.google.gson.reflect.TypeToken;

public class TagRadioBoolean extends TagItem {
    public static final String TYPE_STRING = "TagRadioBoolean";

    private final Info[] values;

    public TagRadioBoolean(Info... values) {
        super(TagRadioBoolean.TYPE_STRING);
        this.values = values;
    }

    public Info[] getValues() {
        return this.values;
    }

    public Info[] getCopiedValues() {
        Info[] copy = new Info[this.values.length];
        for (int i = 0; i < this.values.length; i++) {
            Info original = this.values[i];
            copy[i] = new Info(original.getName(), original.isFlag());
        }
        return copy;
    }

    @Override
    public TagItem copy() {
        return new TagRadioBoolean(this.getCopiedValues());
    }

    public static final class Info {
        private final String name;
        private boolean flag;

        public Info(String name, boolean flag) {
            this.name = name;
            this.flag = flag;
        }

        public String getName() {
            return this.name;
        }

        public Info setFlag(boolean flag) {
            this.flag = flag;
            return this;
        }

        public boolean isFlag() {
            return this.flag;
        }
    }
}
