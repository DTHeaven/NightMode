package im.quar.nightmode.compiler;

import com.squareup.javapoet.TypeName;

/**
 * Created by DTHeaven on 16/1/26.
 */
final class FieldBinding {
    private final String name;
    private final TypeName type;
    private int[] values;

    FieldBinding(String name, TypeName type, int[] values) {
        this.name = name;
        this.type = type;
        this.values = values;
    }

    public String getName() {
        return this.name;
    }

    public TypeName getType() {
        return this.type;
    }

    public int[] getValues() {
        return this.values;
    }

}
