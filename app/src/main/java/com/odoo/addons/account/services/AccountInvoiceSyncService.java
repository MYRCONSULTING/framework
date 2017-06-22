package com.odoo.addons.account.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.account.models.AccountInvoice;
import com.odoo.addons.projects.models.ProjectProject;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class AccountInvoiceSyncService extends OSyncService {

    public static final String TAG = AccountInvoiceSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(getApplicationContext(), AccountInvoice.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(80);
    }

}
