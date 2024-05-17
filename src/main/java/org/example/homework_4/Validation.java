package org.example.homework_4;

public class Validation {
    //1. has 3 static validate methods to validate title, year, and sales respectively
    public static String validateTittle(String title) {
        String errorMessage = "";
        if (title.isEmpty()) {
            return errorMessage = "Title cannot be empty";
        }
        if (!title.matches("[A-Z][a-zA-Z0-9\\-\\:\\.\\s]*")) {
            return errorMessage = "Title must start with a capital letter";
        }

        return errorMessage;

    }

    public static String validateYear(String year) {
        String errorMessage = "";
        if (year.isEmpty()) {
            return errorMessage = "Year cannot be empty";
        }
        if (!year.matches("\\d{4}")) {
            if(year.length()>4){
                return errorMessage = "Year must be only 4 digits";
            }
            return errorMessage = "Year must be only digits";
        }
        return errorMessage;

    }

    public static String validateSales(String sales) {
        String errorMessage = "";
        if ((sales.isEmpty())) {
            return errorMessage = "Sales cannot be empty";
        }
        if (!sales.matches("\\d+(\\.\\d+)?")) {
            return  errorMessage = "Sales must contain digits. The decimal point is optional. If the decimal point is included, there must be at least one digit before and at least one digit after it.";
        }
        return errorMessage;

    }
}//end of Validation class
