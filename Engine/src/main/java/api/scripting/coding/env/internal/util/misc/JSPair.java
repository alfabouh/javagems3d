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

package api.scripting.coding.env.internal.util.misc;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.service.collections.Pair;

@JSCodingClass(binding = "JSPair", description = "Wrapper for generic Pair<K, V>, holds two values and provides utility methods for JS.")
public class JSPair<K, V> {
    @JSHideFromDoc
    private final Pair<K, V> pair;

    @JSCodingConstructor(description = "Create a new Pair", paramNames = {"first", "second"})
    public JSPair(K first, V second) {
        this.pair = new Pair<>(first, second);
    }

    @JSCodingFunctionOrMethod(description = "Get first element of the pair")
    public K getFirst() {
        return pair.first();
    }

    @JSCodingFunctionOrMethod(description = "Get second element of the pair")
    public V getSecond() {
        return pair.second();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java Pair")
    public Pair<K, V> getJavaPair() {
        return pair;
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    @JSCodingFunctionOrMethod(description = "Create an array of pairs from given pairs", paramNames = {"pairs"})
    public static <K, V> JSPair<K, V>[] array(JSPair<K, V>... pairs) {
        if (pairs == null) return new JSPair[0];
        JSPair<K, V>[] result = new JSPair[pairs.length];
        System.arraycopy(pairs, 0, result, 0, pairs.length);
        return result;
    }

    @Override
    public String toString() {
        return pair.first() + " + " + pair.second();
    }
}