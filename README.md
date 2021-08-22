# fluentDAO
Abstraction (and correction) of the previous DAO (Data access object) [implementation](https://github.com/Carji/AccessDataExam) in Java.

## Context.

The purpose of this exercise is implementing a DAO layer, with the intention of keeping the domain model completely decoupled from the persistence layer. Along the way certain abstractions will be introduced, such as lambda functions and generics.

The connection to the database will be handled using Java Database Connectivity (JDBC), storing the connection parameters as enviroment variables for increased security.

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

One functional interface is implemented in this case.

### Statement.

Functional interface whose purpose is to provide a statement and an entity to the lambda expression.

## Runnables.

Class where we store an entity, an SQL query and a statement.

## IEntityManager.

Interface where the CRUD operation methods are stablished.
