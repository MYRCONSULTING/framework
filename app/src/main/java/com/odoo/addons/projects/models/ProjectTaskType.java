package com.odoo.addons.projects.models;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.odoo.addons.survey.models.SurveySurvey;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

import org.json.JSONArray;

import java.util.List;

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
    OColumn x_task_type = new OColumn("x_task_type", OSelection.class)
            .addSelection("0","IN PREPARATION")
            .addSelection("1","PENDING")
            .addSelection("2","ON FIELD")
            .addSelection("3","RETURNED FROM FIELD")
            .addSelection("4","CANCEL")
            .addSelection("5","OTHER");


    public ProjectTaskType(Context context, OUser user) {
        super(context, "project.task.type", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public int getCodProjectTaskType(int val) {
        int rpta = -1;
        if (val!= -1) {
            ProjectTaskType projectTaskType = new ProjectTaskType(getContext(),null);
            List<ODataRow> rowProjectTaskType = projectTaskType.select(null,"x_task_type = ?",new String[]{String.valueOf(val)},"id asc");
            if (!rowProjectTaskType.isEmpty()){
                rpta = rowProjectTaskType.get(0).getInt("id");
            }
            return rpta;
        }
        return -1;
    }

    public int getCodProjectTaskType_Id(int val) {
        int rpta = -1;
        if (val!= -1) {
            ProjectTaskType projectTaskType = new ProjectTaskType(getContext(),null);
            List<ODataRow> rowProjectTaskType = projectTaskType.select(null,"x_task_type = ?",new String[]{String.valueOf(val)},"id asc");
            if (!rowProjectTaskType.isEmpty()){
                rpta = rowProjectTaskType.get(0).getInt("_id");
            }
            return rpta;
        }
        return -1;
    }


}
