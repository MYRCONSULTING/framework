package com.odoo.addons.servicesorder.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class ServicesOrder extends OModel {
    public static final String KEY = ServicesOrder.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.servicesorder.services_order";

    OColumn name = new OColumn("No. OS", OVarchar.class).setSize(100);
    OColumn partner_id = new OColumn("partner_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn partner_delivery_id = new OColumn("partner_delivery_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn order_ref = new OColumn("No. de Pedido", OVarchar.class).setSize(100);



    public ServicesOrder(Context context, OUser user) {
        super(context, "services.order", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    @Override
    public ODomain defaultDomain() {
        ODomain domain = new ODomain();
        //domain.add("user_id", "=", getUser().getUserId());
        return domain;
    }

    public String getOrder_Ref(ODataRow row) {
        String add = row.getString("order_ref");
        return add;
    }
}
