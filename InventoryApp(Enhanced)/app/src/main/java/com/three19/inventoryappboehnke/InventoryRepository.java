package com.three19.inventoryappboehnke;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.*;

import java.util.*;

//Inventory repository for all data connection between InventoryApp and Firebase

public class InventoryRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference itemsRef = db.collection("inventory");

    // Callback for fetching item lists
    public interface ItemCallback {
        void onComplete(List<Item> items);
    }

    // Callback for confirming operations like add/update/delete
    public interface OperationCallback {
        void onComplete(boolean success);
    }

    // Callback for analytics - last N items
    public interface OnItemsLoadedListener {
        void onItemsLoaded(List<Item> items);
    }

    // Callback for analytics - category summary
    public interface OnCategorySummaryListener {
        void onSummaryLoaded(Map<String, Integer> summary);
    }

    // Get all inventory items
    public void getAllItems(ItemCallback callback) {
        itemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Item> itemList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Item item = doc.toObject(Item.class);
                    item.setId(doc.getId());
                    itemList.add(item);
                }
                callback.onComplete(itemList);
            } else {
                callback.onComplete(Collections.emptyList());
            }
        });
    }

    // Add an inventory item
    public void addItem(Item item, OperationCallback callback) {
        itemsRef.add(item)
                .addOnSuccessListener(documentReference -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    // Update item by Firestore document ID
    public void updateItem(String id, String name, int quantity, OperationCallback callback) {
        itemsRef.document(id)
                .update("name", name, "quantity", quantity)
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    // Delete item by ID
    public void removeItem(String id, OperationCallback callback) {
        itemsRef.document(id).delete()
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    // Get the last N added items
    public void getLastNItems(int count, OnItemsLoadedListener listener) {
        db.collection("inventory")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(count)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Item> items = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Item item = doc.toObject(Item.class);
                        item.setId(doc.getId());
                        items.add(item);
                    }
                    listener.onItemsLoaded(items);
                })
                .addOnFailureListener(e -> listener.onItemsLoaded(Collections.emptyList()));
    }

    // Get a summary of items grouped by category
    public void getCategorySummary(OnCategorySummaryListener listener) {
        db.collection("inventory").get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Integer> summary = new HashMap<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String category = doc.getString("category");
                        int qty = doc.getLong("quantity") != null ? doc.getLong("quantity").intValue() : 0;
                        if (category != null) {
                            summary.put(category, summary.getOrDefault(category, 0) + qty);
                        }
                    }
                    listener.onSummaryLoaded(summary);
                })
                .addOnFailureListener(e -> listener.onSummaryLoaded(Collections.emptyMap()));
    }
}