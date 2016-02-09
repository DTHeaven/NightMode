package im.quar.nightmode.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by DTHeaven on 16/2/9.
 */
final class MethodBinding {
    private final String name;
    private final List<Parameter> parameters;

    MethodBinding(String name, List<Parameter> parameters) {
        this.name = name;
        this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
}
