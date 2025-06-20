package com.three19.inventoryappboehnke;

//Item holds information for a single item in the inventory (name, quantity, category & timestamp)

public class Item {
    private String id;
    private String name;
    private int quantity;
    private String category;
    private long timestamp;

    public Item() {
        // Required for Firebase
    }

    //Constructor for initializing fields
    public Item(String id, String name, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
    }

    //Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
