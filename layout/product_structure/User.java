package layout.product_structure;

public class User {
    String role;
    int userId;
    int selectedId;

    public String getRole() {
        return role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(int selectedId) {
        this.selectedId = selectedId;
    }

}
