package com.odoo.addons.servicesorder.models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.OFloat;
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

    OColumn partner_delivery_id = new OColumn("partner_delivery_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn driver_id = new OColumn("driver_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn order_ref = new OColumn("No. de Pedido", OVarchar.class).setSize(100);
    OColumn date = new OColumn("Fecha", ODate.class);
    OColumn partner_delivery = new OColumn("Dirección Recojo", OVarchar.class).setSize(200);
    OColumn partner_id = new OColumn("partner_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    @Odoo.Functional(method = "storePartnerName", store = true, depends = {"partner_id"})
    OColumn partner_name = new OColumn("partner_name", OVarchar.class).setLocalColumn();
    OColumn last_state = new OColumn("Ultimo Evento", ServicesOrderEventType.class, OColumn.RelationType.ManyToOne);
    OColumn client_contact_name = new OColumn("client_contact_name", OVarchar.class).setSize(200);
    OColumn client_contact_phone = new OColumn("client_contact_phone", OVarchar.class).setSize(200);
    OColumn address_delivery = new OColumn("address_delivery", OVarchar.class).setSize(250);
    OColumn client_ubigeo = new OColumn("client_ubigeo", OVarchar.class).setSize(100);
    OColumn product_description = new OColumn("product_description", OVarchar.class).setSize(250);
    OColumn amount = new OColumn("product_description", OFloat.class);

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
        Log.i(TAG,  " Usuario X >> " + getUser().getUserId());
        Log.i(TAG,  " Usuario X >> " + getUser().getPartnerId());
        Log.i(TAG,  " Usuario Y >> " + getUser().getName());
        //domain.add("driver_id", "=", getUser().getPartnerId());
        return domain;
    }

    public String getOrder_Ref(ODataRow row) {
        String add = row.getString("order_ref");
        return add;
    }

    public String storePartnerName(OValues values) {
        String partner = "";
        try {
            if (!values.getString("partner_id").equals("false")) {
                //JSONArray partner_id = new JSONArray(values.getString("partner_id").toString().split(","));
                String partnerArray[]  = values.getString("partner_id").toString().split(",");

                if (partnerArray.length>0){
                    String xpartner = partnerArray[1].substring(0,partnerArray[1].length()-1).toString();
                    partner =  xpartner;
                }
                return partner;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

}
