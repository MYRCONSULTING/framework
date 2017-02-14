package com.odoo.addons.projects.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class ProjectProject extends OModel {
    public static final String KEY = ProjectProject.class.getSimpleName();
    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    public ProjectProject(Context context, OUser user) {
        super(context, "project.project", user);
    }
}
