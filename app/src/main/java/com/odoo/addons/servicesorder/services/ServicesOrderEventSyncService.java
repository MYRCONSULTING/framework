package com.odoo.addons.servicesorder.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.servicesorder.models.ServicesOrderEvent;
import com.odoo.addons.servicesorder.models.ServicesOrderEventType;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class ServicesOrderEventSyncService extends OSyncService {

    public static final String TAG = ServicesOrderEventSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(getApplicationContext(), ServicesOrderEvent.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(500);
    }


}
