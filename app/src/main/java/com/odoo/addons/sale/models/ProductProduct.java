/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p>
 * Created on 13/1/15 11:11 AM
 */
package com.odoo.addons.sale.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class ProductProduct extends OModel {
    public static final String TAG = ProductProduct.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.sale.product_product";
    OColumn name_template = new OColumn("Name Template", OVarchar.class).setSize(64);
    OColumn default_code = new OColumn("Internal Reference", OVarchar.class).setSize(64);
    OColumn lst_price = new OColumn("Public price", OFloat.class);
    OColumn sale_ok = new OColumn("Stock OK", OBoolean.class).setDefaultValue(false);
    //OColumn product_tmpl_id = new OColumn("Product Template", ProductTemplate.class,OColumn.RelationType.ManyToOne).setRequired();


    public ProductProduct(Context context, OUser user) {
        super(context, "product.product", user);
        setDefaultNameColumn("name_template");
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
