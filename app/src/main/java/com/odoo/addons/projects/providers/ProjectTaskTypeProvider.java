package com.odoo.addons.projects.providers;
import com.odoo.addons.projects.models.ProjectTask;
import com.odoo.addons.projects.models.ProjectTaskType;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class ProjectTaskTypeProvider extends BaseModelProvider {
    public static final String TAG = ProjectTaskTypeProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ProjectTaskType.AUTHORITY;
    }
}
