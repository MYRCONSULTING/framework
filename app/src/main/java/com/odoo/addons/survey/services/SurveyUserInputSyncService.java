package com.odoo.addons.survey.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.survey.models.SurveyPage;
import com.odoo.addons.survey.models.SurveyUserInput;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class SurveyUserInputSyncService extends OSyncService {

    public static final String TAG = SurveyUserInputSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(getApplicationContext(), SurveyUserInput.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(80);
    }

}
