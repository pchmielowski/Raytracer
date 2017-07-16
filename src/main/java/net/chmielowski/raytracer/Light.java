package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;


final class Light {
    private final Vector3D center;

    Light(Vector3D center) {
        this.center = center;
    }

     Vector3D getDirection(Vector3D pointOfHit) {
        return center.subtract(pointOfHit).normalize();
    }

    private double getIntensity(Vector3D pointOfHit, Vector3D normalToPointOfHit) {
        return Math.max(0., normalToPointOfHit.dotProduct(getDirection(pointOfHit)));
    }

    Color getColor(Vector3D pointOfHit, Vector3D normalToPointOfHit, Color color, Intersection intersection) {
        double colorIntensity = getIntensity(pointOfHit, normalToPointOfHit);
        return new Color(intersection.withIntensity(colorIntensity, color.getRed()),
                intersection.withIntensity(colorIntensity, color.getGreen()),
                intersection.withIntensity(colorIntensity, color.getBlue()));
    }
}
