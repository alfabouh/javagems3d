package api.scripting.coding.env.internal.util.resources.instances.sound;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;

@JSCodingClass(binding = "JSSoundFormats", description = "...")
public enum JSSoundFormats {
    @JSCodingField(description = "AL_FORMAT_MONO8", paramName = "AL_FORMAT_MONO8") AL_FORMAT_MONO8(4352),
    @JSCodingField(description = "AL_FORMAT_MONO16", paramName = "AL_FORMAT_MONO16") AL_FORMAT_MONO16(4353),
    @JSCodingField(description = "AL_FORMAT_STEREO8", paramName = "AL_FORMAT_STEREO8") AL_FORMAT_STEREO8(4354),
    @JSCodingField(description = "AL_FORMAT_STEREO16", paramName = "AL_FORMAT_STEREO16") AL_FORMAT_STEREO16(4355);

    private final int value;
    JSSoundFormats(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return super.toString();
    }
}
