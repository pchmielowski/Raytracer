import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;

/**
 * Created by piotrek on 16.07.17.
 */
final class Light {
    private final Vector3D center;

    Light(Vector3D center) {
        this.center = center;
    }

    private Vector3D getDirection(Vector3D pointOfHit) {
        return center.subtract(pointOfHit).normalize();
    }

    private double getIntensity(Vector3D pointOfHit, Vector3D normalToPointOfHit) {
        return Math.max(0., normalToPointOfHit.dotProduct(getDirection(pointOfHit)));
    }

    Color getColor(Vector3D pointOfHit, Vector3D normalToPointOfHit, Color color, Sphere.Intersection intersection) {
        double colorIntensity = getIntensity(pointOfHit, normalToPointOfHit);
        return new Color(intersection.withIntensity(colorIntensity, color.getRed()),
                intersection.withIntensity(colorIntensity, color.getGreen()),
                intersection.withIntensity(colorIntensity, color.getBlue()));
    }
}
