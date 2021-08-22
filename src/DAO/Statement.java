package DAO;

import java.sql.*;

public interface Statement<V> {
    public void run(PreparedStatement statement, V entity) throws SQLException;
}
