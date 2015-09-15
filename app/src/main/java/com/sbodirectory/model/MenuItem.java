package com.sbodirectory.model;

/**
 * Created by AnhNDT on 4/26/15.
 */
public class MenuItem {
    private int menuId;
    private String menuValue;
    public MenuItem() {}
    public MenuItem(int menuId, String menuValue) {
        this.menuId = menuId;
        this.menuValue = menuValue;
    }
    public int getMenuId() {
        return menuId;
    }
    public String getMenuValue() {
        return menuValue;
    }
}
