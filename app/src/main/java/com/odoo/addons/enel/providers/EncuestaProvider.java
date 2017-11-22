package com.odoo.addons.enel.providers;

import com.odoo.addons.enel.models.Encuesta;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class EncuestaProvider extends BaseModelProvider {
    public static final String TAG = EncuestaProvider.class.getSimpleName();

    @Override
    public String authority() {
        return Encuesta.AUTHORITY;
    }

}
