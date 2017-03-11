package com.odoo.addons.survey.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyUserInput extends OModel {
    public static final String KEY = SurveyUserInput.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.survey.survey_user_input";

    OColumn token = new OColumn("token", OVarchar.class).setSize(100);
    //OColumn partner_id = new OColumn("partner_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    //OColumn survey_id = new OColumn("survey_id", SurveySurvey.class, OColumn.RelationType.ManyToOne);
    //OColumn x_project_task_ids = new OColumn("x_project_task_ids" , ProjectTask.class, OColumn.RelationType.ManyToOne);



    public SurveyUserInput(Context context, OUser user) {
        super(context, "survey.user_input", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }


}
