package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoLocation {
    private double latitude;
    private double longitude;
    private static final double EARTH_RADIUS = 6357;

    public static  double distanceBetweenLocations(GeoLocation geoLocation0, GeoLocation geoLocation1) {
        double lon0 = Math.toRadians(geoLocation0.getLongitude());
        double lat0 = Math.toRadians(geoLocation0.getLatitude());
        double lon1 = Math.toRadians(geoLocation1.getLongitude());
        double lat1 = Math.toRadians(geoLocation1.getLatitude());

        double dlat = lat1 - lat0;
        double dlon = lon1 - lon0;

        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat0) * Math.cos(lat1) * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double distance = c * GeoLocation.EARTH_RADIUS;

        System.out.println(distance);

        return distance;
    }
}
