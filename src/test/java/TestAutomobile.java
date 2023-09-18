import edu.csuglobal.Automobile;
import edu.csuglobal.AutomobileInventory;

import java.time.Year;
import java.util.Scanner;

import static util.speters33w.ANSIColor.color;

/**
 * 1. Create an additional class to call your automobile class (e.g., Main or AutomobileInventory).
 * Include a try...catch construct and print it to the console any errors.
 * 2. Call automobile class with parameterized constructor (e.g., "make, model, color, year, mileage").
 * a. Then call the method to list the values. Loop through the array and print to the screen.
 * 3. Call the remove vehicle method to clear the variables.
 * a. Print the return value.
 * 4. Add a new vehicle.
 * a. Print the return value.
 * b. Call the list method and print the new vehicle information to the screen.
 * 5. Update the vehicle.
 * a. Print the return value.
 * b. Call the listing method and print the information to the screen.
 * 6. Display a message asking if the user wants to print the information to a file (Y or N).
 * a. Use a scanner to capture the response. If &quot;Y&quot;, print the file to a predefined location
 * (e.g., C:\Temp\Autos.txt). Note: you may want to create a method to print the information in the main class.
 * b. If &quot;N&quot;, indicate that a file will not be printed.
 */
public class TestAutomobile {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("\nCreate an additional class to call your automobile class (e.g., Main or AutomobileInventory).");
        AutomobileInventory automobileInventory = new AutomobileInventory();

        System.out.println("\nCall automobile class with parameterized constructor (e.g., \"make, model, color, year, mileage\").");
        Automobile automobile = new Automobile("1C6RD7LT6CS320434", "Dodge", "Ram 1500 BigHorn",
                "Red", Year.of(2012), 176519);
        automobileInventory.addAutomobile(automobileInventory, automobile);

        System.out.println("\nThen call the method to list the values.");
        System.out.println(automobile);
        automobileInventory.listInventory(automobileInventory);

        System.out.println("\nLoop through the array and print to the screen.");
        automobileInventory.listInventory(automobileInventory);

        System.out.println("\nCall the remove vehicle method to clear the variables.");
        automobileInventory.removeAutomobile(automobileInventory.getInventory(), "1C6RD7LT6CS320434");

        System.out.println("\nPrint the return value.");
        automobileInventory.listInventory(automobileInventory);

        System.out.println("\nAdd a new vehicle.");
        automobile = new Automobile("1GTR9CED7KZ251641", "GMC", "Sierra",
                "Silver", Year.of(2019), 69772);
        automobileInventory.addAutomobile(automobileInventory, automobile);

        System.out.println("\nPrint the return value.");
        System.out.println(automobile);

        System.out.println("\nCall the list method and print the new vehicle information to the screen.");
        automobileInventory.listInventory(automobileInventory);

        System.out.println("\nUpdate the vehicle.");
        System.out.printf("%nThe VIN of the first automobile in the inventory is %s%s%s%n",
                color("green"), automobileInventory.getInventory().get(0).getVin(), color("reset"));
        automobileInventory.updateAutomobile(input, automobileInventory);

        System.out.println("\nPrint the return value.");
        automobileInventory.listInventory(automobileInventory);

        System.out.println("\nDisplay a message asking if the user wants to print the information to a file (Y or N).");
        System.out.println("    a. Use a scanner to capture the response. " +
                           "If \"Y\", print the file to a predefined location." +
                           "\n       (e.g., /user.dir/automobileInventory.adb). " +
                           "Note: you may want to create a method to print the information in the main class.");
        System.out.println("    b. If \"N\", indicate that a file will not be printed.\n");
        automobileInventory.quit(input, automobileInventory.getInventory());

        input.close();
    }
}
