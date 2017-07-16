package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

final class Sphere extends Shape {

    private final Vector3D center;
    private final double radius;

    Sphere(Vector3D center, double radius, Material material) {
        super(material);
        this.center = center;
        this.radius = radius;
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
        return new Intersection(true, this, direction, distanceToCamera, /*pointOfHit*/ null);
    }

    @Override
    public Vector3D getCenter() {
        return center;
    }

    @Override
    public Vector3D getNormal(Vector3D pointOfHit) {
        // TODO: flip normal if we are inside
        //            if (direction.dotProduct(normalToPointOfHit) > 0)
        //                normalToPointOfHit = normalToPointOfHit.negate();
        return pointOfHit.subtract(getCenter()).normalize();
    }
}
