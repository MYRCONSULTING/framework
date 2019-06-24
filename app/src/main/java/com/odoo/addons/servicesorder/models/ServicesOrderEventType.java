package com.odoo.addons.servicesorder.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class ServicesOrderEventType extends OModel {
    public static final String KEY = ServicesOrderEventType.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.servicesorder.services_order_event_type";

    OColumn name = new OColumn("Nombre", OVarchar.class).setSize(100);
    OColumn decoration = new OColumn("Decoracion", OSelection.class)
            .addSelection("danger","Rojo")
            .addSelection("success","Verde")
            .addSelection("info","Azul")
            .addSelection("it","Italic")
            .addSelection("warning","Amarillo")
            .addSelection("bf","Bold");



    public ServicesOrderEventType(Context context, OUser user) {
        super(context, "events.type", user);
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


}
