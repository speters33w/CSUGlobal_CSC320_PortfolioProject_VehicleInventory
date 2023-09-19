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
import java.nio.charset.StandardCharsets;
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

/**
 * Final Project for Colorado State University CSC320
 * The AutomobileInventory class is a Java class that provides methods for managing an inventory of automobiles.
 * The class includes methods for adding, removing, updating, importing, listing, and saving automobiles.
 * The class also includes a main menu that allows the user to perform these operations.
 */
public class AutomobileInventory {
    /**
     * An ArrayList&lt;Automobile&gt; of all automobiles in the object's inventory.
     */
    private ArrayList<Automobile> inventory;
    /**
     * A String containing the file-name of a list of valid automobile makes
     */
    private String makesList = "auto_brands_us.list";

    /**
     * Default constructor for the AutomobileInventory class. <br />
     * Imports the default inventory file (AutomobileInventory.adb) into the object's inventory. <br />
     * Imports the default valid makes list for US makes. <br /><br />
     * Example:
     * <pre>
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     * </pre>
     */
    public AutomobileInventory() {
        this.inventory = importInventory(new ArrayList<>(), true);
    }

    /**
     * Constructor for the AutomobileInventory class. <br /><br />
     * Example:
     * <pre>
     *     ArrayList&lt;Automobile&gt; inventoryList = new
     *         ArrayList&lt;&gt;(Arrays.asList(new Automobile("W2Y4ECHO5LT039795", "Freightliner",
     *             "Sprinter 2500", "White", java.time.Year.of(2020), 64672)));
     *     AutomobileInventory automobileInventory = new AutomobileInventory(inventoryList);
     * </pre>
     *
     * @param inventoryList An ArrayList&lt;Automobile&gt; of the automobiles to be included in the inventory.
     */
    public AutomobileInventory(ArrayList<Automobile> inventoryList) {
        this.inventory = new ArrayList<>();
        this.inventory.addAll(inventoryList);
    }

    /**
     * Returns a list of all automobiles in the inventory. <br />
     * Example:
     * <pre>
     *     ArrayList&lt;Automobile&gt; inventoryList = new ArrayList&lt;&gt;();
     *     AutomobileInventory automobileInventory = new AutomobileInventory(inventoryList);
     *     inventoryList = automobileInventory.getInventory();
     * </pre>
     *
     * @return A list of all automobiles in the object inventory.
     */
    public ArrayList<Automobile> getInventory() {
        return new ArrayList<>(inventory);
    }

    /**
     * Sets the list of all automobiles in the inventory. <br /><br />
     * Example:
     * <pre>
     *     ArrayList&lt;Automobile&gt; inventoryList = new ArrayList&lt;&gt;();
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     inventoryList = automobileInventory.importInventory(inventoryList, true);
     *     automobileInventory.setInventory(inventoryList);
     * </pre>
     *
     * @param updatedInventory An ArrayList&lt;Automobile&gt; of the automobiles to be included in the inventory.
     */
    public void setInventory(ArrayList<Automobile> updatedInventory) {
        this.inventory.clear();
        this.inventory.addAll(updatedInventory);
    }

