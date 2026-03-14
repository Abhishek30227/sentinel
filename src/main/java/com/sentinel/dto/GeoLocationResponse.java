package com.sentinel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GeoLocationResponse {

    private String ip;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("asn")
    private Asn asn;

    // Location object from API response
    @Data
    public static class Location {
        @JsonProperty("country_name")
        private String countryName;

        private String city;

        // Latitude and longitude come as String from API
        private String latitude;
        private String longitude;
    }

    // ASN object from API response
    @Data
    public static class Asn {
        private String organization;
    }

    // Getters for flat access
    public String getCountryName() { return location != null ? location.getCountryName() : null; }
    public String getCity()        { return location != null ? location.getCity()        : null; }
    public String getIsp()         { return asn      != null ? asn.getOrganization()     : null; }
    public String getLatitude()    { return location != null ? location.getLatitude()    : null; }
    public String getLongitude()   { return location != null ? location.getLongitude()   : null; }
}