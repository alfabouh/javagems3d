package api.scripting.coding.env.internal.util.events;

import api.scripting.coding.env.def.JSHideFromDoc;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@JSHideFromDoc
public final class JSEventSubscriber {
    public Map<String, String> events = new HashMap<>();

    public void subscribeEvent(@NotNull String eventI, @NotNull String functionName) {
        this.events.put(eventI, functionName);
    }

    public void clear() {
        this.events.clear();
    }

    public String getEventName(@NotNull JSEventI eventI) {
        return this.events.get(eventI.name());
    }
}