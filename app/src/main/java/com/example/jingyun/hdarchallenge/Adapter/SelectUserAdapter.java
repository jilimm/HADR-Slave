package com.example.jingyun.hdarchallenge.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.jingyun.hdarchallenge.Items.ChecklistItem;

import java.util.List;

/**
 * Created by Jing Yun on 11/1/2018.
 */

public class SelectUserAdapter extends ArrayAdapter<String> {
    private List<String> teamList;
    Context context;
    private int hidingItemIndex;

    public SelectUserAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<String> objects) {
        super(context,textViewResourceId, objects);
    }

}
