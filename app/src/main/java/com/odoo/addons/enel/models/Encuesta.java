package com.odoo.addons.enel.models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.odoo.base.addons.res.ResPartner;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

import odoo.controls.OField;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class Encuesta extends OModel {
    public static final String KEY = Encuesta.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.enel.enel100k_enel100k";

    OColumn suministro = new OColumn("No. Suministro", OVarchar.class).setSize(14).setRequired();
    OColumn nombres = new OColumn("Nombres", OVarchar.class).setSize(100).setRequired();
    OColumn apellido_paterno = new OColumn("Apellido Paterno", OVarchar.class).setSize(100).setRequired();
    OColumn apellido_materno = new OColumn("Apellido Materno", OVarchar.class).setSize(100).setRequired();
    OColumn dni = new OColumn("No. DNI", OInteger.class).setSize(8).setRequired();
    OColumn telefono_fijo = new OColumn("Teléfono fijo", OInteger.class).setSize(7).setRequired();
    OColumn telefono_celular = new OColumn("Teléfono celular", OInteger.class).setSize(9).setRequired();
    OColumn email = new OColumn("Correo electrónico", OVarchar.class).setSize(100).setRequired();
    OColumn edad = new OColumn("Edad", OInteger.class).setSize(2).setRequired();
    OColumn distrito = new OColumn("Distrito", OVarchar.class).setSize(100).setRequired();
    OColumn direccion = new OColumn("Dirección residencial", OVarchar.class).setSize(100).setRequired();


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
        domain.add("id", "=", 0); // Modelo Bottom - Top Sirve solo para recolectar información
        return domain;
    }

    public String getSuministro(ODataRow row) {
        String add = row.getString("suministro");
        return add;
    }

    @Override
    public boolean allowDeleteRecordOnServer(){
        return false;
    }

    @Override
    public void onSyncFinished(){

        Log.e(TAG, "Limpio 1" );

        int id = 0;
        String type = String.valueOf(id);
        Encuesta encuesta = new Encuesta(getContext(),null);
        encuesta.delete("id > ? ",new String[]{type},true);
        Log.e(TAG, "Limpio 2" );

    }

}
