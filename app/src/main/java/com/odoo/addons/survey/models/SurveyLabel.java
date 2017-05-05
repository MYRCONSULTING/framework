package com.odoo.addons.survey.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyLabel extends OModel {
    public static final String KEY = SurveyLabel.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.survey.survey_label";

    OColumn display_name = new OColumn("display_name", OVarchar.class).setSize(300);
    OColumn question_id = new OColumn("question_id", SurveyQuestion.class, OColumn.RelationType.ManyToOne);
    OColumn question_id_2 = new OColumn("question_id_2", SurveyQuestion.class, OColumn.RelationType.ManyToOne);
    OColumn quizz_mark = new OColumn("quizz_mark", OFloat.class);
    OColumn sequence = new OColumn("sequence", OInteger.class);
    OColumn value = new OColumn("value", OVarchar.class).setSize(300);

    public SurveyLabel(Context context, OUser user) {
        super(context, "survey.label", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }


}
