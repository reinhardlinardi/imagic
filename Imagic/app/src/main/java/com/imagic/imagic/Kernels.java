package com.imagic.imagic;

public class Kernels {
    // 0: sobel Gx, 1: sobel Gy
    int[][][] sobel = {
            {
                {1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}},
            {
                {1, 2, 1},
                {0, 0, 0},
                {-1, -2, -1}
            }
    };

    // 0: prewitt Gx, 1: prewitt Gy
    int[][][] prewitt = {
            {
                {1, 0, -1},
                {1, 0, -1},
                {1, 0, -1}
            },
            {
                {1, 1, 1},
                {0, 0, 0},
                {-1, -1, -1}
            }
    };

    // 0: robert Gx, 1: robert Gy
    int[][][] robert = {
            {
                {1, 0},
                {0, -1}
            },
            {
                {0, 1},
                {-1, 0}
            }
    };

    double[][][] freiChen = {
            /* EDGE SUBSPACE */
            // isotropic average gradient
            {
                {1, Math.sqrt(2), -1},
                {0, 0, 0},
                {-1, -Math.sqrt(2), -1}
            },
            {
                {1, 0, -1},
                {Math.sqrt(2), 0, -Math.sqrt(2)},
                {1, 0, -1}
            },
            // ripple
            {
                {0, -1, Math.sqrt(2)},
                {1, 0, -1},
                {-Math.sqrt(2), 1, 0}
            },
            {
                {Math.sqrt(2), -1, 0},
                {-1, 0, 1},
                {0, 1, -Math.sqrt(2)}
            },
            /* LINE SUBSPACE */
            // line
            {
                {0, 1, 0},
                {-1, 0, -1},
                {0, 1, 0}
            },
            {
                {-1, 0, 1},
                {0, 0, 0},
                {1, 0, -1}
            },
            // discrete laplacian
            {
                {1, -2, 1},
                {-2, 4, -2},
                {1, -2, 1}
            },
            {
                {-2, 1, -2},
                {1, 4, 1},
                {-2, 1, -2}
            },
            /* AVERAGE */
            {
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
            }
    };
}
