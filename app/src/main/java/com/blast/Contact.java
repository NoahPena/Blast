package com.blast;

/**
 * Created by khanguy on 10/19/14.
 */
public class Contact {

    private String type;
    private String name;

    public Contact(String type, String user){
        this.type=type;
        this.name=user;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }
}
