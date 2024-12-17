package sap.ass2.rides.domain;

import sap.ddd.ValueObject;

public enum EbikeState implements ValueObject { 
    AVAILABLE,      
    IN_USE,        
    MAINTENANCE,   
    DISMISSED      
}