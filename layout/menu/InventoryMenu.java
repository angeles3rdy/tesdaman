package layout.menu;

//changes
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

import config.DBConfig;
import layout.enums.DateEnum;
import layout.interfaces.IInventory;
import layout.product_structure.Products;
import layout.product_structure.User;
import layout.textgenerator.AsciiGenerator;

public class InventoryMenu implements IInventory {

    DBConfig db;
    Connection conn;
    Scanner sc;
    AsciiGenerator textToAscii;
    Products prod;
    User user;
    DateEnum dates;

    public InventoryMenu() {
        sc = new Scanner(System.in);
        db = new DBConfig();
        conn = db.getConnection();
        textToAscii = new AsciiGenerator();
        prod = new Products();
        user = new User();
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void exitSystem() {
        System.out.println("████████╗██╗  ██╗ █████╗ ███╗   ██╗██╗  ██╗    ██╗   ██╗ ██████╗ ██╗   ██╗");
        System.out.println("╚══██╔══╝██║  ██║██╔══██╗████╗  ██║██║ ██╔╝    ╚██╗ ██╔╝██╔═══██╗██║   ██║");
        System.out.println("   ██║   ███████║███████║██╔██╗ ██║█████╔╝      ╚████╔╝ ██║   ██║██║   ██║");
        System.out.println("   ██║   ██╔══██║██╔══██║██║╚██╗██║██╔═██╗       ╚██╔╝  ██║   ██║██║   ██║");
        System.out.println("   ██║   ██║  ██║██║  ██║██║ ╚████║██║  ██╗       ██║   ╚██████╔╝╚██████╔╝");
        System.out.println("   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝       ╚═╝    ╚═════╝  ╚═════╝ ");
        System.exit(0);
    }

    @Override
    public void login() {

        char userInput;
        boolean isValid = false;

        // clearScreen();
        System.out.println("█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗");
        System.out.println("");
        System.out.println("\t██╗    ██╗███████╗██╗      ██████╗ ██████╗ ███╗   ███╗███████╗");
        System.out.println("\t██║    ██║██╔════╝██║     ██╔════╝██╔═══██╗████╗ ████║██╔════╝");
        System.out.println("\t██║ █╗ ██║█████╗  ██║     ██║     ██║   ██║██╔████╔██║█████╗  ");
        System.out.println("\t██║███╗██║██╔══╝  ██║     ██║     ██║   ██║██║╚██╔╝██║██╔══╝  ");
        System.out.println("\t╚███╔███╔╝███████╗███████╗╚██████╗╚██████╔╝██║ ╚═╝ ██║███████╗");
        System.out.println("\t ╚══╝╚══╝ ╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚══════╝");
        System.out.println("");
        System.out.println("█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗");

        do {

            System.out.println("\t\t\t\tSIGN IN AS:");
            System.out.println("\t\t\t\t [A] ADMIN");
            System.out.println("\t\t\t\t [S] STAFF");
            System.out.println("\t\t\t\t [E] EXIT");
            System.out.print("\t\t\t\tInput: ");
            userInput = sc.nextLine().toUpperCase().charAt(0);

            switch (userInput) {
                case 'A':
                    isValid = true;
                    user.setRole("admin");
                    clearScreen();
                    adminLogin();
                    break;
                case 'S':
                    user.setRole("staff");
                    isValid = true;

                    break;
                case 'E':
                    isValid = true;
                    clearScreen();
                    exitSystem();
                    break;
                default:
                    isValid = false;

                    break;
            }

        } while (!isValid);
    }

    @Override
    public void createAccount() {
        // textToAscii.printAsciiArt("Angeles");
    }

    @Override
    public void viewInventory(int page) {
        PreparedStatement productsQuery = null;
        ResultSet productsSnapshot;
        char choice;
        boolean isValid = false;

        int limit = 10; // limit to 10 items per page
        int offset = (page - 1) * limit; // calculation for offset

        try {
            PreparedStatement countQuery = conn.prepareStatement("SELECT COUNT(*) FROM products"); // COUNT ALL PRODUCTS
            ResultSet countSnapshot = countQuery.executeQuery();
            int totalProducts = 0;
            if (countSnapshot.next()) {
                totalProducts = countSnapshot.getInt(1);
            }
            int totalPages = (int) Math.ceil(totalProducts / (double) limit); // divide product count to limit

            // Modified products query to include join and fetch the necessary fields
            productsQuery = conn.prepareStatement(
                    "SELECT p.product_id, p.product_name, b.brand AS product_brand, c.category AS product_category, " +
                            "p.product_expiry, p.product_quantity, " +
                            "DATEDIFF(p.product_expiry, CURDATE()) / 30 AS months_left, p.status " +
                            "FROM products p " +
                            "JOIN categories c ON p.category_id = c.categ_id " +
                            "JOIN brands b ON p.brand_id = b.brand_id " +
                            "LIMIT ? OFFSET ?");
            productsQuery.setInt(1, limit);
            productsQuery.setInt(2, offset);
            productsSnapshot = productsQuery.executeQuery();

            textToAscii.printDisplay("\t\t\t\t", "PRODUCTS");

            if (!productsSnapshot.isBeforeFirst()) {
                clearScreen();
                System.out.println(
                        "==================================== No products found. Going back to menu. ====================================\n");
                System.out.println(
                        "============================================ Notify staff to restock ===========================================\n");
                boolean actionValid = false;

                do {
                    System.out.println("\t\t\t\t [ 1 ] Notify Staff");
                    System.out.println("\t\t\t\t [ 0 ] Go back");
                    System.out.print("\t\t\t\t Choice: ");

                    String input = sc.nextLine();

                    switch (input) {
                        case "1":
                            clearScreen();
                            notifyStaff();
                            actionValid = true;
                            break;
                        case "0":
                            System.out.println("Going back...");
                            adminMenu();
                            actionValid = true;
                            break;
                        default:
                            System.out.println("\t\t\t\t Invalid choice. Please select again.");
                            break;
                    }
                } while (!actionValid);
            } else {
                System.out.printf("\n%5s %20s %30s %20s %20s %20s %20s %14s%n", "ID", "CATEGORY", "PRODUCT NAME",
                        "PRODUCT BRAND", "EXPIRY", "QUANTITY", "MONTHS LEFT", "STATUS");
                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                while (productsSnapshot.next()) {
                    System.out.printf("%5s %20s %24s %22s %26s %15s %18s %18s%n",
                            productsSnapshot.getInt("product_id"),
                            productsSnapshot.getString("product_category"),
                            productsSnapshot.getString("product_name"),
                            productsSnapshot.getString("product_brand"),
                            productsSnapshot.getString("product_expiry"),
                            productsSnapshot.getInt("product_quantity"),
                            productsSnapshot.getInt("months_left"),
                            productsSnapshot.getString("status"));
                    System.out.println(
                            "-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                }

                // Show page navigation options
                System.out.println("\t\t\tPage " + page + " of " + totalPages);
                System.out.println("\t\t\t[N] Next Page");
                System.out.println("\t\t\t[P] Previous Page");
                System.out.println("\t\t\t======================================");
                System.out.println("\t\t\t[A] Add Product");
                System.out.println("\t\t\t[C] View Critical Stock Products");
                System.out.println("\t\t\t[E] View Near Expiry Products");
                System.out.println("\t\t\t[0] Go Back");

                do {
                    // Handling user choice for pagination or actions can be implemented here
                } while (!isValid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addProduct() {

    }

    @Override
    public void displayAddProductPrompt() {

        String selectedCategory;
        char selectedCategoryFirstChar;
        String productName;
        char choice;

        boolean isValid = false, // for checking if product length is valid
                isValid2 = false; // for checking if user input is Y or N

        PreparedStatement categoriesQuery;
        ResultSet categoriesSnapshot;

        PreparedStatement isCategoryExistQuery;
        ResultSet isCategoryExistSnapshot;

        PreparedStatement insertToFixedProdsQuery;

        try {
            textToAscii.printDisplay("", "AVAILABLE CATEGORIES");

            categoriesQuery = conn.prepareStatement("SELECT * FROM categories");
            categoriesSnapshot = categoriesQuery.executeQuery();

            while (categoriesSnapshot.next()) {
                System.out.println(
                        "\t\t\t\t\t[ " + categoriesSnapshot.getInt("categ_id") + " ] "
                                + categoriesSnapshot.getString("category"));

            }
            categoriesQuery.close();
            System.out.println(
                    "\t\t\t\t\t[ 0 ] Go Back");

            System.out.print("\t\t\t\tAdd products for category: ");
            selectedCategory = sc.nextLine();
            selectedCategoryFirstChar = selectedCategory.charAt(0);

            if (selectedCategory.matches("\\d+")) { // valid input

                isCategoryExistQuery = conn.prepareStatement("SELECT * FROM categories WHERE categ_id = ?");
                isCategoryExistQuery.setInt(1, Integer.parseInt(selectedCategory));
                isCategoryExistSnapshot = isCategoryExistQuery.executeQuery();

                if (selectedCategoryFirstChar == '0') {
                    isValid = true;
                    clearScreen();
                    adminMenu();
                } else {
                    if (!isCategoryExistSnapshot.next()) { // category invalid
                        clearScreen();
                        System.out.println(
                                "\t===========================CATEGORY NOT FOUND===========================\n");
                        displayAddProductPrompt();
                    } else {
                        clearScreen();
                        prod.setProductCategory(isCategoryExistSnapshot.getString("category"));
                        prod.setProducDbColumn("fixed_" + prod.getProductCategory().toLowerCase());

                        do {

                            textToAscii.printDisplay("\t\t", "ADDING PRODUCTS");

                            System.out
                                    .println("\t\tFor system integrity, only one product can be added at a time.");
                            System.out.println("\t\t\t\tType 0 to go back");
                            System.out.print("\t\t\t\tEnter product name: ");

                            productName = sc.nextLine().toUpperCase();

                            if (productName.charAt(0) == '0') {
                                clearScreen();
                                System.out.println(
                                        "\t================================ACTION CANCELLED================================");

                                adminMenu();
                                return;
                            }

                            if (productName.length() < 4) {
                                isValid = false;
                                clearScreen();
                                System.out
                                        .println("\t\t========Product name must have atleast 4 characters!========\n");
                            } else {
                                isValid = true;
                                prod.setProductName(productName);

                                do {
                                    System.out.print("\t\t\t\tAdd " + prod.getProductName() + " to inventory? [Y/N]: ");
                                    choice = sc.nextLine().toUpperCase().charAt(0);

                                    switch (choice) {
                                        case 'Y':
                                            isValid2 = true;
                                            insertToFixedProdsQuery = conn.prepareStatement(
                                                    "INSERT INTO " + prod.getProducDbColumn() + " (item) VALUES (?)");
                                            insertToFixedProdsQuery.setString(1, productName);
                                            insertToFixedProdsQuery.executeUpdate();
                                            Thread.sleep(1000);
                                            clearScreen();
                                            System.out.println(
                                                    "\t================================PRODUCT ADDED SUCCESSFULLY================================");
                                            adminMenu();
                                            break;
                                        case 'N':
                                            isValid2 = true;

                                            break;

                                        default:
                                            isValid2 = false;
                                            clearScreen();
                                            System.out.println(
                                                    "\t================================INVALID INPUT================================");
                                            break;
                                    }
                                } while (!isValid2);

                            }
                        } while (!isValid);
                    }
                }
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void staffLogin() {

    }

    @Override
    public void adminLogin() {
        PreparedStatement checkAccountQuery = null;
        ResultSet checkAccountSnapshot = null;
        String usernameInp, passwordInp;
        boolean isValid = false;

        try {
            do {
                System.out.println("█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗");
                System.out.println("");
                System.out.println("\t\t █████╗ ██████╗ ███╗   ███╗██╗███╗   ██╗");
                System.out.println("\t\t██╔══██╗██╔══██╗████╗ ████║██║████╗  ██║");
                System.out.println("\t\t███████║██║  ██║██╔████╔██║██║██╔██╗ ██║");
                System.out.println("\t\t██╔══██║██║  ██║██║╚██╔╝██║██║██║╚██╗██║");
                System.out.println("\t\t██║  ██║██████╔╝██║ ╚═╝ ██║██║██║ ╚████║");
                System.out.println("\t\t╚═╝  ╚═╝╚═════╝ ╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝");
                System.out.println("");
                System.out.println("█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗");

                System.out.println("Enter 0 to go back");
                System.out.print("\t\t\tEnter username: ");
                usernameInp = sc.nextLine();
                if (usernameInp.equals("0")) {
                    clearScreen();
                    login();
                    return;
                }
                System.out.print("\t\t\tEnter password: ");
                passwordInp = sc.nextLine();
                if (passwordInp.equals("0")) {
                    clearScreen();
                    login();
                    return;
                }

                checkAccountQuery = conn
                        .prepareStatement("SELECT * FROM user_info WHERE username = ? AND password = ?");
                checkAccountQuery.setString(1, usernameInp);
                checkAccountQuery.setString(2, passwordInp);

                checkAccountSnapshot = checkAccountQuery.executeQuery();

                if (checkAccountSnapshot.next()) { // may account
                    isValid = true;
                    user.setUserId(checkAccountSnapshot.getInt("id"));
                    clearScreen();
                    adminMenu();
                } else {
                    isValid = false;
                    clearScreen();
                    System.out.println(
                            "\t================================Invalid Credentials! Try again.================================\n");
                }

            } while (!isValid);

        } catch (Exception e) {
            isValid = false;
            adminLogin();
            System.out.println("mali");
            return;
        }
    }

    @Override
    public void addCategories() {

        String category;
        char firstCharacter, choice, doesExpire;
        int doesExpireConverted;
        boolean isValid = false;

        PreparedStatement insertCategQuery;
        PreparedStatement createFixedTableQuery;
        PreparedStatement checkCategQuery;
        ResultSet resultSet;

        try {
            textToAscii.printDisplay("\t\t", "ADD CATEGORIES");
            System.out.println("\t\t\tType 0 to go back");
            System.out.print("\t\t\tEnter new category: ");

            category = sc.nextLine().toUpperCase();
            firstCharacter = category.charAt(0);

            if (firstCharacter == '0') {
                clearScreen();
                adminMenu();
                return;
            }

            checkCategQuery = conn.prepareStatement("SELECT category FROM categories WHERE UPPER(category) = ?");
            checkCategQuery.setString(1, category);
            resultSet = checkCategQuery.executeQuery();

            if (resultSet.next()) {
                clearScreen();
                System.out.println("\t\t========Category already exists!========\n");
                addCategories();
                return;
            }

            System.out.println("\t\t\tDoes items in this category expire? [Y/N]: ");
            System.out.print("\t\t\t(THIS CANNOT BE CHANGED): ");
            doesExpire = sc.nextLine().toUpperCase().charAt(0);
            firstCharacter = doesExpire;
            doesExpireConverted = doesExpire == 'Y' ? 1 : 0;

            if (firstCharacter == '0') {
                clearScreen();
                adminMenu();
            } else {
                System.out.println("\t\t\tCONFIRM CATEGORY NAME: " + category);
                System.out.println("\t\t\tIS PERISHABLE: " + (doesExpire == 'Y' ? "Yes\n" : "No\n"));
                System.out.print("\t\t\tChoice [Y/N]: ");
                choice = sc.nextLine().toUpperCase().charAt(0);

                do {
                    switch (choice) {
                        case 'Y':
                            isValid = true;
                            insertCategQuery = conn
                                    .prepareStatement("INSERT INTO categories (category, does_expire) VALUES (?, ?)");
                            insertCategQuery.setString(1, category);
                            insertCategQuery.setInt(2, doesExpireConverted);
                            insertCategQuery.executeUpdate();

                            createFixedTableQuery = conn.prepareStatement("CREATE TABLE fixed_" + category.toLowerCase()
                                    + "(id INT AUTO_INCREMENT PRIMARY KEY, item VARCHAR(255));");
                            createFixedTableQuery.executeUpdate();

                            clearScreen();
                            System.out.println(
                                    "\t===========================CATEGORY ADDED SUCCESSFULLY===========================\n");
                            adminMenu();
                            break;

                        case 'N':
                            isValid = true;
                            clearScreen();
                            System.out.println(
                                    "\t================================ACTION CANCELLED================================\n");
                            addCategories();
                            break;

                        default:
                            isValid = false;
                            clearScreen();
                            System.out.println(
                                    "\t================================Invalid Input!================================\n");
                            break;
                    }
                } while (!isValid);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            clearScreen();
            System.out.println(
                    "\t================================AN ERROR OCCURED================================\n");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void addBrand() {

        PreparedStatement insertToBrandQuery;
        PreparedStatement checkCategById;
        PreparedStatement checkCategoriesQuery;
        ResultSet resultSet;

        char choice; // Y or N
        String choice2; // SELECTED CATEGORY ID
        String productBrand;
        boolean isValid = false, isValid2 = false; // Validations

        try {

            try {
                checkCategoriesQuery = conn.prepareStatement("SELECT * FROM categories"); // check if category exists
                resultSet = checkCategoriesQuery.executeQuery();

                if (!resultSet.isBeforeFirst()) {
                    clearScreen();
                    System.out.println("\t\t========No categories available. Please add categories first!========\n");
                    adminMenu();
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error retrieving categories: " + e.getMessage());
                return;
            }

            do {
                textToAscii.printDisplay("\t\t\t   ", "ADD BRAND");

                System.out.println("\n\t\t\t\tType 0 to go back ");
                System.out.print("\t\t\t\tEnter product brand: ");
                productBrand = sc.nextLine().toUpperCase();

                if (productBrand.charAt(0) == '0') {
                    clearScreen();
                    adminMenu();
                    return;
                }

                if (productBrand.length() < 4) {
                    isValid = false;
                    clearScreen();
                    System.out.println("\t\t========Product name must have at least 4 characters!========\n");
                } else {
                    prod.setProductBrand(productBrand);
                    isValid = true;

                    System.out.println("\n\t\t     Appropriate category not found? Type [C] to add new.");
                    System.out
                            .println("\n\t\t     Select appropriate category for the brand: " + prod.getProductBrand());

                    try {
                        while (resultSet.next()) {
                            System.out.println("\t\t\t\t\t[ " + resultSet.getInt("categ_id") + " ] "
                                    + resultSet.getString("category"));
                        }
                    } catch (Exception e) {
                        System.out.println("Error displaying categories: " + e.getMessage());
                        return;
                    }

                    System.out.println("\t\t\t\t\t[ 0 ] Go Back");
                    System.out.print("\t\t\t\tChoice: ");
                    choice2 = sc.nextLine();

                    if (choice2.equalsIgnoreCase("C")) {
                        clearScreen();
                        System.out.println(
                                "\t===========================GOING BACK WILL PROCEED TO MAIN MENU===========================\n");
                        addCategories();
                        return;
                    }

                    if (choice2.charAt(0) == '0') {
                        clearScreen();
                        System.out.println(
                                "\t================================ACTION CANCELLED================================\n");
                        adminMenu();
                        return;
                    }

                    // Verify category ID
                    try {
                        checkCategById = conn.prepareStatement("SELECT * FROM categories WHERE categ_id = ?");
                        checkCategById.setInt(1, Integer.parseInt(choice2));
                        ResultSet categoryResult = checkCategById.executeQuery();

                        if (!categoryResult.next()) {
                            clearScreen();
                            System.out.println("\t\t========Invalid category ID!========\n");
                            isValid = false; // Restart the process
                        } else {
                            do {
                                System.out.print("\t\t\t\tAdd brand: " + prod.getProductBrand() + " to category "
                                        + categoryResult.getString("category") + "? [Y/N]: ");
                                choice = sc.nextLine().toUpperCase().charAt(0);

                                switch (choice) {
                                    case 'Y':
                                        isValid2 = true;
                                        clearScreen();

                                        // Insert the brand into the brands table
                                        try {
                                            insertToBrandQuery = conn.prepareStatement(
                                                    "INSERT INTO brands (brand, category_id) VALUES (?, ?)");
                                            insertToBrandQuery.setString(1, productBrand);
                                            insertToBrandQuery.setInt(2, Integer.parseInt(choice2));
                                            insertToBrandQuery.executeUpdate();

                                            System.out.println(
                                                    "\t===========================BRAND ADDED SUCCESSFULLY===========================\n");
                                            adminMenu();
                                        } catch (Exception e) {
                                            System.out.println("Error adding brand: " + e.getMessage());
                                        }
                                        break;

                                    case 'N':
                                        isValid2 = true;
                                        clearScreen();
                                        System.out.println(
                                                "\t================================ACTION CANCELLED================================\n");
                                        addBrand();
                                        break;

                                    default:
                                        isValid2 = false;
                                        clearScreen();
                                        System.out.println(
                                                "\t================================Invalid Input!================================\n");
                                        break;
                                }
                            } while (!isValid2);
                        }
                    } catch (Exception e) {
                        System.out.println("Error validating category ID: " + e.getMessage());
                    }
                }
            } while (!isValid);

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            clearScreen();
            System.out.println("\t================================AN ERROR OCCURRED================================\n");
        }
    }

    @Override
    public void viewItemByCategory() {

    }

    @Override
    public void viewCriticalStocks() {

    }

    @Override
    public void viewNearExpiry() {

    }

    @Override
    public void displayCategories() {

        int choice; // when no categories
        String chosenCategory; // when there are categories
        char firstCharacter;
        boolean isValid = false;

        PreparedStatement categoriesQuery;
        ResultSet categoriesSnapshot;

        PreparedStatement checkIfCategoryPresent;
        ResultSet checkIfCategoryPresentSnapshot;

        try {
            textToAscii.printDisplay("", "AVAILABLE CATEGORIES");

            categoriesQuery = conn.prepareStatement("SELECT * FROM categories");
            categoriesSnapshot = categoriesQuery.executeQuery();

            if (!categoriesSnapshot.isBeforeFirst()) {

                do {
                    System.out.println(
                            "\t================================No categories found.================================");
                    System.out.println("\t\t\t\t [1] Add new Categories");
                    System.out.println("\t\t\t\t [0] Go Back");
                    System.out.print("\t\t\t\t Choice: ");

                    choice = sc.nextLine().charAt(0);
                    switch (choice) {
                        case '1':
                            isValid = true;
                            clearScreen();
                            addCategories();
                            break;
                        case '0':
                            isValid = true;
                            clearScreen();
                            adminMenu();
                            break;

                        default:
                            clearScreen();
                            System.out.println(
                                    "\t================================INVALID INPUT================================");
                            displayCategories();
                            break;
                    }
                } while (!isValid);

            } else {

                while (categoriesSnapshot.next()) {
                    System.out.println(
                            "\t\t\t\t\t[ " + categoriesSnapshot.getInt("categ_id") + " ] "
                                    + categoriesSnapshot.getString("category"));

                }
                System.out.println(
                        "\t\t\t\t\t[ 0 ] Go Back");

                System.out.print("\t\t\t\tChoice: ");
                chosenCategory = sc.nextLine();
                firstCharacter = chosenCategory.charAt(0);

                if (chosenCategory.matches("\\d+")) {
                    isValid = true;

                    checkIfCategoryPresent = conn.prepareStatement("SELECT * FROM categories WHERE categ_id = ?");
                    checkIfCategoryPresent.setInt(1, Integer.parseInt(chosenCategory));
                    checkIfCategoryPresentSnapshot = checkIfCategoryPresent.executeQuery();

                    if (firstCharacter == '0') {
                        isValid = true;
                        clearScreen();
                        adminMenu();
                    } else {

                        if (!checkIfCategoryPresentSnapshot.next()) {

                            isValid = true;
                            clearScreen();
                            System.out.println(
                                    "\t================================CATEGORY NOT FOUND================================\n");
                            displayCategories();

                        } else {
                            isValid = true;
                            prod.setProductCategory(checkIfCategoryPresentSnapshot.getString("category")); // category
                                                                                                           // name
                            prod.setCategId(checkIfCategoryPresentSnapshot.getInt("id")); // category id
                            prod.setProducDbColumn(
                                    "fixed_" + checkIfCategoryPresentSnapshot.getString("category").toLowerCase());

                            clearScreen();

                            prod.displayCurrent("", prod.getProductCategory(), "", "", 0, "");
                            displayProductNames();
                        }
                    }

                } else {
                    isValid = true;
                    clearScreen();
                    System.out.println(
                            "\t================================ONLY ENTER DIGITS================================\n");
                    displayCategories();
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(
                    "\t================================INVALID INPUT================================");
        }
    }

    @Override
    public void displayBrands() {

        PreparedStatement brandsQuery;
        ResultSet brandsSnapshot;

        PreparedStatement brandsQueryById;
        ResultSet brandsSnapshotById;

        String choice;
        char firstCharacter;

        try {

            brandsQuery = conn.prepareStatement("SELECT * FROM brands WHERE category_id = " + prod.getCategId() + ";");
            brandsSnapshot = brandsQuery.executeQuery();

            textToAscii.printDisplay("\t", "AVAILABLE BRANDS");
            System.out.println("\t\t\t\t   BRANDS UNDER " + prod.getProductCategory() + "\n");
            System.out.printf("\t\t\t\t   %5s %15s%n", "ID", "ITEM");
            while (brandsSnapshot.next()) {
                System.out.printf("\t\t\t\t   %5s %15s%n",
                        "[" + brandsSnapshot.getInt("id") + "]",
                        brandsSnapshot.getString("brand"));
            }
            System.out.println(
                    "\t\t\t\t     [0]         Go Back");

            System.out.print("\t\t\t\tChoice: ");
            choice = sc.nextLine();
            firstCharacter = choice.charAt(0);

            if (choice.matches("\\d+")) { // kung number ung input
                brandsQueryById = conn.prepareStatement(
                        "SELECT * FROM brands WHERE category_id = " + prod.getCategId() + " AND id = " + choice + ";");
                brandsSnapshotById = brandsQueryById.executeQuery();

                if (firstCharacter == '0') {
                    clearScreen();
                    displayProductNames();
                } else {
                    if (!brandsSnapshotById.next()) {

                        clearScreen();
                        System.out.println(
                                "\t================================BRAND NOT FOUND================================\n");
                        displayBrands();

                    } else {
                        prod.setProductBrand(brandsSnapshotById.getString("brand"));
                        prod.setBrandId(brandsSnapshotById.getInt("id"));
                        clearScreen();
                        prod.displayCurrent(prod.getProductName(), prod.getProductCategory(), prod.getProductBrand(),
                                "", 0, "");

                        displayProductExpiryPrompt();
                    }

                }

            } else {
                clearScreen();
                System.out.println(
                        "\t================================ONLY ENTER DIGITS================================\n");
                displayBrands();
            }

        } catch (Exception e) {
            // System.out.println(e.getMessage());
            clearScreen();
            System.out.println(
                    "\t================================AN ERROR OCCURED================================\n");
            displayProductNames();
        }
    }

    @Override
    public void adminMenu() {
        char choice;
        boolean isValid = false;

        try {
            System.out.println("█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗");
            System.out.println("");
            System.out.println("\t\t███╗   ███╗███████╗███╗   ██╗██╗   ██╗");
            System.out.println("\t\t████╗ ████║██╔════╝████╗  ██║██║   ██║");
            System.out.println("\t\t██╔████╔██║█████╗  ██╔██╗ ██║██║   ██║");
            System.out.println("\t\t██║╚██╔╝██║██╔══╝  ██║╚██╗██║██║   ██║");
            System.out.println("\t\t██║ ╚═╝ ██║███████╗██║ ╚████║╚██████╔╝");
            System.out.println("\t\t╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝");
            System.out.println("");
            System.out.println("█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗█████╗");

            do {
                System.out.println("\t\t\tChoose an option:");
                System.out.println("\t\t\t[1] Show Products");
                System.out.println("\t\t\t[2] Add Initial Products");
                System.out.println("\t\t\t[3] Add Categories");
                System.out.println("\t\t\t[4] Add Brands");
                System.out.println("\t\t\t[5] Manage Staffs");
                System.out.println("\t\t\t[6] My Account");
                System.out.println("\t\t\t[7] Logout");

                System.out.print("\t\t\tChoice: ");
                choice = sc.nextLine().charAt(0);

                switch (choice) {
                    case '1':
                        isValid = true;
                        clearScreen();
                        viewInventory(1);
                        break;
                    case '2':
                        isValid = true;
                        clearScreen();
                        displayAddProductPrompt();
                        break;
                    case '3':
                        isValid = true;
                        clearScreen();
                        addCategories();
                        break;
                    case '4':
                        isValid = true;
                        clearScreen();
                        addBrand();
                        break;
                    case '5':
                        isValid = true;
                        clearScreen();
                        manageStaff();
                        break;
                    case '6':
                        isValid = true;
                        clearScreen();

                        break;
                    case '7':
                        isValid = true;
                        clearScreen();
                        break;

                    default:
                        isValid = false;
                        clearScreen();
                        System.out.println("\t\t\tInvalid Input");
                        break;
                }
            } while (!isValid);

        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void displayProductNames() {

        String chosenProduct;
        char firstCharacter;

        boolean isValid = false;
        char choice; // for when no products/items are found

        PreparedStatement productByCategoryQuery;
        ResultSet productByCategorySnapshot;

        PreparedStatement productByIdQuery;
        ResultSet productByIdSnapshot;

        try {
            textToAscii.printDisplay("\t", "AVAILABLE PRODUCTS");

            productByCategoryQuery = conn
                    .prepareStatement("SELECT * FROM " + prod.getProducDbColumn() + "");

            productByCategorySnapshot = productByCategoryQuery.executeQuery();

            if (!productByCategorySnapshot.isBeforeFirst()) { // kung walang products udner that category
                do {

                    System.out.println(
                            "\n\t================================No products found.================================\n");
                    System.out.println("\t\t\t\t [1] Add Products under " + prod.getProductCategory());
                    System.out.println("\t\t\t\t [0] Go Back");
                    System.out.print("\t\t\t\t Choice: ");

                    choice = sc.nextLine().charAt(0);
                    switch (choice) {
                        case '1':
                            isValid = true;
                            clearScreen();
                            addProduct();
                            break;
                        case '0':
                            isValid = true;
                            clearScreen();
                            displayCategories();
                            break;

                        default:
                            clearScreen();
                            System.out.println(
                                    "\t================================INVALID INPUT================================");
                            textToAscii.printDisplay("\t", "AVAILABLE PRODUCTS");

                            // displayCategories();
                            break;
                    }
                } while (!isValid);

            } else {
                System.out.println("\t\t\t\t   PRODUCTS UNDER " + prod.getProductCategory() + "\n");
                System.out.printf("\t\t\t\t   %5s %15s%n", "ID", "ITEM");
                while (productByCategorySnapshot.next()) {
                    System.out.printf("\t\t\t\t   %5s %15s%n",
                            "[" + productByCategorySnapshot.getInt("id") + "]",
                            productByCategorySnapshot.getString("item"));
                }
                System.out.println(
                        "\t\t\t\t     [0]       Go Back");

                System.out.print("\t\t\t\tChoice: ");
                chosenProduct = sc.nextLine();
                firstCharacter = chosenProduct.charAt(0);

                if (chosenProduct.matches("\\d+")) {
                    productByIdQuery = conn
                            .prepareStatement("SELECT * FROM " + prod.getProducDbColumn() + " WHERE id = ?");
                    productByIdQuery.setInt(1, Integer.parseInt(chosenProduct));
                    productByIdSnapshot = productByIdQuery.executeQuery();

                    if (firstCharacter == '0') {
                        isValid = true;
                        clearScreen();
                        displayCategories();
                    } else {
                        if (!productByIdSnapshot.next()) {
                            isValid = true;
                            clearScreen();
                            System.out.println(
                                    "\t================================PRODUCT NOT FOUND================================\n");
                            displayProductNames();
                        } else {
                            isValid = true;

                            prod.setProductName(productByIdSnapshot.getString("item")); // item name
                            // prod.setCategId(productByIdSnapshot.getInt("id")); // item id
                            // prod.setProducDbColumn(
                            // "fixed_" + productByIdSnapshot.getString("item").toLowerCase());

                            clearScreen();

                            prod.displayCurrent(prod.getProductName(), prod.getProductCategory(), "", "", 0, "");
                            displayBrands();
                        }
                    }

                } else {
                    clearScreen();
                    System.out.println(
                            "\t================================ONLY ENTER DIGITS================================\n");
                    displayProductNames();
                }

            }

        } catch (Exception e) {
            clearScreen();
            // System.out.println(e.getMessage());
            System.out.println(
                    "\t================================AN ERROR OCCURED================================\n");
            displayCategories();

        }
    }

    @Override
    public void displayProductExpiryPrompt() {

        PreparedStatement checkIfDoesExpireQuery;
        ResultSet checkIfDoesExpireSnapshot;

        int doesExpire;

        String month, day, year;
        int parsedMonth, parsedDay, parsedYear;
        int currentYear = LocalDate.now().getYear();

        char firstCharacter;

        try {

            checkIfDoesExpireQuery = conn
                    .prepareStatement("SELECT does_expire FROM categories WHERE id = " + prod.getCategId());
            checkIfDoesExpireSnapshot = checkIfDoesExpireQuery.executeQuery();
            checkIfDoesExpireSnapshot.next();

            doesExpire = checkIfDoesExpireSnapshot.getInt("does_expire");

            if (doesExpire == 0) { // does not expire
                clearScreen();
                prod.setProductExpiry("");
                prod.displayCurrent(prod.getProductName(), prod.getProductCategory(), prod.getProductBrand(),
                        prod.getProductExpiry(), 0, "");
                displayQuantityPrompt();
            }

            else if (doesExpire == 1) { // does expire
                textToAscii.printDisplay("\t   ", "PRODUCT EXPIRY");
                System.out.println("\t\t\tEnter product expiry for " + prod.getProductName() + ".");
                System.out.println("\t\t\tEnter '0' to go back");
                while (true) {
                    System.out.print("\t\t\tEnter month of expiry (01 - 12): ");
                    month = sc.nextLine();
                    firstCharacter = month.charAt(0);

                    if (firstCharacter == '0') {
                        clearScreen();
                        displayBrands();
                        return;
                    }

                    try {
                        parsedMonth = Integer.parseInt(month);
                        if (parsedMonth >= 1 && parsedMonth <= 12) {
                            break;
                        } else {
                            System.out.println("\t\t\tInvalid month. Please enter a valid month between 01 and 12.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\t\t\tPlease enter a valid number for the month.");
                    }
                }

                // Validate year input
                while (true) {
                    System.out.print("\t\t\tEnter year of expiry (2*** and not less than the current year): ");
                    year = sc.nextLine();
                    firstCharacter = year.charAt(0);
                    if (firstCharacter == '0') {
                        clearScreen();
                        displayBrands();
                        return;
                    }

                    try {
                        parsedYear = Integer.parseInt(year);
                        if (parsedYear >= currentYear && parsedYear >= 2000 && parsedYear <= 2999) {
                            break;
                        } else if (parsedYear < currentYear) {
                            System.out
                                    .println(
                                            "\t\t\tThe year cannot be in the past. Please enter a current or future year.");
                        } else {
                            System.out.println("\t\t\tInvalid year. Please enter a year in the format 2***.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\t\t\tPlease enter a valid number for the year.");
                    }
                }

                while (true) {
                    System.out.print("\t\t\tEnter day of expiry (1-31): ");
                    day = sc.nextLine();
                    firstCharacter = day.charAt(0);

                    if (firstCharacter == '0') {
                        clearScreen();
                        displayBrands();
                        return;
                    }
                    try {
                        parsedDay = Integer.parseInt(day); // here, 1 is january
                        dates = DateEnum.values()[parsedMonth - 1]; // enums are 0 based so minus 1
                        if (parsedDay >= 1 && parsedDay <= dates.getDaysInMonth(parsedYear)) {
                            break;
                        } else {
                            System.out.printf("\t\t\tInvalid day for month %02d. Please enter a valid day.\n",
                                    parsedMonth);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\t\t\tPlease enter a valid number for the day.");
                    }
                }

                prod.setProductExpiry(parsedMonth + "-" + parsedDay + "-" + parsedYear);
                prod.setProductExpiry(prod.getProductExpiry());

                clearScreen();
                prod.displayCurrent(prod.getProductName(), prod.getProductCategory(), prod.getProductBrand(),
                        prod.getProductExpiry(), 0, "");

                displayQuantityPrompt();

                // System.out.println("\t\t\tExpiry date is valid: " + parsedDay + "-" +
                // parsedMonth + "-" + parsedYear);
            }

        } catch (Exception e) {
            clearScreen();
            // System.out.println(e.getMessage());
            System.out.println(
                    "\t================================PROCEED AGAIN================================\n");
            displayProductExpiryPrompt();
        }
    }

    @Override
    public void displayQuantityPrompt() {

        int quantity = 0;
        boolean isValid = false;

        try {
            textToAscii.printDisplay("\t    ", "PRODUCT QUANTITY");

            do {
                System.out.println("\t\t\tEnter '0' to go back");
                System.out.println("\t\t\tEnter the quantity of " + prod.getProductName() + " you want to add");
                System.out.print("\t\t\tQuantity: ");

                if (sc.hasNextInt()) {
                    quantity = sc.nextInt();

                    if (quantity > 0) {
                        isValid = true;
                        prod.setProductQuantity(quantity);

                    } else if (quantity < 0) {
                        System.out.println("\t\t\tPlease enter a positive quantity.");
                        isValid = false;

                        // return;
                    } else {
                        isValid = true;
                        clearScreen();
                        displayProductExpiryPrompt();
                    }
                } else {
                    System.out.println("\t\t\tInvalid input. Please enter a whole number.");
                    sc.next();
                    isValid = false;
                }
            } while (!isValid);

            clearScreen();

            prod.displayCurrent(prod.getProductName(), prod.getProductCategory(), prod.getProductBrand(),
                    prod.getProductExpiry(), prod.getProductQuantity(), "");

        } catch (Exception e) {
            clearScreen();
            System.out.println(e.getMessage());
            System.out.println(
                    "\t================================AN ERROR OCCURED================================\n");
            displayProductExpiryPrompt();
        }
    }

    @Override
    public void displayConfirmation() {

    }

    @Override
    public void notifyStaff() {
        try {
            PreparedStatement notifyStaff = conn
                    .prepareStatement("INSERT INTO notif (notifier, type) VALUES ('admin', 'restock');");
            notifyStaff.executeUpdate();

            System.out.println(
                    "\t\t\t\t\t\t==========================NOTIFIED STAFF SUCCESSFULLY==========================\n\n");

            adminMenu();
            // showCriticalProducts();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    @Override
    public void displayNotifications() {

    }

    @Override
    public void displayProductsToRestock() {

    }

    @Override
    public void displayExpiredProducts() {

    }

    @Override
    public void displayLowStockProducts() {

    }

    @Override
    public void notifyAdmin() {
        try {
            PreparedStatement notifyStaff = conn
                    .prepareStatement("INSERT INTO notif (notifier, type) VALUES ('staff', 'done');");
            notifyStaff.executeUpdate();

            System.out.println(
                    "\t\t\t\t\t\t==========================NOTIFIED ADMIN SUCCESSFULLY==========================\n\n");

            staffMenu();
            // showCriticalProducts();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    @Override
    public void myAccount(String role) {
        try {
            int userId = user.getUserId();
            boolean isValid = false;

            textToAscii.printDisplay("\t\t", "MANAGE ACCOUNT");

            if (role.equals("admin") || role.equals("staff")) {
                do {
                    System.out.println("\t\t\t\t[ 1 ] Change password");
                    System.out.println("\t\t\t\t[ 0 ] Go back");
                    System.out.print("\t\t\tSelect an option: ");
                    String option = sc.nextLine();

                    switch (option) {
                        case "0":
                            clearScreen();
                            System.out.println(
                                    "\t================================ACTION CANCELLED================================");
                            adminMenu();
                            break;
                        case "1":
                            System.out.print("\t\t\tEnter your current password: ");
                            String currentPassword = sc.nextLine();

                            if (!validateCurrentPassword(userId, currentPassword)) {
                                System.out.println("\t\t\tCurrent password is incorrect. Please try again.");
                                break; // Loop again
                            }

                            System.out.print("\t\t\tEnter your new password: ");
                            String newPassword = sc.nextLine();
                            System.out.print("\t\t\tConfirm your new password: ");
                            String confirmPassword = sc.nextLine();

                            if (!newPassword.equals(confirmPassword)) {
                                System.out.println("\t\t\tPasswords do not match. Please try again.");
                                break;
                            }

                            if (updatePasswordInDatabase(userId, newPassword)) {
                                System.out.println("\t\t\tPassword changed successfully.");
                                isValid = true;
                                clearScreen();
                                System.out.println(
                                        "\t============================Password changed successfully. Login again===============================\n");
                                login();
                            } else {
                                System.out.println("\t\tFailed to change password. Please try again.");
                                break;
                            }
                            break;
                        default:
                            System.out.println(
                                    "\t================================Invalid Input. Try again.================================");
                            break;
                    }
                } while (!isValid);
            } else {
                System.out.println("Access denied. You do not have permission to change the password.");
            }
        } catch (Exception e) {
            System.out.println("==============================AN ERROR OCCURRED.==============================");
            System.out.println(e.getMessage());
        }
    }

    private boolean validateCurrentPassword(int userId, String currentPassword) {
        boolean isSame = false;
        try {
            PreparedStatement checkAccountQuery = conn
                    .prepareStatement("SELECT * FROM user_info WHERE id = ? AND password = ?");
            checkAccountQuery.setInt(1, userId);
            checkAccountQuery.setString(2, currentPassword);

            ResultSet checkAccountSnapshot = checkAccountQuery.executeQuery();

            if (checkAccountSnapshot.next()) {
                isSame = true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return isSame;
    }

    private boolean updatePasswordInDatabase(int userId, String newPassword) {
        boolean isUpdated = false;
        try {
            PreparedStatement updatePasswordQuery = conn
                    .prepareStatement("UPDATE user_info SET password = ? WHERE id = ?");
            updatePasswordQuery.setString(1, newPassword);
            updatePasswordQuery.setInt(2, userId);

            int rowsAffected = updatePasswordQuery.executeUpdate();
            if (rowsAffected > 0) {
                isUpdated = true;
            }
        } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
        }

        return isUpdated;
    }

    @Override
    public void manageStaff() {
        try {
            textToAscii.printDisplay("\t\t", "MANAGE ACCOUNTS");

            String query = "SELECT id, username, role FROM user_info WHERE role != 'admin' AND is_fired = 0";
            PreparedStatement displayUserQuery = conn.prepareStatement(query);
            ResultSet resultSet = displayUserQuery.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("\n\t\t=================== NO STAFF FOUND ===================");
                boolean actionValid = false;

                do {
                    System.out.println("\n\t\t\t[ 1 ] Add staff");
                    System.out.println("\t\t\t[ 0 ] Go back");
                    System.out.print("\t\t\tChoice: ");
                    String input = sc.nextLine();
                    char actionChoice = input.charAt(0);

                    switch (actionChoice) {
                        case '1':
                            clearScreen();
                            addStaff();
                            actionValid = true;
                            break;
                        case '0':
                            System.out.println("Going back...");
                            actionValid = true;
                            adminMenu();
                            break;
                        default:
                            clearScreen();
                            System.out.println(
                                    "\t\t\t=================== Invalid choice. Please select again. ====================");
                            break;
                    }
                } while (!actionValid);
                return; // Exit the method if no staff found and action taken
            }

            System.out.printf("\n\t\t\t%-5s %-20s %-10s%n", "ID", "Username", "Role");
            System.out.println("\t\t\t------------------------------------------");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String role = resultSet.getString("role");
                System.out.printf("\t\t\t%-5d %-20s %-10s%n", id, username, role);
            }

            boolean actionValid = false;
            do {
                System.out.println("\n\t\t\tWhat do you want to do?");
                System.out.println("\n\t\t\t[ 1 ] Manage Staff");
                System.out.println("\t\t\t[ 2 ] Add Staff");
                System.out.println("\t\t\t[ 0 ] Go back");
                System.out.print("\t\t\tChoice: ");
                String input = sc.nextLine();
                char actionChoice = input.charAt(0);

                switch (actionChoice) {
                    case '1':
                        // Ask for staff ID to manage
                        int selectedId = -1;
                        boolean isValid = false;

                        do {
                            System.out.println("\n\t\t\tType 0 to go back");
                            System.out.print("\t\t\tEnter staff ID to manage: ");
                            String choice = sc.nextLine();

                            if (choice.equals("0")) {
                                System.out.println("Going back...");
                                clearScreen();
                                System.out.println(
                                        "===========================ACTION CANCELLED===========================");
                                adminMenu();
                                return;
                            }

                            try {
                                selectedId = Integer.parseInt(choice);

                                String checkIdQuery = "SELECT COUNT(*) FROM user_info WHERE id = ? AND id != 1";
                                PreparedStatement checkIdStatement = conn.prepareStatement(checkIdQuery);
                                checkIdStatement.setInt(1, selectedId);
                                ResultSet checkIdResult = checkIdStatement.executeQuery();

                                if (checkIdResult.next() && checkIdResult.getInt(1) > 0) {
                                    user.setSelectedId(selectedId);
                                    isValid = true;
                                } else {
                                    System.out.println("\t\t\tInvalid staff ID. Please try again.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("\t\t\tInvalid input. Please enter a numeric staff ID.");
                            }

                        } while (!isValid);

                        // Prompt for actions on the selected staff member
                        boolean manageActionValid = false;

                        do {
                            System.out.println("\n\t\t\t[ 1 ] Mark staff as fired");
                            System.out.println("\t\t\t[ 0 ] Go back");
                            System.out.print("\t\t\tChoice: ");
                            int manageActionChoice = sc.nextInt();
                            sc.nextLine(); // Clear the buffer

                            switch (manageActionChoice) {
                                case 1:
                                    // Update the selected staff member's is_fired column to 1
                                    if (updateStaffAsFired(user.getSelectedId())) {
                                        clearScreen();
                                        System.out.println(
                                                "===========================Staff marked as fired successfully.===========================");
                                        adminMenu();
                                    } else {
                                        clearScreen();
                                        System.out.println(
                                                "===========================Failed to mark staff as fired.===========================");
                                        adminMenu();
                                    }
                                    manageActionValid = true;
                                    break;
                                case 0:
                                    System.out.println("Going back...");
                                    manageActionValid = true;
                                    clearScreen();
                                    manageStaff();
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please select again.");
                                    break;
                            }
                        } while (!manageActionValid);
                        actionValid = true; // Exit the main action loop after handling staff
                        break;

                    case '2':
                        clearScreen();
                        addStaff();
                        actionValid = true;
                        break;

                    case '0':
                        System.out.println("Going back...");
                        actionValid = true;
                        adminMenu(); // Assuming this method leads back to the admin menu
                        break;

                    default:
                        System.out.println("Invalid choice. Please select again.");
                        break;
                }
            } while (!actionValid);

        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private boolean updateStaffAsFired(int staffId) {
        boolean isUpdated = false;
        try {
            String updateQuery = "UPDATE user_info SET is_fired = 1 WHERE id = ?";
            PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
            updateStatement.setInt(1, staffId);
            int rowsAffected = updateStatement.executeUpdate();
            isUpdated = rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("An error occurred while updating staff: " + e.getMessage());
        }
        return isUpdated;
    }

    private void addStaff() {
        try {

            textToAscii.printDisplay("\t\t\t", "ADD STAFF");
            // Prompt for username and password
            System.out.print("\n\t\tEnter Username: ");
            String username = sc.nextLine();

            System.out.print("\t\tEnter Password: ");
            String password = sc.nextLine();

            // Insert the new staff member into the database
            String insertQuery = "INSERT INTO user_info (username, password, role) VALUES (?, ?, 'staff')";
            PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
            insertStatement.setString(1, username);
            insertStatement.setString(2, password);

            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                clearScreen();
                System.out.println("\n\t\t=================== STAFF ADDED SUCCESSFULLY ===================");
                manageStaff();
            } else {
                System.out.println("\n\t\t=================== FAILED TO ADD STAFF ===================");
            }

        } catch (SQLException e) {
            System.out.println("=============================== AN ERROR OCCURRED ===============================");
            System.out.println("Error Message: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("=============================== AN ERROR OCCURRED ===============================");
        }
    }

    @Override
    public void staffMenu() {

    }
}

// SELECT COUNT(fd.id) AS missing_count FROM fixed_dairy fd LEFT JOIN products p
// ON fd.item = p.product_name WHERE p.product_name IS NULL;
// tong query na to ichcheck kung meron ba sa mga fixed_(products) ang wala sa
// products table