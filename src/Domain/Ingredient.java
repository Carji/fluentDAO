package Domain;
public class Ingredient extends Entity{

    private String name;
    private double prize;

    public Ingredient(){
    }

    public Ingredient(String name, double prize){
        this.name = name;
        this.prize = prize;
    }

    public String getName(){
        return this.name;
    }

    public double getPrice(){
        return this.prize;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrice(double prize){
        this.prize = prize;
    }
}