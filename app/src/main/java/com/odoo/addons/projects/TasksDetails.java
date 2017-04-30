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
import android.content.ContentResolver;
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
import android.widget.TextView;
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
import com.odoo.addons.survey.models.SurveyUserInput;
import com.odoo.addons.survey.models.SurveyUserInputLine;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.support.sync.SyncUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.ODateUtils;
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
            case R.id.addressCustomer:
                ResPartner resPartner = new ResPartner(mContext,null);
                if (record.getString("partner_id")!=null && !record.getString("partner_id").equals("false")) {
                    ODataRow recordParthner = resPartner.browse(Integer.valueOf(record.getString("partner_id")));
                    String full_address = resPartner.getAddress(recordParthner);
                    IntentUtils.redirectToMap(this, full_address);
                    break;
                }
        }
    }

    private void checkControls() {
        ResPartner resPartner = new ResPartner(mContext,null);
        String full_address = "";
        if(record.getString("partner_id").equals("false")){
            findViewById(R.id.nameCustomer).setVisibility(View.GONE);
            findViewById(R.id.addressCustomer).setVisibility(View.GONE);
        }else{
            TextView textView;
            textView = (TextView) findViewById(R.id.addressCustomer);
            findViewById(R.id.addressCustomer).setOnClickListener(this);
            ODataRow recordParthner = resPartner.browse(Integer.valueOf(record.getString("partner_id")));
            full_address = resPartner.getAddress(recordParthner);
            textView.setText(full_address);
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
                                    onRefresh(record.getFloat("_id").intValue());
                                }
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRefresh(int idTask) {
            SyncUtils syncUtils = new SyncUtils(getBaseContext(), OUser.current(getBaseContext()));
            //syncUtils.cancelSync(SurveyPage.AUTHORITY);
            SyncTaskDetails syncTaskDetails = new SyncTaskDetails();
            syncTaskDetails.execute(idTask);
    }

    private class SyncTaskDetails extends AsyncTask<Integer,Void,Boolean> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(TasksDetails.this);
            mDialog.setTitle(R.string.title_working);
            mDialog.setMessage(OResource.string(getApplicationContext(),R.string.label_sync_now));
            mDialog.setCancelable(false);
            mDialog.show();
            ContentResolver.setMasterSyncAutomatically(false);
        }

        public Boolean doInBackground(Integer...args){
            ProjectTask projectTask = new ProjectTask(getBaseContext(), null);
            ProjectTaskType projectTaskType = new ProjectTaskType(getBaseContext(),null);
            int stage_id_In_Preparation =  projectTaskType.getCodProjectTaskType_Id(TypeTask.IN_PREPARATION.getValue()); //9 En Preparación
            int stage_id_Pending = projectTaskType.getCodProjectTaskType_Id(TypeTask.PENDING.getValue());//10 Pendiente de envio de campo.
            int stage_id_On_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.ON_FIELD.getValue());//11 En Campo
            int stage_id_Return_From_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.RETURNED_FROM_FIELD.getValue());//12 Retornada de campo
            int stage_id_Cancel = projectTaskType.getCodProjectTaskType_Id(TypeTask.CANCEL.getValue());//13 Cancelada
            ODomain domain = new ODomain();
            int idTask = args[0];

            OValues valuesProjectTask = new OValues();
            if (!(Boolean.valueOf(projectTask.browse(idTask).getString("x_recursive"))) && (projectTask.browse(idTask).getString("stage_id").equals(String.valueOf(stage_id_On_Field)))) {
                valuesProjectTask.put("stage_id",stage_id_Return_From_Field);
                Boolean rpta = projectTask.update(idTask,valuesProjectTask);
            }

            /*
            Es importante que el orden del bloque de abajo que actualiza el projectTask este antes de los de SurveyUserInput y SurveyUserInputLines
             */

            if (inNetwork()){
                int recordSever = projectTask.browse(idTask).getInt("id");
                ODataRow rowSync = new ODataRow();
                rowSync.put("id", recordSever);
                projectTask.quickCreateRecord(rowSync);

            }

            if (idTask >0){
                SurveyUserInput surveyUserInput = new SurveyUserInput(getBaseContext(), null);
                SurveyUserInputLine surveyUserInputLine = new SurveyUserInputLine(getBaseContext(),null);
                int idUserInput = 0;
                ODataRow rowSurveyUserInput = surveyUserInput.getSurveyUserInput(getBaseContext(),idTask);
                if (rowSurveyUserInput!=null){
                    idUserInput = rowSurveyUserInput.getInt(OColumn.ROW_ID);
                }

                if (idUserInput > 0){
                    int recordSever = surveyUserInput.browse(idUserInput).getInt("id");
                    ODataRow rowSync = new ODataRow();
                    OValues valuesUserInput = new OValues();
                    valuesUserInput.put("state", "done");
                    surveyUserInput.update(idUserInput,valuesUserInput);

                    if (inNetwork()){
                        rowSync.put("id", recordSever);
                        surveyUserInput.quickCreateRecord(rowSync);
                    }
                    // Fuerza Syncronización de User Input Line.
                    if (inNetwork()){
                        List<ODataRow> recordSurveyUserInputLine = surveyUserInputLine.getSurveyUserInputLineByInputLineList2(getBaseContext(),String.valueOf(idUserInput));
                        if (recordSurveyUserInputLine!= null)
                        {
                            for (ODataRow rowSyncUserInputLine:recordSurveyUserInputLine) {
                                int recordSeverInputLine = rowSyncUserInputLine.getInt("id");
                                ODataRow rowSyncInputLine = new ODataRow();
                                rowSyncInputLine.put("id", recordSeverInputLine);
                                surveyUserInputLine.quickCreateRecord(rowSyncInputLine);
                            }
                        }
                    }
                }
            }
        return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mDialog.dismiss();
            if (success) {
                ContentResolver.setMasterSyncAutomatically(true);
                finish();
            }
            if (!inNetwork()){
                Toast.makeText(TasksDetails.this, R.string.toast_network_required,Toast.LENGTH_SHORT).show();
            }

        }
    }
    private class SyncTaskDetailsAllRecord extends AsyncTask<Void,Void,Boolean> {
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

        public Boolean doInBackground(Void...args){
            ProjectTaskType projectTaskType = new ProjectTaskType(mContext,null);
            int stage_id_In_Preparation =  projectTaskType.getCodProjectTaskType_Id(TypeTask.IN_PREPARATION.getValue()); //9 En Preparación
            int stage_id_Pending = projectTaskType.getCodProjectTaskType_Id(TypeTask.PENDING.getValue());//10 Pendiente de envio de campo.
            int stage_id_On_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.ON_FIELD.getValue());//11 En Campo
            int stage_id_Return_From_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.RETURNED_FROM_FIELD.getValue());//12 Retornada de campo
            int stage_id_Cancel = projectTaskType.getCodProjectTaskType_Id(TypeTask.CANCEL.getValue());//13 Cancelada
            ProjectTask projectTask = new ProjectTask(mContext, null);

            ODomain domain = new ODomain();

            domain.add("|");
            domain.add("stage_id", "=", String.valueOf(stage_id_Return_From_Field));
            domain.add("|");
            domain.add("stage_id", "=", String.valueOf(stage_id_Cancel));
            domain.add("stage_id", "=", String.valueOf(stage_id_On_Field));
            projectTask.quickSyncRecords(domain);


            //Fuerza Syncronización
            List<Integer> listProjectTask = projectTask.getProjectTaskOnField(getBaseContext());
            for (Integer rowTask : listProjectTask) {
                ODataRow rowSync = new ODataRow();
                rowSync.put("id", rowTask);
                projectTask.quickCreateRecord(rowSync);
            }
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

    public int createRecursive(){
        OValues oValues = new OValues();
        ProjectTaskType projectTaskType = new ProjectTaskType(mContext,null);
        int stage_id_In_Preparation =  projectTaskType.getCodProjectTaskType_Id(TypeTask.IN_PREPARATION.getValue()); //9 En Preparación
        int stage_id_Pending = projectTaskType.getCodProjectTaskType_Id(TypeTask.PENDING.getValue());//10 Pendiente de envio de campo.
        int stage_id_On_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.ON_FIELD.getValue());//11 En Campo
        int stage_id_Return_From_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.RETURNED_FROM_FIELD.getValue());//12 Retornada de campo
        int stage_id_Cancel = projectTaskType.getCodProjectTaskType_Id(TypeTask.CANCEL.getValue());//13 Cancelada

        ProjectTask projectTask = new ProjectTask(mContext, null);
        ODataRow recordProjectTask = projectTask.browse(record.getInt(OColumn.ROW_ID));
        oValues.put("x_create_source", "True");
        oValues.put("name", recordProjectTask.getString("name"));
        oValues.put("project_id", recordProjectTask.getString("project_id"));
        oValues.put("x_survey_id", recordProjectTask.getString("x_survey_id"));
        oValues.put("description", recordProjectTask.getString("description"));
        oValues.put("date_deadline", recordProjectTask.getString("date_deadline"));
        oValues.put("date_start", recordProjectTask.getString("date_start"));
        oValues.put("date_end", recordProjectTask.getString("date_end"));
        oValues.put("color", recordProjectTask.getString("color"));
        oValues.put("partner_id", recordProjectTask.getString("partner_id"));
        oValues.put("stage_id", stage_id_On_Field);
        oValues.put("priority", recordProjectTask.getString("priority"));
        oValues.put("x_recursive", recordProjectTask.getString("x_recursive"));
        int recordTask = projectTask.insert(oValues);
        return recordTask;
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
                    OFieldList.add(txtBox);
                }
            }
        }
        linearlayoutTask = (LinearLayout) findViewById(R.id.taskFormEdit);
        for(EditText f : OFieldList) {
            linearlayoutTask.addView(f);
        }
    }

    public boolean inNetwork() {
        App app = (App) mContext.getApplicationContext();
        return app.inNetwork();
    }

}