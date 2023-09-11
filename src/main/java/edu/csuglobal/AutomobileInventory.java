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

    public ArrayList<Automobile> getInventory() {
        return inventory;
    }

    /**
     * Main menu for the Automobile Inventory program.
     * @param input Scanner for user input.
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
        String option = input.nextLine().substring(0, 1).toUpperCase();
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
     * This method adds an automobile to an AutomobileInventory object, and returns the AutomobileInventory object.
     */
    public AutomobileInventory addAutomobile(AutomobileInventory automobileInventory, Automobile automobile) {
        automobileInventory.inventory.add(automobile);
        System.out.printf("%sSuccessfully added %s.%s%n",
                color("cyan"), automobile, color("reset"));
        return automobileInventory;
    }

    /**
     * Allows the user to add a new automobile to the inventory.
     * Provides some input verification.
     *
     * @param input The user's input.
     * @return The automobile that was added.
     */
    public ArrayList<Automobile> addAutomobile(Scanner input, ArrayList<Automobile> inventory) {

        Automobile automobile = new Automobile();

        // Checks to ensure the user has entered a non-duplicate VIN, and adds the VIN to the automobile.
        boolean valid;
        do {
            valid = true;
            System.out.print("Enter the VIN of the automobile: ");
            String vin = input.nextLine().toUpperCase();
            for (Automobile auto : inventory) {
                if (vin.equalsIgnoreCase("quit")) {
                    return inventory;
                }
                // If this were a production program, better VIN validation would need to be used.
                if (vin.isEmpty() || auto.getVin().equalsIgnoreCase(vin)) {
                    System.out.printf("%sInvalid VIN, or VIN already exists.%s%n",
                            color("red"), color("reset"));
                    System.out.print("Retype VIN, or type quit to exit.\n");
                    valid = false;
                }
            }
            automobile.setVin(vin);
        } while (!valid);

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
        }

        // Reads the file into an ArrayList for input validation.
        List<String> autoMakesList = new ArrayList<>();
        boolean overrideMake = false;
        try {
            FileReader makesList = new FileReader(file);
            Iterable<CSVRecord> make = CSVFormat.DEFAULT.parse(makesList);
            make.forEach(line -> autoMakesList.add(line.get(0)));
            if (!autoMakesList.contains("AutomobileInventoryMakeList")) {
                System.out.printf("%sInvalid list of valid automobile makes.%s%n",
                        color("red"), color("reset"));
                this.makesList = null;
                return inventory;
            }

        } catch (IOException e) {
            System.out.printf("%sInvalid list of valid automobile makes.%s%n",
                    color("red"), color("reset"));
            overrideMake = true;
        }

        String make = "";

        // Checks if the user has entered a valid make, if so, adds the make to the automobile.

        do {
            valid = true;
            System.out.print("Enter the make of the automobile: ");
            String makeEntered = input.nextLine();
            if (makeEntered.length() >= 2 && !Character.isUpperCase(makeEntered.charAt(1))) {
                makeEntered = capitalizeFully(makeEntered, ' ', '-', '.');
            }
            if (makeEntered.equalsIgnoreCase("quit")) {
                return inventory;
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
                valid = false;
            }
            automobile.setMake(make);
        } while (!valid);

        // Adds a user entered model (no validation) to the Automobile.
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

        // Adds user-entered color to the Automobile, color must be a valid color from Colors enum.
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

        // Adds user-entered year to the Automobile, year must be a valid year.
        do {
            Year year = null;
            System.out.print("Enter the model year of the automobile: ");
            String modelYearString = input.nextLine();
            try {
                int modelYear = Integer.parseInt(modelYearString);
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
                System.out.print("Retype year.\n");
                valid = false;
            }
            automobile.setYear(year);
        } while (!valid);

        // Adds user-entered odometer reading to the Automobile. Must be an integer.
        do {
            int odometer = -1;
            System.out.print("Enter the odometer reading of the automobile: ");
            String odometerString = input.nextLine().replace(",", "");
            try {
                odometer = Integer.parseInt(odometerString);
                valid = true; // NOTE, dealer may use -1 for bad odometer readings and unknown values.
            } catch (NumberFormatException e) {
                System.out.printf("%sInvalid odometer reading.%s%n",
                        color("red"), color("reset"));
                System.out.print("Re-enter the odometer reading.\n");
                valid = false;
            }
            automobile.setMileage(odometer);
        } while (!valid);

        inventory.add(automobile);
        System.out.printf("%sSuccessfully added %s.%s%n",
                color("cyan"), automobile, color("reset"));
        return inventory;
    }

    /**
     * Removes an automobile from the automobile inventory using the VIN.
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

    public ArrayList<Automobile> updateAutomobile(Scanner input, ArrayList<Automobile> inventory) {
        //todo create a menu and implement
        return inventory;
    }

    /**
     * Imports an automobile inventory from a CSV file.
     * On program initiation, backs up the default file automobileInventory.adb, and imports the file into the program.
     * If not during initialization, prompts the user to select a csv database file, limited to file extension adb.
     *
     * @param input         scanner for user input
     * @param inventory     the automobile inventory
     * @param initialImport true on program initialization, imports the default database if true.
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
            String filePath = file.getAbsolutePath();
            System.out.printf("%s%s%s%n", color("green"), filePath, color("reset"));
            if (initialImport) {
                DateTools now = new DateTools();
                String backupFilename = String.format("%s%s%s%s",
                        filePath.substring(0, file.getAbsolutePath().length() - 4),
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
                duplicateFlag = checkVin(automobile.getVin(), inventory);
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
     * Used to check for duplicate VINs in the inventory.
     * importInventory will delete duplicate VIN records.
     *
     * @param vin       The VIN from an imported file to check
     * @param inventory The current inventory
     * @return true if the VIN is already in the inventory, false otherwise
     */
    public boolean checkVin(String vin, ArrayList<Automobile> inventory) {
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
     * Lists the automobiles in the inventory.
     * @param input Scanner used to prompt the user for input.
     * @param inventory The current inventory.
     */
    public void listInventory(Scanner input, ArrayList<Automobile> inventory) {
        if (inventory.size() == 0) {
            System.out.printf("%sThere are no automobiles in the inventory.%s%n",
                    color("red"), color("reset"));
        }
        for (Automobile automobile : inventory) {
            System.out.println(automobile.toString());
        }
        System.out.print("\nType any key to continue: ");
        input.nextLine();
    }

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
            csvPrinter.flush();
        // This does not throw an exception, but prints current records to console for possible salvage.
        } catch (IOException ioe) {
            System.out.printf("%n%s***** SAVE UNSUCCESSFUL! *****%s%n%n",
                    color("red"), color("reset"));
            System.out.printf("%s%s%s%n",
                    color("red"), ioe.getMessage(), color("reset"));
            System.out.printf("%s%s%s%n",
                    color("red"), "Current automobile inventory contains: ", color("reset"));
            listInventory(input, inventory);
        }
    }

    /**
     * Prompts the user to save the automobile inventory, then quits the program.
     * @param input Scanner used to prompt the user for input.
     * @param inventory the modified Automobile inventory.
     */
    public void quit(Scanner input, ArrayList<Automobile> inventory) {
        while (true) {
            System.out.println("Save your inventory? (y/n)");
            String save = input.nextLine().toLowerCase();
            if (save.equalsIgnoreCase("y")) {
                saveInventory(input, inventory);
                break;
            } else if (save.equalsIgnoreCase("n")) {
                break;
            }
            System.out.printf("%sInvalid input. Please try again.%s%n",
                    color("red"), color("reset"));
        }
        input.close();
        System.exit(0);
    }

    /**
     * This is the main entry method for the automobileInventory class.
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

    public static void main(String[] args) {
        automobileInventory();
    }
}
