package sap.ass2.ebikes.domain;

import sap.ddd.ValueObject;

public class P2d implements java.io.Serializable, ValueObject {
    private double x, y; 

    public P2d(double x, double y) {
        this.x = x; 
        this.y = y; 
    }

    public double x() {
        return x; 
    }

    public double y() {
        return y; 
    }

    public P2d sum(V2d v) {
        return new P2d(x + v.x(), y + v.y()); 
    }

    public V2d sub(P2d v) {
        return new V2d(x - v.x(), y - v.y()); 
    }

    public V2d toV2d() {
        return new V2d(this.x, this.y);
    }
    
    public static P2d zero() {
        return new P2d(0, 0);
    }

    public String toString() {
        return "P2d(" + x + "," + y + ")"; 
    }
}
