import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


class Camera {
    private static final float FIELD_OF_VIEW = 60;

    static Vector3D direction(int x, int y) {
        final double angle = Math.tan(Math.PI * 0.5 * FIELD_OF_VIEW / 180.);
        return new Vector3D(
                (2. * ((x + 0.5) * invert(Main.WIDTH)) - 1.) * angle * (float) (Main.WIDTH / Main.HEIGHT),
                (1. - 2. * ((y + 0.5) * invert(Main.HEIGHT))) * angle,
                -1).normalize();
    }

    private static double invert(int number) {
        return 1. / (double) number;
    }
}
