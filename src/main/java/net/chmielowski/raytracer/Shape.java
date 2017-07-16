package net.chmielowski.raytracer;


import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

interface Shape {
    Intersection intersection(int x, int y);

    Intersection intersection(Vector3D origin, Vector3D direction);
}
