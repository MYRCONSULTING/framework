package com.odoo.addons.survey.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyQuestion extends OModel {
    public static final String KEY = SurveyQuestion.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.survey.survey_question";

    OColumn question = new OColumn("question", OVarchar.class).setSize(100);
    OColumn page_id = new OColumn("page_id", SurveyPage.class, OColumn.RelationType.ManyToOne);
    OColumn survey_id = new OColumn("survey_id", SurveySurvey.class, OColumn.RelationType.ManyToOne);
    OColumn type = new OColumn("type", OSelection.class)
            .addSelection("free_text","Zona para textos largos")
            .addSelection("textbox","Entrada de texto")
            .addSelection("numerical_box","Valor númerico")
            .addSelection("datetime","Fecha y Hora")
            .addSelection("simple_choice","Elección Múltiple: Sólo una respuesta")
            .addSelection("multiple_choice","Elección Múltiple: Permitidas respuestas multiples")
            .addSelection("matrix","Matriz");

    public SurveyQuestion(Context context, OUser user) {
        super(context, "survey.question", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }


}
