package ru.nsu.ccfit.khudyakov.core.mapping.document;

import org.bson.Document;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.MongoPersistentProperty;

public class DocumentAccessorImpl extends DocumentAccessor{

    public static final String ID = "_id";

    public DocumentAccessorImpl(Document document) {
        super(document);
    }

    public void put(MongoPersistentProperty property, Object value) {
        String fieldName = property.getFieldName();
        document.put(fieldName, value);
    }

    @Override
    public void putId(ObjectId value) {
        document.put(ID, value);
    }

    public Object get(MongoPersistentProperty property) {
        return document.get(property.getFieldName());
    }

    public ObjectId getId() {
        return document.getObjectId("_id");
    }

}
