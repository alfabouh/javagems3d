package javagems3d.mapping.tags.items;

import org.jetbrains.annotations.NotNull;

public class TagString extends TagItem {
    public static final String TYPE_STRING = "TagString";

    private String text;

    public TagString(@NotNull String text) {
        super(TagString.TYPE_STRING);
        this.text = text;
    }

    public TagString setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public TagItem copy() {
        return new TagString(this.getText());
    }
}
