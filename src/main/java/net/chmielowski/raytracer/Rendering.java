package net.chmielowski.raytracer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Rendering {
    static Rendering INSTANCE;
    private final List<Light> lights;
    private final List<Shape> objects;

    Rendering(List<Shape> objects, List<Light> lights) {
        this.objects = objects;
        this.lights = lights;
        INSTANCE = this;
    }

    private static String colorAsString(Color color) {
        return String.format("%d %d %d", color.getRed(), color.getGreen(), color.getBlue());
    }

    void create() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out.ppm")));
        writer.write("P3");
        writer.newLine();
        writer.write(Main.WIDTH + " " + Main.HEIGHT);
        writer.newLine();
        writer.write("256");
        writer.newLine();
        final String content = IntStream.range(0, Main.HEIGHT)
                .boxed()
                .map(y -> IntStream
                        .range(0, Main.WIDTH)
                        .boxed()
                        .map(x -> colorAt(Camera.SOURCE, Camera.direction(x, y)))
                        .map(Rendering::colorAsString)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n", "", "\n"));
        writer.write(content);
        writer.flush();
        writer.close();
    }

    Color colorAt(Vector3D rayOrigin, Vector3D rayDirection) {
        return objects.stream()
                .map(shape -> shape.intersection(rayOrigin, rayDirection))
                .filter(Intersection::intersects)
                .reduce(Intersection::isCloser)
                .map(intersection -> intersection.getColor(lights, objects))
                .orElse(Color.BLACK);
    }

}
