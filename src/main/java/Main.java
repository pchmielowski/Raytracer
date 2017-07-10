import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
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
        double factor = 10.0;
        final int i = (int) ((double) column / factor * ((double) row / factor) * ((double) row / factor) % 255);
        return String.format("%d %d %d", i, i, i);
    }

}
