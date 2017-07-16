package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.util.function.Function;


class Disc implements Shape {
    private final Vector3D center;
    private final int radius;
    private final Vector3D normal;
    private final Color color;
    private final Function<Double, Double> shader;

    Disc(Vector3D center, int radius, Vector3D normal, Color color, Function<Double, Double> shader) {
        this.center = center;
        this.radius = radius;
        this.normal = normal;
        this.color = color;
        this.shader = shader;
    }

    @Override
    public Intersection intersection(Vector3D origin, Vector3D direction) {
        // Equation from https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
        double d = center.subtract(origin).dotProduct(normal) / direction.dotProduct(normal);
        final Vector3D pointOfHit = origin.add(direction).scalarMultiply(d);
        final Intersection intersection = new Intersection(pointOfHit.subtract(center).getNorm() <= radius, this,/*camera direction*/null,/*distance to camera*/0,
                pointOfHit);
        intersection.color = color;
        return intersection;

    }

    @Override
    public Function<Double, Double> getShader() {
        return shader;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Vector3D getCenter() {
        return center;
    }

    @Override
    public Vector3D getNormal(Vector3D pointOfHit) {
        return normal.normalize();
    }
}
