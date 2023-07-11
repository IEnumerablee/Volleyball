package ru.ienumerable.volleyball.tools.math;

import org.bukkit.Location;

public class Angle {

    public double yaw, pitch;

    public Angle(double yaw, double pitch, boolean convert) {
        if(convert){
            yaw = convertAngle(yaw);
            pitch = convertAngle(pitch);
        }else {
            this.yaw = yaw;
            this.pitch = yaw;
        }
    }

    public Angle(Vector vec){
        vectorToAngle(vec);
    }

    public Angle(Location loc){
        yaw = convertAngle(loc.getYaw());
        pitch = convertAngle(loc.getPitch());
    }

    public Angle(){
        yaw = 0;
        pitch = 0;
    }

    public void add(Angle ang){
        pitch += ang.pitch;
        yaw += ang.yaw;
    }

    public void normalize(double val){
        mul(val / length());
    }

    public double length(){
        return Math.sqrt(yaw * yaw + pitch * pitch);
    }

    private double convertAngle(double val){
        return -val * 0.017453;
    }

    public void vectorToAngle(Vector vec){
        pitch = -((Math.atan2(Math.abs(vec.x) + Math.abs(vec.z), vec.y) / 0.017453) - 90);
        yaw = -Math.atan2(vec.x, vec.z) / 0.017453;
    }

    private void mul(double val){
        pitch *= val;
        yaw *= val;
    }

}
