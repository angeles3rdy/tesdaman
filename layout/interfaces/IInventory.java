package layout.interfaces;

public interface IInventory { //ALL THE FUNCTIONS NEEDED TO RUN THE INVENTORY SYSTEM
    
    public abstract void login();

    public abstract void createAccount();

    public abstract void staffLogin();

    public abstract void adminLogin();

    //-- admin actions -- ADDING

    public abstract void addCategories();

    public abstract void addBrand();

    public abstract void addProduct();

    //-- admin actions -- DISPLAYING

    public abstract void adminMenu();

    public abstract void viewInventory(int page);

    public abstract void viewItemByCategory();

    public abstract void viewCriticalStocks();

    public abstract void viewNearExpiry();

    public abstract void displayCategories();

    public abstract void displayBrands();

    public abstract void displayProductNames();

    public abstract void displayProductExpiryPrompt();

    public abstract void displayQuantityPrompt();

    public abstract void displayConfirmation();

}
