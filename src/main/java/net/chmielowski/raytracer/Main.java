package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static final Color COOL_COLOR = new Color(70, 80, 170);

    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    static final Vector3D CAMERA_SOURCE = new Vector3D(0, 0, 0);

    public static void main(final String[] args) throws IOException {
        new Rendering(
                Arrays.asList(
                        new Sphere(new Vector3D(5.0, 0, -20), 3, COOL_COLOR, intensity -> Math.pow(intensity, 2)),
                        new Sphere(new Vector3D(0.0, 0, -25), 3, COOL_COLOR, intensity -> 2 * Math.pow(intensity, 8)),
                        new Sphere(new Vector3D(-5.0, 0, -30), 3, COOL_COLOR, Math::sin),
                        new Sphere(new Vector3D(-10.0, 0, -35), 3, COOL_COLOR, (a) -> -.2 * Math.cos(a) + .8 * Math.sin(a))
                ),
                Arrays.asList(
                        new Light(new Vector3D(15., 1., -13.)),
                        new Light(new Vector3D(-15., -5., 0.)),
                        new Light(new Vector3D(0., 0., -130.))
                ))
                .create();
    }

}

