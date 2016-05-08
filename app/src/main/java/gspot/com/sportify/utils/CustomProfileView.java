package gspot.com.sportify.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import gspot.com.sportify.R;

public class CustomProfileView extends LinearLayout {
    private View mHeaderView;
    private ExpandableListView mListView;

    @SuppressWarnings("UnusedDeclaration")
    public CustomProfileView(Context context) {
        super(context);
        init(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CustomProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate custom view into LinearLayout
        inflater.inflate(
                R.layout.profile_expandable_list, this);

        //Inflate header with no parent so we can set it as the ListView header in onFinishInflate()
        mHeaderView = inflater.inflate(R.layout.activity_view_profile, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mListView = (ExpandableListView) (findViewById(R.id.sports_list));

        Log.d("MyView", "Almost done...");

        mListView.addHeaderView(mHeaderView, null, false);
    }
}