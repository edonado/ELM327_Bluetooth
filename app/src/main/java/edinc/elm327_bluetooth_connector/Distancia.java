package edinc.elm327_bluetooth_connector;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ferch5003
 */
public class Distancia {

    private double Latitude1, Latitude2, Longitude1, Longitude2;

    public Distancia(double lat1, double lat2, double long1, double long2) {
        this.Latitude1 = lat1;
        this.Latitude2 = lat2;
        this.Longitude1 = long1;
        this.Longitude2 = long2;
    }

    private double sqrt(double num) {
        return Math.sqrt(num);
    }

    private double sin(double x) {
        return Math.sin(x);
    }

    private double cos(double x) {
        return Math.cos(x);
    }

    private double asin(double x) {
        return Math.asin(x);
    }

    private double deg2rad(double x) {
        return Math.toRadians(x);
    }

    public double haversine() {
        double earth_radius = 6371;

        double dLat = deg2rad(this.Latitude1 - this.Latitude2);
        double dLon = deg2rad(this.Longitude1 - this.Longitude2);

        double a = sin(dLat / 2) * sin(dLat / 2) + cos(deg2rad(this.Latitude2)) * cos(deg2rad(this.Latitude1)) * sin(dLon / 2) * sin(dLon / 2);
        double c = 2 * asin(sqrt(a));
        double d = earth_radius * c;

        return d;
    }
    /*
    public void setLatitude1(double latitude1) {
        Latitude1 = latitude1;
    }

    public void setLatitude2(double latitude2) {
        Latitude2 = latitude2;
    }

    public void setLongitude1(double longitude1) {
        Longitude1 = longitude1;
    }

    public void setLongitude2(double longitude2) {
        Longitude2 = longitude2;
    }
    */
}


