package com.odoo.addons.survey.providers;
import com.odoo.addons.survey.models.SurveyLabel;
import com.odoo.addons.survey.models.SurveyQuestion;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyLabelProvider extends BaseModelProvider {
    public static final String TAG = SurveyLabelProvider.class.getSimpleName();

    @Override
    public String authority() {
        return SurveyLabel.AUTHORITY;
    }
}
