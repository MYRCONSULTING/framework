package com.odoo.addons.servicesorder.providers;

import com.odoo.addons.servicesorder.models.ServicesOrderEvent;
import com.odoo.addons.servicesorder.models.ServicesOrderEventType;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class ServicesOrderEventProvider extends BaseModelProvider {
    public static final String TAG = ServicesOrderEventProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ServicesOrderEvent.AUTHORITY;
    }

}
