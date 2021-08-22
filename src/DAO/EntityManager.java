package DAO;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import Config.IConfiguration;

public class EntityManager implements IEntityManager{

    private List<IRunnables> runnables = new ArrayList<IRunnables>();
    private IConfiguration configuration = null;

    public EntityManager(IConfiguration configuration){
        this.configuration = configuration;
    }

    public static EntityManager buildConnection(IConfiguration configuration){
        return new EntityManager(configuration);
    }

    public void save() {

        Connection connection = null;

        try{
            connection = DriverManager.getConnection(
                this.configuration.getUrl(),
                this.configuration.getUser(),
                this.configuration.getPassword()
            );
            connection.setAutoCommit(false);

            for(IRunnables runnable : this.runnables){
                
                PreparedStatement statement = connection.prepareStatement(runnable.getSql());
                runnable.run(statement);
                statement.executeUpdate();
            }
            connection.commit();
        }
        catch(SQLException exception){
            try {
                connection.rollback();
            } 
            catch (SQLException exception2) {
                exception2.printStackTrace();
            }
            exception.printStackTrace();
        }
        finally{
            this.runnables = Clean.clear(this.runnables);
            try {
                if(!connection.isClosed()){
                    connection.close();
                }
            } 
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }  

    public <T> Optional<T> select(Class<T> classT, DAOResultSet<T> resultset) {

        T entity = null;
        Connection connection = null;

        try{
            connection = DriverManager.getConnection(
                this.configuration.getUrl(),
                this.configuration.getUser(),
                this.configuration.getPassword()
            );
 
            IRunnables runnable = this.runnables.get(0);

            PreparedStatement statement = connection.prepareStatement(runnable.getSql());
            runnable.run(statement);

            ResultSet resultSetSql = statement.executeQuery();

            while(resultSetSql.next()){

                entity = classT.getConstructor().newInstance();
                resultset.run(resultSetSql, entity);
            }
        }
        catch(SQLException |  IllegalAccessException | IllegalArgumentException | InstantiationException |InvocationTargetException | SecurityException | NoSuchMethodException exception){
            exception.printStackTrace();
        }
        finally{
            this.runnables = Clean.clear(this.runnables);
            try {
                if(!connection.isClosed()){
                    connection.close();
                }
            } 
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        }       
        return Optional.of(entity);
    }

    public <T> IEntityManager addStatement(T entity, String sql, Statement<T> statement) {
        IRunnables runnable = new Runnables<T>(sql, entity, statement);
        this.runnables.add(runnable);
        return this;
    }


    public <T> IEntityManager addRangeStatement(Iterable<T> iterable, String sql, Statement<T> statement) {
        
        for(T entity : iterable){
            IRunnables runnable = new Runnables<T>(sql, entity, statement);
            this.runnables.add(runnable);
        }
        return this;
    }


}
