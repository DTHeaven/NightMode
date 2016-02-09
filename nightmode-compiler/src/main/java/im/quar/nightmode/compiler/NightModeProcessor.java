package im.quar.nightmode.compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;

import im.quar.nightmode.MultiBackground;
import im.quar.nightmode.MultiImageTint;
import im.quar.nightmode.MultiModeListener;
import im.quar.nightmode.MultiTextColor;
import im.quar.nightmode.internal.ListenerMethod;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class NightModeProcessor extends AbstractProcessor {

    private static final String VIEW_TYPE = "android.view.View";
    private static final String TEXT_VIEW_TYPE = "android.widget.TextView";
    private static final String IMAGE_VIEW_TYPE = "android.widget.ImageView";
    private static final String MODE_CHANGER_SUFFIX = "$$ModeChanger";

    private Messager mMessager;
    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(MultiBackground.class.getCanonicalName());
        types.add(MultiTextColor.class.getCanonicalName());
        types.add(MultiImageTint.class.getCanonicalName());
        types.add(MultiModeListener.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, BindingClass> targetClassMap = findAndParseTargets(roundEnv);

        Iterator<BindingClass> iterator = targetClassMap.values().iterator();
        while (iterator.hasNext()) {
            try {
                iterator.next().brewJava().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private Map<TypeElement, BindingClass> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<>();

        for (Element element : env.getElementsAnnotatedWith(MultiTextColor.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseTextColorElement(element, targetClassMap);
        }

        for (Element element : env.getElementsAnnotatedWith(MultiBackground.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseBackgroundElement(element, targetClassMap);
        }

        for (Element element : env.getElementsAnnotatedWith(MultiImageTint.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseImageTintElement(element, targetClassMap);
        }

        for (Element element : env.getElementsAnnotatedWith(MultiModeListener.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseListenerElement(MultiModeListener.class, element, targetClassMap);
        }

        return targetClassMap;
    }

    private void parseTextColorElement(Element element, Map<TypeElement, BindingClass> targetClassMap) {
        // Verify common generated code restrictions.
        if (isInaccessibleViaGeneratedCode(MultiTextColor.class, "fields", element)
                || isBindingInWrongPackage(MultiTextColor.class, element)) {
            return;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target type extends from TextView.
        TypeMirror elementType = element.asType();
        if (elementType.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        if (!isSubtypeOfType(elementType, TEXT_VIEW_TYPE)) {
            error(element, "@%s fields must extend from TextView. (%s.%s)",
                    MultiTextColor.class.getSimpleName(), enclosingElement.getQualifiedName(), element.getSimpleName());
        }

        int[] values = element.getAnnotation(MultiTextColor.class).value();

        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        }

        String name = element.getSimpleName().toString();
        TypeName type = TypeName.get(elementType);

        FieldBinding binding = new FieldBinding(name, type, values);
        bindingClass.addTextViewField(binding);
    }

    private void parseBackgroundElement(Element element, Map<TypeElement, BindingClass> targetClassMap) {
        // Verify common generated code restrictions.
        if (isInaccessibleViaGeneratedCode(MultiBackground.class, "fields", element)
                || isBindingInWrongPackage(MultiBackground.class, element)) {
            return;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target type extends from View.
        TypeMirror elementType = element.asType();
        if (elementType.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        if (!isSubtypeOfType(elementType, VIEW_TYPE)) {
            error(element, "@%s fields must extend from View. (%s.%s)",
                    MultiBackground.class.getSimpleName(), enclosingElement.getQualifiedName(), element.getSimpleName());
        }

        int[] values = element.getAnnotation(MultiBackground.class).value();

        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        }

        String name = element.getSimpleName().toString();
        TypeName type = TypeName.get(elementType);

        FieldBinding binding = new FieldBinding(name, type, values);
        bindingClass.addBackgroundField(binding);
    }

    private void parseImageTintElement(Element element, Map<TypeElement, BindingClass> targetClassMap) {
        // Verify common generated code restrictions.
        if (isInaccessibleViaGeneratedCode(MultiImageTint.class, "fields", element)
                || isBindingInWrongPackage(MultiImageTint.class, element)) {
            return;
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target type extends from View.
        TypeMirror elementType = element.asType();
        if (elementType.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        if (!isSubtypeOfType(elementType, IMAGE_VIEW_TYPE)) {
            error(element, "@%s fields must extend from ImageView. (%s.%s)",
                    MultiImageTint.class.getSimpleName(), enclosingElement.getQualifiedName(), element.getSimpleName());
        }

        int[] values = element.getAnnotation(MultiImageTint.class).value();

        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        }

        String name = element.getSimpleName().toString();
        TypeName type = TypeName.get(elementType);

        FieldBinding binding = new FieldBinding(name, type, values);
        bindingClass.addImageTintField(binding);
    }

    private void parseListenerElement(Class<? extends Annotation> annotationClass, Element element, Map<TypeElement, BindingClass> targetClassMap) {
        // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
        if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a method.", annotationClass.getSimpleName()));
        }

        ExecutableElement executableElement = (ExecutableElement) element;
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the method and its containing class are accessible via generated code.
        boolean hasError = isInaccessibleViaGeneratedCode(annotationClass, "methods", element);
        hasError |= isBindingInWrongPackage(annotationClass, element);

        ListenerMethod method = annotationClass.getAnnotation(ListenerMethod.class);
        if (method == null) {
            throw new IllegalStateException(
                    String.format("No @%s defined on @%s.", ListenerMethod.class.getSimpleName(),
                            annotationClass.getSimpleName()));
        }

        // Verify that the method has equal to or less than the number of parameters as the listener.
        List<? extends VariableElement> methodParameters = executableElement.getParameters();
        if (methodParameters.size() > method.parameters().length) {
            error(element, "@%s methods can have at most %s parameter(s). (%s.%s)",
                    annotationClass.getSimpleName(), method.parameters().length,
                    enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
        }

        // Verify method return type matches the listener.
        TypeMirror returnType = executableElement.getReturnType();
        if (returnType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) returnType;
            returnType = typeVariable.getUpperBound();
        }

        if (!returnType.toString().equals(method.returnType())) {
            error(element, "@%s methods must have a '%s' return type. (%s.%s)",
                    annotationClass.getSimpleName(), method.returnType(),
                    enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
        }

        if (hasError) {
            return;
        }

        Parameter[] parameters = Parameter.NONE;
        if (!methodParameters.isEmpty()) {
            parameters = new Parameter[methodParameters.size()];
            BitSet methodParameterUsed = new BitSet(methodParameters.size());
            String[] parameterTypes = method.parameters();
            for (int i = 0; i < methodParameters.size(); i++) {
                VariableElement methodParameter = methodParameters.get(i);
                TypeMirror methodParameterType = methodParameter.asType();
                if (methodParameterType instanceof TypeVariable) {
                    TypeVariable typeVariable = (TypeVariable) methodParameterType;
                    methodParameterType = typeVariable.getUpperBound();
                }

                for (int j = 0; j < parameterTypes.length; j++) {
                    if (methodParameterUsed.get(j)) {
                        continue;
                    }
                    if (isSubtypeOfType(methodParameterType, parameterTypes[j])
                            || isInterface(methodParameterType)) {
                        parameters[i] = new Parameter(j, TypeName.get(methodParameterType));
                        methodParameterUsed.set(j);
                        break;
                    }
                }
                if (parameters[i] == null) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Unable to match @")
                            .append(annotationClass.getSimpleName())
                            .append(" method arguments. (")
                            .append(enclosingElement.getQualifiedName())
                            .append('.')
                            .append(element.getSimpleName())
                            .append(')');
                    for (int j = 0; j < parameters.length; j++) {
                        Parameter parameter = parameters[j];
                        builder.append("\n\n  Parameter #")
                                .append(j + 1)
                                .append(": ")
                                .append(methodParameters.get(j).asType().toString())
                                .append("\n    ");
                        if (parameter == null) {
                            builder.append("did not match any listener parameters");
                        } else {
                            builder.append("matched listener parameter #")
                                    .append(parameter.getListenerPosition() + 1)
                                    .append(": ")
                                    .append(parameter.getType());
                        }
                    }
                    builder.append("\n\nMethods may have up to ")
                            .append(method.parameters().length)
                            .append(" parameter(s):\n");
                    for (String parameterType : method.parameters()) {
                        builder.append("\n  ").append(parameterType);
                    }
                    builder.append(
                            "\n\nThese may be listed in any order but will be searched for from top to bottom.");
                    error(executableElement, builder.toString());
                    return;
                }
            }
        }

        String name = executableElement.getSimpleName().toString();
        BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        MethodBinding methodBinding = new MethodBinding(name, Arrays.asList(parameters));
        bindingClass.addMethod(methodBinding);
    }

    private BindingClass getOrCreateTargetClass(Map<TypeElement, BindingClass> targetClassMap,
                                                TypeElement enclosingElement) {
        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + MODE_CHANGER_SUFFIX;

            bindingClass = new BindingClass(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, bindingClass);
        }
        return bindingClass;
    }

    private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass,
                                                   String targetThing, Element element) {
        boolean hasError = false;
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify method modifiers.
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        // Verify containing type.
        if (enclosingElement.getKind() != CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        // Verify containing class visibility is not private.
        if (enclosingElement.getModifiers().contains(PRIVATE)) {
            error(enclosingElement, "@%s %s may not be contained in private classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        return hasError;
    }

    private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass,
                                            Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        if (qualifiedName.startsWith("android.")) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }
        if (qualifiedName.startsWith("java.")) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return true;
        }

        return false;
    }

    private boolean isInterface(TypeMirror typeMirror) {
        return typeMirror instanceof DeclaredType
                && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
    }

    private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (otherType.equals(typeMirror.toString())) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private void info(String s) {
        mMessager.printMessage(NOTE, s);
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        mMessager.printMessage(ERROR, message, element);
    }
}
