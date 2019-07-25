package com.odoo.addons.servicesorder;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.servicesorder.models.ServicesOrderEvent;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by Ricardo Livelli on 21/11/2017.
 */

public class ServicesOrderEventDetails extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener {
    public static final String TAG = ServicesOrderEventDetails.class.getSimpleName();
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Bundle extras;
    private ServicesOrderEvent orderservicesevent;
    private ODataRow record = null;
    private ImageView servicesorderImage = null;
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
    private String newImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private String xos_id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_order_event_detail);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.servicesorder_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        servicesorderImage = (ImageView) findViewById(R.id.servicesorder_event_image);
        findViewById(R.id.captureImage).setOnClickListener(this);

        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
            newImage = savedInstanceState.getString(KEY_NEW_IMAGE);
        }
        app = (App) getApplicationContext();
        orderservicesevent = new ServicesOrderEvent(this, null);
        Intent intent = getIntent();
        xos_id = intent.getStringExtra("os_id");
        extras = getIntent().getExtras();
        if (hasRecordInExtra())
            mEditMode = false;
        else
            mEditMode = true;



        setupToolbar();
    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
        findViewById(R.id.captureImage).setVisibility(edit ? View.VISIBLE : View.GONE);
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_services_order_event_detail_more).setVisible(!edit);
            //mMenu.findItem(R.id.menu_services_order_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_services_order_event_edit).setVisible(false);
            mMenu.findItem(R.id.menu_services_order_event_save).setVisible(edit);
            mMenu.findItem(R.id.menu_services_order_event_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle(OResource.string(this,R.string.label_create_new));
            }
            mForm = (OForm) findViewById(R.id.servicesorderEventFormEdit);
            findViewById(R.id.servicesorderevent_view_layout).setVisibility(View.GONE);
            findViewById(R.id.servicesorderEvent_edit_layout).setVisibility(View.VISIBLE);
        } else {
            mForm = (OForm) findViewById(R.id.servicesorderEventForm);
            findViewById(R.id.servicesorderEvent_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.servicesorderevent_view_layout).setVisibility(View.VISIBLE);
        }
        setColor(color);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            servicesorderImage.setColorFilter(Color.parseColor("#ffffff"));
            servicesorderImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(OColumn.ROW_ID);
            record = orderservicesevent.browse(rowId);
            record.put("os_id", orderservicesevent.getOrder_Ref(record));
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString("os_id"));
            setOrderServiceImage();
            if (record.getInt("id") != 0 && record.getString("large_image").equals("false")) {
                ServicesOrderEventDetails.BigImageLoader bigImageLoader = new ServicesOrderEventDetails.BigImageLoader();
                bigImageLoader.execute(record.getInt("id"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*
            case R.id.order_event_ref:
                IntentUtils.requestMessage(this, record.getString("os_id"));
                break;

            case R.id.telefono_fijo:
                IntentUtils.requestCall(this, record.getString("telefono_fijo"));
                break;
            case R.id.telefono_celular:
                IntentUtils.requestCall(this, record.getString("telefono_celular"));
                break;
             */

        }
    }

    private void checkControls() {
        //findViewById(R.id.order_event_ref).setOnClickListener(this);
        //findViewById(R.id.telefono_fijo).setOnClickListener(this);
        //findViewById(R.id.telefono_celular).setOnClickListener(this);
    }

    private void setOrderServiceImage() {

        if (record != null && !record.getString("image_small").equals("false")) {
            servicesorderImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String base64 = newImage;
            if (newImage == null) {
                if (!record.getString("large_image").equals("false")) {
                    base64 = record.getString("large_image");
                } else {
                    base64 = record.getString("image_small");
                }
            }
            servicesorderImage.setImageBitmap(BitmapUtils.getBitmapImage(this, base64));
        } else {
            servicesorderImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            servicesorderImage.setColorFilter(Color.WHITE);
            int color = OStringColorUtil.getStringColor(this, record.getString("os_id"));
            servicesorderImage.setBackgroundColor(color);
        }
    }

    private void setColor(int color) {
        mForm.setIconTintColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_services_order_event_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (newImage != null) {
                        values.put("image_small", newImage);
                        values.put("large_image", newImage);
                    }
                    if (record != null) {
                        orderservicesevent.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {

                        if (Integer.parseInt(xos_id) > 0) {
                            values.put("os_id", xos_id);
                            //values.put("os_id",record.getString("os_id"));
                            values.put("decoration", "success");
                            //values.put("date_create_user","2019-07-19 16:00:17");
                            final int row_id = orderservicesevent.insert(values);
                            if (row_id != OModel.INVALID_ROW_ID) {
                                finish();
                            }
                        }

                    }
                }
                break;
            case R.id.menu_services_order_event_cancel:
            case R.id.menu_services_order_event_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                    setOrderServiceImage();
                } else {
                    finish();
                }
                break;
            case R.id.menu_services_order_evento_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_services_order_evento_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_services_order_evento_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (orderservicesevent.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(ServicesOrderEventDetails.this, R.string.toast_record_deleted,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_services_order_event_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (field.getFieldName().equals("is_company")) {
            Boolean checked = Boolean.parseBoolean(value.toString());
            int view = (checked) ? View.GONE : View.VISIBLE;
            findViewById(R.id.parent_id).setVisibility(view);
        }
    }

    private class BigImageLoader extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            String image = null;
            try {
                Thread.sleep(300);
                OdooFields fields = new OdooFields();
                fields.addAll(new String[]{"image_medium"});
                OdooResult record = orderservicesevent.getServerDataHelper().read(null, params[0]);
                if (record != null && !record.getString("image_medium").equals("false")) {
                    image = record.getString("image_medium");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (!result.equals("false")) {
                    OValues values = new OValues();
                    values.put("large_image", result);
                    orderservicesevent.update(record.getInt(OColumn.ROW_ID), values);
                    record.put("large_image", result);
                    setOrderServiceImage();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_MODE, mEditMode);
        outState.putString(KEY_NEW_IMAGE, newImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            newImage = values.getString("datas");
            servicesorderImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            servicesorderImage.setColorFilter(null);
            servicesorderImage.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }


}
