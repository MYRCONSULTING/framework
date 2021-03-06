package com.odoo.addons.account.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class AccountInvoice extends OModel {
    public static final String KEY = AccountInvoice.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.account.account_invoice";

    OColumn number = new OColumn("Number", OVarchar.class).setSize(100);
    OColumn partner_id = new OColumn("partner_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn amount_total = new OColumn("Number", OFloat.class).setSize(100);
    OColumn state = new OColumn("state", OSelection.class)
            .addSelection("draft","draft")
            .addSelection("proforma","proforma")
            .addSelection("proforma2","proforma")
            .addSelection("open","open")
            .addSelection("paid","paid")
            .addSelection("cancel","cancel");
    OColumn residual = new OColumn("Number", OFloat.class).setSize(100);
    @Odoo.Functional(method = "storePartnerName", store = true, depends = {"partner_id"})
    OColumn partner_name = new OColumn("partner_name", OVarchar.class)
            .setLocalColumn();

    public AccountInvoice(Context context, OUser user) {
        super(context, "account.invoice", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    @Override
    public ODomain defaultDomain() {
        ODomain domain = new ODomain();
        domain.add("user_id", "=", getUser().getUserId());
        return domain;
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
