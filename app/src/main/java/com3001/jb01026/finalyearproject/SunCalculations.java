package com3001.jb01026.finalyearproject;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SunCalculations {

    double longitude = -0.57280;
    double latitude = 51.25132;


    public double calc_d () {

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        int d = cal.get(Calendar.DAY_OF_MONTH);
        int m = cal.get(Calendar.MONTH); // 5
        int y = cal.get(Calendar.YEAR); // 20

        return 367*y - 7 * ( y + (m+9)/12 ) / 4 - 3 * ( ( y + (m-9)/7 ) / 100 + 1 ) / 4 + 275*m/9 + d - 730515;

    }

    public double calc_w () {
        return 282.9404 + 4.70935E-5 * calc_d();
    }

    public double calc_M () {
        return rev(356.0470+0.9856002585*calc_d());
    }

    public double calc_L () {
        return rev(calc_w() + calc_M());
    }

    public double calc_e() {
        return 0.016709 - 1.151E-9 * calc_d();
    }

    public double calc_E() {
        double e = calc_e();
        double M = calc_M();

        return M + (180/Math.PI) * e * Math.sin(M*Math.PI/180) * (1+e*Math.cos(M*Math.PI/180));
    }

    public double calc_oblecl() {
        return 23.4393 - 3.563E-7 * calc_d();
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

    public double calc_long() {
        return rev(calc_v() + calc_w());
    }

    public double calc_RA() {
        double x = calc_r()*Math.cos(convDegrees(calc_long()));
        double y = calc_r()*Math.sin(convDegrees(calc_long()));
        double z = 0;

        double xequat = x;
        double yequat = y * Math.cos(convDegrees(calc_oblecl())) - z * Math.sin(convDegrees(calc_oblecl()));

        return rev(Math.atan2(yequat, xequat)*180/Math.PI);
    }

    public double calc_GMST0() {
        return rev(calc_L()+180);
    }

    public double calc_SIDTIME() {
        ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));
        double hours = zonedDateTimeNow.getHour() + zonedDateTimeNow.getMinute()/60 + zonedDateTimeNow.getSecond()/60/60;
        double degrees = hours*15;

        return calc_GMST0() + degrees + longitude;
    }

    public double calc_HA() {

        return rev(calc_SIDTIME() - calc_RA());

    }

    public double calc_decl() {
        double x = calc_r()*Math.cos(convDegrees(calc_long()));
        double y = calc_r()*Math.sin(convDegrees(calc_long()));
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

        double xhor = x * Math.sin(convDegrees(latitude)) - z * Math.cos(convDegrees(latitude));
        double yhor = y;
        double zhor = x * Math.cos(convDegrees(latitude)) + z * Math.sin(convDegrees(latitude));

        return Math.asin(zhor)*180/Math.PI;


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

}
