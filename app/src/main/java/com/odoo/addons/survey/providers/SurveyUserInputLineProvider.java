package com.odoo.addons.survey.providers;
import com.odoo.addons.survey.models.SurveyUserInput;
import com.odoo.addons.survey.models.SurveyUserInputLine;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyUserInputLineProvider extends BaseModelProvider {
    public static final String TAG = SurveyUserInputLineProvider.class.getSimpleName();

    @Override
    public String authority() {
        return SurveyUserInputLine.AUTHORITY;
    }
}
