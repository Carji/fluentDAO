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
            catch (SQLException excepcion2) {
                excepcion2.printStackTrace();
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

    public <V> Optional<V> select(Class<V> classV, DAOResultSet<V> resultset) {

        V entity = null;
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

            ResultSet resultSetSQL = statement.executeQuery();

            while(resultSetSQL.next()){

                entity = classV.getConstructor().newInstance();
                resultset.run(resultSetSQL, entity);
            }
        }
        catch(SQLException |  IllegalAccessException | IllegalArgumentException | InstantiationException |InvocationTargetException | SecurityException | NoSuchMethodException exception){
            exception.printStackTrace();
        }
        finally {
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

    public <V> IEntityManager addStatement(V entity, String sql, Statement<V> statement) {
        IRunnables runnable = new Runnables<V>(sql, entity, statement);
        this.runnables.add(runnable);
        return this;
    }


    public <V> IEntityManager addRangeStatement(Iterable<V> iterable, String sql, Statement<V> statement) {
        
        for(V entity : iterable){
            IRunnables runnable = new Runnables<V>(sql, entity, statement);
            this.runnables.add(runnable);
        }
        return this;
    }


}
