package com.odoo.addons.sale.providers;

import com.odoo.addons.sale.models.ProductProduct;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class ProductProductProvider extends BaseModelProvider {
    public static final String TAG = ProductProductProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ProductProduct.AUTHORITY;
    }
}
