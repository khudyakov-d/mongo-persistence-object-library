package ru.nsu.ccfit.khudyakov.core.mapping.context.property;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PersistentPropertyAccessorImpl<T> implements PersistentPropertyAccessor<T>{

    private final T bean;

    public PersistentPropertyAccessorImpl(T object) {
        this.bean = object;
    }

    @Override
    public void setProperty(PersistentProperty<?> property, Object value) {
        try {
            PropertyDescriptor descriptor = property.getDescriptor();
            Method writeMethod = descriptor.getWriteMethod();
            writeMethod.invoke(bean, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object getPropertyValue(PersistentProperty<?> property) {
        try {
            PropertyDescriptor descriptor = property.getDescriptor();
            Method readMethod = descriptor.getReadMethod();
            return readMethod.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public T getBean() {
        return bean;
    }

}
