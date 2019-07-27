package com.odoo.addons.servicesorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.servicesorder.models.ServicesOrder;
import com.odoo.addons.servicesorder.models.ServicesOrderEventType;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.ORecordValues;
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

public class ServicesOrderF extends BaseFragment implements ISyncStatusObserverListener,
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener, View.OnClickListener,
        AdapterView.OnItemClickListener, OCursorListAdapter.OnRowViewClickListener {

    public static final String KEY = ServicesOrderF.class.getSimpleName();
    private View mView;
    private String mCurFilter = null;
    private ListView listView;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;
    public static final String EXTRA_KEY_PROJECT = "extra_key_project";
    public static final String TAG = OModel.class.getSimpleName();
    private ServicesOrder servicesOrder;
    private com.odoo.addons.servicesorder.models.ServicesOrderEvent servicesOrderEvent;
    private LinearLayout horizontalScrollView;

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
        listView = (ListView) mView.findViewById(R.id.listview);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.services_order_row_item);
        mAdapter.setOnRowViewClickListener(R.id.btnFormulario, this);
        mAdapter.setOnRowViewClickListener(R.id.btnDetailTask, this);
        mAdapter.setOnRowViewClickListener(R.id.btnEndTask, this);
        mAdapter.setOnViewBindListener(this);
        mAdapter.setHasSectionIndexers(true, "name");
        listView.setAdapter(mAdapter);
        listView.setFastScrollAlwaysVisible(true);
        listView.setOnItemClickListener(this);
        setHasSyncStatusObserver(KEY, this, db());
        setHasFloatingButton(view, R.id.fabButton, listView, this);
        getLoaderManager().initLoader(0, null, this);
        setTitle(OResource.string(getContext(),R.string.sync_label_os));
        hideFab();
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        ServicesOrderEventType servicesOrderEventType = new ServicesOrderEventType(getContext(), null);
        ResPartner resPartner = new ResPartner(getContext(), null);
        String nameServicesOrderEventType = "";
        String nameCustomer = "";
        OControls.setText(view, R.id.text0, row.getString("name"));
        OControls.setText(view, android.R.id.text1, row.getString("order_ref"));

        if (row.getString("last_state") != null && !row.getString("last_state").equals("false")) {
            try {
                nameServicesOrderEventType = servicesOrderEventType.browse(Integer.valueOf(row.getString("last_state"))).getString("name");
                if (nameServicesOrderEventType.equals("false"))
                    nameServicesOrderEventType = "";

                if (nameServicesOrderEventType.isEmpty()) {
                    OControls.setGone(view, R.id.txtTypeEvent);
                } else {
                    OControls.setText(view, R.id.txtTypeEvent, nameServicesOrderEventType);
                }

            } catch (Exception e) {

            }

        }

        OControls.setText(view, R.id.txtTypeEvent, nameServicesOrderEventType);
        if (row.getString("partner_delivery") != null && !row.getString("partner_delivery").equals("false") && !row.getString("partner_delivery").isEmpty()) {
            OControls.setText(view, R.id.direccionrecojo, row.getString("partner_delivery"));
        } else {
            OControls.setGone(view, R.id.direccionrecojo);
        }

        if (row.getString("partner_id") != null && !row.getString("partner_id").equals("false") && !row.getString("partner_id").isEmpty()) {
            nameCustomer = resPartner.browse(Integer.valueOf(row.getString("partner_id"))).getString("name");
            if (nameCustomer == "false") {
                OControls.setGone(view, R.id.nameCustomer);
            } else {
                OControls.setText(view, R.id.nameCustomer, nameCustomer);
            }

        } else {
            OControls.setGone(view, R.id.nameCustomer);
        }



        OControls.setText(view, R.id.client_contact_name, row.getString("client_contact_name"));
        OControls.setText(view, R.id.client_contact_phone, row.getString("client_contact_phone"));
        OControls.setText(view, R.id.address_delivery, row.getString("address_delivery"));

        Bitmap img;
        if (row.getString("image_small").equals("false")) {
            String strName = row.getString("name");
            strName = strName.substring(strName.length() - 1);
            img = BitmapUtils.getAlphabetImage(getActivity(), strName);
        } else {
            img = BitmapUtils.getBitmapImage(getActivity(), row.getString("image_small"));
        }
        OControls.setImage(view, R.id.image_small, img);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "name != ?";
        List<String> args = new ArrayList<>();
        args.add("");
        if (id > 0)
            args.add(String.valueOf(id));

        if (mCurFilter != null) {
            where += " and name like ? ";
            args.add("%" + mCurFilter + "%");
        }
        String selection = (args.size() > 0) ? where : where;
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
                    setHasSwipeRefreshView(mView, R.id.swipe_container, ServicesOrderF.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, ServicesOrderF.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_universe);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_os_found));
                    OControls.setText(mView, R.id.subTitle, _s(R.string.label_no_so_found_swipe));

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
    public Class<ServicesOrder> database() {
        return ServicesOrder.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> menu = new ArrayList<>();
        menu.add(new ODrawerItem(KEY).setTitle(OResource.string(context, R.string.sync_label_OSMenu))
                .setIcon(R.drawable.ic_action_universe)
                .setInstance(new ServicesOrderF()));
        return menu;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(ServicesOrder.AUTHORITY);
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
            data.putString(EXTRA_KEY_PROJECT, row.getString("name"));
        }


        IntentUtils.startActivity(getActivity(), ServicesOrderDetails.class, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        //OUser.current(getContext());
        loadActivity(row);
    }

    @Override
    public void onRowViewClick(int position, Cursor cursor, View view,
                               final View parent) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        Bundle data = new Bundle();
        switch (view.getId()) {
            case R.id.btnFormulario:
                if (row != null) {
                    data = row.getPrimaryBundleData();
                    data.putString(EXTRA_KEY_PROJECT, row.getString("_id"));
                }
                startFragment(new ServicesOrderEvent(), true, data);
                break;
            case R.id.btnDetailTask:
                if (row != null) {
                    loadActivity(row);
                }
                break;
            case R.id.btnEndTask:
                if (row != null) {
                    if (inNetwork()) {
                        //parent().sync().requestSync(com.odoo.addons.servicesorder.models.ServicesOrderEvent.AUTHORITY);
                        saveServiceOrderF(row);
                    } else {
                        Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG)
                                .show();
                    }


                }
                break;

            default:
                break;
        }

    }

    public void saveServiceOrderF(ODataRow row) {

        ServicesOrderF.UpdateServicesOrder updateServicesOrder = new ServicesOrderF.UpdateServicesOrder();
        //updateServicesOrder.execute(row.getInt(OColumn.ROW_ID));
        updateServicesOrder.execute(row.getInt("id"));
    }

    private class UpdateServicesOrder extends AsyncTask<Integer, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(R.string.title_working);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Actualizando ...");
            progressDialog.setMax(3);
            progressDialog.setCancelable(false);
            progressDialog.setProgress(1);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                servicesOrder = new ServicesOrder(getActivity(), null);
                servicesOrderEvent = new com.odoo.addons.servicesorder.models.ServicesOrderEvent(getActivity(), null);
                ODomain oDomain = new ODomain();
                servicesOrderEvent.quickSyncRecords(oDomain);
                ORecordValues data = new ORecordValues();
                data.put("x_phone", false);
                int record = servicesOrder.getServerDataHelper().updateOnServer(data, params[0]);
                return String.valueOf(record);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String record) {
            super.onPostExecute(record);
            progressDialog.dismiss();

            if (record != null) {

                servicesOrder = new ServicesOrder(getActivity(), null);
                servicesOrderEvent = new com.odoo.addons.servicesorder.models.ServicesOrderEvent(getActivity(), null);

                servicesOrder.delete("_id = ?", new String[]{record}, true);
                servicesOrderEvent.delete("os_id = ?", new String[]{record}, true);

            }

        }
    }

}
