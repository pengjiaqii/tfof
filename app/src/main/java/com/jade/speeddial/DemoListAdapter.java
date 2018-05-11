package com.jade.speeddial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Jade on 2018/5/8.
 */

public class DemoListAdapter extends BaseAdapter {

    private final ArrayList<Picture> mList;

    public DemoListAdapter(ArrayList<Picture> diaryList) {
        this.mList = diaryList;
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nine_grid_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.squareGridView = (NineGridView) convertView.findViewById(R.id.square_grid_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Picture diary = mList.get(position);

        NineGridViewAdapter<Picture> adapter = new NineGridViewAdapter<Picture>() {
            @Override
            public int getCount() {
                return diary.photos.size();
            }

            @Override
            public Picture getItem(int position) {
                return diary;
            }

            @Override
            public int getImageUrl(int position) {
                return diary.photos.get(position);
            }

            @Override
            public void onItemClick(View view, int index, Picture t) {
                Toast.makeText(view.getContext(), "click--->" + index ,Toast.LENGTH_SHORT).show();
            }
        };
        viewHolder.squareGridView.setAdapter(adapter);
        return convertView;
    }

    class ViewHolder {
        NineGridView squareGridView;
    }
}
