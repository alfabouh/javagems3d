package api.scripting.legacy.classes.global.timer;


import api.scripting.legacy.doc.annotations.JSMethodDoc;
import api.scripting.legacy.doc.annotations.JSTypeDoc;

@JSTypeDoc(description = "Timer. Callback", priority = JSTypeDoc.Priority.MED)
@FunctionalInterface
public interface TimerCallbackJS {
    @JSMethodDoc(description = "Callback function", args = {}, order = 0)
    void action();
}
