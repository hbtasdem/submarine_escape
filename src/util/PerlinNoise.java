package util;

public class PerlinNoise {
    private int[] permutation;

    public PerlinNoise(int seed) {
        permutation = new int[512];
        java.util.Random random = new java.util.Random(seed);
        int[] p = new int[256];
        for (int i = 0; i < 256; i++)
            p[i] = i;
        for (int i = 0; i < 256; i++) {
            int j = random.nextInt(256);
            int tmp = p[i];
            p[i] = p[j];
            p[j] = tmp;
        }
        for (int i = 0; i < 512; i++)
            permutation[i] = p[i & 255];
    }

    private static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    private static float grad(int hash, float x) {
        return ((hash & 1) == 0 ? x : -x);
    }

    public float noise(float x) {
        int X = (int) Math.floor(x) & 255;
        x -= Math.floor(x);
        float u = fade(x);
        int a = permutation[X];
        int b = permutation[X + 1];

        return lerp(u, grad(a, x), grad(b, x - 1)) * 2; // range [-1, 1]
    }
}
