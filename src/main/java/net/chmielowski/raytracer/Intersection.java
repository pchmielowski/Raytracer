package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.util.Collection;


class Intersection {
    private final boolean intersects;
    private final Shape shape;
    private final double distanceToCamera;
    private final Vector3D pointOfHit;

    Intersection(boolean intersects, Shape shape, double distanceToCamera, Vector3D pointOfHit) {
        this.intersects = intersects;
        this.shape = shape;
        this.distanceToCamera = distanceToCamera;
        this.pointOfHit = pointOfHit;
    }

    static Intersection not(Sphere sphere) {
        return new Intersection(false, sphere, 0, null);
    }

    private static Color sumColors(final Color first, final Color second) {
        return new Color(getMin(first.getRed(), second.getRed()),
                getMin(first.getGreen(), second.getGreen()),
                getMin(first.getBlue(), second.getBlue()));
    }

    private static int getMin(int first, int second) {
        return Math.min(first + second, 255);
    }

    boolean intersects() {
        return this.intersects;
    }

    Intersection isCloser(Intersection other) {
        return distanceToCamera < other.distanceToCamera ? this : other;
    }

    Color getColor(Collection<Light> lights, Collection<Shape> objects) {
        final Vector3D normalToPointOfHit = shape.getNormal(pointOfHit);
        return lights.stream()
                .filter(light -> objects.stream()
                        .noneMatch(object -> isOnAWayToLight(pointOfHit, light, object)))
                .map(light -> shape.getColor(pointOfHit, normalToPointOfHit, light))
                .reduce(Intersection::sumColors)
                .orElse(Color.BLACK);
    }

    private boolean isOnAWayToLight(Vector3D pointOfHit, Light light, Shape object) {
        return object.intersection(pointOfHit, light.getDirection(pointOfHit)).intersects;
    }

}
