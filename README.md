# fluentDAO
Abstraction (and correction) of the previous DAO (Data access object) [implementation](https://github.com/Carji/AccessDataExam) in Java.

## Context.

The purpose of this exercise is implementing a DAO layer, with the intention of keeping the domain model completely decoupled from the persistence layer. Along the way certain abstractions will be introduced, such as lambda functions and generics.

The connection to the database will be handled using Java Database Connectivity (JDBC), storing the connection parameters as enviroment variables for increased security.

## **Use of generics.**

Across the exercise generic classes have been implemented. These are noted with the parameter T between <>. The use of generics have two advantages:

- **Code reuse.** A class/method/interface can be written once, then used as many times as needed.
- **Type safety.** Generics make errors appear at run time. This saves time coding and debugging.
- **Individual Type Casting is not needed.** Casts are mostly eliminated.
- **Generics allow the implementation of generic algorithms.** These can work on different types and objects, and facilitate the readability of our code.

## **Configuration**.

Handled by [IConfiguration.java](https://github.com/Carji/fluentDAO/blob/main/src/Config/IConfiguration.java) interface. It's methods are implemented in [Configuration.java](https://github.com/Carji/fluentDAO/blob/main/src/Config/Configuration.java). This class represents a singleton class which contains all the required driver information to connect to the database.

```java
    public String getUrl(){return System.getenv("DB_URL");}
    public String getUser(){return  System.getenv("user");}
    public String getPassword(){return System.getenv("password");} 
```

## **Model object**.

The data manipulation tasks will be performed using a model object of [Ingredient](https://github.com/Carji/fluentDAO/blob/main/src/Domain/Ingredient.java).

## **Connection**.

Established into EntityManager inside each CRUD method with the parameters described previously.

## **Functional interfaces**:

Added in Java version 8. 

>Functional interfaces provide target types for lambda expressions and method references. Each functional interface has a single abstract method, called the functional method for that functional interface, to which the lambda expression's parameter and return types are matched or adapted. Functional interfaces can provide a target type in multiple contexts, such as assignment context, method invocation, or cast context.

Two functional interfaces are implemented in this case.

### Statement.

Functional interface whose purpose is to provide a statement and an entity to the lambda expression.

### DAOResultSet.

Functional interface whose purpose is to provide an abstract method comprised of a ResultSet and an entity.

## **Clean.**

Class used to clean Runnables with the implemented method on it.

## **Runnables.**

Class where we store an entity, an SQL query and a statement.

## **IEntityManager.**

Interface where the CRUD operation methods are stablished.

## **EntityManager.**

The methods stated at IEntityManager are implemented in this class. Before implementing the four methods of the corresponding interface, there are several requirements:

* Configuration from the singleton class needs to be imported, declared as null, taken as field and instanced.
* A list of Runnables must me declared.

### **Save().**

The flow of the method is the usual try-catch-finally.
- Connection initialiced as null.


Try:
```java
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
```
- Singleton used to get connection configuration.
- Autocommit set as false (to allow rollback in case of exceptions).
- Iterate through runnables with a *for*, then commit.

Catch:
- If any exception arises -> rollback. This reverts the failed transaction.

Finally:
- Runnables are cleared with Clean.clear().
- Connection is closed in case it's still open.

### **Select().**

- Entity initialiced as null.
- Connection initialiced as null.
```java
    public <T> Optional<T> select(Class<T> classT, DAOResultSet<T> resultset) {

        T entity = null;
        Connection connection = null;
```



The flow of the method is the usual try-catch-finally.

Try:

```java
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
```
- Singleton used to get connection configuration.
- Pick the runnable at position 0.
- Statement is prepared, run and the query is executed.
- Iterating through the resultset, required entities are created.

Catch:
- Usual exceptions are printed here, if required.

Finally:
- Runnables are cleared with Clean.clear().
- Connection is closed in case it's still open.

Selected objects are returned (if not null).

```java
        return Optional.of(entity);
```

# DAOIngredient.

Two samples of the code are provided at App.java, one of save() to include an ingredient in the database, and other of select() to recover the added ingredient from the database by it's ID.

## Save().

```java
        final int ingId = (int) Math.round(Math.random()*100);
        
        final String saveSql = "INSERT INTO ingredient(id, name, price) VALUES(?, ?, ?)";

        Ingredient ingr = new Ingredient();
        ingr.setId(ingId+"");
        ingr.setName("TomatoSauce"+(ingId+""));
        ingr.setPrice(0.75);

        EntityManager
            .buildConnection(Configuration.getConfiguration())
            .addStatement(ingr, saveSql, (statement, entity)->{
                statement.setString(1, entity.id);   
                statement.setString(2, entity.getName());  
                statement.setDouble(3, entity.getPrice());  
            })
            .save();
            System.out.println("Added ingredient " + ingr.getName() + 
                               " with price " + ingr.getPrice() + "€ " +
                               "and id " +ingr.getId() +"."
            );
```
- A random ID is generated.
- The Sql query required is defined on a final string.
- A new ingredient object is created, then it's fields (ID, name, price) are set correctly.

All that's left is to use the previously developed fluent interface, being:
```java
Entity.buildConnection().addStatement().save();
```
Take into account that a lambda function is used on the .addStatement() part, to correctly provide the required info for the statement (it replaces the "?" in saveSql string with the correct parameters, preventing SQL injection).

## Select().

```java
Ingredient ingr2 = new Ingredient();

        final String selectSql = "SELECT id, name, price FROM ingredient WHERE id=?";
        
        ingr2 = EntityManager
            .buildConnection(Configuration.getConfiguration())
            .addStatement(ingr, selectSql, (statement, entity) -> {
                statement.setString(1, ingr.id);
            })
            .select(Ingredient.class, (resultSet, entity) -> {
                entity.setId(resultSet.getString("id"));
                entity.setName(resultSet.getString("name"));
                entity.setPrice(resultSet.getDouble("price"));
            })
            .orElseThrow();

            System.out.println("We obtained an object with " +ingr2.getClass() +", and fields 'name'=" + ingr2.getName() + 
            ", 'price'=" + ingr2.getPrice() + "€ " +
            "and 'id'=" +ingr2.getId() +"."
            );
```

The case is analog to the save one. The most remarkable difference is that we need to provide a DAOResultSet and the class of the previously instanced ingredient as parameters for the select section

- A new ingredient object is created.
- The Sql query required is defined on a final string.

All that's left is to use the previously developed fluent interface, being:

```java
Entity.buildConnection().addStatement().select().OrElseThrow();
```
Take into account that a lambda function is used on the .addStatement() part, to correctly provide the required info for the statement (it replaces the "?" in saveSql string with the correct parameters, preventing SQL injection).

The method will return a non-null object with ingredient class and the fields resulting from the select operation in the SQL database.

## Results.

If we run the code in App.java, we obtain the following:

```shell
Added ingredient TomatoSauce58 with price 0.75€ and id 58.
We obtained an object with class Domain.Ingredient, and fields 'name'=TomatoSauce58, 'price'=0.75€ and 'id'=58.
```

- One new entry on the ingredient table appears, with ID 58, name TomatoSauce58 and price 0.75€
- An ingredient class object is returned (named ingredient2) with the same fields as the one in the database.
