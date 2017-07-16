package net.chmielowski.raytracer;


import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.util.function.Function;

abstract class Shape {

    private final Material material;

    Shape(Material material) {
        this.material = material;
    }

    Color getColor(Vector3D pointOfHit, Vector3D normalToPointOfHit, Light light) {
        return material.getColor(pointOfHit, normalToPointOfHit, light);
    }

    Intersection intersection(int x, int y) {
        return intersection(Main.CAMERA_SOURCE, Camera.direction(x, y));
    }


    public abstract Intersection intersection(Vector3D origin, Vector3D direction);

    public abstract Vector3D getCenter();

    public abstract Vector3D getNormal(Vector3D pointOfHit);

    static class Material {
        private final Color color;
        private final Function<Double, Double> shader;

        Material(Color color, Function<Double, Double> shader) {
            this.color = color;
            this.shader = shader;
        }

        static int withIntensity(double colorIntensity, int value, Function<Double, Double> shader) {
            return Math.max(Math.min((int) (value * shader.apply(colorIntensity)), 255), 0);
        }

        Color getColor(Vector3D pointOfHit, Vector3D normalToPointOfHit, Light light) {
            double colorIntensity = light.getIntensity(pointOfHit, normalToPointOfHit);
            return new Color(withIntensity(colorIntensity, color.getRed(), shader),
                    withIntensity(colorIntensity, color.getGreen(), shader),
                    withIntensity(colorIntensity, color.getBlue(), shader));
        }
    }
}
