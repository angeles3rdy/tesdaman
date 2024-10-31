package layout.menu;
//changes
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Scanner;

import config.DBConfig;
import layout.enums.DateEnum;
import layout.interfaces.IInventory;
import layout.product_structure.Products;
import layout.textgenerator.AsciiGenerator;

public class InventoryMenu implements IInventory {

    DBConfig db;
    Connection conn;
    Scanner sc;
    AsciiGenerator textToAscii;
    Products prod;
    DateEnum dates;

    public InventoryMenu() {
        sc = new Scanner(System.in);
        db = new DBConfig();
        conn = db.getConnection();
        textToAscii = new AsciiGenerator();
        prod = new Products();
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

        clearScreen();
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
                    clearScreen();
                    adminLogin();
                    break;
                case 'S':
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

            productsQuery = conn.prepareStatement("SELECT * FROM products LIMIT ? OFFSET ?");
            productsQuery.setInt(1, limit);
            productsQuery.setInt(2, offset);
            productsSnapshot = productsQuery.executeQuery();

            textToAscii.printDisplay("\t\t\t\t", "PRODUCTS");

            if (!productsSnapshot.isBeforeFirst()) {
                System.out.println("\t\t\tNo products found.");
                // adminMenu();
            } else {
                System.out.printf("%5s %20s %30s %20s %20s %20s %20s %14s%n", "ID", "CATEGORY", "PRODUCT NAME",
                        "PRODUCT BRAND", "EXPIRY", "QUANTITY", "MONTHS LEFT", "STATUS");
                System.out.println(
                        "-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                while (productsSnapshot.next()) {
                    System.out.printf("%5s %20s %24s %22s %26s %15s %18s %18s%n",
                            productsSnapshot.getInt("productId"),
                            productsSnapshot.getString("productCategory"),
                            productsSnapshot.getString("productName"),
                            productsSnapshot.getString("productBrand"),
                            productsSnapshot.getString("productExpiry"),
                            productsSnapshot.getString("productQuantity"),
                            productsSnapshot.getInt("monthsLeft"),
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

                } while (!isValid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addProduct() {
        try {

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void staffLogin() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'staffLogin'");
    }

    @Override
    public void adminLogin() {
        PreparedStatement checkAccountQuery = null;
        ResultSet checkAccountSnapshot = null;
        String usernameInp, passwordInp;
        boolean isValid = false;

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

        try {
            do {
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

                if (checkAccountSnapshot.next()) {
                    isValid = true;
                    clearScreen();
                } else {
                    isValid = false;
                    clearScreen();
                    System.out.println("Invalid credentials!");
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

        try {
            PreparedStatement insertCategQuery;
            PreparedStatement createFixedTableQuery;
            textToAscii.printDisplay("\t\t", "ADD CATEGORIES");
            System.out.println("\t\t\tType 0 to go back");
            System.out.print("\t\t\tEnter new category: ");
            category = sc.nextLine().toUpperCase();
            firstCharacter = category.charAt(0);

            System.out.println("\t\t\tDoes items in this category expires? [Y/N]: ");
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
            // adminMenu();
        }
    }

    @Override
    public void addBrand() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addBrand'");
    }

    @Override
    public void viewItemByCategory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'viewItemByCategory'");
    }

    @Override
    public void viewCriticalStocks() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'viewCriticalStocks'");
    }

    @Override
    public void viewNearExpiry() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'viewNearExpiry'");
    }

    @Override
    public void displayCategories() {

        int choice; // when no categories
        String chosenCategory; // when there are categories
        char firstCharacter;
        boolean isValid = false;
        int count = 0;

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
                            "\t\t\t\t\t[ " + (count + 1) + " ] " + categoriesSnapshot.getString("category"));

                    count++;
                }
                System.out.println(
                        "\t\t\t\t\t[ 0 ] Go Back");

                System.out.print("\t\t\t\tChoice: ");
                chosenCategory = sc.nextLine();
                firstCharacter = chosenCategory.charAt(0);

                if (chosenCategory.matches("\\d+")) {
                    isValid = true;

                    checkIfCategoryPresent = conn.prepareStatement("SELECT * FROM categories WHERE id = ?");
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
                System.out.println("\t\t\t[2] Add Products");
                System.out.println("\t\t\t[3] Manage Staffs");
                System.out.println("\t\t\t[4] My Account");
                System.out.println("\t\t\t[5] Logout");

                System.out.print("\t\t\tChoice: ");
                choice = sc.nextLine().charAt(0);

                switch (choice) {
                    case '1':
                        isValid = true;
                        clearScreen();
                        break;
                    case '2':
                        isValid = true;
                        clearScreen();
                        displayCategories();
                        break;
                    case '3':
                        isValid = true;
                        clearScreen();

                        break;
                    case '4':
                        isValid = true;
                        clearScreen();

                        break;
                    case '5':
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
            System.out.println(e.getMessage());
            System.out.println(
                    "\t================================AN ERROR OCCURED================================\n");
            displayBrands();
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'displayConfirmation'");
    }

}
