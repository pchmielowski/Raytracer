package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


class Disc extends Shape {
    private final Vector3D center;
    private final int radius;
    private final Vector3D normal;

    Disc(Vector3D center, int radius, Vector3D normal, Material material) {
        super(material);
        this.center = center;
        this.radius = radius;
        this.normal = normal;
    }

    @Override
    public Intersection intersection(Vector3D origin, Vector3D direction) {
        // Equation from https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
        double d = center.subtract(origin).dotProduct(normal) / direction.dotProduct(normal);
        final Vector3D pointOfHit = origin.add(direction).scalarMultiply(d);
        return new Intersection(pointOfHit.subtract(center).getNorm() <= radius, this,/*camera direction*//*distance to camera*/0,
                pointOfHit);

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
