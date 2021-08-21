package DAO;

import java.sql.*;

public interface DAOResultSet<T> {
    public void run(ResultSet resultSet, T entity) throws SQLException;
}
