package sap.ass2.rides.domain;

import java.util.Date;
import java.util.Optional;

import sap.ddd.Aggregate;

public class Ride implements Aggregate<String>{
    private Date startedDate;          
    private Optional<Date> endDate;    
    private User user;                  
    private Ebike ebike;                
    private String id;                  

    public Ride(String id, User user, Ebike ebike) {
        this.id = id;                    
        this.startedDate = new Date();   
        this.endDate = Optional.empty();  
        this.user = user;                
        this.ebike = ebike;              
    }

    public String getId() {
        return id;                        
    }

    public Date getStartedDate() {
        return startedDate;               
    }

    public Optional<Date> getEndDate() {
        return endDate;                   
    }

    public User getUser() {
        return user;                      
    }

    public Ebike getEbike() {
        return ebike;                    
    }

    public String toString() {
        return "{ id: " + this.id + ", user: " + user.id() + ", bike: " + ebike.id() + " }"; 
    }
}
