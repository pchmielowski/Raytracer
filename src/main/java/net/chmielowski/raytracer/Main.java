package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

public class Main {

    static final int WIDTH = 1024;
    static final int HEIGHT = 768;

    private static final Function<Double, Double> RUBBER = Math::sin;
    private static final Function<Double, Double> BASIC = Function.identity();
    private static final Function<Double, Double> METAL = intensity -> 2 * Math.pow(intensity, 8);

    public static void main(final String[] args) throws IOException {
        final Color color = new Color(0xCCCCCC);
        new Rendering(
                Arrays.asList(
                        new Sphere(new Vector3D(2., 0, -15), 1, new Shape.Material(Color.RED, BASIC)),
                        new Sphere(new Vector3D(0., 0, -20), 1, new Shape.Material(Color.BLUE, BASIC)),
                        new Sphere(new Vector3D(-2., 0, -25), 1, new Shape.Material(Color.CYAN, BASIC)),
                        new Sphere(new Vector3D(-4., 0, -30), 1, new Shape.Material(Color.MAGENTA, BASIC)),
                        new Disc(new Vector3D(0., -6., -60), 30, new Vector3D(0., 1, 0), new Shape.Material(color, BASIC))
                ),
                Arrays.asList(
                        new Light(new Vector3D(15., 1., 10.), .5),
                        new Light(new Vector3D(-2., 10, -10), .2)
                ))
                .create();
    }

}

