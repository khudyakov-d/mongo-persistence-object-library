package ru.nsu.ccfit.khudyakov.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {

    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }
        return clazz;
    }

    public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> clazz) {
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            Map<String, PropertyDescriptor> pds = new HashMap<>();

            for (PropertyDescriptor descriptor : descriptors) {
                pds.put(descriptor.getName(), descriptor);
            }
            return pds;
        } catch (IntrospectionException ie) {
            throw new IllegalStateException("Could not examine class '" + clazz.getName() +
                    "' using Introspector.getBeanInfo() to determine property information.", ie);
        }
    }

}
