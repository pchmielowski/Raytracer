import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final Color COOL_COLOR = new Color(170, 120, 150);
    private final static List<Sphere> spheres = Arrays.asList(
            new Sphere(new Vector3D(5.0, 0, -20), 3, COOL_COLOR, intensity -> Math.pow(intensity, 2)),
            new Sphere(new Vector3D(0.0, 0, -25), 3, COOL_COLOR, intensity -> 2 * Math.pow(intensity, 8)),
            new Sphere(new Vector3D(-5.0, 0, -30), 3, COOL_COLOR, Math::sin),
            new Sphere(new Vector3D(-10.0, 0, -35), 3, COOL_COLOR, (a) -> -.2 * Math.cos(a) + .8 * Math.sin(a))
    );

    private static final List<Light> lights = Arrays.asList(
            new Light(new Vector3D(15., 1., -13.)),
            new Light(new Vector3D(-15., -5., 0.)),
            new Light(new Vector3D(0., 0., -130.))
    );
    static final int COLUMNS = 800;
    static final int ROWS = 600;
    static final Vector3D RAY_ORIGIN = new Vector3D(0, 0, 0);

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
                .map(sphere -> sphere.intersection(row, column))
                .filter(intersection -> intersection.intersects)
                .reduce(Main::chooseClosest)
                .map(intersection -> intersection.getColor(lights))
                .orElse(Color.BLACK);
    }

    private static Sphere.Intersection chooseClosest(Sphere.Intersection a, Sphere.Intersection b) {
        return a.tNear() < b.tNear() ? a : b;
    }

    private static String colorAsString(Color color) {
        return String.format("%d %d %d", color.getRed(), color.getGreen(), color.getBlue());
    }

    static Color sumColors(final Color first, final Color second) {
        return new Color(getMin(first.getRed(), second.getRed()),
                getMin(first.getGreen(), second.getGreen()),
                getMin(first.getBlue(), second.getBlue()));
    }

    private static int getMin(int first, int second) {
        return Math.min(first + second, 255);
    }

}

final class Sphere {

    private Function<Double, Double> shader;

    static class Intersection {
        final boolean intersects;
        private final double t0;
        private final double t1;
        final Sphere sphere;
        final Vector3D rayDir;

        Intersection(boolean intersects, double t0, double t1, Sphere sphere, Vector3D rayDir) {
            this.intersects = intersects;
            this.t0 = t0;
            this.t1 = t1;
            this.sphere = sphere;
            this.rayDir = rayDir;
        }

        static Intersection not(Sphere sphere) {
            return new Intersection(false, 0, 0, sphere, null);
        }

        int withIntensity(double colorIntensity, int value) {
            return Math.max(Math.min((int) (value * sphere.shader.apply(colorIntensity)), 255), 0) ;
        }

        Color getColor(List<Light> lights) {
            Vector3D pointOfHit = Main.RAY_ORIGIN.add(rayDir).scalarMultiply(tNear());
            final Vector3D normalToPointOfHit = pointOfHit.subtract(sphere.center).normalize();
//            if (rayDir.dotProduct(normalToPointOfHit) > 0)
//                normalToPointOfHit = normalToPointOfHit.negate();

            return lights.stream()
                    .map(light -> {
                        Vector3D lightDirection = light.center.subtract(pointOfHit).normalize();
                        final double colorIntensity = Math.max(0., normalToPointOfHit.dotProduct(lightDirection));
                        final Color color = sphere.color;
                        return new Color(withIntensity(colorIntensity, color.getRed()),
                                withIntensity(colorIntensity, color.getGreen()),
                                withIntensity(colorIntensity, color.getBlue()));
                    })
                    .reduce(Main::sumColors)
                    .orElse(Color.BLACK);
        }

        double tNear() {
            return this.t0 < 0 ? this.t1 : this.t0;
        }
    }

    final Vector3D center;
    private final double radius;
    final Color color;
    private static final float FIELD_OF_VIEW = 60;

    Sphere(Vector3D center, double radius, Color color, Function<Double, Double> shader) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.shader = shader;
    }

    Intersection intersection(int row, int column) {
        final Vector3D l = center.subtract(Main.RAY_ORIGIN);
        final Vector3D rayDir = rayDir(row, column);
        final double tca = l.dotProduct(rayDir);
        if (tca < 0) return Intersection.not(this);
        final double d2 = l.dotProduct(l) - tca * tca;
        if (d2 > radius * radius) return Intersection.not(this);
        final double thc = Math.sqrt(radius * radius - d2);
        final double t0 = tca - thc;
        final double t1 = tca + thc;
        return new Intersection(true, t0, t1, this, rayDir);
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
    final Vector3D center;

    Light(Vector3D center) {
        this.center = center;
    }
}