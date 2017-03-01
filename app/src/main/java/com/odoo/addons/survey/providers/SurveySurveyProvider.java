package com.odoo.addons.survey.providers;
import com.odoo.addons.survey.models.SurveySurvey;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveySurveyProvider extends BaseModelProvider {
    public static final String TAG = SurveySurveyProvider.class.getSimpleName();

    @Override
    public String authority() {
        return SurveySurvey.AUTHORITY;
    }
}
