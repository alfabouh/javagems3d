package javagems3d.system.service.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("your.annotations.RequireEmptyConstructor")
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