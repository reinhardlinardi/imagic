package com.imagic.imagic;

public class Kernels {
    // 0: sobel Gx, 1: sobel Gy
    int[][][] sobel = {{{1, 0, -1},
                        {2, 0, -2},
                        {1, 0, -1}},
                       {{1, 2, 1},
                        {0, 0, 0},
                        {-1, -2, -1}}};

    // 0: prewitt Gx, 1: prewitt Gy
    int[][][] prewitt = {{{1, 0, -1},
                          {1, 0, -1},
                          {1, 0, -1}},
                         {{1, 1, 1},
                          {0, 0, 0},
                          {-1, -1, -1}}};

    // 0: robert Gx, 1: robert Gy
    int[][][] robert = {{{1, 0},
                         {0, -1}},
                        {{0, 1},
                         {-1, 0}}};
}
