package com.sliit.smartbin.smartbin.model;

public enum BulkCategory {
    FURNITURE("Furniture", "Sofas, Tables, Chairs", 3500.0),
    APPLIANCES("Appliances", "TV, Refrigerator, Washing Machine", 5000.0),
    ELECTRONICS("Electronics", "Computer, Printer, Mobile", 2500.0),
    MATTRESS("Mattress", "Mattress & Bedding", 3000.0),
    CONSTRUCTION("Construction Waste", "Building materials, debris", 4500.0),
    YARD("Yard Waste", "Branches, Leaves", 1500.0),
    OTHER("Other Large Items", "Miscellaneous large items", 2000.0);
    
    private final String displayName;
    private final String description;
    private final Double basePrice;
    
    BulkCategory(String displayName, String description, Double basePrice) {
        this.displayName = displayName;
        this.description = description;
        this.basePrice = basePrice;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Double getBasePrice() {
        return basePrice;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
