package DAO;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Statement<T> {
    public void run(PreparedStatement statement, T entity) throws SQLException;
}
