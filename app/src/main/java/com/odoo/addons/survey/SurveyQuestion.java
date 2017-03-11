package com.odoo.addons.survey;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.projects.TasksDetails;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by Ricardo Livelli on 09/03/2017.
 */

public class SurveyQuestion extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener, OCursorListAdapter.OnViewBindListener {

    public static final String TAG = SurveyQuestion.class.getSimpleName();

    private View mView;
    private ListView listView;
    private OCursorListAdapter listAdapter;
    private Bundle extra = null;
    public static final String EXTRA_KEY_PROJECT = "extra_key_project";
    private String mCurFilter = null;
    private ODataRow record = null;
    private OForm mForm;
    private com.odoo.addons.survey.models.SurveyQuestion surveyQuestion;
    private Spinner spinner1, spinner2;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_question, menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        extra = getArguments();
        listView = (ListView) mView.findViewById(R.id.listview);
        surveyQuestion = new com.odoo.addons.survey.models.SurveyQuestion(getActivity(), null);
        listAdapter = new OCursorListAdapter(getActivity(), null, R.layout.question_row_item);
        listView.setAdapter(listAdapter);
        listAdapter.setOnViewBindListener(this);
        listAdapter.setHasSectionIndexers(true, "question");

        setHasSyncStatusObserver(TAG, this, db());

        getLoaderManager().initLoader(extra.getInt("_id"), extra, this);
        if (extra.getString("extra_key_project") == null || (extra.getString("extra_key_project").isEmpty()))
            setTitle(extra.getString("extra_key_project"));
        else
            setTitle("Preguntas1");


    }

    // add items into spinner dynamically
    public void addItemsOnSpinner2(View view, Cursor cursor, ODataRow row) {

        spinner2 = (Spinner) view.findViewById(R.id.simpleChoice_UserInput);
        List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_save:
                Log.i(TAG, "Id SurveyQuestion  Id   Save : " );
                break;
            case R.id.menu_syncronize:
                Log.i(TAG, "Id SurveyQuestion  Id   Syncronize : " );
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        addItemsOnSpinner2(view,cursor,row);
        OControls.setText(view, R.id.editText_UserInput, row.getString("question"));
        OControls.setVisible(view,R.id.editText_UserInput);
        Bitmap img;
        if (row.getString("image_small").equals("false")) {
            img = BitmapUtils.getAlphabetImage(getActivity(), row.getString("question"));
        } else {
            img = BitmapUtils.getBitmapImage(getActivity(), row.getString("image_small"));
        }
        OControls.setImage(view, R.id.image_small, img);

        /////////

        mForm = (OForm) view.findViewById(R.id.taskFormEdit);
        int rowId = row.getInt("_id");
        mForm.setEditable(true);
        record = surveyQuestion.browse(rowId);
        Log.i(TAG, "Id SurveyQuestion _Id   : " + row.get("_id"));
        mForm.initForm(record);
        OField textView =  (OField) mForm.findViewById(R.id.question);
        //textView.setmLabel("sssss");
        //OControls.setText(view, R.id.question, row.getString("question"));
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
        menu.add(new ODrawerItem(TAG).setTitle("Preguntas")
                .setIcon(R.drawable.ic_action_universe)
                .setInstance(new SurveyQuestion()));
        return menu;
    }

    @Override
    public Class<com.odoo.addons.survey.models.SurveyQuestion> database() {
        return com.odoo.addons.survey.models.SurveyQuestion.class;
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
            OControls.setText(mView, R.id.title, "No Question found");
            OControls.setText(mView, R.id.subTitle, "Swipe to check new question");
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
            parent().sync().requestSync(com.odoo.addons.survey.models.SurveyQuestion.AUTHORITY);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.changeCursor(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "page_id = ?";
        List<String> args = new ArrayList<>();
        if (id > 0)
            args.add(String.valueOf(id));
        if (data!= null)
            // args.add(data.get("id").toString());

            if (mCurFilter != null) {
                where += " and question like ? ";
                args.add(mCurFilter + "%");
            }
        String selection = (args.size() > 0) ? where : null;
        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;
        return new CursorLoader(getActivity(), db().uri(),
                null, selection, selectionArgs, "question");
    }
}
