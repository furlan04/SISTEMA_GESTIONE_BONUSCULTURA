package it.unimib.sd2025.Repository;

/**
 * Repository is a generic interface for managing entities in a database.
 * It provides methods to check existence, create, and retrieve entities by their ID.
 *
 * @param <T> the type of entity managed by the repository
 */
public interface Repository<T> {
    public boolean exists(String id) throws Exception;
    public T create(T entity) throws Exception;
    public T get(String id) throws Exception;
}
