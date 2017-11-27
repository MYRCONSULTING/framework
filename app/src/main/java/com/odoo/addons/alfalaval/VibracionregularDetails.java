package com.odoo.addons.alfalaval;

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
import com.odoo.addons.alfalaval.models.Vibracionregular;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.enel.models.Encuesta;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by Ricardo Livelli on 21/11/2017.
 */

public class VibracionregularDetails extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener {
    public static final String TAG = VibracionregularDetails.class.getSimpleName();
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Bundle extras;
    private Vibracionregular vibracionregular;
    private ODataRow record = null;
    private ImageView vibracionregularImage = null;
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
    private String newImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alfalaval_vibracionregular_detail);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.vibracionregular_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        vibracionregularImage = (ImageView) findViewById(R.id.vibracionregular_image);
        findViewById(R.id.captureImage).setOnClickListener(this);

        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
            newImage = savedInstanceState.getString(KEY_NEW_IMAGE);
        }
        app = (App) getApplicationContext();
        vibracionregular = new Vibracionregular(this,null);
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
            mMenu.findItem(R.id.menu_alfalaval_vibracionregular_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_alfalaval_vibracionregular_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_alfalaval_vibracionregular_save).setVisible(edit);
            mMenu.findItem(R.id.menu_alfalaval_vibracionregular_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("cliente"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle(OResource.string(this,R.string.label_create_new));
            }
            mForm = (OForm) findViewById(R.id.alfalaval_vibracionregularFormEdit);
            findViewById(R.id.alfalaval_vibracionregular_view_layout).setVisibility(View.GONE);
            findViewById(R.id.alfalaval_vibracionregular_edit_layout).setVisibility(View.VISIBLE);
        } else {
            mForm = (OForm) findViewById(R.id.alfalaval_vibracionregularForm);
            findViewById(R.id.alfalaval_vibracionregular_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.alfalaval_vibracionregular_view_layout).setVisibility(View.VISIBLE);
        }
        setColor(color);
    }


    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            vibracionregularImage.setColorFilter(Color.parseColor("#ffffff"));
            vibracionregularImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(OColumn.ROW_ID);
            record = vibracionregular.browse(rowId);
            record.put("codigocliente", vibracionregular.getCodigocliente(record));
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString("cliente"));
            setVibracionregularImage();
            if (record.getInt("id") != 0 && record.getString("large_image").equals("false")) {
                VibracionregularDetails.BigImageLoader bigImageLoader = new VibracionregularDetails.BigImageLoader();
                bigImageLoader.execute(record.getInt("id"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        /*
        switch (v.getId()) {

            case R.id.email:
                IntentUtils.requestMessage(this, record.getString("email"));
                break;
            case R.id.telefono_fijo:
                IntentUtils.requestCall(this, record.getString("telefono_fijo"));
                break;
            case R.id.telefono_celular:
                IntentUtils.requestCall(this, record.getString("telefono_celular"));
                break;

        }
        */
    }

    private void checkControls() {
        //findViewById(R.id.email).setOnClickListener(this);
        //findViewById(R.id.telefono_fijo).setOnClickListener(this);
        //findViewById(R.id.telefono_celular).setOnClickListener(this);
    }

    private void setVibracionregularImage() {

        if (record != null && !record.getString("image_small").equals("false")) {
            vibracionregularImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String base64 = newImage;
            if (newImage == null) {
                if (!record.getString("large_image").equals("false")) {
                    base64 = record.getString("large_image");
                } else {
                    base64 = record.getString("image_small");
                }
            }
            vibracionregularImage.setImageBitmap(BitmapUtils.getBitmapImage(this, base64));
        } else {
            vibracionregularImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            vibracionregularImage.setColorFilter(Color.WHITE);
            int color = OStringColorUtil.getStringColor(this, record.getString("cliente"));
            vibracionregularImage.setBackgroundColor(color);
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
            case R.id.menu_alfalaval_vibracionregular_save:
                OValues values = mForm.getValues();
                //Aquí validar campos obligatorios.

                if (values != null) {
                    if (newImage != null) {
                        values.put("image_small", newImage);
                        values.put("large_image", newImage);
                    }
                    if (record != null) {
                        vibracionregular.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        final int row_id = vibracionregular.insert(values);
                        if (row_id != OModel.INVALID_ROW_ID) {
                            finish();
                        }
                    }
                }else{
                    //findViewById(R.id.suministroEdit);
                    Toast.makeText(this, R.string.hint_question, Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.menu_alfalaval_vibracionregular_cancel:
            case R.id.menu_alfalaval_vibracionregular_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                    setVibracionregularImage();;
                } else {
                    finish();
                }
                break;
            case R.id.menu_alfalaval_vibracionregular_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_alfalaval_vibracionregular_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_alfalaval_vibracionregular_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (vibracionregular.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(VibracionregularDetails.this, R.string.toast_record_deleted,
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
        getMenuInflater().inflate(R.menu.menu_alfalaval_vibracionregular_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (field.getFieldName().equals("cliente")) {
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
                OdooResult record = vibracionregular.getServerDataHelper().read(null, params[0]);
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
                    vibracionregular.update(record.getInt(OColumn.ROW_ID), values);
                    record.put("large_image", result);
                    setVibracionregularImage();
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
            vibracionregularImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            vibracionregularImage.setColorFilter(null);
            vibracionregularImage.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }


}
