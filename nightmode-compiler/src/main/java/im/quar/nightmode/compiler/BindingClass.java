package im.quar.nightmode.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by DTHeaven on 16/1/26.
 */
final class BindingClass {

    private static final ClassName MODE_CHANGER = ClassName.get("im.quar.nightmode", "ModeChanger");
    private static final ClassName CHANGER = ClassName.get("im.quar.nightmode.changer", "Changer");

    private final ArrayList<FieldBinding> mTextViewFields = new ArrayList<>();
    private final ArrayList<FieldBinding> mBackgroundFields = new ArrayList<>();
    private final ArrayList<FieldBinding> mImageTintFields = new ArrayList<>();

    private final String classPackage;
    private final String className;
    private final String targetClass;

    BindingClass(String classPackage, String className, String targetClass) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
    }

    public void addTextViewField(FieldBinding field) {
        mTextViewFields.add(field);
    }

    public void addBackgroundField(FieldBinding field) {
        mBackgroundFields.add(field);
    }

    public void addImageTintField(FieldBinding field) {
        mImageTintFields.add(field);
    }

    public JavaFile brewJava() {
        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC)
                .addTypeVariable(TypeVariableName.get("T", ClassName.bestGuess(targetClass)))
                .addSuperinterface(ParameterizedTypeName.get(MODE_CHANGER, TypeVariableName.get("T")))
                .addMethod(createBindMethod());

        return JavaFile.builder(classPackage, result.build())
                .addFileComment("Generated code from Night Mode Manager. Do not modify!")
                .build();
    }

    private MethodSpec createBindMethod() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("change")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(CHANGER, "changer", FINAL)
                .addParameter(TypeVariableName.get("T"), "target", FINAL)
                .addParameter(int.class, "targetMode")
                .addParameter(boolean.class, "withAnimation");

        StringBuilder builder = new StringBuilder();
        result.addCode("//TextColor\n");
        for (FieldBinding field : mTextViewFields) {
            String values = arrayToString(field.getValues(), builder);
            result.addStatement("changer.changeTextColor(target.$N, new int[]$L, targetMode, withAnimation)", field.getName(), values);
        }

        result.addCode("\n//Background\n");
        for (FieldBinding field : mBackgroundFields) {
            String values = arrayToString(field.getValues(), builder);
            result.addStatement("changer.changeBackground(target.$N, new int[]$L, targetMode, withAnimation)", field.getName(), values);
        }

        result.addCode("\n//ImageTint\n");
        for (FieldBinding field : mImageTintFields) {
            String values = arrayToString(field.getValues(), builder);
            result.addStatement("changer.changeImageTint(target.$N, new int[]$L, targetMode, withAnimation)", field.getName(), values);
        }

        return result.build();
    }

    private String arrayToString(int[] array, StringBuilder builder) {
        builder.setLength(0);
        builder.append("{");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(array[i]);
        }
        builder.append("}");

        return builder.toString();
    }
}
