package gspot.com.sportify.Controller;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import gspot.com.sportify.Model.MySport;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.StateWrapper;

/**
 * Created by patrickhayes on 5/6/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, MySport> _listDataChild;
    private StateWrapper _state;
    private Profile _profile;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, MySport> listChildData,
                                 StateWrapper state, Profile profile) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._state = state;
        this._profile = profile;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Log.e("ExpandableListAdapter", "in getChildView group position: " + groupPosition + " childpos: " + childPosition );
        Log.e("ExpandableListAdapter", "State " + _state );


        final MySport childSport = (MySport) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.prolife_expandable_list_item, null);
        }

        //bind
        Spinner skillLevelSpinner = (Spinner) convertView.findViewById(R.id.skill_lv_spinner);
        TextView skillLevelText = (TextView) convertView.findViewById(R.id.skill_lv_text);
        EditText sportBioContent = (EditText) convertView.findViewById(R.id.sport_bio_content);
        Button deleteSport = (Button) convertView.findViewById(R.id.delete_sport_button);
        //always set the bio
        sportBioContent.setText(childSport.getmBio());

        if (_state.getState() == StateWrapper.State.EDIT) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(_context, R.array.skill_lv_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            skillLevelSpinner.setAdapter(adapter);
            skillLevelSpinner.setVisibility(View.VISIBLE);
            skillLevelText.setVisibility(View.GONE);
            sportBioContent.setEnabled(true);
            deleteSport.setVisibility(View.VISIBLE);

            deleteSport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(_profile.getmMySports().size() > 0) {
                        _profile.getmMySports().remove(groupPosition);
                        //TODO instead of save the profile it should ideally just remove it from the view
                        //TODO until they click the save button
                        _profile.updateProfile();
                    }
                }
            });

            sportBioContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //don't do anything
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //don't do anything
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if(_profile.getmMySports().size() > 0) {
                        _profile.getmMySports().get(groupPosition).setmBio(editable.toString());
                    }
                }
            });
        }
        else {
            skillLevelSpinner.setVisibility(View.GONE);
            skillLevelText.setVisibility(View.VISIBLE);
            skillLevelText.setText(childSport.skillLevelToString());
            sportBioContent.setEnabled(false);
            deleteSport.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profile_expandable_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
