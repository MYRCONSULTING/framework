package com.odoo.addons.survey.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

import java.util.List;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyQuestion extends OModel {
    public static final String KEY = SurveyQuestion.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.survey.survey_question";

    OColumn question = new OColumn("question", OVarchar.class).setSize(100);
    OColumn page_id = new OColumn("page_id", SurveyPage.class, OColumn.RelationType.ManyToOne);
    OColumn survey_id = new OColumn("survey_id", SurveySurvey.class, OColumn.RelationType.ManyToOne);
    OColumn constr_mandatory = new OColumn("constr_mandatory", OBoolean.class).setDefaultValue(false);
    OColumn constr_error_msg = new OColumn("constr_error_msg", OVarchar.class).setSize(300);
    OColumn validation_required = new OColumn("validation_required", OBoolean.class).setDefaultValue(false);
    OColumn validation_length_min = new OColumn("validation_length_min",OInteger.class);
    OColumn validation_length_max = new OColumn("validation_length_max",OInteger.class);
    OColumn validation_error_msg = new OColumn("validation_error_msg", OVarchar.class).setSize(300);
    OColumn validation_min_float_value = new OColumn("validation_min_float_value",OFloat.class);
    OColumn validation_max_float_value = new OColumn("validation_max_float_value",OFloat.class);
    OColumn validation_min_date = new OColumn("validation_min_date", ODateTime.class);
    OColumn validation_max_date = new OColumn("validation_max_date", ODateTime.class);
    OColumn display_mode = new OColumn("display_mode", OSelection.class)
            .addSelection("columns","Botones de opción/Casillas de verificación")
            .addSelection("dropdown","Caja de selección");
    OColumn comments_allowed = new OColumn("comments_allowed", OBoolean.class).setDefaultValue(false);
    OColumn comments_message = new OColumn("comments_message", OVarchar.class).setSize(300);
    OColumn comment_count_as_answer = new OColumn("comment_count_as_answer", OBoolean.class).setDefaultValue(false);
    OColumn column_nb = new OColumn("column_nb", OSelection.class)
            .addSelection("12","1")
            .addSelection("6","2")
            .addSelection("4","3")
            .addSelection("3","4")
            .addSelection("2","6");
    OColumn matrix_subtype = new OColumn("matrix_subtype", OSelection.class)
            .addSelection("simple","Una elección por fila")
            .addSelection("multiple","Opciónes múltiples por renglón");
    OColumn validation_email = new OColumn("validation_email", OBoolean.class).setDefaultValue(false);
    OColumn labels_ids = new OColumn("labels_ids" , SurveyLabel.class, OColumn.RelationType.OneToMany);
    OColumn labels_ids_2 = new OColumn("labels_ids_2" , SurveyLabel.class, OColumn.RelationType.OneToMany);












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

    public static List<ODataRow> getSurveyQuestion(Context context,String rowIdSurvey, String rowIdPage ) {
        SurveyQuestion surveyQuestion = new SurveyQuestion(context,null);
        List<ODataRow> rowSurveyQuestion = surveyQuestion.select(null,"survey_id = ? and page_id = ?",
                new String[]{rowIdSurvey,rowIdPage},"id asc");
        return rowSurveyQuestion;
    }





}
