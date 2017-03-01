package com.odoo.addons.survey.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyPage extends OModel {
    public static final String KEY = SurveyPage.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.survey.survey_page";

    OColumn title = new OColumn("title", OVarchar.class).setSize(100);
    OColumn survey_id = new OColumn("survey_id", SurveySurvey.class, OColumn.RelationType.ManyToOne);
    OColumn question_ids = new OColumn("question_ids", SurveyQuestion.class, OColumn.RelationType.OneToMany);

    public SurveyPage(Context context, OUser user) {
        super(context, "survey.page", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

}
