package DAO;

import java.sql.*;

public class Runnables<V> implements IRunnables {

    private final String sqlQuery;
    private final V entity;
    private final Statement<V> statement;

    public Runnables(String sqlQuery, V entity, Statement<V> statement){
        this.sqlQuery = sqlQuery;
        this.entity = entity;
        this.statement = statement;
    }

    public String getSql() {
        return this.sqlQuery;
    }

    public void run(PreparedStatement statement) throws SQLException {
        this.statement.run(statement, this.entity);
    }
}
