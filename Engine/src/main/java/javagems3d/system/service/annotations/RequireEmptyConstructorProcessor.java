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

package javagems3d.system.service.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("your.def.RequireEmptyConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RequireEmptyConstructorProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(RequireEmptyConstructor.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                continue;
            }

            TypeElement classElement = (TypeElement) element;
            boolean hasEmptyConstructor = false;

            for (Element enclosed : classElement.getEnclosedElements()) {
                if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                    ExecutableElement actor = (ExecutableElement) enclosed;
                    if (actor.getParameters().isEmpty() &&
                        actor.getModifiers().contains(Modifier.PUBLIC)) {
                        hasEmptyConstructor = true;
                        break;
                    }
                }
            }

            if (!hasEmptyConstructor) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Class " + classElement.getSimpleName() + " must have a public empty constructor", classElement);
            }
        }
        return true;
    }
}