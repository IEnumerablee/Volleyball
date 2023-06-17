package ru.ienumerable.volleyball.tools.math;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Ray {

    private final Angle angle;
    private Location offset;


    public Ray(Angle angle) {
        this.angle = angle;
    }

    public Ray(Location loc, boolean useOffset){
        angle = new Angle(loc);
        if(useOffset) offset = loc;
    }

    public Ray(Location loc, Angle angle){
        this.angle = angle;
        offset = loc;
    }

    public List<Vector> cast(double distance, double step){

        if(distance < step) throw new IllegalArgumentException("Distance cannot below the step");

        List<Vector> points = new ArrayList<>();

        for(double i = step; i <= distance;i += step){
            points.add(getPoint(i));
        }

        return points;
    }

    public Vector getPoint(double distance) {
        double cc = distance - (Math.abs(angle.pitch) * (distance / (Math.PI / 2)));

        Vector point = new Vector(
                cc * Math.sin(angle.yaw),
                distance * Math.sin(angle.pitch),
                cc * Math.cos(angle.yaw)
        );

        if(offset != null){
            point.x += offset.getX();
            point.y += offset.getY();
            point.z += offset.getZ();
        }

        return point;
    }

}
