/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.instances.inner.JSRadioInfo;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.system.external.mapping.tags.items.TagRadioBoolean;

import java.util.Arrays;

@JSCodingClass(binding = "JSTagRadioBoolean", description = "Radio Boolean tag item.")
public class JSTagRadioBoolean {
    @JSHideFromDoc
    private final TagRadioBoolean tag;

    @JSCodingConstructor(description = "Create a radio boolean tag with given options.")
    public JSTagRadioBoolean(JSRadioInfo... values) {
        TagRadioBoolean.Info[] infos = (TagRadioBoolean.Info[]) Arrays.stream(values).map(JSRadioInfo::toJava).toArray();
        this.tag = new TagRadioBoolean(infos);
    }

    @JSCodingConstructor(description = "Wrap existing TagItem as JSTagRadioBoolean.")
    public JSTagRadioBoolean(JSTagItem tagItem) {
        this.tag = (TagRadioBoolean) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagRadioBoolean getJava() {
        return this.tag;
    }

    @JSHideFromDoc
    public JSTagItem toTagItem() {
        return new JSTagItem(this.tag);
    }

    @JSCodingFunctionOrMethod(description = "Get all radio options.")
    public JSRadioInfo[] getValues() {
        TagRadioBoolean.Info[] values = this.tag.getValues();
        JSRadioInfo[] wrapped = new JSRadioInfo[values.length];
        for (int i = 0; i < values.length; i++) {
            wrapped[i] = new JSRadioInfo(values[i]);
        }
        return wrapped;
    }

    @JSCodingFunctionOrMethod(description = "Set the selected index (only one can be true).", paramNames = {"index"})
    public JSTagRadioBoolean setSelectedIndex(int index) {
        TagRadioBoolean.Info[] values = this.tag.getValues();
        for (int i = 0; i < values.length; i++) {
            values[i].setFlag(i == index);
        }
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the selected index, or -1 if none.")
    public int getSelectedIndex() {
        TagRadioBoolean.Info[] values = this.tag.getValues();
        for (int i = 0; i < values.length; i++) {
            if (values[i].isFlag()) return i;
        }
        return -1;
    }

    @JSCodingFunctionOrMethod(description = "Copy the tag.")
    public JSTagRadioBoolean copy() {
        return new JSTagRadioBoolean(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        return "JSTagRadioBoolean{" + Arrays.toString(getValues()) + "}";
    }
}