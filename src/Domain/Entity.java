package Domain;
public abstract class Entity {
    public String id;

    public void generateID(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @Override
    public boolean equals (Object obj) {

        if (!(obj instanceof Entity)){
            return false;
        }

        Entity tmpEntity = (Entity) obj;

        return this.getId().equals(tmpEntity.getId());
    }

    @Override
    public int hashCode(){
        return this.getId().hashCode();
    }
}
