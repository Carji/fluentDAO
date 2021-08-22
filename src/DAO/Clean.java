package DAO;

import java.util.List;

public class Clean {
    public static List<IRunnables> clear(List<IRunnables> runnables){
        runnables.clear();
        return runnables;
    }
}
