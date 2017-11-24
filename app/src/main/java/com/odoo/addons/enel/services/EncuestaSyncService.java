package com.odoo.addons.enel.services;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.odoo.addons.enel.models.Encuesta;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class EncuestaSyncService extends OSyncService {

    public static final String TAG = EncuestaSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(getApplicationContext(), Encuesta.class, this, true,36);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        Log.i(TAG, "Para por aquí Service created 1");
        //adapter.syncDataLimit(37);


    }


}
