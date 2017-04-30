package com.odoo.addons.survey.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.List;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyUserInput extends OModel {
    public static final String KEY = SurveyUserInput.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.survey.survey_user_input";

    OColumn token = new OColumn("token", OVarchar.class).setSize(100);
    OColumn partner_id = new OColumn("partner_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn survey_id = new OColumn("survey_id", SurveySurvey.class, OColumn.RelationType.ManyToOne);
    OColumn deadline = new OColumn("deadline", ODateTime.class);
    OColumn display_name = new OColumn("display_name",OVarchar.class).setSize(100);
    OColumn email = new OColumn("email",OVarchar.class).setSize(100);
    OColumn type = new OColumn("type",OVarchar.class).setDefaultValue("manually");
    OColumn test_entry = new OColumn("test_entry", OBoolean.class).setDefaultValue(true);
    OColumn state = new OColumn("state", OSelection.class)
            .addSelection("new","Sin comenzar aún")
            .addSelection("skip","Parcialmente Completado")
            .addSelection("done","Completada");
    OColumn project_task_ids = new OColumn("project_task_ids" , ProjectTask.class, OColumn.RelationType.ManyToMany);



    public SurveyUserInput(Context context, OUser user) {
        super(context, "survey.user_input", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public ODataRow getSurveyUserInput(Context context, int rowIdProjectTask) {
        SurveyUserInput surveyUserInput = new SurveyUserInput(context,null);
        int size=0;

        ODataRow rowResult = null;
        List<ODataRow> rows = surveyUserInput.select(null,"state!=?",new String[]{"done"},"_id desc");
        for(ODataRow rowSurveyUserInput: rows){
            // code of block
            size = rowSurveyUserInput.getM2MRecord("project_task_ids").browseEach().size();
            if (size>0){
                List<ODataRow> listRow = rowSurveyUserInput.getM2MRecord("project_task_ids").browseEach();
                for (ODataRow rowTask: listRow) {
                    if (rowTask.getInt(OColumn.ROW_ID) == rowIdProjectTask){
                        rowResult = rowSurveyUserInput;
                        return rowResult;
                    }
                } 
            }
        }
       return null;
    }

}
