package com.university.lab2;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO contract. T = entity type, ID = type of its primary key.
 * StudentDAO and CourseDAO both implement it, so they share the same CRUD vocabulary.
 */
public interface GenericDAO<T, ID> {
    T save(T entity);                 // Create  (returns the entity with its new id)
    Optional<T> findById(ID id);      // Read one (Optional = "might not exist")
    List<T> findAll();                // Read all
    boolean update(T entity);         // Update  (true if a row changed)
    boolean delete(ID id);            // Delete  (true if a row was removed)
}
