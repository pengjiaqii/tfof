package com.jade.speeddial;

import android.view.View;

/**
 * Created by Jade on 2018/5/8.
 */

public interface NineGridViewAdapter<T> {
    int getCount();

    T getItem(int position);

    int getImageUrl(int position);

    void onItemClick(View view, int index, T t);
}
