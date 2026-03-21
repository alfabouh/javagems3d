package api.scripting.coding.env.internal.util.resources.instances.sound;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import javagems3d.audio.sound.SoundBuffer;

@JSCodingClass(binding = "JSOggSound", description = "...")
public class JSOggSound implements JSCanBeCachedInMemory {
    @JSHideFromDoc
    private final SoundBuffer soundBuffer;

    public JSOggSound(SoundBuffer soundBuffer) {
        this.soundBuffer = soundBuffer;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public SoundBuffer getJavaSoundBuffer() {
        return this.soundBuffer;
    }
}
