package com.odoo.addons.survey.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.addons.projects.models.ProjectTaskType;
import com.odoo.addons.projects.models.TypeTask;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyUserInputLine extends OModel {
    public static final String KEY = SurveyUserInputLine.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.survey.survey_user_input_line";


    OColumn page_id = new OColumn("page_id", SurveyPage.class, OColumn.RelationType.ManyToOne);
    OColumn question_id = new OColumn("question_id", SurveyQuestion.class,OColumn.RelationType.ManyToOne);
    OColumn survey_id = new OColumn("survey_id", SurveySurvey.class, OColumn.RelationType.ManyToOne);
    OColumn user_input_id = new OColumn("user_input_id",SurveyUserInput.class,OColumn.RelationType.ManyToOne);
    OColumn value_text = new OColumn("value_text",OVarchar.class).setSize(100);
    OColumn value_free_text = new OColumn("value_free_text",OVarchar.class).setSize(100);
    OColumn value_number = new OColumn("value_number",OFloat.class);
    OColumn skipped = new OColumn("skipped",OBoolean.class).setDefaultValue(false);
    OColumn answer_type = new OColumn("answer_type", OSelection.class)
            .addSelection("text","Entrada de texto")
            .addSelection("number","Valor númerico")
            .addSelection("date","Fecha")
            .addSelection("free_text","Zona para textos largos")
            .addSelection("suggestion","Suggestion");

    public SurveyUserInputLine(Context context, OUser user) {
        super(context, "survey.user_input_line", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public static List<ODataRow> getSurveyUserInputLineByQuestionList(Context context, String question_id) {
        SurveyUserInputLine surveyUserInputLine = new SurveyUserInputLine(context,null);
        List<ODataRow> rowSurveyUserInputLine = surveyUserInputLine.select(null,"question_id = ? ",
                new String[]{question_id},"id asc");
        return rowSurveyUserInputLine;
    }

    public static List<ODataRow> getSurveyUserInputLineByInputLineList(Context context, String user_input_id,int idPage) {
        SurveyQuestion surveyQuestion = new SurveyQuestion(context,null);
        SurveyUserInputLine surveyUserInputLine = new SurveyUserInputLine(context,null);
        List<ODataRow> rowSurveyUserInputLine = surveyUserInputLine.select(null,"user_input_id = ? ",new String[]{user_input_id},"id asc");
        List<ODataRow> rowSurveyUserInputLine2 = new ArrayList<ODataRow>();
        for (int i=0; i<rowSurveyUserInputLine.size(); i++) {
            System.out.println(rowSurveyUserInputLine.get(i).getString("question_id"));
            int recordPage = surveyQuestion.browse(rowSurveyUserInputLine.get(i).getInt("question_id")).getM2ORecord("page_id").browse().getInt(OColumn.ROW_ID);
            if (recordPage == idPage){
                if (rowSurveyUserInputLine.get(i)!=null){
                    rowSurveyUserInputLine2.add(rowSurveyUserInputLine.get(i));
                }
            }
        }

        return rowSurveyUserInputLine2;
    }

    public static List<ODataRow> getSurveyUserInputLineByInputLineList2(Context context, String user_input_id) {
        SurveyUserInputLine surveyUserInputLine = new SurveyUserInputLine(context,null);
        List<ODataRow> rowSurveyUserInputLine = surveyUserInputLine.select(null,"user_input_id = ? ",new String[]{user_input_id},"id asc");
        return rowSurveyUserInputLine;
    }

    public static List<ODataRow> getSurveyUserInputLineByInputLineAndTypeAndQuestionList(Context context, String user_input_id, String answer_type, String question_id) {
        SurveyUserInputLine surveyUserInputLine = new SurveyUserInputLine(context,null);
        List<ODataRow> rowSurveyUserInputLine = surveyUserInputLine.select(null,"user_input_id = ? and answer_type=? and question_id=? ",
                new String[]{user_input_id,answer_type,question_id},"id asc");
        return rowSurveyUserInputLine;
    }

    public static List<ODataRow> getSurveyUserInputLineBySurveyList(Context context, String survey_id) {
        SurveyUserInputLine surveyUserInputLine = new SurveyUserInputLine(context,null);
        List<ODataRow> rowSurveyUserInputLine = surveyUserInputLine.select(null,"survey_id = ? ",
                new String[]{survey_id},"id asc");
        return rowSurveyUserInputLine;
    }

    @Override
    public void onSyncFinished(){
        ProjectTaskType projectTaskType = new ProjectTaskType(getContext(),null);
        int stage_id_Return_From_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.RETURNED_FROM_FIELD.getValue());//12 Retornada de campo
        ProjectTask projectTask = new ProjectTask(getContext(),null);
        //Clean Return onField
        String type = String.valueOf(stage_id_Return_From_Field);
        projectTask.delete("stage_id = ? and x_recursive = ?",new String[]{type,"false"},true);
    }
}
