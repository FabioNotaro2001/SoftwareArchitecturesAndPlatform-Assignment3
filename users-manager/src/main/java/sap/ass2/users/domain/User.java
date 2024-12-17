package sap.ass2.users.domain;

import sap.ddd.Entity;

public class User implements Entity<String> {
    private String id;      
    private int credit;     

    public User(String id) {
        this.id = id;         
        this.credit = 0;       
    }

    public User(String id, int credit) {
        this.id = id;           
        this.credit = credit;   
    }

    public String getId() {
        return id;              
    }

    public int getCredit() {
        return credit;          
    }

    public void rechargeCredit(int deltaCredit) {
        credit += deltaCredit;  
    }

    public void decreaseCredit(int amount) {
        credit -= amount;       
        if (credit < 0) {      
            credit = 0;         
        }
    }

    public String toString() {
        return "{ id: " + id + ", credit: " + credit + " }";
    }
}
