package DAO;

import java.sql.*;

public class Runnables<T> implements IRunnables {

    private final String sqlQuery;
    private final T entity;
    private final Statement<T> statement;

    public Runnables(String sqlQuery, T entity, Statement<T> statement){
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
