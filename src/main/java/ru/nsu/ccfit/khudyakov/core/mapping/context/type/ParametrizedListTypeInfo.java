package ru.nsu.ccfit.khudyakov.core.mapping.context.type;

import java.util.List;

public class ParametrizedListTypeInfo<T> extends TypeInfoDiscoverer<List<?>> {

    protected Class<List<?>> parentType;
    protected Class<T> argumentType;

    protected ParametrizedListTypeInfo(Class<List<?>> parentType, Class<T> argumentType) {
        super(argumentType);
        this.parentType = parentType;
        this.argumentType = argumentType;
    }

    @Override
    public Class<List<?>> getType() {
        return parentType;
    }

    public Class<T> getArgumentType() {
        return argumentType;
    }

}
