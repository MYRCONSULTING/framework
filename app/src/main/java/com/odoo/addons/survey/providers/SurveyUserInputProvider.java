package com.odoo.addons.survey.providers;
import com.odoo.addons.survey.models.SurveySurvey;
import com.odoo.addons.survey.models.SurveyUserInput;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyUserInputProvider extends BaseModelProvider {
    public static final String TAG = SurveyUserInputProvider.class.getSimpleName();

    @Override
    public String authority() {
        return SurveyUserInput.AUTHORITY;
    }
}
