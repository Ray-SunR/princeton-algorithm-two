import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class SeamCarver {
    private int[][] colors;
    private double [][] energys;
    private int width;
    private int height;
    private boolean transposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        width = picture.width();
        height = picture.height();
        colors = new int[height][width]; // Use row as major key such that it's easier to remove vertical seam
        energys = new double[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                colors[h][w] = picture.getRGB(w, h);
            }
        }

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                calculateEnery(w, h);
            }
        }
        transposed = false;
    }

    // current picture
    public Picture picture() {
        if (transposed) {
            transpose();
            transposed = false;
        }
        final Picture result = new Picture(width, height);
        for (int w = 0 ; w < width; w++) {
            for (int h = 0; h < height; h++) {
                result.set(w, h, new Color(colors[h][w]));
            }
        }
        return result;
    }

    // width of current picture
    public int width() {
        return transposed ? height : width;
    }

    // height of current picture
    public int height() {
        return transposed ? width : height;
    }

    // energy of pixel at column x and row y
    public  double energy(int x, int y) {
        int mx = x;
        int my = y;
        if (transposed) {
            mx = y;
            my = x;
        }
        if (mx < 0 || mx >= width || my < 0 || my >= height) {
            throw new IllegalArgumentException();
        }
        return energyImpl(mx, my);
    }

    private double energyImpl(int x, int y) {
        return energys[y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!transposed) {
            transpose();
            transposed = true;
        }
        int[] result = findVerticalSeamImpl();
        return result;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (transposed) {
            transpose();
            transposed = false;
        }
        return findVerticalSeamImpl();
    }

    private int[] findVerticalSeamImpl() {
        // Calculate vertical seam for all pixels in the first row
        double disTo[][] = new double[width][height];
        Coordinate[][] edgeTo = new Coordinate[width][height];
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                if (h == 0) {
                    disTo[w][h] = 0;
                } else {
                    disTo[w][h] = Double.POSITIVE_INFINITY;
                }
            }
        }
        return seamImpl(disTo, edgeTo);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }

        // We expect this method to be called with non-transposed. Thus, if transposed, horizontal becomes vertical
        if (seam.length != (transposed ? height : width)) {
            throw new IllegalArgumentException();
        }

        if ((transposed ? width : height) <= 1) {
            throw new IllegalArgumentException();
        }

        if (!transposed) {
            transpose();
            transposed = true;
        }

        removeVerticalSeamImpl(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (transposed) {
            transpose();
            transposed = false;
        }
        removeVerticalSeamImpl(seam);
    }

    private void removeVerticalSeamImpl(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }

        if (seam.length != height) {
            throw new IllegalArgumentException();
        }

        if (width <= 1) {
            throw new IllegalArgumentException();
        }

        for (int toRemoveY = 0; toRemoveY < height; toRemoveY++) {
            int toRemoveX = seam[toRemoveY];
            if (toRemoveX < 0 || toRemoveX >= width) {
                throw new IllegalArgumentException();
            }
            if (toRemoveY + 1 < height && Math.abs(toRemoveX - seam[toRemoveY + 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }

        for (int toRemoveY = 0; toRemoveY < height; toRemoveY++) {
            int toRemoveX = seam[toRemoveY];
            System.arraycopy(colors[toRemoveY], toRemoveX + 1, colors[toRemoveY], toRemoveX,
                    width - toRemoveX - 1);
            System.arraycopy(energys[toRemoveY], toRemoveX + 1, energys[toRemoveY], toRemoveX,
                    width - toRemoveX - 1);

        }
        width--;

        // update energy array along the seam that's removed
        for (int toRemoveY = 0; toRemoveY < height; toRemoveY++) {
            int toRemoveX = seam[toRemoveY];
            for (final Coordinate pos : getSeamSurroundings(toRemoveX, toRemoveY)) {
                calculateEnery(pos.x, pos.y);
            }
        }
    }

    private int[] seamImpl(double disTo[][], final Coordinate edgeTo[][]) {
        for (int h = 0; h < height - 1; h++) {
            for (int w = 0; w < width; w++) {
                for (final Coordinate adj : getAdj(w, h)) {
                    relax(new Coordinate(w, h), adj, disTo, edgeTo);
                }
            }
        }

        // Scan the last row and pick the one with minimum energy
        int destX = -1;
        int destY = -1;
        double minEnergy = Double.POSITIVE_INFINITY;
        for (int w = 0 ; w < width; w++) {
            if (disTo[w][height - 1] < minEnergy) {
                destX = w;
                destY = height - 1;
                minEnergy = disTo[w][height - 1];
            }
        }

        if (destX == -1 || destY == -1) {
            throw new RuntimeException();
        }

        int result[] = new int[height];
        final Stack<Integer> path = new Stack<>();
        path.push(destX);
        while (edgeTo[destX][destY] != null) {
            path.push(edgeTo[destX][destY].x);
            destX = edgeTo[destX][destY].x;
            destY = edgeTo[destX][destY].y;
        }

        int ccount = 0;
        for (final Integer x : path) {
            result[ccount++] = x;
        }

        return result;
    }

    private void transpose() {
        int newColors[][] = new int[width][height];
        double newEnergys[][] = new double[width][height];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                newColors[w][h] = colors[h][w];
                newEnergys[w][h] = energys[h][w];
            }
        }
        colors = newColors;
        energys = newEnergys;
        int temp = width;
        width = height;
        height = temp;
    }

    private Iterable<Coordinate> getSeamSurroundings(int x, int y) {
        final List<Coordinate> result = new ArrayList<>();
        if (x >= 0 && x < width && y >= 0 && y < height) {
            result.add(new Coordinate(x, y));
        }

        if (x - 1 >= 0 && x - 1 < width && y >= 0 && y < height) {
            result.add(new Coordinate(x - 1, y));
        }

        if (y - 1 >= 0 && y - 1 < height && x >=0 && x < width) {
            result.add(new Coordinate(x, y - 1));
        }

        if (y + 1 < height && y + 1 >= 0 && x >=0 && x < width) {
            result.add(new Coordinate(x, y + 1));
        }

        // No need to add the right pixel because their energy won't change

        return result;
    }

    private void relax(final Coordinate from, final Coordinate to, final double disTo[][],
                       final Coordinate edgeTo[][]) {
        int toX = to.x;
        int toY = to.y;
        int fromX = from.x;
        int fromY = from.y;
        if (disTo[toX][toY] > disTo[fromX][fromY] + energyImpl(toX, toY)) {
            disTo[toX][toY] = disTo[fromX][fromY] + energyImpl(toX, toY);
            edgeTo[toX][toY] = from;
        }
    }

    private Iterable<Coordinate> getAdj(int x, int y) {
        List<Coordinate> result = new ArrayList<>();
        if (x + 1 < width && y + 1 < height) {
            result.add(new Coordinate(x + 1, y + 1));
        }

        if (x - 1 >= 0 && y + 1 < height) {
            result.add(new Coordinate(x - 1, y + 1));
        }

        if (y + 1 < height) {
            result.add(new Coordinate(x, y + 1));
        }

        return result;
    }

    private void calculateEnery(int x, int y) {
        if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
            energys[y][x] = 1000;
        } else {
            int redDiff = getRed(colors[y][x - 1])  - getRed(colors[y][x + 1]);
            int greenDiff = getGreen(colors[y][x - 1]) - getGreen(colors[y][x + 1]);
            int blueDiff = getBlue(colors[y][x - 1]) - getBlue(colors[y][x + 1]);

            double deltaX = redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;

            redDiff = getRed(colors[y -1][x]) - getRed(colors[y + 1][x]);
            greenDiff = getGreen(colors[y - 1][x]) - getGreen(colors[y + 1][x]);
            blueDiff = getBlue(colors[y - 1][x]) - getBlue(colors[y + 1][x]);

            double deltaY = redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
            energys[y][x] = Math.sqrt(deltaX + deltaY);
        }
    }

    private int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    private int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    private int getBlue(int color) {
        return (color >> 0) & 0xFF;
    }
}