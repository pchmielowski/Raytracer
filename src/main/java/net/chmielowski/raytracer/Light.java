package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


final class Light {
    private final Vector3D center;

    Light(Vector3D center) {
        this.center = center;
    }

    Vector3D getDirection(Vector3D pointOfHit) {
        return center.subtract(pointOfHit).normalize();
    }

    double getIntensity(Vector3D pointOfHit, Vector3D normalToPointOfHit) {
        return Math.max(0., normalToPointOfHit.dotProduct(getDirection(pointOfHit)));
    }

}
