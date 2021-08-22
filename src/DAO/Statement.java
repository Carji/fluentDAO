package DAO;

import java.sql.*;

public interface Statement<T> {
    public void run(PreparedStatement statement, T entity) throws SQLException;
}
