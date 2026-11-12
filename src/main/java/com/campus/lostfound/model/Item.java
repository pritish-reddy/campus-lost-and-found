package com.campus.lostfound.model;

/** One row of lost_items or found_items (both tables have the same columns). */
public class Item {

    private Long id;
    private String itemName;
    private String description;
    private String location;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
