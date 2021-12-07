package ru.nsu.ccfit.khudyakov.mapping.property;

import ru.nsu.ccfit.khudyakov.type.TypeInfo;

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
        return null;
    }

    public Field getField() {
        return field;
    }

}
