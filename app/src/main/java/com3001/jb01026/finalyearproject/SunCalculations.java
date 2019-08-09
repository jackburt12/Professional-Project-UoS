package com3001.jb01026.finalyearproject;

import android.util.Log;

import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com3001.jb01026.finalyearproject.model.GardenPlot;
import com3001.jb01026.finalyearproject.model.Plot;
import com3001.jb01026.finalyearproject.model.ShadowObject;

public class SunCalculations {

    double longitude;
    double latitude;

    public SunCalculations() {

        longitude = -0.5728;
        latitude = 51.25132;

    }

    double d, SIDTIME;

    public void calc_d () {

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        int d = cal.get(Calendar.DAY_OF_MONTH);
        int m = cal.get(Calendar.MONTH) + 1; // 5
        int y = cal.get(Calendar.YEAR); // 20

        this.d = 367*y - 7 * ( y + (m+9)/12 ) / 4 - 3 * ( ( y + (m-9)/7 ) / 100 + 1 ) / 4 + 275*m/9 + d - 730515;

    }

    public void calc_d(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int d = cal.get(Calendar.DAY_OF_MONTH);
        int m = cal.get(Calendar.MONTH) + 1; // 5
        int y = cal.get(Calendar.YEAR); // 20

        this.d = 367*y - 7 * ( y + (m+9)/12 ) / 4 - 3 * ( ( y + (m-9)/7 ) / 100 + 1 ) / 4 + 275*m/9 + d - 730515;
    }

    public double calc_w () {
        return 282.9404 + 4.70935E-5 * d;
    }

    public double calc_M () {
        return rev(356.0470+0.9856002585*d);
    }

    public double calc_L () {
        return rev(calc_w() + calc_M());
    }

    public double calc_e() {
        return 0.016709 - 1.151E-9 * d;
    }

    public double calc_E() {
        double e = calc_e();
        double M = calc_M();

        return M + (180/Math.PI) * e * Math.sin(M*Math.PI/180) * (1+e*Math.cos(M*Math.PI/180));
    }

    public double calc_oblecl() {
        return 23.4393 - 3.563E-7 * d;
    }

    public double calc_r() {
        double x = Math.cos(convDegrees(calc_E())) - calc_e();
        double y = Math.sin(convDegrees(calc_E())) * Math.sqrt(1-(Math.pow(calc_e(),2)));


        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

    public double calc_v() {
        double x = Math.cos(convDegrees(calc_E())) - calc_e();
        double y = Math.sin(convDegrees(calc_E())) * Math.sqrt(1-Math.pow(calc_e(),2));
        return (Math.atan2(y, x)) * 180/Math.PI;
    }

    public double calc_long_peri() {
        return rev(calc_v() + calc_w());
    }

    public double calc_RA() {
        double x = calc_r()*Math.cos(convDegrees(calc_long_peri()));
        double y = calc_r()*Math.sin(convDegrees(calc_long_peri()));
        double z = 0;

        double xequat = x;
        double yequat = y * Math.cos(convDegrees(calc_oblecl())) - z * Math.sin(convDegrees(calc_oblecl()));

        return rev(Math.atan2(yequat, xequat)*180/Math.PI);
    }

    public double calc_GMST0() {
        return rev(calc_L()+180);
    }

    public void calc_SIDTIME() {
        ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));

        double hours = zonedDateTimeNow.getHour() + (double)zonedDateTimeNow.getMinute()/60 + (double)zonedDateTimeNow.getSecond()/60/60;
        double degrees = hours*15;

