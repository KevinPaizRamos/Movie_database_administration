package org.example.homework_4;

import com.google.gson.annotations.SerializedName;

/**
 * Movie class to hold information about a movie.
 */
public class Movie {
   //title
    @SerializedName("title")
    private String title;

    //year
    @SerializedName("year")
    private int year;

    //sales
    @SerializedName("sales")
    private double sales;

    //constructor
    public Movie(String title, int year, double sales) {
        this.title = title;
        this.year = year;
        this.sales = sales;
    }

    //getters
    public String getTitle() {
        return title;
    }

    public double getSales() {
        return sales;
    }

    public int getYear() {
        return year;
    }
    //setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
