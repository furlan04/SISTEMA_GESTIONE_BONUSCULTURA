package it.unimib.sd2025;

import java.util.Map;

public class Database {
    private Map<String, String> data;
    
    public Database (){
        this.data = new Map();
    }

    public action(String command){
        String method = command.split(" ")[0];
        String key = command.split(" ")[1];
        String value = "";
        if(method.equals("set")){
            value = command.split(" ")[2];
        }

        switch (method.toLowerCase()) {
            case "get":
                return get(key);
        
            case "set":
                return set(key, value);

            case "delete":
                return delete(key);

            default:
                throw Exception("command not found!");
        }
    }

    public String get(String key){

    }
    
    public String set(String key, String value){

    }

    public String delete(String key){

    }
}
