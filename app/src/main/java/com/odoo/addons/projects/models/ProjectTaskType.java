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
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

import org.json.JSONArray;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class ProjectTaskType extends OModel {
    public static final String KEY = ProjectTaskType.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.projects.project_task_type";
    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn description = new OColumn("Description", OText.class);
    OColumn sequence = new OColumn("sequence", OInteger.class);
    OColumn project_ids = new OColumn("project_ids",ProjectTask.class, OColumn.RelationType.ManyToMany);


    public ProjectTaskType(Context context, OUser user) {
        super(context, "project.task.type", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }


}
