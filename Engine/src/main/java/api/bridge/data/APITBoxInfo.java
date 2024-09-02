/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package api.bridge.data;

import org.jetbrains.annotations.NotNull;
import api.app.main.JGemsTBoxApplication;
import api.app.main.JGemsTBoxEntry;

public class APITBoxInfo {
    private final JGemsTBoxEntry tBoxEntry;
    private final JGemsTBoxApplication appInstance;

    public APITBoxInfo(@NotNull JGemsTBoxApplication appInstance, @NotNull JGemsTBoxEntry tBoxEntry) {
        this.tBoxEntry = tBoxEntry;
        this.appInstance = appInstance;
    }

    public JGemsTBoxEntry getTBoxEntry() {
        return this.tBoxEntry;
    }

    public JGemsTBoxApplication getAppInstance() {
        return this.appInstance;
    }
}
