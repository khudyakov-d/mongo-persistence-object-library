package ru.nsu.ccfit.khudyakov.core.mapping.context.property;

import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfoDiscoverer;

import java.lang.reflect.Field;

public class Property {

    private final Field field;
    private final TypeInfo<?> parentType;

    public Property(TypeInfo<?> parentType, Field field) {
        this.field = field;
        this.parentType = parentType;
    }

    public String getName() {
        return field.getName();
    }

    public TypeInfo<?> getType() {
        return TypeInfoDiscoverer.getInfo(field.getGenericType());
    }

    public Field getField() {
        return field;
    }

}
