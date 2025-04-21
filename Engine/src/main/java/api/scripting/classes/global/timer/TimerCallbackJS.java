package api.scripting.classes.global.timer;


import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;

@JSTypeDoc(description = "Timer. Callback", priority = JSTypeDoc.Priority.MED)
@FunctionalInterface
public interface TimerCallbackJS {
    @JSMethodDoc(description = "Callback function", args = {}, order = 0)
    void action();
}
