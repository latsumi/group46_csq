package com.java.group46_csq.util;

import java.util.Random;

/**
 * Created by zhy on 17-9-14.
 */

public class GetRandCat {
    public static int getRandCat(int[] arr) {
        int total = 0;
        int[] temp = new int[arr.length + 1];
        temp[0] = 0;
        for (int i = 0; i < arr.length; i++) {
            total += arr[i];
            temp[i+1] = temp[i] + arr[i];
        }

        Random rand = new Random();
        int r = rand.nextInt(total);
        for (int i = 0; i < arr.length; i++) {
            if (r < temp[i+1] && r >= temp[i]) {
                return i+1;
            }
        }
        return 0;
    }

    //unit test
    //test the function
    public static void main(String[] args) {
        int[] arr = {1,2,3};
        int[] res = new int[4];
        for (int i = 0; i < 600; i++) {
            int t = getRandCat(arr);
            res[t] += 1;
        }
    }
}
