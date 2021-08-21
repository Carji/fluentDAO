import Config.Configuration;
import DAO.EntityManager;
import Domain.Ingredient;

public class App {
    public static void main(String[] args) throws Exception {
        
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
            }).orElseThrow();

            System.out.println("We obtained an object with " +ingr2.getClass() +", and fields 'name'=" + ingr2.getName() + 
            ", 'price'=" + ingr2.getPrice() + "€ " +
            "and 'id'=" +ingr2.getId() +"."
            );
    }
}
