package com.odoo.addons.projects.models;

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

public class ProjectProject extends OModel {
    public static final String KEY = ProjectProject.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.projects.project_project";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn state = new OColumn("state", OSelection.class)
            .addSelection("open","open")
            .addSelection("close","close")
            .addSelection("pending","pending")
            .addSelection("cancelled","cancelled");
    public ProjectProject(Context context, OUser user) {
        super(context, "project.project", user);
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
}
