package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.util.*;
import java.util.function.Function;


final class Sphere {

    private Function<Double, Double> shader;

    static class Intersection {
        final boolean intersects;
        final Sphere sphere;
        final Vector3D cameraDirection;
        private final double distanceToCamera;

        Intersection(boolean intersects, Sphere sphere, Vector3D cameraDirection, double distanceToCamera) {
            this.intersects = intersects;
            this.sphere = sphere;
            this.cameraDirection = cameraDirection;
            this.distanceToCamera = distanceToCamera;
        }

        static Intersection not(Sphere sphere) {
            return new Intersection(false, sphere, null, 0);
        }

        static Color sumColors(final Color first, final Color second) {
            return new Color(getMin(first.getRed(), second.getRed()),
                    getMin(first.getGreen(), second.getGreen()),
                    getMin(first.getBlue(), second.getBlue()));
        }

        private static int getMin(int first, int second) {
            return Math.min(first + second, 255);
        }

        int withIntensity(double colorIntensity, int value) {
            return Math.max(Math.min((int) (value * sphere.shader.apply(colorIntensity)), 255), 0);
        }

        Color getColor(Collection<Light> lights, Collection<Sphere> objects) {
            final Vector3D pointOfHit = Main.CAMERA_SOURCE.add(cameraDirection).scalarMultiply(distanceToCamera());
            final Vector3D normalToPointOfHit = getNormal(pointOfHit);

            return lights.stream()
                    .filter(light -> objects.stream()
                            .noneMatch(object -> isOnAWayToLight(pointOfHit, normalToPointOfHit, light, object)))
                    .map(light -> light.getColor(pointOfHit, normalToPointOfHit, sphere.color, this))
                    .reduce(Intersection::sumColors)
                    .orElse(Color.BLACK);
        }

        private boolean isOnAWayToLight(Vector3D pointOfHit, Vector3D normalToPointOfHit, Light light, Sphere object) {
            return object.intersection(
                    pointOfHit.add(normalToPointOfHit),
                    light.getDirection(pointOfHit)).intersects;
        }

        private Vector3D getNormal(Vector3D pointOfHit) {
            // TODO: flip normal if we are inside
            //            if (direction.dotProduct(normalToPointOfHit) > 0)
            //                normalToPointOfHit = normalToPointOfHit.negate();
            return pointOfHit.subtract(sphere.center).normalize();
        }

        double distanceToCamera() {
            return distanceToCamera;
        }
    }

    final Vector3D center;
    private final double radius;
    final Color color;

    Sphere(Vector3D center, double radius, Color color, Function<Double, Double> shader) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.shader = shader;
    }

    Intersection intersection(int x, int y) {
        return intersection(Main.CAMERA_SOURCE, Camera.direction(x, y));
    }

    private Intersection intersection(Vector3D origin, Vector3D direction) {
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
