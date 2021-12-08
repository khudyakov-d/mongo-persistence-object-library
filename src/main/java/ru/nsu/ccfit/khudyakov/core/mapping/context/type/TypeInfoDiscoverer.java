package ru.nsu.ccfit.khudyakov.core.mapping.context.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class TypeInfoDiscoverer<T> implements TypeInfo<T> {

    protected final Type type;

    protected TypeInfoDiscoverer(Type type) {
        this.type = type;
    }

    @Override
    public boolean isCollection() {
        return List.class.equals(getType());
    }

    public static TypeInfo<?> getInfo(Type fieldType) {
        if (fieldType == null) {
            throw new IllegalArgumentException();
        }

        if (fieldType instanceof Class) {
            return ClassTypeInfo.from((Class<?>) fieldType);
        }

        if (fieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) fieldType;
            validateParametrizedType(parameterizedType);
            return new ParametrizedListTypeInfo<>((Class<List<?>>) parameterizedType.getRawType(),
                    (Class<?>) parameterizedType.getActualTypeArguments()[0]);
        }

        throw new IllegalArgumentException();
    }

    private static void validateParametrizedType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();

        if (!(rawType instanceof Class)) {
            throw new IllegalArgumentException();
        }

        Class<?> clazz = (Class<?>) rawType;
        if (!List.class.equals(clazz)) {
            throw new IllegalArgumentException();
        }
    }

}
