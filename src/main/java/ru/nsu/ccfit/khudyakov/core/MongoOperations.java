package ru.nsu.ccfit.khudyakov.core;

import org.bson.Document;

import java.util.List;

public interface MongoOperations {

    <T> T save(T objectToSave);

    <T> T findById(Object id, Class<T> entityClass);

    <T> List<T> find(Document queryDocument, Class<T> entityClass);

    void remove(Object object);

}
