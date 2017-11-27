package com.odoo.addons.alfalaval.providers;

import com.odoo.addons.alfalaval.models.Vibracionregular;
import com.odoo.addons.enel.models.Encuesta;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class VibracionregularProvider extends BaseModelProvider {
    public static final String TAG = VibracionregularProvider.class.getSimpleName();

    @Override
    public String authority() {
        return Vibracionregular.AUTHORITY;
    }

}
