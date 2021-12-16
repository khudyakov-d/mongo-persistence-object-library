package ru.nsu.ccfit.khudyakov.core;

import ru.nsu.ccfit.khudyakov.core.mapping.query.Criteria;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, I> {

    Optional<T> findById(I id);

    List<T> findAll(Criteria criteria);

    <P> List<P> findAll(Criteria criteria, Class<P> projectionClass);

    <S extends T> S save(S entity);

    <S extends T> void delete(S entity);

}
