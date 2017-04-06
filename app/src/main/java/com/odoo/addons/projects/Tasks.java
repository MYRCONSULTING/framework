package com.odoo.addons.projects;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.odoo.R;
import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.addons.projects.models.ProjectTaskType;
import com.odoo.addons.projects.models.TypeTask;
import com.odoo.addons.survey.SurveySurvey;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.account.OdooAccountQuickManage;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.notification.ONotificationBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class Tasks extends BaseFragment implements ISyncStatusObserverListener,
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener, View.OnClickListener,
        AdapterView.OnItemClickListener , OCursorListAdapter.OnRowViewClickListener {

    public static final String KEY = Tasks.class.getSimpleName();
    private View mView;
    private String mCurFilter = null;
    private ListView listView;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;
    private Bundle extra = null;
    public static final String EXTRA_KEY_SURVEY_TASK = "extra_key_survey_task";
    private Context mContext = null;
    private String action;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mContext = getActivity();
        setHasSyncStatusObserver(KEY, this, db());
        action = getActivity().getIntent().getAction();
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        extra = getArguments();
        listView = (ListView) mView.findViewById(R.id.listview);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.task_row_item);
        mAdapter.setOnRowViewClickListener(R.id.btnDetailTask,this);
        mAdapter.setOnRowViewClickListener(R.id.btnFormulario,this);
        mAdapter.setOnViewBindListener(this);
        mAdapter.setHasSectionIndexers(true, "name");
        listView.setAdapter(mAdapter);
        listView.setFastScrollAlwaysVisible(true);
        listView.setOnItemClickListener(this);
        setHasFloatingButton(view, R.id.fabButton, listView, this);
        getLoaderManager().initLoader(extra.getInt("_id"), extra, this);
        if (extra.getString("extra_key_project") == null || (extra.getString("extra_key_project").isEmpty()))
            setTitle(OResource.string(getContext(), R.string.sync_label_schedule));
        else
            setTitle(OResource.string(getContext(), R.string.sync_label_schedule)+"/"+OResource.string(getContext(), R.string.sync_label_tasks));
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        if (row.getInt("x_survey_id")==null || row.getInt("x_survey_id")==0){
            OControls.setInvisible(view,R.id.btnFormulario);
        }

        OControls.setText(view, R.id.nameTask, row.getString("name"));
        if (row.getString("name").trim().isEmpty() || (row.getString("name").trim().equals("false"))){
            OControls.setGone(view, R.id.nameTask);
        }else{
            OControls.setText(view, R.id.nameTask, row.getString("name"));
        }

        if (row.getString("date_start").equals("false")){
            OControls.setGone(view,R.id.nameDateStart);
        }else{
            String date_start = ODateUtils.convertToDefault(row.getString("date_start"),
                    ODateUtils.DEFAULT_FORMAT, "MMM dd hh:mm a");
            OControls.setText(view, R.id.nameDateStart, date_start);
        }

        ResPartner resPartner = new ResPartner(getContext(),null);
        String nameCustomer = "";
        String image_small = null;
        String full_address = "";
        Bitmap img=null;
        if (row.getString("partner_id")!=null && !row.getString("partner_id").equals("false")){
            nameCustomer = resPartner.browse(Integer.valueOf(row.getString("partner_id"))).getString("name");
            image_small = resPartner.browse(Integer.valueOf(row.getString("partner_id"))).getString("image_small");
            ODataRow recordParthner = resPartner.browse(Integer.valueOf(row.getString("partner_id")));
            full_address = resPartner.getAddress(recordParthner);

            if (nameCustomer.equals("false"))
                nameCustomer = "";
            if (full_address.equals("false"))
                full_address = "";

            if (nameCustomer.isEmpty())
            {
                OControls.setGone(view,R.id.nameCustomer);
            }else{
                OControls.setText(view, R.id.nameCustomer, nameCustomer);
            }

            if (full_address.trim().isEmpty()){
                OControls.setGone(view,R.id.addressCustomer);
            }else{
                OControls.setText(view, R.id.addressCustomer, full_address);
            }

            if (image_small.equals("false")) {
                img = BitmapUtils.getAlphabetImage(getActivity(), nameCustomer);
            }else{
                img = BitmapUtils.getBitmapImage(getActivity(), image_small);
            }

        }else{
            OControls.setGone(view,R.id.nameCustomer);
            OControls.setGone(view,R.id.addressCustomer);
            if (row.getString("name").trim().isEmpty() || (row.getString("name").trim().equals("false"))){
                String code = TextUtils.substring(row.getString("code"),1,row.getString("code").length());
                img = BitmapUtils.getNumberImage(getActivity(), Integer.valueOf(code).toString());
            }else{
                img = BitmapUtils.getAlphabetImage(getActivity(), row.getString("name"));
            }
        }

        if (Integer.valueOf(row.getString("x_task_type")).equals(Integer.valueOf(TypeTask.RETURNED_FROM_FIELD.getValue()))){
            OControls.setText(view, R.id.nameState,OResource.string(getContext(), R.string.sync_label_Done_Pending));
        }else{
            OControls.setGone(view, R.id.nameState);
        }

        OControls.setImage(view, R.id.image_small, img);
        OControls.setText(view, R.id.codeTask,row.getString("code") );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        List<String> args = new ArrayList<>();
        String where = "(x_task_type = ?  or x_task_type = ? )";
        args.add(String.valueOf(TypeTask.ON_FIELD.getValue()));
        args.add(String.valueOf(TypeTask.RETURNED_FROM_FIELD.getValue()));

        if (id > 0){
            where += " and project_id = ?";
            args.add(String.valueOf(id));
        }

        if (data!= null)
           // args.add(data.get("id").toString());

        if (mCurFilter != null) {
            where += " and (partner_name like ? or name like ?  )";
            args.add("%" + mCurFilter + "%");
            args.add("%" + mCurFilter + "%");
        }
        String selection = (args.size() > 0) ? where : null;
        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;
        return new CursorLoader(getActivity(), db().uri(),
                null, selection, selectionArgs, "date_start,partner_id");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        OControls.setGone(mView, R.id.fabButton);
        mAdapter.changeCursor(data);
        if (data.getCount() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, Tasks.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, Tasks.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_notes_content);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_task_found));
                    OControls.setText(mView, R.id.subTitle, _s(R.string.label_no_task_found_swipe));
                }
            }, 500);
            if (db().isEmptyTable() && !syncRequested) {
                syncRequested = true;
                onRefresh();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public Class<ProjectTask> database() {
        return ProjectTask.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(KEY).setTitle(OResource.string(context, R.string.sync_label_schedule))
                .setIcon(R.drawable.ic_action_notes_content)
                .setInstance(new Tasks()));
        return menu;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        int val = extra.getInt("_id");
        if (val > 0 ){
            try {
                getLoaderManager().restartLoader(extra.getInt("_id"), extra, this);
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(ProjectTask.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG)
                    .show();
        }
    }


    // Hasta aquí los metodos del ejemplo

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_partners, menu);
        setHasSearchView(this, menu, R.id.menu_partner_search);
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
        getLoaderManager().restartLoader(extra.getInt("_id"), extra, this);
        return true;
    }

    @Override
    public void onSearchViewClose() {
        // nothing to do
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
            data.putString(EXTRA_KEY_SURVEY_TASK,row.getString("x_survey_id"));
        }
        IntentUtils.startActivity(getActivity(), TasksDetails.class, data);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        loadActivity(row);
    }

    @Override
    public void onRowViewClick(int position, Cursor cursor, View view,
                               final View parent) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        if (inNetwork()) {
            switch (view.getId()) {
                case R.id.btnFormulario:
                    Bundle data = new Bundle();
                    if (row != null) {
                        data = row.getPrimaryBundleData();
                        data.putString(EXTRA_KEY_SURVEY_TASK,row.getString("x_survey_id"));
                    }
                    startFragment(new SurveySurvey(), true, data);
                    break;
                case R.id.btnDetailTask:
                    loadActivity(row);
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(getActivity(), _s(R.string.sync_label_tasks),
                    Toast.LENGTH_LONG).show();
        }
    }


}