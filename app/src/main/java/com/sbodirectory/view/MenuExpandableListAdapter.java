package com.sbodirectory.view;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbodirectory.R;
import com.sbodirectory.model.Category;
import com.sbodirectory.model.MenuGroup;
import com.sbodirectory.model.MenuItem;
import com.sbodirectory.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AnhNDT on 4/10/15.
 */
public class MenuExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private List<MenuGroup> mMenuGroups;
//    private Map<String, List<String>> mItems;
//    private List<String> mGroups;
    public MenuExpandableListAdapter(Activity context, List<Category> categories) {
        this.mContext = context;

        Resources res = mContext.getResources();
        mMenuGroups = new ArrayList<>();
        mMenuGroups.add(new MenuGroup(0, res.getString(R.string.featured_companies), R.drawable.icon_menu_building));
        mMenuGroups.add(new MenuGroup(1, res.getString(R.string.miles), R.drawable.icon_menu_miles, res.getStringArray(R.array.miles)));
        mMenuGroups.add(new MenuGroup(2, res.getString(R.string.kilometers), R.drawable.icon_menu_kilometers, res.getStringArray(R.array.kilometers)));
        mMenuGroups.add(new MenuGroup(3, res.getString(R.string.categories), R.drawable.icon_menu_category, categories));
        mMenuGroups.add(new MenuGroup(4, res.getString(R.string.my_website), R.drawable.icon_menu_building));
//        mGroups = new ArrayList<>(3);
//        mGroups.add(res.getString(R.string.featured_companies));
//        mGroups.add(res.getString(R.string.miles));
//        mGroups.add(res.getString(R.string.kilometers));
//
//        mItems = new HashMap<>(3);
//        mItems.put(mGroups.get(0), new ArrayList<String>(0));
//        mItems.put(mGroups.get(1), Arrays.asList(res.getStringArray(R.array.miles)));
//        mItems.put(mGroups.get(2), Arrays.asList(res.getStringArray(R.array.kilometers)));
    }

    private boolean isValidData() {
        return mMenuGroups != null && mMenuGroups.size() > 0;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return (isValidData()) ? mMenuGroups.get(groupPosition).get(childPosition) : null;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        ChildViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.menu_child_layout, null);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder)convertView.getTag();
        }
//        final String value = (String)getChild(groupPosition, childPosition);
        MenuItem menuItem = (MenuItem)getChild(groupPosition, childPosition);
        holder.text.setText(menuItem.getMenuValue());
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return isValidData() ? mMenuGroups.get(groupPosition).size() : 0;
    }

    public Object getGroup(int groupPosition) {
        return (mMenuGroups != null) ? mMenuGroups.get(groupPosition) : null;
    }

    public int getGroupCount() {
        return (mMenuGroups != null) ? mMenuGroups.size() : 0;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_group_layout, null);
            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder)convertView.getTag();
        }
        MenuGroup group = (MenuGroup)getGroup(groupPosition);
        holder.text.setText(group.groupName);
        holder.icon.setBackgroundResource(group.iconResId);
        holder.iconArrow.setVisibility(group.hasChild() ? View.VISIBLE : View.GONE);

//        final String value = (String) getGroup(groupPosition);
//        if (groupPosition == 0) {
//            holder.icon.setBackgroundResource(R.drawable.icon_menu_building);
//            holder.iconArrow.setVisibility(View.GONE);
//        } else if (groupPosition == 1) {
//            holder.icon.setBackgroundResource(R.drawable.icon_menu_miles);
//            holder.iconArrow.setVisibility(View.VISIBLE);
//        } else if (groupPosition == 2) {
//            holder.icon.setBackgroundResource(R.drawable.icon_menu_kilometers);
//            holder.iconArrow.setVisibility(View.VISIBLE);
//        }
        if (isExpanded) {
            holder.text.setTextColor(mContext.getResources().getColor(R.color.menu_text_selected));
            holder.iconArrow.setBackgroundResource(R.drawable.icon_arrow_down);
        } else {
            holder.text.setTextColor(mContext.getResources().getColor(R.color.menu_text_normal));
            holder.iconArrow.setBackgroundResource(R.drawable.icon_arrow_right);
        }
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupViewHolder {
        public ImageView icon, iconArrow;
        public TextView text;
        public GroupViewHolder(View parent) {
            icon = (ImageView) parent.findViewById(R.id.icon);
            iconArrow = (ImageView) parent.findViewById(R.id.iconArrow);
            text = (TextView)parent.findViewById(R.id.text);
        }
    }
    private class ChildViewHolder {
        public TextView text;
        public ChildViewHolder(View parent) {
            text = (TextView)parent.findViewById(R.id.text);
        }
    }
}