    /**
     * Main menu for the Automobile Inventory program. <br />
     * Allows the user to add, remove, update, import, list, or save automobiles; or quit. <br />
     * Prints a list of menu options to the console. <br />
     * Takes the first character from the user's input and processes it through a switch statement. <br />
     * Recursively reloads the menu after performing operations except quit. <br /><br />
     * Example:
     * <pre>
     *     Scanner input = new Scanner(System.in);
     *     ArrayList&lt;Automobile&gt; inventoryList = new
     *         ArrayList&lt;&gt;(Arrays.asList(new Automobile("W2Y4ECHO5LT039795", "Freightliner",
     *             "Sprinter 2500", "White", java.time.Year.of(2020), 64672)));
     *     AutomobileInventory automobileInventory = new AutomobileInventory(inventoryList);
     *     automobileInventory.menu(input, automobileInventory);
     * </pre>
     *
     * @param input Scanner for user input.
     * @param automobileInventory The AutomobileInventory object to be updated.
     */
    public void menu(Scanner input, AutomobileInventory automobileInventory) {
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
            menu(input, automobileInventory);
        }
        switch (option) {
            case "A":
                automobileInventory.inventory = addAutomobile(input, automobileInventory.inventory);
                menu(input, automobileInventory);
                break;
            case "R":
                automobileInventory.inventory = removeAutomobile(input, automobileInventory.inventory);
                menu(input, automobileInventory);
                break;
            case "U":
                updateAutomobile(input, automobileInventory);
                menu(input, automobileInventory);
                break;
            case "I":
                automobileInventory.inventory = (importInventory(automobileInventory.inventory, false));
                menu(input, automobileInventory);
                break;
            case "L":
                listInventory(automobileInventory);
                menu(input, automobileInventory);
                break;
            case "S":
                saveInventory(automobileInventory.inventory);
                menu(input, automobileInventory);
                break;
            case "Q":
                quit(input, automobileInventory.inventory);
                break;
            default:
                System.out.printf("%sInvalid option, please try again.%s%n", color("red"), color("reset"));
                System.out.print("\nType any key to continue: ");
                input.nextLine();
                menu(input, automobileInventory);
                break;
        }

    }

    /**
     * Imports an automobile inventory from a CSV file with the extension .adb
     * and appends it to the current inventory file. <br />
     * Uses <span style="font-family: monospace;">System.getProperty(&quot;user.dir&quot;)</span>
     * as the root of the relative path. <br />
     * If initialImport is set to true, backs up the current default file with the current date and time
     * appended to the filename to user.dir/bak/, then imports AutomobileInventory.adb,
     * and appends it to the inventory. <br />
     * If initialImport is set to false, prompts the user to select a database file,
     * imports the file and appends it to the inventory. <br /><br />
     * Example:
     * <pre>
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     automobileInventory.setInventory(automobileInventory.inventory, false);
     * </pre>
     *
     * @param currentInventory The inventory to append the imported list to.
     * @param initialImport    Imports the default database if true.
     * @return The inventory with the imported list appended to it.
     */
    public ArrayList<Automobile> importInventory(ArrayList<Automobile> currentInventory, boolean initialImport) {

        File file = new File(System.getProperty("user.dir") + "/AutomobileInventory.adb");
        FileReader fileReader = null;

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
                return currentInventory;
            }
        }

        try {
            fileReader = new FileReader(file, StandardCharsets.UTF_8);
            String databasePath = file.getCanonicalPath();
            System.out.printf("%s%s%s%n", color("green"), databasePath, color("reset"));
            if (initialImport) {
                String filePath = System.getProperty("user.dir") + "/bak/" + file.getName();
                DateTools now = new DateTools();
                String backupFilename = String.format("%s%s%s%s",
                        filePath.substring(0, file.getCanonicalPath().length()),
                        "_", now.getTime(), ".bak");
                System.out.printf("%sBacking up the database, please wait...%s%n",
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
                duplicateFlag = duplicateVin(automobile.getVin(), currentInventory);
                automobile.setMake(automobileRecord.get("make"));
                automobile.setModel(automobileRecord.get("model"));
                automobile.setColor(automobileRecord.get("color"));
                automobile.setYear(Year.of(Integer.parseInt(automobileRecord.get("year"))));
                automobile.setMileage(Integer.parseInt(automobileRecord.get("mileage")));
                if (!duplicateFlag) {
                    currentInventory.add(automobile);
                    System.out.printf("%sSuccessfully added %s.%s%n",
                            color("cyan"), automobile, color("reset"));
                }
            });
        } catch (IOException e) {
            System.out.printf("%sMissing or corrupted database.%s%n",
                    color("red"), color("reset"));
            System.out.println("Current automobile currentInventory contains: ");
            for (Automobile automobile : currentInventory) {
                System.out.printf("%s%s%s%n", color("cyan"), automobile, color("reset"));
            }
            System.out.printf("%n%sERROR - IMPORT ABORTED.%s%n%n",
                    color("red"), color("reset"));
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        for (Automobile automobile : currentInventory) {
            System.out.printf("%s%s%s%n", color("cyan"), automobile, color("reset"));
        }
        return currentInventory;
    }

    /**
     * Imports a list of valid automobile makes from a file. <br />
     * Allows the user to select a list of valid automobile makes from a file chooser dialogue. <br />
     * Validates the list using a simple keyword at the beginning of the file. <br />
     * Returns null if there is no valid makes list chosen. <br /><br />
     * Example:
     * <pre>
     *     List&lt;String&gt; autoMakesList = importMakesList(false);
     * </pre>
     *
     * @param overrideMake Allows the user to select a list of valid automobile makes.
     * @return A List&lt;String&gt; of a list of valid automobile makes.
     */
    public List<String> importMakesList(boolean overrideMake) {

        // Allows the user to select a list of valid automobile makes from a file chooser.
        File file = new File(makesList);

        if (overrideMake) {
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

        // Reads the automobile makes list file into an ArrayList for input validation.
        List<String> autoMakesList = new ArrayList<>();
        FileReader importedMakesList = null;

        try {
            importedMakesList = new FileReader(file, StandardCharsets.UTF_8);
            Iterable<CSVRecord> make = CSVFormat.DEFAULT.parse(importedMakesList);
            make.forEach(line -> autoMakesList.add(line.get(0)));
            if (!autoMakesList.contains("AutomobileInventoryMakeList")) {
                System.out.printf("%sInvalid list of valid automobile makes.%s%n",
                        color("red"), color("reset"));
            }
        } catch (IOException ioe) {
            System.out.printf("%sInvalid list of valid automobile makes.%s%n",
                    color("red"), color("reset"));
        } finally {

            if (importedMakesList != null) {
                try {
                    importedMakesList.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return autoMakesList;
    }

    /**
     * Lists the automobiles in the inventory. <br />
     * If the inventory is empty, it will print an error message to the console. <br /><br />
     * Example:
     * <pre>
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     automobileInventory.listInventory();
     * </pre>
     *
     * @param automobileInventory The automobile inventory to list.
     */
    public void listInventory(AutomobileInventory automobileInventory) {

        ArrayList<Automobile> currentInventory = automobileInventory.inventory;

        if (currentInventory.size() == 0) {
            System.out.printf("%sThere are no automobiles in the inventory.%s%n",
                    color("red"), color("reset"));
        }
        for (Automobile automobile : currentInventory) {
            System.out.println(automobile.toString());
        }
    }

    /**
     * Adds an automobile to an AutomobileInventory. <br />
     * Prints a record of the automobile to the console. <br /><br />
     * Example:
     * <pre>
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     Automobile automobile = new Automobile("5Y2SL62803Z412974",
     *         "Dongfeng", "EX1", "Silver", Year.of(2021), 2000);
     *     automobileInventory.addAutomobile(automobile);
     * </pre>
     *
     * @param currentInventory The AutomobileInventory to update.
     * @param automobile       The automobile to add to the inventory.
     */
    public void addAutomobile(AutomobileInventory currentInventory, Automobile automobile) {

        currentInventory.inventory.add(automobile);

        System.out.printf("%sSuccessfully added %s.%s%n",
                color("cyan"), automobile, color("reset"));
    }

    /**
     * Allows the user to add a new automobile to the inventory. <br />
     * VIN is entered directly in this method, and may not be altered after Automobile record creation. <br />
     * VIN is checked against the inventory for duplicates or blanks. <br />
     * Provides some input verification of the make, color, and year. <br />
     * Prints a record of the automobile to the console. <br /><br />
     * Example:
     * <pre>
     *     Scanner input = new Scanner(System.in);
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     automobileInventory.addAutomobile(input, automobileInventory);
     * </pre>
     *
     * @param input            Scanner for user input.
     * @param currentInventory The AutomobileInventory to update.
     * @return The inventory with the new automobile added.
     */
    public ArrayList<Automobile> addAutomobile(Scanner input, ArrayList<Automobile> currentInventory) {

        Automobile automobile = new Automobile();

        boolean valid;
        do {
            valid = true;
            String vin = enterVin(input, "");
            if (vin.equalsIgnoreCase("QUIT")) {
                return currentInventory;
            }
            // If this were a production program, better VIN validation would need to be used.
            if (vin.isEmpty() || duplicateVin(vin, currentInventory)) {
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

        currentInventory.add(automobile);
        System.out.printf("%sSuccessfully added %s.%s%n",
                color("cyan"), automobile, color("reset"));
        return currentInventory;
    }

    /**
     * Used to prompt the user for a VIN. <br />
     * The prompt will append the modifier to the end of the prompt. <br /><br />
     * Example:
     * <pre>
     *     String vin = AutomobileInventory.enterVin(input, "");
     * </pre>
     *
     * @param input    Scanner for user input.
     * @param modifier The modifier to append to the end of the prompt.
     * @return The VIN entered by the user in upper case.
     */
    String enterVin(Scanner input, String modifier) {
        System.out.printf("%s%s%s", "Enter the VIN of the automobile ", modifier, ": ");
        return input.nextLine().toUpperCase();
    }

    /**
     * Used to check for duplicate VINs in the currentInventory. <br />
     * importInventory will ignore duplicate VIN records. <br /><br />
     * Example:
     * <pre>
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     String vinToCheck = "5Y2SL62803Z412974";
     *     if (automobileInventory.duplicateVin(vinToCheck, automobileInventory.getInventory)) {
     *         System.out.println("Duplicate VIN found. Automobile is in the inventory.");
     *     }
     * </pre>
     *
     * @param vin              The VIN to check against the inventory.
     * @param currentInventory The inventory to check against.
     * @return true if the VIN is already in the currentInventory, false otherwise
     */
    public boolean duplicateVin(String vin, ArrayList<Automobile> currentInventory) {
        for (Automobile automobile : currentInventory) {
            if (automobile.getVin().equalsIgnoreCase(vin)) {
                System.out.printf("%sDuplicate VIN found. Ignoring record. VIN: %s.%s%n",
                        color("red"), vin, color("reset"));
                return true;
            }
        }
        return false;
    }

    /**
     * Adds or updates the make of an automobile. <br />
     * Prompts the user for a valid make from the automobile makesList. <br />
     * Suggests close matches if the user input does not match any of the automobile makes. <br />
     * Allows the user to override valid makes for makes not in the database,
     * and appends an asterisk to the make for database review. <br /><br />
     * Example:
     * <pre>
     *     Scanner input = new Scanner(System.in);
     *     Automobile automobile = new Automobile("7X11Y231387", "Ford", "Pinto", "Chartreuse", Year.of(1977), 365485);
     *     automobile = addMake(input, automobile);
     * </pre>
     *
     * @param input      Scanner for user input.
     * @param automobile The automobile to be updated.
     * @return The automobile with the updated make.
     */
    public Automobile addMake(Scanner input, Automobile automobile) {
        String make = "";
        boolean overrideMake = false;  // For future modification to allow use of international list.
        List<String> autoMakesList = importMakesList(overrideMake);
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
                overrideMake = true;
                make = make + " *"; // To make it easier to find overrides in a database.
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
     * Adds or updates the model of an automobile. <br />
     * Prompts the user for a model. <br />
     * Corrects capitalization if user does not capitalize the first letter of the model. <br />
     * No validation is performed other than a check for an empty String. <br /><br />
     * <pre>
     *     Scanner input = new Scanner(System.in);
     *     Automobile automobile = new Automobile("8322870", "Studebaker", "Starliner", "Green", Year.of(1953), 107684);
     *     automobile = addModel(input, automobile);
     * </pre>
     *
     * @param input      Scanner for user input.
     * @param automobile The automobile object to be updated.
     * @return The automobile with the updated model.
     */
    public Automobile addModel(Scanner input, Automobile automobile) {
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
     * Adds or updates the color of an automobile. <br />
     * Prompts the user for a valid color from the selenium Colors Enum. <br />
     * Suggests close matches if the user input does not match any of the automobile colors. <br /><br />
     * Example:
     * <pre>
     *     Scanner input = new Scanner(System.in);
     *     Automobile automobile = new Automobile("A7A687C104099", "AMC", "Pacer", "Woodside", Year.of(1977), 38248);
     *     automobile = addColor(input, automobile);
     * </pre>
     *
     * @param input      Scanner for user input.
     * @param automobile The automobile object to be updated.
     * @return The automobile with the updated color.
     **/
    public Automobile addColor(Scanner input, Automobile automobile) {
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
     * Adds or updates the year of an automobile. <br />
     * Prompts the user for a valid year from the automobile Year as a four digit integer. <br />
     * Checks the year entered is between 1900 and the current year. <br />
     * Prompts user to enter -1 if year is unknown. <br />
     * Example:
     * <pre>
     *    Scanner input = new Scanner(System.in);
     *    Automobile automobile = new Automobile("7183771100866", "Pontiac", "Acadian", "White", Year.of(1967), 123456);
     *    automobile = addYear(input, automobile);
     * </pre>
     *
     * @param input      Scanner for user input.
     * @param automobile The automobile to be updated.
     * @return The updated automobile with the updated year.
     **/
    public Automobile addYear(Scanner input, Automobile automobile) {
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
                //CHECKSTYLE:OFF: MagicNumber
                if (modelYear >= 1900 && modelYear <= Year.now().getValue()) {
                    //CHECKSTYLE:ON: MagicNumber
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
     * Adds or updates the odometer reading of an automobile. <br />
     * Allows the user to enter -1 if the odometer reading is invalid or unknown. <br />
     * Example:
     * <pre>
     *     Scanner input = new Scanner(System.in);
     *     Automobile automobile = new Automobile("1FINE31L65HA33941", "Ford",
     *         "Econoline", "White", Year.of(2005), 286539);
     *     automobile = addMileage(input, automobile);
     * </pre>
     *
     * @param input      Scanner for user input.
     * @param automobile The automobile to be updated.
     * @return The automobile with the updated odometer reading.
     */
    public Automobile addMileage(Scanner input, Automobile automobile) {
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
     * Removes an automobile from the automobile inventory using the VIN. <br />
     * Requests a VIN from the user,
     * then uses <span style="font-family: monospace;">
     * removeAutomobile(ArrayList&lt;Automobile&gt; inventory, String vin)</span> <br />
     * to remove the automobile from the inventory. <br /><br />
     * Example:
     * <pre>
     *     Scanner input = new Scanner(System.in);
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     automobileInventory.setInventory(automobileInventory.removeAutomobile
     *         (input, automobileInventory.getInventory());
     * </pre>
     *
     * @param input            Scanner for user input
     * @param currentInventory An ArrayList&lt;Automobile&gt; to remove the automobile from.
     * @return The updated ArrayList&lt;Automobile&gt; automobile inventory with the automobile removed.
     */
    public ArrayList<Automobile> removeAutomobile(Scanner input, ArrayList<Automobile> currentInventory) {
        String vin = enterVin(input, "to be removed");
        return removeAutomobile(currentInventory, vin);
    }

    /**
     * Removes an automobile from the automobile inventory using the VIN. <br />
     * Iterates through the automobile inventory until the VIN is found,
     * then removes the automobile from the inventory. <br />
     * If the inventory does not contain the VIN, it prints an error message to the console. <br /><br />
     * Example:
     * <pre>
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     String vinToRemove = "5Y2SL62803Z412974";
     *     automobileInventory.removeAutomobile(automobileInventory.getInventory(), vinToRemove);
     * </pre>
     *
     * @param currentInventory The ArrayList&lt;Automobile&gt; to remove the automobile from.
     * @param vin              The VIN of the automobile to be removed.
     * @return the updated ArrayList&lt;Automobile&gt; with the automobile removed.
     */
    public ArrayList<Automobile> removeAutomobile(ArrayList<Automobile> currentInventory, String vin) {
        boolean removed = false;
        for (Automobile automobile : currentInventory) {
            if (automobile.getVin().equalsIgnoreCase(vin)) {
                System.out.printf("%sSuccessfully removed %s.%s%n",
                        color("cyan"), automobile, color("reset"));
                currentInventory.remove(automobile);
                removed = true;
                break;
            }
        }
        if (!removed) {
            System.out.printf("%sCould not find automobile with VIN %s.%s%n",
                    color("red"), vin, color("reset"));
        }
        return currentInventory;
    }

    /**
     * Allows the user to update an automobile currently in the database. <br />
     * Prompts the user to select an automobile from the automobile inventory using the VIN. <br />
     * Loops through the automobile inventory until the automobile is found. <br />
     * If the automobile is not found, it prints an error message to the console and returns to the main menu. <br />
     * If the automobile is found, it displays a menu with options to update the automobile. <br />
     * Then it returns the updated automobile inventory. <br /><br />
     * Example:
     * <pre>
     *     Scanner input = new Scanner(System.in);
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     automobileInventory.addAutomobile(input, automobileInventory.getInventory());
     * </pre>
     *
     * @param input Scanner for user input.
     * @param automobileInventory The ArrayList&lt;Automobile&gt; to update.
     */
    public void updateAutomobile(Scanner input, AutomobileInventory automobileInventory) {
        Automobile automobile = new Automobile(); // todo exceeds max cyclomatic complexity by 1.
        String vin = enterVin(input, "to be updated");
        for (Automobile element : automobileInventory.getInventory()) {
            if (element.getVin().equalsIgnoreCase(vin)) {
                System.out.printf("%sAutomobile found: %s.%s%n",
                        color("cyan"), element, color("reset"));
                automobile = element;
                break;
            }
        }
        if (automobile.getVin() == null) {
            System.out.printf("%sCould not find automobile with VIN %s.%s%n",
                    color("red"), vin, color("reset"));
            menu(input, automobileInventory);
        }

        System.out.printf("%sPlease select an option:%s%n", color("reset"), color("reset"));
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
            updateAutomobile(input, automobileInventory);
        }
        switch (option) {
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
                menu(input, automobileInventory);
                break;
            default:
                System.out.printf("%sInvalid option, please try again.%s%n", color("red"), color("reset"));
                System.out.print("\nType any key to continue: ");
                input.nextLine();
                updateAutomobile(input, automobileInventory);
                break;
        }
        System.out.printf("%sSuccessfully updated %s.%s%n",
                color("cyan"), automobile, color("reset"));  // todo prints "successfully added" twice on invalid input.
    }

    /**
     * Saves the automobile currentInventory. <br />
     * This will overwrite the existing default database file. <br />
     * In the case of an exception, it will print an error message to the console,
     * and a list of the current currentInventory for possible salvage from the console log. <br /><br />
     * Example:
     * <pre>
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     automobileInventory.saveInventory(automobileInventory.getInventory());
     * </pre>
     *
     * @param currentInventory The automobile inventory to save.
     */
    public void saveInventory(ArrayList<Automobile> currentInventory) {
        FileWriter writer = null;
        try {
            writer = new FileWriter("automobileInventory.adb", StandardCharsets.UTF_8);
            CSVPrinter csvPrinter = new CSVPrinter(writer,
                    CSVFormat.Builder.create()
                            .setDelimiter(",")
                            .setRecordSeparator("\n")
                            .setEscape('\\')
                            .setQuoteMode(QuoteMode.NONE)
                            .setHeader("vin", "make", "model", "color", "year", "mileage")
                            .build());
            for (Automobile automobile : currentInventory) {
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
                    color("red"), "Current automobile currentInventory contains: ", color("reset"));
            for (Automobile automobile : currentInventory) {
                System.out.printf("%s%s%s%n", color("cyan"), automobile, color("reset"));
            }
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * Used to end the program. <br />
     * Asks the user if the automobile inventory should be saved,
     * if yes, it saves the inventory (overwriting the default). <br />
     * Then quits the program if yes or no. <br />
     * If the entry does not begin with y or n,
     * it will print an error message to the console and restart the method. <br /><br />
     * Example:
     * <pre>
     *     Scanner input = new Scanner;
     *     AutomobileInventory automobileInventory = new AutomobileInventory();
     *     automobileInventory.quit(input, automobileInventory.getInventory());
     * </pre>
     *
     * @param input            Scanner used to prompt the user for input.
     * @param currentInventory The modified inventory it will save if asked.
     */
    public void quit(Scanner input, ArrayList<Automobile> currentInventory) {
        while (true) {
            System.out.println("Save your inventory? (y/n)");
            String yesNo = input.nextLine();
            if (!yesNo.isEmpty()) {
                yesNo = yesNo.toLowerCase().substring(0, 1);
                if (yesNo.equalsIgnoreCase("y")) {
                    saveInventory(currentInventory);
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
    }

    /**
     * This is the main entry method for the automobileInventory class. <br />
     * Creates a Scanner to be used throughout the program. <br />
     * Creates a new automobileInventory object. <br />
     * Creates a new ArrayList to be used throughout the program. <br />
     * Imports a default database file into the array list and creates a backup in the /bak/ directory. <br />
     * Closes the Scanner and ends the program. <br /><br />
     * Example:
     * <pre>
     *     edu.csuglobal.AutomobileInventory.automobileInventory();
     * </pre>
     */
    public static void automobileInventory() {
        Scanner input = new Scanner(System.in, StandardCharsets.UTF_8);
        ArrayList<Automobile> inventoryList = new ArrayList<>();
        AutomobileInventory automobileInventory = new AutomobileInventory(inventoryList);
        System.out.printf("%sInitializing database, please wait...%s%n",
                color("reset"), color("reset"));
        automobileInventory.inventory = automobileInventory.importInventory
                (automobileInventory.inventory, true);
        System.out.print("\nDatabase Initialized.\n");
        automobileInventory.menu(input, automobileInventory);
        input.close();
    }

    /**
     * Runnable main method for the automobileInventory class. <br /><br />
     * Example:
     * <pre>
     *     edu.csuglobal.AutomobileInventory.main(new String[0]);
     * </pre>
     *
     * @param args command line arguments (not used).
     */
    public static void main(String[] args) {
        automobileInventory();
    }
}
