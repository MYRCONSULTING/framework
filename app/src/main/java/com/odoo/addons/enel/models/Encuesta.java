package com.odoo.addons.enel.models;

import android.content.Context;
import android.net.Uri;

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

public class Encuesta extends OModel {
    public static final String KEY = Encuesta.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.enel.enel100k_enel100k";

    OColumn suministro = new OColumn("No. Suministro", OVarchar.class).setSize(100);
    OColumn nombres = new OColumn("Nombres", OVarchar.class).setSize(100);
    OColumn apellido_paterno = new OColumn("Apellido Paterno", OVarchar.class).setSize(100);
    OColumn apellido_materno = new OColumn("Apellido Materno", OVarchar.class).setSize(100);
    OColumn dni = new OColumn("No. DNI", OVarchar.class).setSize(100);
    OColumn telefono_fijo = new OColumn("Teléfono fijo", OVarchar.class).setSize(100);
    OColumn telefono_celular = new OColumn("Teléfono celular", OVarchar.class).setSize(100);
    OColumn email = new OColumn("Correo electrónico", OVarchar.class).setSize(100);
    OColumn edad = new OColumn("Edad", OInteger.class);
    OColumn distrito = new OColumn("Distrito", OVarchar.class).setSize(100);
    OColumn direccion = new OColumn("Dirección residencial", OVarchar.class).setSize(100);


    public Encuesta(Context context, OUser user) {
        super(context, "enel100k.enel100k", user);
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

    public String getSuministro(ODataRow row) {
        String add = row.getString("suministro");
        return add;
    }
}
