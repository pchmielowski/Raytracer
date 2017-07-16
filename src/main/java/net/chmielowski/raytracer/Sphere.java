package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.util.function.Function;

final class Sphere implements Shape {

    Function<Double, Double> shader;

    final Vector3D center;
    private final double radius;
    final Color color;

    Sphere(Vector3D center, double radius, Color color, Function<Double, Double> shader) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.shader = shader;
    }

    @Override
    public Intersection intersection(int x, int y) {
        return intersection(Main.CAMERA_SOURCE, Camera.direction(x, y));
    }

    @Override
    public Intersection intersection(Vector3D origin, Vector3D direction) {
        final Vector3D fromCamToCenter = center.subtract(origin);
        final double tca = fromCamToCenter.dotProduct(direction);
        if (tca < 0) {
            return Intersection.not(this);
        }
        final double d2 = fromCamToCenter.getNormSq() - tca * tca;
        if (d2 > radius * radius) {
            return Intersection.not(this);
        }
        final double thc = Math.sqrt(radius * radius - d2);
        final double distanceToCamera = tca < thc ? tca + thc : tca - thc;
        return new Intersection(true, this, direction, distanceToCamera);
    }

}
