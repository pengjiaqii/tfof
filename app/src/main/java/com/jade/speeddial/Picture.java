package com.jade.speeddial;

import java.util.ArrayList;

/**
 * Created by Jade on 2018/5/8.
 */

public class Picture {
    public static final int [] pics = {
            R.mipmap.aaa,
            R.mipmap.bbb,
            R.mipmap.ccc,
            R.mipmap.ddd,
            R.mipmap.eee,
            R.mipmap.fff,
            R.mipmap.ggg,
            R.mipmap.hhh,
            R.mipmap.iii
    };

    public ArrayList<Integer> photos;

    public Picture(int size) {
        size = Math.min(size, pics.length);
        if (size == 0) size = 9;
        photos = new ArrayList<>();
        for (int i = 0;i < size; i ++) {
            photos.add(pics[i]);
        }
    }
}
