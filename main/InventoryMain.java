package main;

import layout.menu.InventoryMenu;

public class InventoryMain {
    public static void main(String[] args) {
        InventoryMenu menu = new InventoryMenu();
        menu.clearScreen();
        menu.adminMenu();
    }
}
