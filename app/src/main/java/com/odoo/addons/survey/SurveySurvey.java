package com.odoo.addons.survey;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.odoo.R;
import com.odoo.addons.projects.Tasks;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo Livelli on 09/03/2017.
 */

public class SurveySurvey extends BaseFragment implements ISyncStatusObserverListener,
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener, View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static final String TAG = SurveySurvey.class.getSimpleName();

    private View mView;
    private ListView listView;
    private OCursorListAdapter listAdapter;
    public static final String EXTRA_KEY_PROJECT = "extra_key_project";
    private String mCurFilter = null;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        listView = (ListView) mView.findViewById(R.id.listview);
        listAdapter = new OCursorListAdapter(getActivity(), null, R.layout.survey_row_item);
        listView.setAdapter(listAdapter);
        listAdapter.setOnViewBindListener(this);
        listAdapter.setHasSectionIndexers(true, "title");
        listView.setFastScrollAlwaysVisible(true);
        listView.setOnItemClickListener(this);
        setHasSyncStatusObserver(TAG, this, db());
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OControls.setText(view, android.R.id.text1, row.getString("title"));
        Bitmap img;
        if (row.getString("image_small").equals("false")) {
            img = BitmapUtils.getAlphabetImage(getActivity(), row.getString("title"));
        } else {
            img = BitmapUtils.getBitmapImage(getActivity(), row.getString("image_small"));
        }
        OControls.setImage(view, R.id.image_small, img);
    }

    @Override
    public void onStatusChange(Boolean changed) {
        if(changed){
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(TAG).setTitle("Encuestas")
                .setIcon(R.drawable.ic_action_universe)
                .setInstance(new SurveySurvey()));
        return menu;
    }

    @Override
    public Class<com.odoo.addons.survey.models.SurveySurvey> database() {
        return com.odoo.addons.survey.models.SurveySurvey.class;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), db().uri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listAdapter.changeCursor(data);
        if (data.getCount() > 0) {
            OControls.setGone(mView, R.id.loadingProgress);
            OControls.setVisible(mView, R.id.swipe_container);
            //OControls.setGone(mView, R.id.no_items_found);
            setHasSwipeRefreshView(mView, R.id.swipe_container, this);
        } else {
            OControls.setGone(mView, R.id.loadingProgress);
            OControls.setGone(mView, R.id.swipe_container);
            OControls.setVisible(mView, R.id.data_list_no_item);
            setHasSwipeRefreshView(mView, R.id.data_list_no_item, this);
            OControls.setText(mView, R.id.title, "No Surveys found");
            OControls.setText(mView, R.id.subTitle, "Swipe to check new survey");
        }
        if (db().isEmptyTable()) {
            // Request for sync
            // Request for sync
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(com.odoo.addons.survey.models.SurveySurvey.AUTHORITY);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.changeCursor(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabButton:
                loadActivity(null);
                break;
        }
    }

    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
            data.putString(EXTRA_KEY_PROJECT,row.getString("name"));
        }
        startFragment(new SurveyPage(), true, data);
    }
    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public void onSearchViewClose() {
        // nothing to do
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) listAdapter.getItem(position));
        loadActivity(row);
    }
}
