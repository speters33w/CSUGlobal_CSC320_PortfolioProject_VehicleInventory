package util.speters33w;

import java.time.LocalDateTime;

/**
 * Tools to manipulate the current date and time format.
 * Written for a CSUGlobal project, but portable across other projects.
 * @author Stephan Peters (speters33w)
 **/
public class DateTools {
    private final LocalDateTime now;
    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;
    private final int second;

    /**
     * Constructor.
     * Gets the current date and time as integer values and stores them in the DateTools object.
     */
    public DateTools() {
        this.now = LocalDateTime.now();
        this.year = this.now.getYear();
        this.month = this.now.getMonthValue();
        this.day = this.now.getDayOfMonth();
        this.hour = this.now.getHour();
        this.minute = this.now.getMinute();
        this.second = this.now.getSecond();
    }

    /**
     * Returns the current date and time as a String in the format: YYYYMMDDhhmmss.
     * @return The current date and time as a String in the format: YYYYMMDDhhmmss.
     */
    public String getTime() {
        return String.format("%02d%02d%02d%02d%02d%02d", year, month, day, hour, minute, second);
    }


    /**
     * Main method used to test the DateTools class.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        DateTools dateTools = new DateTools();
        System.out.println(dateTools.now);
        System.out.println(dateTools.getTime());
    }
}
