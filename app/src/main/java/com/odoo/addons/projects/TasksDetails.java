/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 8/1/15 5:47 PM
 */
package com.odoo.addons.projects;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.addons.projects.models.ProjectTaskType;
import com.odoo.addons.projects.models.TypeTask;
import com.odoo.addons.survey.models.SurveyPage;
import com.odoo.addons.survey.models.SurveyQuestion;
import com.odoo.addons.survey.models.SurveySurvey;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.OControlHelper;
import odoo.controls.OField;
import odoo.controls.OForm;

public class TasksDetails extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener {
    public static final String TAG = TasksDetails.class.getSimpleName();
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Bundle extras;
    private ProjectTask projectTask;
    private ODataRow record = null;
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
    private String newImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    public static final String EXTRA_KEY_SURVEY_TASK = "extra_key_survey_task";
    private LinearLayout linearlayoutTask;
    private float textSize = -1;
    private int appearance = -1;
    private int textColor = Color.BLUE;
    private Context mContext = null;

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next pages.
     */
    ViewPager pager = null;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */


    private com.odoo.addons.survey.SurveySurvey surveySurvey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.tasks_detail);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.tasks_collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle(OResource.string(this, R.string.sync_label_tasks));
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
            newImage = savedInstanceState.getString(KEY_NEW_IMAGE);
        }
        app = (App) getApplicationContext();
        projectTask = new ProjectTask(this, null);
        extras = getIntent().getExtras();

        /////////////////////////////

        /*
        OField oField = new OField(this);
        oField.initControl();
        oField.getId();
        oField.setModel(ProjectTask.get(this,"project.task",null));
        oField.setmField_name("date_deadline");
        oField.setmLabel("Fecha final 12345678:");
        oField.setmType(OField.FieldType.Date);
        oField.setValue("2017-03-31");
        oField.setIcon(R.drawable.ic_action_message);
        oField.setResId(R.drawable.ic_action_message);
        linearlayoutTask = (LinearLayout) findViewById(R.id.taskFormView);
        linearlayoutTask.addView(oField);
        */

        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();

    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
        if (mMenu != null) {
            //mMenu.findItem(R.id.menu_task_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_task_detail_more).setVisible(false);
            //mMenu.findItem(R.id.menu_task_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_task_edit).setVisible(false);
            mMenu.findItem(R.id.menu_task_save).setVisible(edit);
            mMenu.findItem(R.id.menu_task_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle(OResource.string(this, R.string.label_create_new));
            }
            mForm = (OForm) findViewById(R.id.taskFormEdit);
            findViewById(R.id.task_view_layout).setVisibility(View.GONE);
            findViewById(R.id.task_edit_layout).setVisibility(View.VISIBLE);
        } else {
            mForm = (OForm) findViewById(R.id.taskFormView);
            findViewById(R.id.task_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.task_view_layout).setVisibility(View.VISIBLE);
        }
        setColor(color);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(OColumn.ROW_ID);
            record = projectTask.browse(rowId);
            //record.put("full_address", resPartner.getAddress(record));
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString("name"));
            if (record.getInt("id") != 0) { // ALERTA

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nameTask:
                IntentUtils.requestMessage(this, record.getString("name"));
                break;
            case R.id.descriptionTask:
                IntentUtils.requestMessage(this, record.getString("description"));
                break;
        }
    }

    private void checkControls() {
        //findViewById(R.id.nameTask).setOnClickListener(this);
        //findViewById(R.id.descriptionTask).setOnClickListener(this);
        //findViewById(R.id.nameTaskEdit).setFocusable(false);
        //findViewById(R.id.nameTaskEdit).setEnabled(false);
        //findViewById(R.id.nameTaskEdit).setOnKeyListener(null);
        //bindSurvey();
    }

    private void bindSurvey(){
        ArrayList<EditText> OFieldList = new ArrayList<EditText>();
        List<ODataRow> rowSurveyQuestion = null;
        //// 1.- Verifica la Encuesta que corresponde a la tarea seleccionada
        String rowIdSurvey = null;
        if (extras.containsKey(EXTRA_KEY_SURVEY_TASK) && rowIdSurvey == null) {
            rowIdSurvey = extras.getString(EXTRA_KEY_SURVEY_TASK);
            SurveySurvey surveySurvey = new SurveySurvey(this, null);
            ODataRow rowSurvey = surveySurvey.browse(Integer.parseInt(rowIdSurvey));
            Log.i(TAG, "Id SurveySurvey Title : " + rowSurvey.get("title"));
            /// 2.- Recorre la cantidad de páginas de cada encuesta
            SurveyPage surveyPage = new SurveyPage(this, null);
            List<ODataRow> rowSurveyPage = surveyPage.getSurveyPage(this, rowIdSurvey);
            for (ODataRow rowPage : rowSurveyPage) {
                Log.i(TAG, "Id SurveyPage Title : " + rowPage.get("title"));
                //Log.i(TAG, "Id SurveyPage _Id   : " + rowPage.get("_id"));
                //Log.i(TAG, "Id SurveyPage  Id   : " + rowPage.get("id"));
                /// 3.- Recorre la cantidad de Preguntas por página de cada encuesta
                SurveyQuestion surveyQuestion = new SurveyQuestion(this, null);
                rowSurveyQuestion = surveyQuestion.getSurveyQuestion(this, rowIdSurvey,
                        rowPage.get("_id").toString());
                for (ODataRow row : rowSurveyQuestion) {
                    Log.i(TAG, "Id SurveyQuestion Question : " + row.get("question"));
                    //Log.i(TAG, "Id SurveyQuestion _Id   : " + row.get("_id"));
                    //Log.i(TAG, "Id SurveyQuestion  Id   : " + row.get("id"));

                    OField oField = new OField(this);
                    oField.setId(Integer.parseInt(row.get("_id").toString()));
                    oField.setModel(SurveyQuestion.get(this,"survey.question",null));
                    oField.setmLabel(row.get("question").toString());
                    oField.setmField_name("question");
                    oField.setmType(OField.FieldType.Text);
                    oField.initControl();
                    oField.setIcon(R.drawable.ic_action_message);
                    oField.setResId(R.drawable.ic_action_message);


                    ///////
                    EditText txtBox;
                    txtBox = new EditText(this);
                    txtBox.setId(Integer.parseInt(row.get("_id").toString()));
                    txtBox.setHint(row.get("question").toString());
                    txtBox.setText(row.get("question").toString());
                    txtBox.setTextAppearance(this,appearance);
                    txtBox.setPadding(20, 10, 10, 10);

                    txtBox.setTextColor(textColor);
                    txtBox.setTypeface(OControlHelper.lightFont());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    txtBox.setLayoutParams(params);
                    txtBox.setBackgroundColor(Color.TRANSPARENT);
                    txtBox.setTextSize(textSize);
                    ////////7

                    OFieldList.add(txtBox);
                }
            }
        }
        linearlayoutTask = (LinearLayout) findViewById(R.id.taskFormEdit);
        for(EditText f : OFieldList) {
            linearlayoutTask.addView(f);
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
            case R.id.menu_task_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (record != null) {
                        projectTask.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        final int row_id = projectTask.insert(values);
                        if (row_id != OModel.INVALID_ROW_ID) {
                            finish();
                        }
                    }
                }
                break;
            case R.id.menu_task_cancel:
            case R.id.menu_task_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                } else {
                    finish();
                }
                break;
            case R.id.menu_task_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_task_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_task_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (projectTask.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(TasksDetails.this, R.string.toast_record_deleted,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        });

                break;
            case R.id.menu_task_end:
                OAlert.showConfirm(this, OResource.string(this,R.string.confirm_are_you_sure_want_sync),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    ProjectTaskType projectTaskType = new ProjectTaskType(mContext,null);
                                    OValues valuesProjectTask = new OValues();
                                    valuesProjectTask.put("stage_id",projectTaskType.getCodProjectTaskType_Id(TypeTask.RETURNED_FROM_FIELD.getValue()));
                                    valuesProjectTask.put("x_task_type",TypeTask.RETURNED_FROM_FIELD.getValue());
                                    if (projectTask.update(record.getInt(OColumn.ROW_ID),valuesProjectTask)) {
                                        //Syncronizar
                                        onRefresh();
                                    }
                                }
                            }
                        });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRefresh() {
            SyncTaskDetails syncTaskDetails = new SyncTaskDetails();
            syncTaskDetails.execute(record);
    }

    private class SyncTaskDetails extends AsyncTask<ODataRow,Void,Boolean> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(TasksDetails.this);
            mDialog.setTitle(R.string.title_working);
            mDialog.setMessage(OResource.string(getApplicationContext(),R.string.label_sync_now));
            mDialog.setCancelable(false);
            mDialog.show();
        }

        public Boolean doInBackground(ODataRow...args){
            ODataRow record = args[0];
            ProjectTaskType projectTaskType = new ProjectTaskType(mContext,null);
            ProjectTask projectTask = new ProjectTask(mContext, null);
            ODomain domain = new ODomain();
            domain.add("stage_id", "=", projectTaskType.getCodProjectTaskType_Id(TypeTask.RETURNED_FROM_FIELD.getValue()));
            projectTask.quickSyncRecords(domain);
            projectTask.delete(record.getInt(OColumn.ROW_ID),true);
            return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mDialog.dismiss();
            if (success) {
                finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        /*
        if (field.getFieldName().equals("is_company")) {
            Boolean checked = Boolean.parseBoolean(value.toString());
            int view = (checked) ? View.GONE : View.VISIBLE;
            findViewById(R.id.parent_id).setVisibility(view);
        }
        */
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

        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }

}