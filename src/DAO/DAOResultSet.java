package DAO;

import java.sql.SQLException;
import java.sql.ResultSet;

public interface DAOResultSet<T> {
    public void run(ResultSet resultSet, T entity) throws SQLException;
}
