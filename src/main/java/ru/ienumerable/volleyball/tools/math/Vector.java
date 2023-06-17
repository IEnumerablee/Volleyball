package ru.ienumerable.volleyball.tools.math;

import org.bukkit.Location;

public class Vector {

    public double x, y, z;

    public Vector() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Location loc){
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
    }

    public void add(double val){
        x += val;
        y += val;
        z += val;
    }

    public void sub(double val){
        x -= val;
        y -= val;
        z -= val;
    }

    public void mul(double val){
        x *= val;
        y *= val;
        z *= val;
    }

    public void div(double val){
        x /= val;
        y /= val;
        z /= val;
    }

    public void add(Vector vec){
        x += vec.x;
        y += vec.y;
        z += vec.z;
    }

    public void sub(Vector vec){
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
    }

    public void mul(Vector vec){
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
    }

    public void div(Vector vec){
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
    }

    public void abs(){
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
    }

    public void ceil(){
        x = Math.ceil(x);
        x = Math.ceil(x);
        x = Math.ceil(x);
    }

    public void floor(){
        x = Math.floor(x);
        y = Math.floor(y);
        z = Math.floor(z);
    }

    public void normalize(double val){
        mul(val / length());
    }

    public double length(){
        return Math.sqrt(x * x + y * y + z * z);
    }

    public void bounce(int axis){

        if(axis <= 0 || axis > 3) throw new IllegalArgumentException("axis can only be in the range 1 to 3");

        if(axis == 1) x = 0 - x;
        if(axis == 2) y = 0 - y;
        if(axis == 3) z = 0 - z;
    }

    public Vector cloneV(){
        return new Vector(x, y, z);
    }

}
