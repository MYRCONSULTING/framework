package com.odoo.addons.projects.providers;
import com.odoo.addons.projects.models.ProjectProject;
import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class ProjectProjectProvider extends BaseModelProvider {
    public static final String TAG = ProjectProjectProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ProjectProject.AUTHORITY;
    }
}
