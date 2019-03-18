package com.odoo.addons.servicesorder.providers;

import com.odoo.addons.enel.models.Encuesta;
import com.odoo.addons.servicesorder.models.ServicesOrder;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class ServicesOrderProvider extends BaseModelProvider {
    public static final String TAG = ServicesOrderProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ServicesOrder.AUTHORITY;
    }

}
