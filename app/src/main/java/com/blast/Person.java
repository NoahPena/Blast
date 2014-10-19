package com.blast;

import java.util.ArrayList;

public class Person {

    private String name;
    private ArrayList<Contact> contacts;

    public Person(String name, ArrayList<Contact> contacts){
        this.name = name;
        this.contacts = contacts;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

	public String toString(){
		return name;
	}
}
