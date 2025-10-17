package com.sliit.smartbin.smartbin.dto;

/**
 * CODE SMELL FIX: Primitive Obsession & Data Clumps
 * 
 * BEFORE: latitude, longitude, radius passed as separate primitive parameters
 * AFTER: Grouped into meaningful value object
 * 
 * BENEFITS:
 * - Reduces parameter count (Long Parameter List fix)
 * - Groups related data (fixes Data Clumps)
 * - Type-safe and self-documenting
 * - Can add validation and behavior to location data
 */
public class LocationDTO {
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    
    // Default Colombo coordinates
    private static final Double DEFAULT_LATITUDE = 6.9271;
    private static final Double DEFAULT_LONGITUDE = 79.8612;
    private static final Double DEFAULT_RADIUS = 5.0;
    
    public LocationDTO() {
        this.latitude = DEFAULT_LATITUDE;
        this.longitude = DEFAULT_LONGITUDE;
        this.radiusKm = DEFAULT_RADIUS;
    }
    
    public LocationDTO(Double latitude, Double longitude) {
        this.latitude = latitude != null ? latitude : DEFAULT_LATITUDE;
        this.longitude = longitude != null ? longitude : DEFAULT_LONGITUDE;
        this.radiusKm = DEFAULT_RADIUS;
    }
    
    public LocationDTO(Double latitude, Double longitude, Double radiusKm) {
        this.latitude = latitude != null ? latitude : DEFAULT_LATITUDE;
        this.longitude = longitude != null ? longitude : DEFAULT_LONGITUDE;
        this.radiusKm = radiusKm != null ? radiusKm : DEFAULT_RADIUS;
    }
    
    /**
     * Validate if coordinates are within valid ranges
     * 
     * CODE SMELL FIX: Behavior belongs with data (not scattered in controllers)
     */
    public boolean isValid() {
        return latitude != null && longitude != null &&
               latitude >= -90 && latitude <= 90 &&
               longitude >= -180 && longitude <= 180 &&
               radiusKm != null && radiusKm > 0;
    }
    
    /**
     * Calculate distance to another location (Haversine formula)
     * 
     * CODE SMELL FIX: Domain logic encapsulated in value object
     */
    public double distanceTo(LocationDTO other) {
        if (!this.isValid() || !other.isValid()) {
            return -1.0;
        }
        
        final double R = 6371; // Earth radius in km
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLat = Math.toRadians(other.latitude - this.latitude);
        double deltaLon = Math.toRadians(other.longitude - this.longitude);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    // Getters and Setters
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getRadiusKm() {
        return radiusKm;
    }
    
    public void setRadiusKm(Double radiusKm) {
        this.radiusKm = radiusKm;
    }
}

