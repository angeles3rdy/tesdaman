package layout.product_structure;

public class Products {
    private final static String DATE_FORMAT = "YYYY-MM-DD";
  
    private String productName, productCategory, productBrand, productExpiry, doesExpire;
    private int productQuantity, monthsLeft, categId, brandId;

    private String producDbColumn;

    public String getDoesExpire() {
        return doesExpire;
    }

    public void setDoesExpire(String doesExpire) {
        this.doesExpire = doesExpire;
    }

    public String getProducDbColumn() {
        return producDbColumn;
    }

    public void setProducDbColumn(String producDbColumn) {
        this.producDbColumn = producDbColumn;
    }

    public void displayCurrent (String productName, String productCategory, String productBrand, String productExpiry, int productQuantity, String prodcutBrand) {
        System.out.println("\t\t\t\t\tProduct Name: " + productName);
        System.out.println("\t\t\t\t\tProduct Category: " + productCategory);
        System.out.println("\t\t\t\t\tProduct Brand: " + productBrand);
        System.out.println("\t\t\t\t\tProduct Expiry: " + productExpiry);
        System.out.println("\t\t\t\t\tProduct Quantity: " + productQuantity);
        System.out.println("\t\t\t\t\tCategory ID: " + categId);
    }
    
    public int getCategId() {
        return categId;
    }

    public void setCategId(int categId) {
        this.categId = categId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public static String getDateFormat() {
        return DATE_FORMAT;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductCategory() {
        return productCategory;
    }
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    public String getProductBrand() {
        return productBrand;
    }
    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }
    
    public int getProductQuantity() {
        return productQuantity;
    }
    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
    public int getMonthsLeft() {
        return monthsLeft;
    }
    public void setMonthsLeft(int monthsLeft) {
        this.monthsLeft = monthsLeft;
    }

    public String getProductExpiry() {
        return productExpiry;
    }

    public void setProductExpiry(String productExpiry) {
        this.productExpiry = productExpiry;
    }
}
