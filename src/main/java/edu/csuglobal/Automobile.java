package edu.csuglobal;

// Removed lombok because Eclipse is the suggested IDE for the course.
// Eclipse requires modification to use lombok,
// and may not run on the instructor's machine for grading.
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.ToString;

import java.time.Year;



/**
 * An automobile class used for dealer inventory.
 */
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
public class Automobile {
    private String vin;
    private String make;
    private String model;
    private String color;
    private Year year;
    private int mileage;

    /**
     * Default constructor for the automobile class.
     */
    public Automobile(){}

    /**
     * Constructor for the automobile class.
     * @param vin the vin number of the automobile
     * @param make the make of the automobile
     * @param model the model of the automobile
     * @param color the color of the automobile
     * @param year the java.Time.Year of the automobile
     * @param mileage the odometer reading of the automobile
     */
    public Automobile(String vin, String make, String model, String color, Year year, int mileage) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.color = color;
        this.year = year;
        this.mileage = mileage;
    }

    /**
     * Sets the VIN of the automobile
     * @param vin the VIN of the automobile
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Sets the make of the automobile
     * @param make the make of the automobile
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Sets the model of the automobile
     * @param model the model of the automobile
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Sets the color of the automobile
     * @param color the color of the automobile
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Sets the year of the automobile
     * @param year the java.Time.Year of the automobile
     */
    public void setYear(Year year) {
        this.year = year;
    }

    /**
     * Sets the odometer reading of the automobile
     * @param mileage the odometer reading of the automobile
     */
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    /**
     * Returns the VIN of the automobile
     * @return the VIN of the automobile
     */
    public String getVin() {
        return vin;
    }

    /**
     * Returns the make of the automobile
     * @return the make of the automobile
     */
    public String getMake() {
        return make;
    }

    /**
     * Returns the model of the automobile
     * @return the model of the automobile
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the color of the automobile
     * @return the color of the automobile
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns the java.Time.Year of the automobile
     * @return the java.Time.Year of the automobile
     */
    public Year getYear() {
        return year;
    }

    /**
     * Returns the year of the automobile as a String
     * @return the year of the automobile as a String
     */
    public String getYearAsString() {
        return year.toString();
    }

    /**
     * Returns the odometer reading of the automobile
     * @return the odometer reading of the automobile
     */
    public int getMileage() {
        return mileage;
    }

    /**
     * Returns the automobile as a String[] array
     * @return the automobile as a String[] array
     */
    public String[] getAutomobile() {
        return new String[]{getVin(), getMake(), getModel(), getColor(),
                            getYear().toString(), Integer.toString(getMileage())};
    }

    /**
     * Returns the automobile as a String
     * @return the automobile as a String
     */
    @Override
    public String toString() {
        return "Automobile(" +
               "\"" + vin +
               "\", \"" + make +
               "\", \"" + model +
               "\", \"" + color +
               "\", " + getYearAsString() +
               ", " + mileage +
               ")";
    }
}




