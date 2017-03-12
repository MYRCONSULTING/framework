package com.odoo.addons.survey.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

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


}
