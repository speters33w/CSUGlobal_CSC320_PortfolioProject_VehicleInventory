package edu.csuglobal;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.openqa.selenium.support.Colors;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util.speters33w.DateTools;

import static org.apache.commons.text.WordUtils.capitalizeFully;
import static util.speters33w.ANSIColor.color;
import static util.speters33w.SearchTools.possibleMatches;

public class AutomobileInventory {
    private ArrayList<Automobile> inventory = new ArrayList<>();
    private String makesList;
    boolean overrideMake = true;

    public ArrayList<Automobile> getInventory() {
        return inventory;
    }

    /**
     * Main menu for the Automobile Inventory program.
     * Allows the user to add, remove, update, import, list, and save automobiles, or quit.
     * Prints a list of menu options to the console.
     * Takes the first character from the user's input and processes it through a switch statement.
     * Recursively reloads the menu after performing operations.
     *
     * @param input     Scanner for user input.
     * @param inventory ArrayList of automobiles in current inventory.
     */
    public void menu(Scanner input, ArrayList<Automobile> inventory) {
        System.out.printf("%n%sWelcome to the Automobile Inventory%s%n", color("cyanbd"), color("reset"));
        System.out.printf("%sPlease select an option:%s%n", color("reset"), color("reset"));
        System.out.printf("%s[A]dd a new automobile%s%n", color("green"), color("reset"));
        System.out.printf("%s[R]emove an automobile%s%n", color("green"), color("reset"));
        System.out.printf("%s[U]pdate an automobile%s%n", color("green"), color("reset"));
        System.out.printf("%s[I]mport automobiles%s%n", color("green"), color("reset"));
        System.out.printf("%s[L]ist all automobiles%s%n", color("green"), color("reset"));
        System.out.printf("%s[S]ave automobiles%s%n", color("green"), color("reset"));
        System.out.printf("%s[Q]uit%s%n", color("green"), color("reset"));
        System.out.print("Enter an option: ");
        String option = input.nextLine();
        if (!option.isEmpty()) {
            option = option.substring(0, 1).toUpperCase();
        } else {
            menu(input, inventory);
        }
        switch (option) {
            case "A":
                inventory = addAutomobile(input, inventory);
                menu(input, inventory);
                break;
            case "R":
                inventory = removeAutomobile(input, inventory);
                menu(input, inventory);
                break;
            case "U":
                inventory = updateAutomobile(input, inventory);
                menu(input, inventory);
                break;
            case "I":
                inventory = importInventory(input, inventory, false);
                System.out.print("\nType any key to continue: ");
                input.nextLine();
                menu(input, inventory);
                break;
            case "L":
                listInventory(input, inventory);
                menu(input, inventory);
                break;
            case "S":
                saveInventory(input, inventory);
                menu(input, inventory);
                break;
            case "Q":
                quit(input, inventory);
                break;
            default:
                System.out.printf("%sInvalid option, please try again.%s%n", color("red"), color("reset"));
                System.out.print("\nType any key to continue: ");
                input.nextLine();
                menu(input, inventory);
                break;
        }

    }

    /**
     * Imports an automobile inventory from a CSV file.
     * On program initiation, backs up the default file automobileInventory.adb, and imports the file into the program.
     * If not during initialization, prompts the user to select a csv database file, limited to file extension adb.
     *
     * @param input         scanner for user input
     * @param inventory     the automobile inventory
     * @param initialImport true on program initialization, imports the default database if true.
     * @return the imported automobile inventory
     */
    public ArrayList<Automobile> importInventory(Scanner input, ArrayList<Automobile> inventory, boolean initialImport) {
        File file = new File("automobileInventory.adb");
        if (!initialImport) {
            String userDir = System.getProperty("user.dir");
            JFileChooser fileChooser = new JFileChooser(userDir);
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileFilter filter = new FileNameExtensionFilter("Automobile Database File", "adb");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Select a database:");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            System.out.print("Select a database file to import: ");
            int option = fileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                System.out.printf("%s%s%s%n", color("blue"), file.getName(), color("reset"));
            } else {
                System.out.printf("%sNo database selected for import.%s%n",
                        color("red"), color("reset"));
                return inventory;
            }
        }

