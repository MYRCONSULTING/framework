package com.odoo.addons.survey;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.projects.TasksDetails;
import com.odoo.addons.survey.models.SurveyUserInput;
import com.odoo.addons.survey.models.SurveyUserInputLine;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OM2ORecord;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by Ricardo Livelli on 09/03/2017.
 */

public class SurveyQuestion extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ISyncStatusObserverListener, OCursorListAdapter.OnViewBindListener {

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
    private Spinner spinner1;
    private SurveyUserInput surveyUserInput;
    private SurveyUserInputLine surveyUserInputLine;
    private Bundle extrasUserInput;
    private EditText editText;
    private HashMap<Integer,ODataRow> mapsurveyUserInputLine = new HashMap<Integer,ODataRow>();
    private HashMap<Integer,String> mapsurveyQuestion = new HashMap<Integer,String>();
    private HashMap<Integer,String> mapIdComponent = new HashMap<Integer,String>();
    private int rowIdUserInput = 0;
    private String idSurvey = "";
    ODataRow recordSurveyUserInput = null;
    public static final String EXTRA_KEY_SURVEY = "extra_key_survey";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_question, menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setHasSwipeRefreshView(view, R.id.swipe_container, this);
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

        idSurvey = extra.getString(EXTRA_KEY_SURVEY);
        loadQuestionByUserInputLine();
    }

    public  void loadQuestionByUserInputLine(){
        //ODataRow recordPage = surveyQuestion.browse(rowId).getM2ORecord("page_id").browse();
        //ODataRow recordSurvey = surveyQuestion.browse(rowId).getM2ORecord("survey_id").browse();
        List<ODataRow> recordSurveyUserInputLine= null;
        rowIdUserInput = 0;
        int rowTaskId = extra.getInt("id_task");

        if (surveyUserInput.getSurveyUserInputList(getContext(),String.valueOf(rowTaskId)).size()>0){
            recordSurveyUserInput = surveyUserInput.getSurveyUserInputList(getContext(),String.valueOf(rowTaskId)).get(0);
            rowIdUserInput = recordSurveyUserInput.getInt("_id");
            recordSurveyUserInputLine = getSurveyUserInputLineByInputList(getContext(),String.valueOf(rowIdUserInput));
            if (recordSurveyUserInputLine.size()>0 && recordSurveyUserInputLine != null)
            {
                int sizeRecord = recordSurveyUserInputLine.size();
                for(int x=0; x<sizeRecord; x++){
                    mapsurveyUserInputLine.put(recordSurveyUserInputLine.get(x).getInt("question_id"),recordSurveyUserInputLine.get(x));
                }
            }
        }
    }


    // add items into spinner dynamically
    public void addItemsOnSpinner(View view, Cursor cursor, ODataRow row) {

        spinner1 = (Spinner) view.findViewById(R.id.simpleChoice_UserInput);
        List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_save:
                saveInputUser();
                Log.i(TAG, "Save User Input : " );
                Toast.makeText(getActivity(), "Graba", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_syncronize:
                Log.i(TAG, "Syncronize : " );
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void saveInputUser(){

        Set setId = mapIdComponent.entrySet();

        Iterator iteratorId = setId.iterator();
        while(iteratorId.hasNext()) {
            Map.Entry mentry = (Map.Entry) iteratorId.next();
            System.out.print("key is: " + mentry.getKey() + " & Value is: ");
            System.out.println(mentry.getValue());
            EditText txtEdit = (EditText) getActivity().findViewById(Integer.valueOf(mentry.getKey().toString()));
            txtEdit.getText();
            //mapIdComponent.get(12);


        }

        //mView.findViewById(R.id.listview);
        if (rowIdUserInput==0){ // Registro Nuevo - Id de User Input - Respuesta relacionada a una tarea.
            surveyUserInput = new SurveyUserInput(getActivity(), null);
            surveyUserInputLine = new SurveyUserInputLine(getActivity(),null);

            // Add User Input
            OValues valuesUserInput = new OValues();
            final String uuid = UUID.randomUUID().toString();
            System.out.println("uuid = " + uuid);
            valuesUserInput.put("token", uuid);
            valuesUserInput.put("x_project_task_ids", extra.getInt("id_task"));
            valuesUserInput.put("survey_id",idSurvey);
            final int row_idUserInput = surveyUserInput.insert(valuesUserInput);
            // Add User Input Line
            Set set = mapsurveyQuestion.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                Map.Entry mentry = (Map.Entry)iterator.next();
                System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
                System.out.println(mentry.getValue());
                OValues valuesUserInputLine = new OValues();
                valuesUserInputLine.put("survey_id",idSurvey);
                ODataRow recordPage = surveyQuestion.browse(Integer.valueOf(mentry.getKey().toString())).getM2ORecord("page_id").browse();
                valuesUserInputLine.put("page_id", recordPage.getInt("_id"));
                valuesUserInputLine.put("question_id", mentry.getKey().toString());
                valuesUserInputLine.put("user_input_id",row_idUserInput);
                valuesUserInputLine.put("skipped",false);
                EditText txtEdit = (EditText) getActivity().findViewById(Integer.valueOf(mentry.getKey().toString()));
                switch (mentry.getValue().toString()) {
                    case "free_text":
                        valuesUserInputLine.put("answer_type","free_text");
                        valuesUserInputLine.put("value_free_text",txtEdit.getText());
                        break;
                    case "textbox":
                        valuesUserInputLine.put("answer_type","text");
                        valuesUserInputLine.put("value_text",txtEdit.getText());
                        break;
                    case "numerical_box":
                        valuesUserInputLine.put("answer_type","number");
                        valuesUserInputLine.put("value_number",txtEdit.getText());
                        break;
                }
                final int row_idUserInputLine = surveyUserInputLine.insert(valuesUserInputLine);
            }


            /*
            // Add User Input Line
            for(int x=0; x<mapsurveyUserInputLine.size(); x++){
                ODataRow dataUserInputLine = mapsurveyUserInputLine.get(x);
                OValues valuesUserInputLine = new OValues();
                valuesUserInputLine.put("survey_id",recordSurveyUserInput.getInt("survey_id"));
                valuesUserInputLine.put("page_id", recordPage.getInt("_int"));
                // For para las questions
                valuesUserInputLine.put("question_id", dataUserInputLine.getInt("question_id"));
                valuesUserInputLine.put("user_input_id",row_idUserInput);
                valuesUserInputLine.put("value_text","YaPe");
                valuesUserInputLine.put("skipped",false);
                valuesUserInputLine.put("answer_type","text");
                final int row_idUserInputLine = surveyUserInputLine.insertOrUpdate(dataUserInputLine.getInt("_id"),valuesUserInputLine);
            }
            */

        }else{ // Ya existe una respuesta asociada a la tarea.

        }




    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        LinearLayout linearlayoutTask;
        ODataRow recordSurveyUserInputLine;
        int rowId = row.getInt("_id");
        recordSurveyUserInputLine = mapsurveyUserInputLine.get(rowId);
        OControls.setText(view, R.id.textViewQuestion, row.getString("question"));
        mapsurveyQuestion.put(rowId,row.getString("type"));
        mapIdComponent.put(rowId,"idComponent"+rowId);
        linearlayoutTask = (LinearLayout) view.findViewById(R.id.taskFormEdit);
        switch (row.getString("type")) {
            case "free_text":
                /*
                OControls.setVisible(view,R.id.editTextArea_UserInput);
                if (recordSurveyUserInputLine!= null){
                    OControls.setText(view, R.id.editTextArea_UserInput, recordSurveyUserInputLine.get("value_free_text").toString());
                }
                */
                EditText txtEdit_free_text = new EditText(getContext());
                txtEdit_free_text.setId(rowId);
                txtEdit_free_text.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                txtEdit_free_text.setTag("idComponent"+rowId);
                if (recordSurveyUserInputLine!= null && recordSurveyUserInputLine.size()>0){
                    txtEdit_free_text.setText(recordSurveyUserInputLine.get("value_free_text").toString());
                }
                txtEdit_free_text.setHint("Complete la informacion");
                linearlayoutTask.addView(txtEdit_free_text);

                break;
            case "textbox":
                /*
                OControls.setVisible(view,R.id.editText_UserInput);
                if (recordSurveyUserInputLine!= null){
                    OControls.setText(view, R.id.editText_UserInput, recordSurveyUserInputLine.get("value_text").toString());
                }
                */
                EditText txtEdit_textbox = new EditText(getContext());
                txtEdit_textbox.setId(rowId);
                txtEdit_textbox.setInputType(InputType.TYPE_CLASS_TEXT);
                txtEdit_textbox.setTag("idComponent"+rowId);
                if (recordSurveyUserInputLine!= null && recordSurveyUserInputLine.size()>0){
                    txtEdit_textbox.setText(recordSurveyUserInputLine.get("value_text").toString());
                }
                txtEdit_textbox.setHint("Complete la informacion");
                linearlayoutTask.addView(txtEdit_textbox);

                break;
            case "numerical_box":
                /*
                OControls.setVisible(view,R.id.editTextNumerical_UserInput);
                if (recordSurveyUserInputLine!= null){
                    OControls.setText(view, R.id.editTextNumerical_UserInput, recordSurveyUserInputLine.get("value_number").toString());
                }
                */
                EditText txtEdit_numerical_box = new EditText(getContext());
                txtEdit_numerical_box.setId(rowId);
                txtEdit_numerical_box.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                txtEdit_numerical_box.setTag("idComponent"+rowId);
                if (recordSurveyUserInputLine!= null && recordSurveyUserInputLine.size()>0){
                    txtEdit_numerical_box.setText(recordSurveyUserInputLine.get("value_number").toString());
                }
                txtEdit_numerical_box.setHint("Complete la informacion");
                linearlayoutTask.addView(txtEdit_numerical_box);
                break;
        }

        Bitmap img;
        if (row.getString("image_small").equals("false")) {
            img = BitmapUtils.getAlphabetImage(getActivity(), row.getString("question"));
        } else {
            img = BitmapUtils.getBitmapImage(getActivity(), row.getString("image_small"));
        }
        OControls.setImage(view, R.id.image_small, img);

        // Para los otros tipos de campos.
        addItemsOnSpinner(view,cursor,row);
    }

    public List<ODataRow> getSurveyUserInputLineByInputList(Context context, String user_input_id) {
        SurveyUserInputLine surveyUserInputLine = new SurveyUserInputLine(context,null);
        if (surveyUserInputLine.getSurveyUserInputLineByInputLineList(getContext(),user_input_id).size()>0)
        {
            List<ODataRow> recordSurveyUserInputLine = surveyUserInputLine.getSurveyUserInputLineByInputLineList(getContext(),user_input_id);
            return recordSurveyUserInputLine;
        }
        else
        {
            return null;
        }
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
            //setHasSwipeRefreshView(mView, R.id.swipe_container, this);
        } else {
            OControls.setGone(mView, R.id.loadingProgress);
            OControls.setGone(mView, R.id.swipe_container);
            OControls.setVisible(mView, R.id.data_list_no_item);
            //setHasSwipeRefreshView(mView, R.id.data_list_no_item, this);
            OControls.setText(mView, R.id.title, "No Question found");
            OControls.setText(mView, R.id.subTitle, "Swipe to check new question");
        }
        if (db().isEmptyTable()) {
            // Request for sync
            // Request for sync
            //onRefresh();
        }
    }

    /*
    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(com.odoo.addons.survey.models.SurveyQuestion.AUTHORITY);
        }
    }
    */

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
                null, selection, selectionArgs, "id");
    }
}
