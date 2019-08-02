package com3001.jb01026.finalyearproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com3001.jb01026.finalyearproject.R;

public class FilterListAdapter extends BaseExpandableListAdapter {

    private HashMap<String, List<String>> mStringListHashMap;
    private String[] mListHeaderGroup;

    private int count = 0;
    public List<boolean[]> checkedArray;

    public FilterListAdapter(HashMap<String, List<String>> stringListHashMap) {
        mStringListHashMap = stringListHashMap;
        mListHeaderGroup = mStringListHashMap.keySet().toArray(new String[0]);

        checkedArray = new ArrayList<>();
        for(int i = 0; i < getGroupCount(); i++) {
            boolean[] temp = new boolean[getChildrenCount(i)];
            for(int j = 0; j < getChildrenCount(i); j++) {
                temp[j] = true;
            }
            checkedArray.add(temp);
        }

    }


    @Override
    public int getGroupCount() {
        return mListHeaderGroup.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mStringListHashMap.get(mListHeaderGroup[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListHeaderGroup[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mStringListHashMap.get(mListHeaderGroup[groupPosition]).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition*childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_list_group, parent, false);

        TextView textView = convertView.findViewById(R.id.filter_group_text);
        textView.setText(String.valueOf(getGroup(groupPosition)));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_list_item, parent, false);

        TextView textView = convertView.findViewById(R.id.filter_item_text);
        textView.setText(String.valueOf(getChild(groupPosition, childPosition)));

        CheckBox checkBox = convertView.findViewById(R.id.filter_item_checkbox);
        checkBox.setChecked(checkedArray.get(groupPosition)[childPosition]);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void toggleChecked(int groupPosition, int childPosition) {
        checkedArray.get(groupPosition)[childPosition] = !checkedArray.get(groupPosition)[childPosition];
        this.notifyDataSetChanged();
    }

}