        try {
            FileReader fileReader = new FileReader(file);
            String databasePath = file.getCanonicalPath();
            System.out.printf("%s%s%s%n", color("green"), databasePath, color("reset"));
            if (initialImport) {
                String filePath = System.getProperty("user.dir") + "/bak/" + file.getName();  //todo check this on Linux
                DateTools now = new DateTools();
                String backupFilename = String.format("%s%s%s%s",
                        filePath.substring(0, file.getCanonicalPath().length()),
                        "_", now.getTime(), ".bak");
                System.out.printf("%sBacking up database, please wait...%s%n",
                        color("reset"), color("reset"));
                File backup = new File(String.valueOf(backupFilename));
                Files.copy(file.toPath(), backup.toPath());
                System.out.printf("%s%s%s%n", color("green"), backupFilename, color("reset"));
            }
            final CSVFormat csvFormat = CSVFormat.Builder.create()
                    .setHeader("vin", "make", "model", "color", "year", "mileage")
                    .setAllowMissingColumnNames(true)
                    .setSkipHeaderRecord(true)
                    .build();
            final Iterable<CSVRecord> automobiles = csvFormat.parse(fileReader);
            automobiles.forEach(automobileRecord -> {
                boolean duplicateFlag;
                Automobile automobile = new Automobile();
                automobile.setVin(automobileRecord.get("vin"));
                duplicateFlag = duplicateVin(automobile.getVin(), inventory);
                automobile.setMake(automobileRecord.get("make"));
                automobile.setModel(automobileRecord.get("model"));
                automobile.setColor(automobileRecord.get("color"));
                automobile.setYear(Year.of(Integer.parseInt(automobileRecord.get("year"))));
                automobile.setMileage(Integer.parseInt(automobileRecord.get("mileage")));
                if (!duplicateFlag) {
                    inventory.add(automobile);
                    System.out.printf("%sSuccessfully added %s.%s%n",
                            color("cyan"), automobile, color("reset"));
                }
            });
        } catch (IOException e) {
            System.out.printf("%sMissing or corrupted database.%s%n",
                    color("red"), color("reset"));
            System.out.println("Current automobile inventory contains: ");
            listInventory(input, inventory);
            System.out.printf("%n%sERROR - IMPORT ABORTED.%s%n%n",
                    color("red"), color("reset"));
        }
        return inventory;
    }

    /**
     * Imports a list of valid automobile makes from a file.
     * Allows the user to select the list makes from a file chooser dialogue.
     * Validates the list using a simple keyword at the beginning of the file.
     * Returns null if there is no valid makes list chosen.
     *
     * @return the list of valid automobile makes.
     */
    List<String> importMakesList() {
        // Allows the user to select a list of valid automobile makes from a file chooser.
        File file = new File("auto_brands_us.list");
        if (makesList == null) {
            String userDir = System.getProperty("user.dir");
            JFileChooser fileChooser = new JFileChooser(userDir);
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileFilter filter = new FileNameExtensionFilter("List File", "list", "txt");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Select list of automobile makes:");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            System.out.print("Select list of automobile makes: ");
            int option = fileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                System.out.printf("%s%s%s%n", color("blue"), file.getName(), color("reset"));
            } else {
                System.out.printf("%sDatabase not selected. Using %s.%s%n",
                        color("blue"), file.getPath(), color("reset"));
            }
            this.makesList = file.getPath();
            this.overrideMake = false;
        }

        // Reads the automobile makes list file into an ArrayList for input validation.
        List<String> autoMakesList = new ArrayList<>();
        try {
            FileReader makesList = new FileReader(file);
            Iterable<CSVRecord> make = CSVFormat.DEFAULT.parse(makesList);
            make.forEach(line -> autoMakesList.add(line.get(0)));
            if (!autoMakesList.contains("AutomobileInventoryMakeList")) {
                System.out.printf("%sInvalid list of valid automobile makes.%s%n",
                        color("red"), color("reset"));
                this.overrideMake = true;
            }

        } catch (IOException ioe) {
            System.out.printf("%sInvalid list of valid automobile makes.%s%n",
                    color("red"), color("reset"));
            this.overrideMake = true;
        }
        return autoMakesList;
    }

    /**
     * Lists the automobiles in the inventory.
     * If the inventory is empty, it will print an error message to the console.
     * Creates a String[] of automobiles.
     *
     * @param input     Scanner used to prompt the user for input.
     * @param inventory The current inventory.
     * @return a String array of the automobiles.
     */
    public String[] listInventory(Scanner input, ArrayList<Automobile> inventory) {
        String[] inventoryAsStrings = new String[inventory.size()];
        if (inventory.size() == 0) {
            System.out.printf("%sThere are no automobiles in the inventory.%s%n",
                    color("red"), color("reset"));
        }
        int count = 0;
        for (Automobile automobile : inventory) {
            System.out.println(automobile.toString());
            inventoryAsStrings[count++] = automobile.toString();
        }
        System.out.print("\nType any key to continue: ");
        input.nextLine();
        return inventoryAsStrings;
    }

    /**
     * Adds an automobile to an AutomobileInventory.
     * Prints a record of the automobile to the console.
     * Returns the updated AutomobileInventory.
     *
     * @param inventory the AutomobileInventory to update.
     * @param automobile the automobile to add.
     * @return AutomobileInventory the updated AutomobileInventory.
     */
    public AutomobileInventory addAutomobile(AutomobileInventory inventory, Automobile automobile) {
        inventory.inventory.add(automobile);
        System.out.printf("%sSuccessfully added %s.%s%n",
                color("cyan"), automobile, color("reset"));
        return inventory;
    }

    /**
     * Allows the user to add a new automobile to the inventory.
     * VIN is entered directly in this method, and may not be altered after Automobile record creation.
     * VIN is checked against the inventory for duplicates or blanks.
     * Provides some input verification of the make, color, and year through the called methods.
     * Prints a record of the automobile to the console.
     *
     * @param input The user's input.
     * @param inventory The current inventory.
     * @return The automobile that was added.
     */
    public ArrayList<Automobile> addAutomobile(Scanner input, ArrayList<Automobile> inventory) {

        importMakesList();
        Automobile automobile = new Automobile();

        boolean valid;
        do {
            valid = true;
            System.out.print("Enter the VIN of the automobile: ");
            String vin = input.nextLine().toUpperCase();
            if (vin.equalsIgnoreCase("quit")) {
                return inventory;
            }
            // If this were a production program, better VIN validation would need to be used.
            if (vin.isEmpty() || duplicateVin(vin, inventory)) {
                System.out.printf("%sInvalid VIN, or VIN already exists.%s%n",
                        color("red"), color("reset"));
                System.out.print("Retype VIN, or type quit to exit.\n");
                valid = false;
            }
            automobile.setVin(vin);
        } while (!valid);

        automobile = addMake(input, automobile);
        automobile = addModel(input, automobile);
        automobile = addColor(input, automobile);
        automobile = addYear(input, automobile);
        automobile = addMileage(input, automobile);

        inventory.add(automobile);
        System.out.printf("%sSuccessfully added %s.%s%n",
                color("cyan"), automobile, color("reset"));
        return inventory;
    }

    /**
     * Used to check for duplicate VINs in the inventory.
     * importInventory will ignore duplicate VIN records.
     *
     * @param vin       The VIN from an imported file to check
     * @param inventory The current inventory
     * @return true if the VIN is already in the inventory, false otherwise
     */
    public boolean duplicateVin(String vin, ArrayList<Automobile> inventory) {
        for (Automobile automobile : inventory) {
            if (automobile.getVin().equalsIgnoreCase(vin)) {
                System.out.printf("%sDuplicate VIN found. Ignoring record. VIN: %s.%s%n",
                        color("red"), vin, color("reset"));
                return true;
            }
        }
        return false;
    }

    /**
     * Adds or updates the make of an automobile object.
     * Prompts the user for a valid make from the automobile makesList.
     * Suggests close matches if the user input does not match any of the automobile makes.
     * Allows the user to override valid makes for makes not in the database,
     * and appends an asterisk to the make for database review.
     *
     * @param input      Scanner for user input.
     * @param automobile the automobile object to be updated.
     * @return the automobile with the updated make.
     */
    Automobile addMake(Scanner input, Automobile automobile) {

        String make = "";
        List<String> autoMakesList = importMakesList();
        boolean validMake;

        do {
            validMake = true;
            System.out.print("Enter the make of the automobile: ");
            String makeEntered = input.nextLine();
            if (makeEntered.length() >= 2 && !Character.isUpperCase(makeEntered.charAt(1))) {
                makeEntered = capitalizeFully(makeEntered, ' ', '-', '.');
            }
            if (makeEntered.equalsIgnoreCase("quit")) {
                return automobile;
            }
            if (makeEntered.toUpperCase().contains("OVERRIDE")) {
                make = make + " *"; // To make it easier to find overrides in a database.
                overrideMake = true;
            }
            if (!overrideMake) {
                make = makeEntered;
            }
            if (!overrideMake && (makeEntered.isEmpty() || !autoMakesList.contains(makeEntered))) {
                System.out.printf("%sInvalid or unknown make: %s %s%n",
                        color("red"), makeEntered, color("reset"));
                possibleMatches(autoMakesList, makeEntered);
                System.out.print("Retype make, type \"OVERRIDE\" to override validation or type \"quit\" to exit.\n");
                validMake = false;
            }
            automobile.setMake(make);
        } while (!validMake);
        return automobile;
    }

    /**
     * Adds or updates the model of an automobile.
     * Prompts the user for a model.
     * Corrects capitalization if user does not capitalize the first letter of the model.
     * No validation is performed other than a check for an empty String.
     *
     * @param input      Scanner for user input.
     * @param automobile the automobile object to be updated.
     * @return the automobile with the updated model.
     */
    Automobile addModel(Scanner input, Automobile automobile) {
        // Adds a user entered model (no validation) to the Automobile.
        boolean valid = true;
        do {
            System.out.print("Enter the model of the automobile: ");
            String model = input.nextLine();
            if (model.length() >= 2 && !Character.isUpperCase(model.charAt(1))) {
                model = capitalizeFully(model, ' ', '-', '.');
            }
            if (model.isEmpty()) {
                System.out.printf("%sInvalid model.%s%n",
                        color("red"), color("reset"));
                System.out.print("Retype model.\n");
                valid = false;
            }
            automobile.setModel(model);
        } while (!valid);
        return automobile;
    }

    /**
     * Adds or updates the color of an automobile.
     * Prompts the user for a valid color from the automobile Colors Enum.
     * Suggests close matches if the user input does not match any of the automobile colors.
     *
     * @param input      Scanner for user input.
     * @param automobile the automobile object to be updated.
     * @return the updated Automobile with the updated color.
     **/
    Automobile addColor(Scanner input, Automobile automobile) {
        // Adds user-entered color to the Automobile, color must be a valid color from Colors enum.
        boolean valid;
        do {
            System.out.print("Enter the color of the automobile: ");
            String carColor = input.nextLine().toUpperCase();
            try {
                Colors.valueOf(carColor);
                valid = true;
            } catch (IllegalArgumentException e) {
                System.out.printf("%sInvalid color %s.%s%n",
                        color("red"), carColor, color("reset"));
                List<String> colors = Stream.of(Colors.values())
                        .map(Colors::name)
                        .collect(Collectors.toList());
                possibleMatches(colors, carColor);
                System.out.print("Retype color.\n");
                valid = false;
            }
            automobile.setColor(carColor.substring(0, 1).toUpperCase() +
                                carColor.substring(1).toLowerCase());
        } while (!valid);
        return automobile;
    }

    /**
     * Adds or updates the year of an automobile.
     * Prompts the user for a valid year from the automobile Year as a four digit integer.
     * Checks the year entered is between 1900 and the current year.
     * Suggests close matches if the user input does not match any of the automobile years.
     * Prompts user to enter -1 if year is unknown.
     *
     * @param input      scanner for user input.
     * @param automobile the automobile object to be updated.
     * @return the updated Automobile with the updated year.
     **/
    Automobile addYear(Scanner input, Automobile automobile) {
        // Adds user-entered year to the Automobile, year must be a valid year.
        boolean valid;
        do {
            Year year = null;
            System.out.print("Enter the model year of the automobile: ");
            String modelYearString = input.nextLine();
            try {
                int modelYear = Integer.parseInt(modelYearString);
                if (modelYear == -1) {
                    break;
                }
                if (modelYear >= 1900 && modelYear <= Year.now().getValue()) {
                    year = Year.of(modelYear);
                    valid = true;
                } else {
                    System.out.printf("%sInvalid model year.%s%n",
                            color("red"), color("reset"));
                    System.out.print("Retype year.\n");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                System.out.printf("%sInvalid model year.%s%n",
                        color("red"), color("reset"));
                System.out.print("Retype year.\nEnter -1 if model year is unknown or inaccurate.\n");
                valid = false;
            }
            automobile.setYear(year);
        } while (!valid);
        return automobile;
    }

    /**
     * Adds or updates the odometer reading of an automobile.
     * Allows the user to enter -1 if the odometer reading is invalid or unknown.
     *
     * @param input      Scanner for user input.
     * @param automobile the automobile object to be updated.
     * @return the automobile with the updated odometer reading.
     */
    Automobile addMileage(Scanner input, Automobile automobile) {
        // Adds user-entered odometer reading to the Automobile. Must be an integer.
        boolean valid;
        do {
            int odometer = -1;
            System.out.print("Enter the odometer reading of the automobile: ");
            String odometerString = input.nextLine().replace(",", "");
            try {
                odometer = Integer.parseInt(odometerString);
                valid = odometer >= -1; // NOTE, dealer may use -1 for bad odometer readings and unknown values.
            } catch (NumberFormatException e) {
                System.out.printf("%sInvalid odometer reading.%s%n",
                        color("red"), color("reset"));
                System.out.print("Re-enter the odometer reading.\n");
                valid = false;
            }
            automobile.setMileage(odometer);
        } while (!valid);
        return automobile;
    }

    /**
     * Removes an automobile from the automobile inventory using the VIN.
     * Requests a VIN from the user,
     * then uses removeAutomobile(ArrayList&lt;Automobile&gt; inventory, String vin)
     * to remove the automobile from the inventory.
     *
     * @param input     scanner for user input
     * @param inventory the automobile inventory
     * @return the updated automobile inventory
     */
    public ArrayList<Automobile> removeAutomobile(Scanner input, ArrayList<Automobile> inventory) {
        System.out.print("Enter the VIN of the automobile to be removed: ");
        String vin = input.nextLine().toUpperCase();
        return removeAutomobile(inventory, vin);
    }

    /**
     * Removes an automobile from the automobile inventory using the VIN.
     * Iterates through the automobile inventory until the VIN is found,
     * then removes the automobile from the inventory.
     * If the inventory does not contain the VIN, it prints an error message to the console.
     *
     * @param inventory the automobile inventory
     * @param vin       the VIN of the automobile to be removed
     * @return the updated automobile inventory
     */
    public ArrayList<Automobile> removeAutomobile(ArrayList<Automobile> inventory, String vin) {
        boolean removed = false;
        for (Automobile automobile : inventory) {
            if (automobile.getVin().equalsIgnoreCase(vin)) {
                System.out.printf("%sSuccessfully removed %s.%s%n",
                        color("cyan"), automobile, color("reset"));
                inventory.remove(automobile);
                removed = true;
                break;
            }
        }
        if (!removed) {
            System.out.printf("%sCould not find automobile with VIN %s.%s%n",
                    color("red"), vin, color("reset"));
        }
        return inventory;
    }

    /**
     * Allows the user to update an automobile currently in the database.
     * Prompts the user to select an automobile from the automobile inventory using the VIN.
     * Loops through the automobile inventory until the automobile is found.
     * If the automobile is not found, it prints an error message to the console and returns to the main menu.
     * If the automobile is found, it displays a menu with options to update the automobile.
     * Then it returns the updated automobile inventory.
     *
     * @param input     Scanner for user input.
     * @param inventory the automobile inventory.
     * @return the updated automobile inventory
     */
    public ArrayList<Automobile> updateAutomobile(Scanner input, ArrayList<Automobile> inventory) {
        String customerSupport = "1-800-555-7836 (1-800-555-RTFM)";
        Automobile automobile = new Automobile();
        System.out.print("Enter the VIN of the automobile to be updated: ");
        String vin = input.nextLine().toUpperCase();
        for (Automobile element : inventory) {
            if (element.getVin().equalsIgnoreCase(vin)) {
                System.out.printf("%sAutomobile found: %s.%s%n",
                        color("cyan"), element, color("reset"));
                automobile = element;
                break;
            }
            System.out.printf("%sCould not find automobile with VIN %s.%s%n",
                    color("red"), vin, color("reset"));
            menu(input, inventory);
        }

        System.out.printf("%sPlease select an option:%s%n", color("reset"), color("reset"));
        System.out.printf("%sUpdate the [V]in of an automobile%s%n", color("green"), color("reset"));
        System.out.printf("%sUpdate the [B]rand of an automobile%s%n", color("green"), color("reset"));
        System.out.printf("%sUpdate the [M]odel of an automobile%s%n", color("green"), color("reset"));
        System.out.printf("%sUpdate the [C]olor of an automobile%s%n", color("green"), color("reset"));
        System.out.printf("%sUpdate the [Y]ear of an automobile%s%n", color("green"), color("reset"));
        System.out.printf("%sUpdate the [O]dometer of an automobile%s%n", color("green"), color("reset"));
        System.out.printf("%s[X] to cancel%s%n", color("green"), color("reset"));
        System.out.print("Enter an option: ");
        String option = input.nextLine();
        if (!option.isEmpty()) {
            option = option.substring(0, 1).toUpperCase();
        } else {
            updateAutomobile(input, inventory);
        }
        switch (option) {
            case "V":
                System.out.print("The VIN may not be altered after inventory record creation.\n" +
                                 "If the VIN is invalid, create a new record with the correct vin, " +
                                 "and delete the record with the incorrect vin.\n" +
                                 "You may contact customer support at " + customerSupport + ".\n");
                menu(input, inventory);
                break;
            case "B":
                automobile = addMake(input, automobile);
                break;
            case "M":
                automobile = addModel(input, automobile);
                break;
            case "C":
                automobile = addColor(input, automobile);
                break;
            case "Y":
                automobile = addYear(input, automobile);
                break;
            case "O":
                automobile = addMileage(input, automobile);
                break;
            case "X":
                menu(input, inventory);
                break;
            default:
                System.out.printf("%sInvalid option, please try again.%s%n", color("red"), color("reset"));
                System.out.print("\nType any key to continue: ");
                input.nextLine();
                updateAutomobile(input, inventory);
                break;
        }
        System.out.printf("%sSuccessfully updated %s.%s%n",
                color("cyan"), automobile, color("reset"));
        return inventory;
    }

    /**
     * Saves the automobile inventory.
     * This will overwrite the existing default database file.
     * In the case of an exception, it will print an error message to the console,
     * and a list of the current inventory for possible salvage from the console log.
     *
     * @param input     Scanner to pass to listInventory in the case of an exception.
     * @param inventory the Automobile inventory.
     */
    public void saveInventory(Scanner input, ArrayList<Automobile> inventory) {
        try {
            FileWriter writer = new FileWriter("automobileInventory.adb");
            CSVPrinter csvPrinter = new CSVPrinter(writer,
                    CSVFormat.Builder.create()
                            .setDelimiter(",")
                            .setRecordSeparator("\n")
                            .setEscape('\\')
                            .setQuoteMode(QuoteMode.NONE)
                            .setHeader("vin", "make", "model", "color", "year", "mileage")
                            .build());
            for (Automobile automobile : inventory) {
                System.out.println(automobile.toString());
                csvPrinter.printRecord(automobile.getVin(), automobile.getMake(), automobile.getModel(),
                        automobile.getColor(), automobile.getYear().toString(), String.valueOf(automobile.getMileage()));
            }
            System.out.printf("%s***** SAVE SUCCESSFUL! *****%s%n",
                    color("green"), color("reset"));
            csvPrinter.close(true);
        } catch (IOException ioe) {
            System.out.printf("%n%s***** SAVE UNSUCCESSFUL! *****%s%n%n",
                    color("red"), color("reset"));
            System.out.printf("%s%s%s%n",
                    color("red"), ioe.getMessage(), color("reset"));
            System.out.printf("%s%s%s%n",
                    color("red"), "Current automobile inventory contains: ", color("reset"));
            listInventory(input, inventory);
            System.out.print("\nType any key to continue: ");
        }
    }

    /**
     * Asks the user if the automobile inventory should be saved,
     * if yes, saves the inventory, then quits the program if yes or no.
     * If the entry does not begin with y or n, it will print an error message to the console and restart the method.
     * There is no &quot;do you really want to quit?&quot; message,
     * user can re-run the program after quitting in error.
     *
     * @param input     Scanner used to prompt the user for input.
     * @param inventory the modified Automobile inventory.
     */
    public void quit(Scanner input, ArrayList<Automobile> inventory) {
        while (true) {
            System.out.println("Save your inventory? (y/n)");
            String yesNo = input.nextLine();
            if (!yesNo.isEmpty()) {
                yesNo = yesNo.toLowerCase().substring(0, 1);
                if (yesNo.equalsIgnoreCase("y")) {
                    saveInventory(input, inventory);
                    break;
                } else if (yesNo.equalsIgnoreCase("n")) {
                    System.out.printf("%sThe current inventory was not saved.%n" +
                                      "All edits are lost.%s%n",
                            color("red"), color("reset"));
                    break;
                }
            }
            System.out.printf("%sInvalid input. Please try again.%s%n",
                    color("red"), color("reset"));
        }
        input.close();
        System.exit(0);
    }

    /**
     * This is the main entry method for the automobileInventory class.
     * Creates a Scanner to be used throughout the program.
     * Creates a new automobileInventory object.
     * Creates a new ArrayList to be used throughout the program.
     * Imports a default database file into the array list and creates a backup in the /bak/ directory.
     * Prompts the user to start the automobileInventory program.
     * Closes the Scanner and ends the program.
     */
    public static void automobileInventory() {
        Scanner input = new Scanner(System.in);
        AutomobileInventory automobileInventory = new AutomobileInventory();
        automobileInventory.inventory = new ArrayList<>();
        System.out.printf("%sInitializing database, please wait...%s%n",
                color("reset"), color("reset"));
        automobileInventory.inventory = automobileInventory.importInventory
                (input, automobileInventory.inventory, true);
        System.out.print("\nDatabase Initialized.\nType any key to continue: ");
        input.nextLine();
        automobileInventory.menu(input, automobileInventory.inventory);
        input.close();
    }

    /**
     * Runnable main method for the automobileInventory class.
     *
     * @param args command line arguments (not used).
     */
    public static void main(String[] args) {
        automobileInventory();
    }
}
