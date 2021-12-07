package ru.nsu.ccfit.khudyakov.mapping.property;

import java.lang.reflect.Field;

public class PersistentPropertyAccessorImpl<T> implements PersistentPropertyAccessor<T>{

    private final T bean;

    public PersistentPropertyAccessorImpl(T object) {
        this.bean = object;
    }

    @Override
    public void setProperty(PersistentProperty<?> property, Object value) {
        Field field = property.getField();
        field.setAccessible(true);
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object getPropertyValue(PersistentProperty<?> property) {
        Field field = property.getField();
        field.setAccessible(true);
        try {
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
