package com.sbodirectory.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhNDT on 4/26/15.
 */
public class MenuGroup {
    public int groupId;
    public String groupName;
    public int iconResId;
    public List<? extends MenuItem> items;
    public MenuGroup() {}
    public MenuGroup(int groupId, String groupName, int iconResId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.iconResId = iconResId;
    }
    public MenuGroup(int groupId, String groupName, int iconResId, List<? extends MenuItem> items) {
        this(groupId, groupName, iconResId);
        this.items = items;
    }
    public MenuGroup(int groupId, String groupName, int iconResId, String[] arrayMenu) {
        this(groupId, groupName, iconResId);
        if (arrayMenu != null && arrayMenu.length > 0) {
            List<MenuItem> menuItems = new ArrayList<>(arrayMenu.length);
            for (int i=0; i<arrayMenu.length; i++) {
                MenuItem item = new MenuItem(i, arrayMenu[i]);
                menuItems.add(item);
            }
            this.items = menuItems;
        }
    }
    public MenuItem get(int position) {
        return (items != null && position >= 0 && position < items.size()) ? items.get(position) : null;
    }
    public int size() {
        return (items != null) ? items.size() : 0;
    }
    public boolean hasChild() {
        return size() > 0;
    }
//    public int getGroupId() {
//        return groupId;
//    }
//    public String getGroupName() {
//        return groupName;
//    }
}
