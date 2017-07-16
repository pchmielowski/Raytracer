package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

public class Main {
    private static final Color COOL_COLOR = new Color(70, 80, 170);

    static final int WIDTH = 1024;
    static final int HEIGHT = 768;

    static final Vector3D CAMERA_SOURCE = new Vector3D(0., 0., 0.);
    private static final Function<Double, Double> RUBBER = Math::sin;
    private static final Function<Double, Double> BASIC = Function.identity();
    private static final Function<Double, Double> METAL = intensity -> 2 * Math.pow(intensity, 8);

    public static void main(final String[] args) throws IOException {
        new Rendering(
                Arrays.asList(
                        new Sphere(new Vector3D(2., 0, -15), 1, Color.RED, BASIC),
                        new Sphere(new Vector3D(0., 0, -20), 1, Color.BLUE, BASIC),
                        new Sphere(new Vector3D(-2., 0, -25), 1, Color.CYAN, BASIC),
                        new Sphere(new Vector3D(-4., 0, -30), 1, Color.MAGENTA, BASIC),
                        new Disc(new Vector3D(0., -6., -60), 30, new Vector3D(0., 1, 0), new Color(0xCCCCCC), BASIC)
                ),
                Arrays.asList(
                        new Light(new Vector3D(15., 1., 10.)),
                        new Light(new Vector3D(-15., -5., 10.)),
                        new Light(new Vector3D(-2., 10, -10))
                ))
                .create();
    }

}