        this.SIDTIME = calc_GMST0() + degrees + longitude;
    }

    public void calc_SIDTIME(double hours) {

        double degrees = hours*15;
        this.SIDTIME = calc_GMST0() + degrees + longitude;

    }

    public double calc_HA() {

        return rev(SIDTIME - calc_RA());

    }

    public double calc_decl() {
        double x = calc_r()*Math.cos(convDegrees(calc_long_peri()));
        double y = calc_r()*Math.sin(convDegrees(calc_long_peri()));
        double z = 0;

        double xequat = x;
        double yequat = y * Math.cos(convDegrees(calc_oblecl())) - z * Math.sin(convDegrees(calc_oblecl()));
        double zequat = y * Math.sin(convDegrees(calc_oblecl())) + z * Math.cos(convDegrees(calc_oblecl()));

        return Math.atan2(zequat, Math.sqrt(Math.pow(xequat,2) + Math.pow(yequat,2)))*180/Math.PI;
    }

    public double calc_altitude() {

        double x = Math.cos(convDegrees(calc_HA())) * Math.cos(convDegrees(calc_decl()));
        double y = Math.sin(convDegrees(calc_HA())) * Math.cos(convDegrees(calc_decl()));
        double z = Math.sin(convDegrees(calc_decl()));

        double zhor = x * Math.cos(convDegrees(latitude)) + z * Math.sin(convDegrees(latitude));

        return Math.asin(zhor)*180/Math.PI;

    }

    public double calc_shadow_length(double objectHeight, double altitude) {

        return objectHeight/Math.tan(altitude/(180/Math.PI));

    }

    public double calc_azimuth() {
        double x = Math.cos(convDegrees(calc_HA())) * Math.cos(convDegrees(calc_decl()));
        double y = Math.sin(convDegrees(calc_HA())) * Math.cos(convDegrees(calc_decl()));
        double z = Math.sin(convDegrees(calc_decl()));

        double xhor = x * Math.sin(convDegrees(latitude)) - z * Math.cos(convDegrees(latitude));
        double yhor = y;

        return Math.atan2(yhor, xhor)*180/Math.PI + 180;


    }


    public double rev (double x) {
        if(x<0) {
            return x + Math.floor(x/360)*-360;
        } else {
            return x-(Math.floor(x/360)*360);
        }
    }

    public double convDegrees(double x) {
        return x*Math.PI/180;
    }

    public void calc_angles(GardenPlot plot, ShadowObject object) {
        Point center = new Point(0,0,SpatialReferences.getWebMercator());

        Point upLeft = new Point((int)-plot.getWidth()/2, (int)plot.getLength()/2,SpatialReferences.getWebMercator());
        Point upRight = new Point((int)plot.getWidth()/2, (int)plot.getLength()/2,SpatialReferences.getWebMercator());
        Point downLeft = new Point((int)-plot.getWidth()/2, (int)-plot.getLength()/2,SpatialReferences.getWebMercator());
        Point downRight = new Point((int)plot.getWidth()/2, (int)-plot.getLength()/2,SpatialReferences.getWebMercator());

        LinearUnit meters = new LinearUnit(LinearUnitId.METERS);
        AngularUnit deg = new AngularUnit(AngularUnitId.DEGREES);

        Point shadowObjectPoint = GeometryEngine.moveGeodetic(center, object.getDistanceFromPlot(), meters, object.getAngleFromPlot(), deg, GeodeticCurveType.GEODESIC);

        GeodeticDistanceResult distance1 = null;
        GeodeticDistanceResult distance2 = null;

        if(shadowObjectPoint.getX() < upLeft.getX()) {
            if(shadowObjectPoint.getY() > upLeft.getY()) {
                //segment 1
                distance1 = GeometryEngine.distanceGeodetic(shadowObjectPoint, upRight, meters, deg, GeodeticCurveType.GEODESIC);
                distance2 = GeometryEngine.distanceGeodetic(shadowObjectPoint, downLeft, meters, deg, GeodeticCurveType.GEODESIC);


            } else if(shadowObjectPoint.getY() > downLeft.getY()){

                //segment 4
                distance1 = GeometryEngine.distanceGeodetic(shadowObjectPoint, upLeft, meters, deg, GeodeticCurveType.GEODESIC);
                distance2 = GeometryEngine.distanceGeodetic(shadowObjectPoint, downLeft, meters, deg, GeodeticCurveType.GEODESIC);

            } else {
                //segment 6
                distance1 = GeometryEngine.distanceGeodetic(shadowObjectPoint, upLeft, meters, deg, GeodeticCurveType.GEODESIC);
                distance2 = GeometryEngine.distanceGeodetic(shadowObjectPoint, downRight, meters, deg, GeodeticCurveType.GEODESIC);

            }
        } else if(shadowObjectPoint.getX() < upRight.getX()) {
            if(shadowObjectPoint.getY() > upLeft.getY()) {
                //segment 2
                distance1 = GeometryEngine.distanceGeodetic(shadowObjectPoint, upRight, meters, deg, GeodeticCurveType.GEODESIC);
                distance2 = GeometryEngine.distanceGeodetic(shadowObjectPoint, upLeft, meters, deg, GeodeticCurveType.GEODESIC);

            } else if(shadowObjectPoint.getY() < downLeft.getY()){
                //segment 7
                distance1 = GeometryEngine.distanceGeodetic(shadowObjectPoint, downLeft, meters, deg, GeodeticCurveType.GEODESIC);
                distance2 = GeometryEngine.distanceGeodetic(shadowObjectPoint, downRight, meters, deg, GeodeticCurveType.GEODESIC);

            } else {
                //middle
            }
        } else {
            if (shadowObjectPoint.getY() > upLeft.getY()) {
                //segment 3
                distance1 = GeometryEngine.distanceGeodetic(shadowObjectPoint, downRight, meters, deg, GeodeticCurveType.GEODESIC);
                distance2 = GeometryEngine.distanceGeodetic(shadowObjectPoint, upLeft, meters, deg, GeodeticCurveType.GEODESIC);

            } else if (shadowObjectPoint.getY() > downLeft.getY()) {
                //segment 5
                distance1 = GeometryEngine.distanceGeodetic(shadowObjectPoint, downRight, meters, deg, GeodeticCurveType.GEODESIC);
                distance2 = GeometryEngine.distanceGeodetic(shadowObjectPoint, upRight, meters, deg, GeodeticCurveType.GEODESIC);

            } else {
                //segment 8
                distance1 = GeometryEngine.distanceGeodetic(shadowObjectPoint, downLeft, meters, deg, GeodeticCurveType.GEODESIC);
                distance2 = GeometryEngine.distanceGeodetic(shadowObjectPoint, upRight, meters, deg, GeodeticCurveType.GEODESIC);

            }

        }

        object.setAzimuths(new double[]{rev(distance1.getAzimuth2()), rev(distance2.getAzimuth2())});


    }

    public double calc_sun_exposure_day(GardenPlot plot, Date date) {

        for(ShadowObject object : plot.getShadowObjects()) {
            if(object.getAzimuths() == null) {
                calc_angles(plot, object);
            }
        }

        calc_d(date);

        double totalHours = 0;

        for(double d = 0; d < 24; d += 0.25) {
            boolean shaded = false;
            calc_SIDTIME(d);
            double altitude = calc_altitude();
            double azimuth = calc_azimuth();
            if(altitude > 0) {
                for(ShadowObject object : plot.getShadowObjects()) {

                    double[] azimuths = object.getAzimuths();

                    if(!(azimuth < azimuths[0] || azimuth > azimuths[1])) {
                        if(calc_shadow_length(object.getHeight(), altitude) > object.getDistanceFromPlot()) {
                            shaded = true;
                        }
                    }
                }
                if(!shaded) {
                    totalHours+=0.25;
                }
            }
        }

        return totalHours;
    }


    public double calc_sun_exposure_month(GardenPlot plot, int month) {

        double totalHours = 0;

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        Date date;
        for (int i = 1; i <= cal.getActualMaximum(Calendar.DATE); i++) {
            cal.set(year, month-1, i);
            System.out.println("Day in month: "+i);
            date = cal.getTime();

            totalHours += calc_sun_exposure_day(plot, date);

        }

        return totalHours;
    }

}
