package com.three19.inventoryappboehnke;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class InventoryAdapter extends BaseAdapter {
    private final Context context;
    private List<Item> items;
    private Item selectedItem; // Track the currently selected item

    public InventoryAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    public void updateItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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

        // Highlight selected item
        if (item.equals(selectedItem)) {
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    public void setSelectedItem(Item item) {
        selectedItem = item;
        notifyDataSetChanged();
    }

    public int getPosition(Item item) {
        return items.indexOf(item);
    }
}
