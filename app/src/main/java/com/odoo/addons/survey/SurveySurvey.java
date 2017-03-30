package com.odoo.addons.survey;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.projects.Tasks;
import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.sys.IOnBackPressListener;

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
    public static final String EXTRA_KEY_SURVEY = "extra_key_survey";
    public static final String EXTRA_KEY_SURVEY_TASK = "extra_key_survey_task";
    public static final String EXTRA_KEY_SURVEY_NAME = "extra_key_survey_task_name";
    private String mCurFilter = null;
    private Bundle extra = null;
    private boolean syncRequested = false;
    private Context mContext = null;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        mContext = getActivity();
        setHasSyncStatusObserver(TAG, this, db());
        return inflater.inflate(R.layout.common_listview, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        extra = getArguments();
        listView = (ListView) mView.findViewById(R.id.listview);
        listAdapter = new OCursorListAdapter(getActivity(), null, R.layout.survey_row_item);
        listView.setAdapter(listAdapter);
        listAdapter.setOnViewBindListener(this);
        listAdapter.setHasSectionIndexers(true, "title");
        listView.setFastScrollAlwaysVisible(true);
        listView.setOnItemClickListener(this);
        getLoaderManager().initLoader(Integer.valueOf(extra.getString(EXTRA_KEY_SURVEY_TASK)), extra, this);
        setTitle(_s(R.string.label_activitis));

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
            try {
                getLoaderManager().restartLoader(Integer.valueOf(extra.getString(EXTRA_KEY_SURVEY_TASK)), extra, this);
            }catch (Exception e){

            }
    }



    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(TAG).setTitle(_s(R.string.label_activitis))
                .setIcon(R.drawable.ic_assignment_black_24dp)
                .setInstance(new SurveySurvey()));
        return menu;
    }

    @Override
    public Class<com.odoo.addons.survey.models.SurveySurvey> database() {
        return com.odoo.addons.survey.models.SurveySurvey.class;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "_id = ?";
        List<String> args = new ArrayList<>();
        if (id > 0)
            args.add(String.valueOf(id));
        if (data!= null)
            // args.add(data.get("id").toString());

        if (mCurFilter != null) {
            where += " and title like ? ";
            args.add("%" + mCurFilter + "%");
        }
        String selection = (args.size() > 0) ? where : null;
        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;
        return new CursorLoader(getActivity(), db().uri(),null, selection, selectionArgs, "_id");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(Integer.valueOf(extra.getString(EXTRA_KEY_SURVEY_TASK)), extra, this);
        return true;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        OControls.setGone(mView, R.id.fabButton);
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
            OControls.setImage(mView, R.id.icon, R.drawable.ic_assignment_black_24dp);
            OControls.setText(mView, R.id.title, _s(R.string.label_activitis_no));
            OControls.setText(mView, R.id.subTitle, _s(R.string.label_activitis_no_swipe));
        }
        if (db().isEmptyTable() && !syncRequested) {
            syncRequested = true;
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(com.odoo.addons.survey.models.SurveySurvey.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG)
                    .show();
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
            data.putInt("id_task",extra.getInt("_id"));
            data.putString(EXTRA_KEY_PROJECT,row.getString("name"));
            data.putString(EXTRA_KEY_SURVEY,row.getString("_id"));
            data.putString(EXTRA_KEY_SURVEY_NAME,row.getString("title"));
        }
        startFragment(new SurveyPage(), true, data);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_partners, menu);
        //setHasSearchView(this, menu, R.id.menu_partner_search);
    }
}
