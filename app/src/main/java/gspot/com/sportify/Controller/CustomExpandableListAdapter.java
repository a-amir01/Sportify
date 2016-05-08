package gspot.com.sportify.Controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import gspot.com.sportify.R;

/**
 * Created by amir on 5/1/16.
 */
public class FilterFragment extends BaseExpandableListAdapter {



    private static final String[] COUNTRIES = new String[] {"Belgium", "France", "Italy", "Germany", "Spain"};

    private Context mContext;
    private HashMap<String, List<String>> mGatheringType;
    private List<String> mGatheringList;

    @Override
    public int getGroupCount() {
        return mGatheringType.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGatheringType.get(mGatheringList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGatheringList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGatheringType.get(mGatheringList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String)getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gathering_type_selection_parent, parent, false);
        }//end if

        TextView parentTextView = (TextView) convertView.findViewById(R.id.gathering_type_parent);
        parentTextView.setText(groupTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childTitle = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gathering_type_selection, parent, false);
        }
        TextView childTextView = (TextView) convertView.findViewById(R.id.gathering_type);
        childTextView.setText(childTitle);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
