package ru.nsu.ccfit.khudyakov.core.mapping.context.type;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public abstract class TypeInfoDiscoverer<T> implements TypeInfo<T> {

    protected final Class<T> type;

    protected TypeInfoDiscoverer(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    public static TypeInfo<?> getInfo(Type fieldType) {
        if (fieldType == null) {
            throw new IllegalArgumentException();
        }

        if (fieldType instanceof Class) {
            return ClassTypeInfo.from((Class<?>) fieldType);
        }

        if (fieldType instanceof GenericArrayType) {
            throw new IllegalArgumentException();
        }

        throw new IllegalArgumentException();
    }

}
