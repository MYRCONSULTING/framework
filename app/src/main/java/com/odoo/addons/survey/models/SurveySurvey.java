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

public class SurveySurvey extends OModel {
    public static final String KEY = SurveySurvey.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.survey.survey_survey";

    OColumn title = new OColumn("title", OVarchar.class).setSize(100);
    OColumn page_ids = new OColumn("page_ids", SurveyPage.class, OColumn.RelationType.OneToMany);



    public SurveySurvey(Context context, OUser user) {
        super(context, "survey.survey", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }


}
