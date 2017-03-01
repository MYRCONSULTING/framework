package com.odoo.addons.survey.providers;
import com.odoo.addons.survey.models.SurveyQuestion;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyQuestionProvider extends BaseModelProvider {
    public static final String TAG = SurveyQuestionProvider.class.getSimpleName();

    @Override
    public String authority() {
        return SurveyQuestion.AUTHORITY;
    }
}
