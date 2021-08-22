package DAO;

import java.util.Optional;

public interface IEntityManager {
    
    public void save();

    public <V> Optional<V> select(Class<V> classV, DAOResultSet<V> resultset);

    public <V> IEntityManager addStatement(final V entity, String sql, Statement<V> statement);
    public <V> IEntityManager addRangeStatement(final Iterable<V> iterable, String sql, Statement<V> statement);
}