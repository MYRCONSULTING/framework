package com.odoo.addons.sale.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.sale.models.ProductProduct;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class ProductProductSyncService extends OSyncService {

    public static final String TAG = ProductProductSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(getApplicationContext(), ProductProduct.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(1000);
    }

}
