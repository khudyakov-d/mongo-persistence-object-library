package ru.nsu.ccfit.khudyakov.core.mapping.document;

import org.bson.Document;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.MongoPersistentProperty;

public abstract class DocumentAccessor {

    protected final Document document;

    protected DocumentAccessor(Document document) {
        this.document = document;
    }

    public abstract void put(MongoPersistentProperty property, Object value);

    public abstract void putId(ObjectId value);

    public abstract Object get(MongoPersistentProperty property);

    public abstract ObjectId getId();

}

