package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.util.Collection;
import java.util.Optional;


class Intersection {
    final boolean intersects;
    private final Shape shape;
    private final Vector3D cameraDirection;
    private final double distanceToCamera;
    private final Vector3D mPointOfHit;
    Color color = null;

    Intersection(boolean intersects, Shape shape, Vector3D cameraDirection, double distanceToCamera, Vector3D pointOfHit) {
        this.intersects = intersects;
        this.shape = shape;
        this.cameraDirection = cameraDirection;
        this.distanceToCamera = distanceToCamera;
        this.mPointOfHit = pointOfHit;
    }

    static Intersection not(Sphere sphere) {
        return new Intersection(false, sphere, null, 0, null);
    }

    private static Color sumColors(final Color first, final Color second) {
        return new Color(getMin(first.getRed(), second.getRed()),
                getMin(first.getGreen(), second.getGreen()),
                getMin(first.getBlue(), second.getBlue()));
    }

    private static int getMin(int first, int second) {
        return Math.min(first + second, 255);
    }

    Color getColor(Collection<Light> lights, Collection<Shape> objects) {
        final Vector3D pointOfHit = Optional.ofNullable(this.mPointOfHit)
                .orElseGet(() -> Main.CAMERA_SOURCE.add(cameraDirection).scalarMultiply(distanceToCamera()));
        final Vector3D normalToPointOfHit = shape.getNormal(pointOfHit);
        return lights.stream()
                .filter(light -> objects.stream()
                        .noneMatch(object -> isOnAWayToLight(pointOfHit, normalToPointOfHit, light, object)))
                .map(light -> shape.getColor(pointOfHit, normalToPointOfHit, light))
                .reduce(Intersection::sumColors)
                .orElse(Color.BLACK);
    }

    private boolean isOnAWayToLight(Vector3D pointOfHit, Vector3D normalToPointOfHit, Light light, Shape object) {
        return object.intersection(
                pointOfHit.add(normalToPointOfHit),
                light.getDirection(pointOfHit)).intersects;
    }

    double distanceToCamera() {
        return distanceToCamera;
    }
}
