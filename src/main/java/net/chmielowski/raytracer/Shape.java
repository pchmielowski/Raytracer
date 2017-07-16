package net.chmielowski.raytracer;


import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.util.function.Function;

interface Shape {

    static int withIntensity(double colorIntensity, int value, Function<Double, Double> shader) {
        return Math.max(Math.min((int) (value * shader.apply(colorIntensity)), 255), 0);
    }

    default Color getColor(Vector3D pointOfHit, Vector3D normalToPointOfHit, Light light) {
        double colorIntensity = light.getIntensity(pointOfHit, normalToPointOfHit);
        return new Color(withIntensity(colorIntensity, getColor().getRed(), getShader()),
                withIntensity(colorIntensity, getColor().getGreen(), getShader()),
                withIntensity(colorIntensity, getColor().getBlue(), getShader()));
    }

    default Intersection intersection(int x, int y) {
        return intersection(Main.CAMERA_SOURCE, Camera.direction(x, y));
    }


    Intersection intersection(Vector3D origin, Vector3D direction);

    Function<Double, Double> getShader();

    Color getColor();

    Vector3D getCenter();

    Vector3D getNormal(Vector3D pointOfHit);
}
