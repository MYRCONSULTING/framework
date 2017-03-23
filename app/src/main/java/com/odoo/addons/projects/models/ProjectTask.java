package com.odoo.addons.projects.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.addons.survey.models.SurveySurvey;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

import org.json.JSONArray;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class ProjectTask extends OModel {
    public static final String KEY = ProjectTask.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.projects.project_tasks";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn project_id = new OColumn("project_id", ProjectProject.class, OColumn.RelationType.ManyToOne);

    OColumn x_survey_id = new OColumn("x_survey_id",SurveySurvey.class,OColumn.RelationType.ManyToOne);
    //OColumn user_id = new OColumn("Assigned", ResUsers.class, OColumn.RelationType.ManyToOne);

    OColumn description = new OColumn("Description", OText.class);
    OColumn date_deadline = new OColumn("date_deadline", ODate.class);
    OColumn date_start = new OColumn("date_start", ODateTime.class);
    OColumn partner_id = new OColumn("partner_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    @Odoo.Functional(method = "storePartnerName", store = true, depends = {"partner_id"})
    OColumn partner_name = new OColumn("partner_name", OVarchar.class)
            .setLocalColumn();

    OColumn stage_id = new OColumn("stage_id", ProjectTaskType.class,OColumn.RelationType.ManyToOne);

    public ProjectTask(Context context, OUser user) {
        super(context, "project.task", user);
        setHasMailChatter(true);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    @Override
    public ODomain defaultDomain() {
        ODomain domain = new ODomain();
        domain.add("user_id", "=", getUser().getUserId());
        return domain;
    }

    public String storePartnerName(OValues values) {
        try {
            if (!values.getString("partner_id").equals("false")) {
                JSONArray partner_id = new JSONArray(values.getString("partner_id"));
                return partner_id.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

}
