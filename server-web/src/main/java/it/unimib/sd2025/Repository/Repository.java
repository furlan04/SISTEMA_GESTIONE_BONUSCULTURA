package it.unimib.sd2025.Repository;

public interface Repository<T> {
    public boolean exists(String id) throws Exception;
    public T create(T entity) throws Exception;
    public T get(String id) throws Exception;
}
