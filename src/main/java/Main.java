import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final Color COOL_COLOR = new Color(70, 80, 170);

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

    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    static final Vector3D CAMERA_SOURCE = new Vector3D(0, 0, 0);

    public static void main(final String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out.ppm")));
        writer.write("P3");
        writer.newLine();
        writer.write(WIDTH + " " + HEIGHT);
        writer.newLine();
        writer.write("256");
        writer.newLine();
        final String content = IntStream.range(0, HEIGHT)
                .boxed()
                .map(y -> IntStream
                        .range(0, WIDTH)
                        .boxed()
                        .map(x -> color(y, x))
                        .map(Main::colorAsString)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n", "", "\n"));
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private static Color color(int x, int y) {
        return spheres.stream()
                .map(sphere -> sphere.intersection(x, y))
                .filter(intersection -> intersection.intersects)
                .reduce(Main::chooseClosest)
                .map(intersection -> intersection.getColor(lights))
                .orElse(Color.BLACK);
    }

    private static Sphere.Intersection chooseClosest(Sphere.Intersection a, Sphere.Intersection b) {
        return a.distanceToCamera() < b.distanceToCamera() ? a : b;
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
        final Sphere sphere;
        final Vector3D cameraDirection;
        private final double distanceToCamera;

        Intersection(boolean intersects, Sphere sphere, Vector3D cameraDirection, double distanceToCamera) {
            this.intersects = intersects;
            this.sphere = sphere;
            this.cameraDirection = cameraDirection;
            this.distanceToCamera = distanceToCamera;
        }

        static Intersection not(Sphere sphere) {
            return new Intersection(false, sphere, null, 0);
        }

        int withIntensity(double colorIntensity, int value) {
            return Math.max(Math.min((int) (value * sphere.shader.apply(colorIntensity)), 255), 0);
        }

        Color getColor(List<Light> lights) {
            final Vector3D pointOfHit = Main.CAMERA_SOURCE.add(cameraDirection).scalarMultiply(distanceToCamera());
            final Vector3D normalToPointOfHit = getNormal(pointOfHit);

            return lights.stream()
                    .map(light -> light.getColor(pointOfHit, normalToPointOfHit, sphere.color, this))
                    .reduce(Main::sumColors)
                    .orElse(Color.BLACK);
        }

        private Vector3D getNormal(Vector3D pointOfHit) {
            // TODO: flip normal if we are inside
            //            if (cameraDirection.dotProduct(normalToPointOfHit) > 0)
            //                normalToPointOfHit = normalToPointOfHit.negate();
            return pointOfHit.subtract(sphere.center).normalize();
        }

        double distanceToCamera() {
            return distanceToCamera;
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

    Intersection intersection(int x, int y) {
        final Vector3D fromCamToCenter = center.subtract(Main.CAMERA_SOURCE);
        final Vector3D cameraDir = cameraDirection(x, y);
        final double tca = fromCamToCenter.dotProduct(cameraDir);
        if (tca < 0) {
            return Intersection.not(this);
        }
        final double d2 = fromCamToCenter.getNormSq() - tca * tca;
        if (d2 > radius * radius) {
            return Intersection.not(this);
        }
        final double thc = Math.sqrt(radius * radius - d2);
        final double distanceToCamera = tca < thc ? tca + thc : tca - thc;
        return new Intersection(true, this, cameraDir, distanceToCamera);
    }

    private static Vector3D cameraDirection(int yy, int xx) {
        final double angle = Math.tan(Math.PI * 0.5 * FIELD_OF_VIEW / 180.);
        final double x = (2. * ((xx + 0.5) * invert(Main.WIDTH)) - 1.) * angle * (float) (Main.WIDTH / Main.HEIGHT);
        final double y = (1. - 2. * ((yy + 0.5) * invert(Main.HEIGHT))) * angle;
        return new Vector3D(x, y, -1).normalize();
    }

    private static double invert(int number) {
        return 1. / (double) number;
    }
}

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