import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    final static List<Sphere> spheres = Arrays.asList(
            new Sphere(new Vector3D(400, 300, 0), 200, Color.BLUE),
            new Sphere(new Vector3D(500, 700, 0), 100, Color.MAGENTA)
    );

    public static void main(final String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out.ppm")));
        int rows = 600;
        int columns = 800;
        writer.write("P3");
        writer.newLine();
        writer.write(columns + " " + rows);
        writer.newLine();
        writer.write("256");
        writer.newLine();
        final String content = IntStream.range(0, rows)
                .boxed()
                .map(row -> IntStream
                        .range(0, columns)
                        .boxed()
                        .map(column -> color(row, column))
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n", "", "\n"));
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private static String color(int row, int column) {
        return colorAsString(spheres.stream()
                .filter(sphere -> Sphere.intersects(row, column, sphere))
                .map(sphere -> sphere.color)
                .reduce(Main::sumColors)
                .orElse(Color.BLACK));
    }

    private static String colorAsString(Color color) {
        return String.format("%d %d %d", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static Color sumColors(final Color first, final Color second) {
        return new Color(
                first.getRed() + second.getRed(),
                first.getGreen() + second.getGreen(),
                first.getBlue() + second.getBlue());
    }

}

final class Sphere {
    private final Vector3D center;
    private final double radius;
    final Color color;

    Sphere(Vector3D center, double radius, Color color) {
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    static boolean intersects(int row, int column, Sphere sphere) {
        return new Vector3D(row, column, 0).distance(sphere.center) < sphere.radius;
    }
}