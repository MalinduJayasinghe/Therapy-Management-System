package lk.ijse.therapy_management_system.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, ID> extends SuperDAO {
    List<T> getAll();
    Optional<T> findById(ID id);
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(ID id);
    String getLastId();
}
