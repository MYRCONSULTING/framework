package com.odoo.addons.survey.providers;
import com.odoo.addons.survey.models.SurveyPage;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyPageProvider extends BaseModelProvider {
    public static final String TAG = SurveyPageProvider.class.getSimpleName();

    @Override
    public String authority() {
        return SurveyPage.AUTHORITY;
    }
}
