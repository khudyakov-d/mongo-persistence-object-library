package ru.nsu.ccfit.khudyakov.type;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassTypeInfo<T> extends TypeInfoDiscoverer<T> {

    private static final Map<Class<?>, ClassTypeInfo<?>> CLASS_TYPE_INFO_CACHE = new ConcurrentHashMap<>();

    public ClassTypeInfo(Class<T> type) {
        super(type);
    }
    
    public static <T> ClassTypeInfo<T> from(Class<T> type) {
        return (ClassTypeInfo<T>) CLASS_TYPE_INFO_CACHE.computeIfAbsent(type, ClassTypeInfo::new);
    }
    
    @Override
    public Class<T> getType() {
        return type;
    }

}
