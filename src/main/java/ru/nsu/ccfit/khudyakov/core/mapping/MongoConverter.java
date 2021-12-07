package ru.nsu.ccfit.khudyakov.core.mapping;

import org.bson.Document;

public interface MongoConverter {

    <S> S read(Class<S> type, Document bson);

    Document write(Object obj);

}
