package com.example.jingyun.hdarchallenge.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jingyun.hdarchallenge.Activity.MainActivity;
import com.example.jingyun.hdarchallenge.Items.ChecklistItem;
import com.example.jingyun.hdarchallenge.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jing Yun on 5/1/2018.
 */

public class ChecklistAdapter extends ArrayAdapter<ChecklistItem> {
    private List<ChecklistItem> checklistItemList;
    Context context;

    //dummy list for demonstration

    public ChecklistAdapter(@NonNull Context context, List<ChecklistItem> checklistItemList) {
        super(context,0, checklistItemList);
        this.context=  context;
        this.checklistItemList = checklistItemList;
    }

    private class ViewHolder{
        CheckBox checklistItem;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        ChecklistItem checklistItem = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.checklist_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checklistItem = (CheckBox) convertView.findViewById(R.id.checkBox);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.checkBox, viewHolder.checklistItem);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.checklistItem.setText(checklistItem.getItemName());

        viewHolder.checklistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean selected = !checklistItemList.get(position).isSelected();
                checklistItemList.get(position).setSelected(selected);

            }
        });

        viewHolder.checklistItem.setChecked(checklistItemList.get(position).isSelected());

        return convertView;
    }



}
