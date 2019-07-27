package com.odoo.addons.servicesorder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.odoo.addons.servicesorder.models.ServicesOrder;
import com.odoo.addons.servicesorder.models.ServicesOrderEventType;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class ServicesOrderEvent extends BaseFragment implements ISyncStatusObserverListener,
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener, View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static final String KEY = ServicesOrderEvent.class.getSimpleName();
    private View mView;
    private String mCurFilter = null;
    private ListView listView;
    private OCursorListAdapter mAdapter = null;
    private Bundle extra = null;
    private boolean syncRequested = false;
    public static final String EXTRA_KEY_PROJECT = "extra_key_project";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        extra = getArguments();
        listView = (ListView) mView.findViewById(R.id.listview);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.services_order_event_row_item);
        mAdapter.setOnViewBindListener(this);
        mAdapter.setHasSectionIndexers(true, "os_id");
        listView.setAdapter(mAdapter);
        listView.setFastScrollAlwaysVisible(true);
        listView.setOnItemClickListener(this);
        setHasSyncStatusObserver(KEY, this, db());
        setHasFloatingButton(view, R.id.fabButton, listView, this);
        if (extra.containsKey(EXTRA_KEY_PROJECT))
            getLoaderManager().initLoader(Integer.valueOf(extra.getString(EXTRA_KEY_PROJECT)), extra, this);
        else
            getLoaderManager().initLoader(0, null, this);
        setTitle(OResource.string(getContext(),R.string.label_event));
        //hideFab();
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {

        ServicesOrderEventType servicesOrderEventType = new ServicesOrderEventType(getContext(),null);
        ServicesOrder servicesOrder = new ServicesOrder(getContext(),null);
        String nameServicesOrderEventType = "";
        String nameServicesOrder = "";

        if (row.getString("os_id")!=null && !row.getString("os_id").equals("false")){
            try {
                ODataRow oDataRow =  servicesOrder.browse(Integer.valueOf(row.getString("os_id")));
                if (oDataRow != null)
                    nameServicesOrder = oDataRow.getString("name");
                if (nameServicesOrder.equals("false"))
                    nameServicesOrder = "";

                if (nameServicesOrder.isEmpty())
                {
                    OControls.setGone(view,R.id.text0);
                }else{
                    OControls.setText(view, R.id.text0, nameServicesOrder);
                }

            }catch (Exception e){

            }

        }

        if (row.getString("state")!=null && !row.getString("state").equals("false")){
            try {
                nameServicesOrderEventType = servicesOrderEventType.browse(Integer.valueOf(row.getString("state"))).getString("name");
                if (nameServicesOrderEventType.equals("false"))
                    nameServicesOrderEventType = "";

                if (nameServicesOrderEventType.isEmpty())
                {
                    OControls.setGone(view,R.id.txtTypeEvent);
                }else{
                    OControls.setText(view, R.id.txtTypeEvent, nameServicesOrderEventType);
                }

            }catch (Exception e){

            }

        }

        OControls.setText(view, android.R.id.text1, row.getString("comment"));

        Bitmap img;
        if (row.getString("image_small").equals("false")) {
            img = BitmapUtils.getAlphabetImage(getActivity(), row.getString("_id"));
        } else {
            img = BitmapUtils.getBitmapImage(getActivity(), row.getString("image_small"));
        }
        OControls.setImage(view, R.id.image_small, img);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "os_id = ?";
        List<String> args = new ArrayList<>();
        //args.add("");
        if (id > 0)
            args.add(String.valueOf(id));

        //args.add("true");
        if (mCurFilter != null) {
            where += " and comment like ? ";
            args.add("%" + mCurFilter + "%");
        }
        //String selection = (args.size() > 0) ? where : where;
        String selection = (args.size() > 0) ? where : null;
        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;

        return new CursorLoader(getActivity(), db().uri(), null, selection, selectionArgs, "id");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data.getCount() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, ServicesOrderEvent.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, ServicesOrderEvent.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_universe);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_os_event_found));
                    OControls.setText(mView, R.id.subTitle, _s(R.string.label_no_so_event_found_swipe));

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
    public Class<com.odoo.addons.servicesorder.models.ServicesOrderEvent> database() {
        return com.odoo.addons.servicesorder.models.ServicesOrderEvent.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(KEY).setTitle(OResource.string(context, R.string.label_event))
                .setIcon(R.drawable.ic_action_universe)
                .setInstance(new ServicesOrderEvent()));
        return menu;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        if (extra.containsKey(EXTRA_KEY_PROJECT))
            getLoaderManager().restartLoader(Integer.valueOf(extra.getString(EXTRA_KEY_PROJECT)), extra, this);
        else
            getLoaderManager().restartLoader(0, null, this);

        try {
            //getLoaderManager().restartLoader(0, null, this);

        } catch (Exception e) {
        }
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(com.odoo.addons.servicesorder.models.ServicesOrderEvent.AUTHORITY);
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
        if (extra.containsKey(EXTRA_KEY_PROJECT))
            getLoaderManager().restartLoader(Integer.valueOf(extra.getString(EXTRA_KEY_PROJECT)), extra, this);
        else
            getLoaderManager().restartLoader(0, null, this);
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
            data.putString(EXTRA_KEY_PROJECT, row.getString("os_id"));
            IntentUtils.startActivity(getActivity(), ServicesOrderEventDetails.class, data);
        } else {
            data.putString(EXTRA_KEY_PROJECT, extra.getString(EXTRA_KEY_PROJECT));
            Intent intent = new Intent(getActivity(), ServicesOrderEventDetails.class);
            String name = extra.getString(EXTRA_KEY_PROJECT);
            intent.putExtra("os_id", name);
            startActivity(intent);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        //OUser.current(getContext());
        loadActivity(row);
    }

}
