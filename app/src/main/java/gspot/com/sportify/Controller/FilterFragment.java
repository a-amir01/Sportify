package gspot.com.sportify.Controller;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import gspot.com.sportify.R;

/**
 * Created by amir on 5/1/16.
 */
public class FilterFragment extends DialogFragment implements AdapterView.OnItemClickListener{

    @Bind(R.id.multiAutoCompleteTextView) MultiAutoCompleteTextView mMultiAutoComplete;

    private static final String[] COUNTRIES = new String[] {"Belgium", "France", "Italy", "Germany", "Spain"};

    public String [] input;
    private boolean mIsPrivateEvent;
    private ArrayList<String> list;
    ArrayAdapter<String> mAdapter;

    public static FilterFragment newInstance(String title) {
        FilterFragment frag = new FilterFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @OnCheckedChanged(R.id.Event_access_specifier)
    void OnCheckedChanged(CompoundButton buttonView, boolean isChecked){

        mIsPrivateEvent = isChecked;

        if(isChecked)
            Toast.makeText(this.getContext(), "checked", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this.getContext(), "not checked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString("title");
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.filter_fragment, null);

        ButterKnife.bind(this, view);

        initializeMultiAutoComplete();


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(view);

        alertDialogBuilder.setPositiveButton("Save",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                input = mMultiAutoComplete.getText().toString().split(" ");

                for(int i=0;i<input.length;i++)
                {
                    input[i] = input[i].replace(",", " ");
                    Toast.makeText(getContext(), input[i], Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

    private void initializeMultiAutoComplete() {
        mAdapter = new ArrayAdapter<String>(
                getActivity().getBaseContext(), android.R.layout.select_dialog_item, COUNTRIES);

        mMultiAutoComplete.setAdapter(mAdapter);
        mMultiAutoComplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mMultiAutoComplete.setThreshold(1);
        mMultiAutoComplete.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
        mAdapter.remove(parent.getItemAtPosition(position).toString());
        Toast.makeText(getContext(), mMultiAutoComplete.getText().toString(), Toast.LENGTH_SHORT).show();
    }

}
