import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private final static List<Sphere> spheres = Arrays.asList(
            new Sphere(new Vector3D(0.0, 0, -50), 4, Color.BLUE),
            new Sphere(new Vector3D(5.0, 0, -25), 3, Color.RED),
            new Sphere(new Vector3D(-5.5, 0, -15), 2, Color.MAGENTA)
    );
    private final static Light light = new Light(new Vector3D(200, 800, 0));

    static final int COLUMNS = 800;
    static final int ROWS = 600;

    public static void main(final String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out.ppm")));
        writer.write("P3");
        writer.newLine();
        writer.write(COLUMNS + " " + ROWS);
        writer.newLine();
        writer.write("256");
        writer.newLine();
        final String content = IntStream.range(0, ROWS)
                .boxed()
                .map(row -> IntStream
                        .range(0, COLUMNS)
                        .boxed()
                        .map(column -> color(row, column))
                        .map(Main::colorAsString)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n", "", "\n"));
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private static Color color(int row, int column) {
        return spheres.stream()
                .map(sphere -> sphere.intersects(row, column))
                .filter(intersection -> intersection.intersects)
                .reduce((a, b) -> a.t0Ort1() < b.t0Ort1() ? a : b)
                .map(intersection -> intersection.sphere.color)
                .orElse(Color.BLACK);
    }

    private static String colorAsString(Color color) {
        return String.format("%d %d %d", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static Color sumColors(final Color first, final Color second) {
        return new Color(getMin(first.getRed(), second.getRed()),
                getMin(first.getGreen(), second.getGreen()),
                getMin(first.getBlue(), second.getBlue()));
    }

    private static int getMin(int first, int second) {
        return Math.min(first + second, 255);
    }

}

final class Sphere {
    static class Intersection {
        final boolean intersects;
        final double t0;
        final double t1;
        final Sphere sphere;

        Intersection(boolean intersects, double t0, double t1, Sphere sphere) {
            this.intersects = intersects;
            this.t0 = t0;
            this.t1 = t1;
            this.sphere = sphere;
        }

        static Intersection not(Sphere sphere) {
            return new Intersection(false, 0, 0, sphere);
        }

        double t0Ort1() {
            return this.t0 < 0 ? this.t1 : this.t0;
        }
    }

    private final Vector3D center;
    private final double radius;
    final Color color;
    private static final float FIELD_OF_VIEW = 45;

    Sphere(Vector3D center, double radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    Intersection intersects(int row, int column) {
        final Vector3D rayOrigin = new Vector3D(0, 0, 10);
        final Vector3D l = center.subtract(rayOrigin);
        final double tca = l.dotProduct(rayDir(row, column));
        if (tca < 0) return Intersection.not(this);
        final double d2 = l.dotProduct(l) - tca * tca;
        if (d2 > radius * radius) return Intersection.not(this);
        final double thc = Math.sqrt(radius * radius - d2);
        final double t0 = tca - thc;
        final double t1 = tca + thc;
        return new Intersection(true, t0, t1, this);
    }

    private Vector3D rayDir(int row, int column) {
        final double angle = Math.tan(Math.PI * 0.5 * FIELD_OF_VIEW / 180.);
        final double x = (2. * ((column + 0.5) * invert(Main.COLUMNS)) - 1.) * angle * (float) (Main.COLUMNS / Main.ROWS);
        final double y = (1. - 2. * ((row + 0.5) * invert(Main.ROWS))) * angle;
        return new Vector3D(x, y, -1).normalize();
    }

    private double invert(int number) {
        return 1. / (double) number;
    }
}

final class Light {
    private final Vector3D center;

    Light(Vector3D center) {
        this.center = center;
    }
}