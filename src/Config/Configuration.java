package Config;

public class Configuration implements IConfiguration {

    private static IConfiguration configuration = null;

    public static IConfiguration getConfiguration(){
        if(configuration == null){
            configuration = new Configuration();
        }
        return configuration;
    }
    public String getUrl(){return System.getenv("DB_URL_SB");}
    public String getUser(){return  System.getenv("user");}
    public String getPassword(){return System.getenv("password");} 
}
