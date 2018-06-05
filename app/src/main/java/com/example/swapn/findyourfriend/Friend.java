package com.example.swapn.findyourfriend;

public class Friend {

    private String name;
    private double latit;
    private double longit;
    private String theAddress;

    public Friend(){

    }

    public Friend(String n, double la, double lo, String addr){
        name = n;
        latit = la;
        longit = lo;
        theAddress = addr;
    }

    public String toString(){
        //return "Name: "+ name+ " Latit: "+latit + " Longit: "+longit+" addr: "+theAddress;
        return "A Friend";
    }

    public String getName(){
        return name;
    }

    public double getLatit() {
        return latit;
    }

    public double getLongit() {
        return longit;
    }

    public String getTheAddress() {
        return theAddress;
    }
}
