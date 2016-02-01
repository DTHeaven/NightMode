package im.quar.nightmode.compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;

import im.quar.nightmode.MultiBackground;
import im.quar.nightmode.MultiTextColor;

import static javax.tools.Diagnostic.Kind.NOTE;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class NightModeProcessor extends AbstractProcessor {

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
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        info("process...........");
        Map<TypeElement, BindingClass> targetClassMap = findAndParseTargets(roundEnv);

        info("brew............");
        Iterator<BindingClass> iterator = targetClassMap.values().iterator();
        while (iterator.hasNext()) {
            try {
                iterator.next().brewJava().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            info(iterator.next().brewJava());
        }

        info("brew end........");
//        for (Element element : roundEnv.getElementsAnnotatedWith(MultiTextColor.class)) {
//            //TODO Only for generate error.
//            TypeElement error = (TypeElement) element;
//        }

        return true;
    }

    private Map<TypeElement, BindingClass> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<>();
        Set<String> erasedTargetNames = new LinkedHashSet<>();

        for (Element element : env.getElementsAnnotatedWith(MultiTextColor.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseTextColorElement(element, targetClassMap);
        }

        for (Element element : env.getElementsAnnotatedWith(MultiBackground.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            parseBackgroundElement(element, targetClassMap);
        }

        return targetClassMap;
    }

    private void parseTextColorElement(Element element, Map<TypeElement, BindingClass> targetClassMap) {
        info("element:" + element.toString());
        info("enclosing:" + element.getEnclosingElement());
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target type extends from ViewGroup.
        TypeMirror elementType = element.asType();
        if (elementType.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

//        if (!isSubtypeOfType(elementType, VIEW_GROUP)) {
//            error(element, "@%s fields must extend from ViewGroup. (%s)",
//                    annotationClass.getSimpleName(), enclosingElement.getQualifiedName());
//        }

        String field = element.getSimpleName().toString();
        String canonicalName = enclosingElement.getQualifiedName().toString();

//        String superCanonicalName = ((TypeElement) element).getSuperclass().toString();
//        String superName = superCanonicalName.substring(superCanonicalName.lastIndexOf('.') + 1);
//
//        if (superCanonicalName.startsWith("android.widget.")) {
//            superCanonicalName = superCanonicalName.substring(superCanonicalName.lastIndexOf('.') + 1);
//        }


        int[] values = element.getAnnotation(MultiTextColor.class).value();
//        int[] themes = element.getAnnotation(MultiTextColor.class).theme();
        for (int value : values) {
            info("value:" + value);
        }

        info("filed:" + field);
        info("enclosing:" + canonicalName);

        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass != null) {
//            ViewBindings viewBindings = bindingClass.getViewBinding(id);
//            if (viewBindings != null) {
//                Iterator<FieldViewBinding> iterator = viewBindings.getFieldBindings().iterator();
//                if (iterator.hasNext()) {
//                    FieldViewBinding existingBinding = iterator.next();
//                    error(element, "Attempt to use @%s for an already bound ID %d on '%s'. (%s.%s)",
//                            Bind.class.getSimpleName(), id, existingBinding.getName(),
//                            enclosingElement.getQualifiedName(), element.getSimpleName());
//                    return;
//                }
//            }
        } else {
            bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        }

        String name = element.getSimpleName().toString();
        TypeName type = TypeName.get(elementType);
//        boolean required = isFieldRequired(element);

        FieldBinding binding = new FieldBinding(name, type, values);
        bindingClass.addTextViewField(binding);

//        FieldViewBinding binding = new FieldViewBinding(name, type, required);
//        bindingClass.addTextViewField(id, binding);
//        // Add the type-erased version to the valid binding targets set.
//        erasedTargetNames.add(enclosingElement.toString());

        //BindClass -- Map<TypeElement, BindingClass> targetClassMap

        //enclosing name

        //backgrounds field, values[], themes[]
        //textColors field, values[], themes[]

//        info("superName:" + superName);
//        info("superCanonicalName:" + superCanonicalName);
//        ParsedInfo info = new ParsedInfo();
//        info.name = name;
//        info.canonicalName = canonicalName;
//        info.superName = superName;
//        info.superCanonicalName = superCanonicalName;
//        infos.add(info);

    }

    private void parseBackgroundElement(Element element, Map<TypeElement, BindingClass> targetClassMap) {
        info("element:" + element.toString());
        info("enclosing:" + element.getEnclosingElement());
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target type extends from ViewGroup.
        TypeMirror elementType = element.asType();
        if (elementType.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

//        if (!isSubtypeOfType(elementType, VIEW_GROUP)) {
//            error(element, "@%s fields must extend from ViewGroup. (%s)",
//                    annotationClass.getSimpleName(), enclosingElement.getQualifiedName());
//        }

        String field = element.getSimpleName().toString();
        String canonicalName = enclosingElement.getQualifiedName().toString();

//        String superCanonicalName = ((TypeElement) element).getSuperclass().toString();
//        String superName = superCanonicalName.substring(superCanonicalName.lastIndexOf('.') + 1);
//
//        if (superCanonicalName.startsWith("android.widget.")) {
//            superCanonicalName = superCanonicalName.substring(superCanonicalName.lastIndexOf('.') + 1);
//        }


        int[] values = element.getAnnotation(MultiBackground.class).value();
        for (int value : values) {
            info("value:" + value);
        }

        info("filed:" + field);
        info("enclosing:" + canonicalName);

        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass != null) {
//            ViewBindings viewBindings = bindingClass.getViewBinding(id);
//            if (viewBindings != null) {
//                Iterator<FieldViewBinding> iterator = viewBindings.getFieldBindings().iterator();
//                if (iterator.hasNext()) {
//                    FieldViewBinding existingBinding = iterator.next();
//                    error(element, "Attempt to use @%s for an already bound ID %d on '%s'. (%s.%s)",
//                            Bind.class.getSimpleName(), id, existingBinding.getName(),
//                            enclosingElement.getQualifiedName(), element.getSimpleName());
//                    return;
//                }
//            }
        } else {
            bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        }

        String name = element.getSimpleName().toString();
        TypeName type = TypeName.get(elementType);

        FieldBinding binding = new FieldBinding(name, type, values);
        bindingClass.addBackgroundField(binding);
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
}
