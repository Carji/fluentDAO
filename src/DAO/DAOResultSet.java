package DAO;

import java.sql.*;

public interface DAOResultSet<V> {
    public void run(ResultSet resultSet, V entity) throws SQLException;
}
