package ru.nsu.ccfit.khudyakov.core.mapping.context.property;

import ru.nsu.ccfit.khudyakov.core.exception.NotValidPropertyTypeException;
import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.PersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;
import ru.nsu.ccfit.khudyakov.core.persistence.Ref;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class MongoPersistentProperty extends AbstractPersistentProperty<MongoPersistentProperty> {

    private static final List<Class<?>> SIMPLE_TYPE_WRAPPERS;

    static {
        SIMPLE_TYPE_WRAPPERS = new ArrayList<>();
        SIMPLE_TYPE_WRAPPERS.add(Byte.class);
        SIMPLE_TYPE_WRAPPERS.add(Short.class);
        SIMPLE_TYPE_WRAPPERS.add(Integer.class);
        SIMPLE_TYPE_WRAPPERS.add(Long.class);
        SIMPLE_TYPE_WRAPPERS.add(Float.class);
        SIMPLE_TYPE_WRAPPERS.add(Double.class);
        SIMPLE_TYPE_WRAPPERS.add(String.class);
        SIMPLE_TYPE_WRAPPERS.add(Boolean.class);
    }

    public MongoPersistentProperty(Property property, PersistentEntity<?, MongoPersistentProperty> owner) {
        super(property, owner);
    }

    @Override
    public PersistentEntity<?, MongoPersistentProperty> getOwner() {
        return owner;
    }

    @Override
    public String getFieldName() {
        return property.getName();
    }

    @Override
    public TypeInfo<?> getTypeInfo() {
        return property.getType();
    }

    @Override
    public Field getField() {
        return property.getField();
    }

    @Override
    public boolean isAssociation() {
        return getField().isAnnotationPresent(Ref.class);
    }

    @Override
    public boolean isTransient() {
        return Modifier.isTransient(getField().getModifiers());
    }

    @Override
    public boolean isLazyAssociation() {
        return getField().getAnnotation(Ref.class).lazy();
    }

    public PropertyDescriptor getDescriptor() {
        return property.getDescriptor();
    }

    public static void validateProperty(Property property) {
        TypeInfo<?> type = property.getType();
        Field propertyField = property.getField();

        boolean isRef = propertyField.isAnnotationPresent(Ref.class);
        boolean isSimpleTypeWrapper = isSimpleTypeWrapper(type.getType());

        if (isRef && (isSimpleTypeWrapper || type.getType().isPrimitive())) {
            throw new NotValidPropertyTypeException(format("The reference field \"%s\" must not be a "
                            + "wrapper over primitive type or primitive type", propertyField));
        }

        if (!isRef && !isSimpleTypeWrapper) {
            throw new NotValidPropertyTypeException(
                    format("The non-reference field \"%s\" must be a wrapper over primitive type", propertyField));
        }
    }

    private static boolean isSimpleTypeWrapper(Class<?> clazz) {
        return SIMPLE_TYPE_WRAPPERS.contains(clazz);
    }

}
