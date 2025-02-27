package javagems3d.system.settings.objects;

import org.jetbrains.annotations.NotNull;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.io.Serializable;

public abstract class SettingObject<T extends Serializable> {
    private final String name;
    private final T defaultValue;
    private T value;

    public SettingObject(String name, @NotNull T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public abstract T tryParseFromString(String string);

    public void setDefault() {
        this.value = this.getDefaultValue();
    }

    public @NotNull T getDefaultValue() {
        return this.defaultValue;
    }

    public @NotNull T getValue() {
        return this.value;
    }

    @SuppressWarnings("all")
    public void setValue(Object value) {
        if (!value.getClass().isAssignableFrom(this.value.getClass())) {
            throw new JGemsRuntimeException("Couldn't cast value");
        }
        this.value = (T) value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }
}
