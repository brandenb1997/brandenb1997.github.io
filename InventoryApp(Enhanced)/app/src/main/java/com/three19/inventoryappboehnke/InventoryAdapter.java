package com.three19.inventoryappboehnke;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

//Inventory adapter binds items to a GridView. It also allows highlighting selected items and updating the list

public class InventoryAdapter extends BaseAdapter {
    private final Context context;
    private List<Item> items;
    private Item selectedItem;

    //InventoryAdapter constructor
    public InventoryAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }
    //Update the data and refresh
    public void updateItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    //Gets the number of items in the dataset
    @Override
    public int getCount() {
        return items.size();
    }

    //Gets the item in position
    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    //Gets the item ID in position
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Marks item as selected
    public void setSelectedItem(Item item) {
        this.selectedItem = item;
        notifyDataSetChanged();
    }

    //Gets the view of each item in the grid.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        Item item = items.get(position);
        TextView itemName = convertView.findViewById(android.R.id.text1);
        TextView itemQuantity = convertView.findViewById(android.R.id.text2);

        itemName.setText(item.getName());
        itemQuantity.setText("Quantity: " + item.getQuantity());

        //Highlight selected item (light grey)
        convertView.setBackgroundColor(item.equals(selectedItem) ? Color.LTGRAY : Color.TRANSPARENT);

        return convertView;
    }
}
