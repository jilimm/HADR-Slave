package com.example.jingyun.hdarchallenge.Items;

/**
 * Created by Jing Yun on 5/1/2018.
 */

public class ChecklistItem {

    private String itemName;
    private String itemType;
    private Boolean selected;

    public ChecklistItem(String name, String type){
        this.itemName = name;
        this.itemType = type;

        //on initialization its not checked
        this.selected = false;
    }

    public String getItemName(){
        return this.itemName;
    }

    public String getItemType(){
        return this.itemName;
    }

    public void setSelected(boolean selected){
        this.selected=selected;
    }

    public boolean isSelected(){
        return this.selected;
    }

}
