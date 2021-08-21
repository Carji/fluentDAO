package DAO;

import java.sql.*;

public interface IRunnables {
    public String getSql();
    public void run(final PreparedStatement statement) throws SQLException;
}
