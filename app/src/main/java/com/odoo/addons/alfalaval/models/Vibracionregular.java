package com.odoo.addons.alfalaval.models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class Vibracionregular extends OModel {
    public static final String KEY = Vibracionregular.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.alfalaval.vibracionregular";

    OColumn cliente = new OColumn("Datos del cliente", OVarchar.class).setSize(12).setRequired();
    OColumn codigocliente = new OColumn("Código del cliente", OVarchar.class).setSize(12).setRequired();
    OColumn numeroos = new OColumn("OS No", OInteger.class).setSize(10).setRequired();
    OColumn equipo = new OColumn("Equipo", OVarchar.class).setSize(12).setRequired();
    OColumn prodn = new OColumn("Prod No", OVarchar.class).setSize(12).setRequired();
    OColumn flujo = new OColumn("Flujo m3/h", OVarchar.class).setSize(12).setRequired();
    OColumn precision = new OColumn("Precisión", OVarchar.class).setSize(12).setRequired();

    //Mediciones Regulares
    //vacio mm
    OColumn mrvaciomm1 = new OColumn("Medición regular 1 En vacío .mm/s", OVarchar.class).setSize(12);
    OColumn mrvaciomm2 = new OColumn("Medición regular 2 En vacío .mm/s", OVarchar.class).setSize(12);
    OColumn mrvaciomm3 = new OColumn("Medición regular 3 En vacío .mm/s", OVarchar.class).setSize(12);
    OColumn mrvaciomm4 = new OColumn("Medición regular 4 En vacío .mm/s", OVarchar.class).setSize(12);
    OColumn mrvaciomm5 = new OColumn("Medición regular 5 En vacío .mm/s", OVarchar.class).setSize(12);
    OColumn mrvaciomm6 = new OColumn("Medición regular 6 En vacío .mm/s", OVarchar.class).setSize(12);

    //vacio ge
    OColumn mrvacioge1 = new OColumn("Medición regular 1 En vacío .gE", OVarchar.class).setSize(12);
    OColumn mrvacioge2 = new OColumn("Medición regular 2 En vacío .gE", OVarchar.class).setSize(12);
    OColumn mrvacioge3 = new OColumn("Medición regular 3 En vacío .gE", OVarchar.class).setSize(12);
    OColumn mrvacioge4 = new OColumn("Medición regular 4 En vacío .gE", OVarchar.class).setSize(12);
    OColumn mrvacioge5 = new OColumn("Medición regular 5 En vacío .gE", OVarchar.class).setSize(12);
    OColumn mrvacioge6 = new OColumn("Medición regular 6 En vacío .gE", OVarchar.class).setSize(12);

    //agua mm
    OColumn mraguamm1 = new OColumn("Medición regular 1 En agua .mm/s", OVarchar.class).setSize(12);
    OColumn mraguamm2 = new OColumn("Medición regular 2 En agua .mm/s", OVarchar.class).setSize(12);
    OColumn mraguamm3 = new OColumn("Medición regular 3 En agua .mm/s", OVarchar.class).setSize(12);
    OColumn mraguamm4 = new OColumn("Medición regular 4 En agua .mm/s", OVarchar.class).setSize(12);
    OColumn mraguamm5 = new OColumn("Medición regular 5 En agua .mm/s", OVarchar.class).setSize(12);
    OColumn mraguamm6 = new OColumn("Medición regular 6 En agua .mm/s", OVarchar.class).setSize(12);

    //agua gE
    OColumn mraguage1 = new OColumn("Medición regular 1 En agua .gE", OVarchar.class).setSize(12);
    OColumn mraguage2 = new OColumn("Medición regular 2 En agua .gE", OVarchar.class).setSize(12);
    OColumn mraguage3 = new OColumn("Medición regular 3 En agua .gE", OVarchar.class).setSize(12);
    OColumn mraguage4 = new OColumn("Medición regular 4 En agua .gE", OVarchar.class).setSize(12);
    OColumn mraguage5 = new OColumn("Medición regular 5 En agua .gE", OVarchar.class).setSize(12);
    OColumn mraguage6 = new OColumn("Medición regular 6 En agua .gE", OVarchar.class).setSize(12);

    //carga mm
    OColumn mrcargamm1 = new OColumn("Medición regular 1 En carga .mm/s", OVarchar.class).setSize(12);
    OColumn mrcargamm2 = new OColumn("Medición regular 2 En carga .mm/s", OVarchar.class).setSize(12);
    OColumn mrcargamm3 = new OColumn("Medición regular 3 En carga .mm/s", OVarchar.class).setSize(12);
    OColumn mrcargamm4 = new OColumn("Medición regular 4 En carga .mm/s", OVarchar.class).setSize(12);
    OColumn mrcargamm5 = new OColumn("Medición regular 5 En carga .mm/s", OVarchar.class).setSize(12);
    OColumn mrcargamm6 = new OColumn("Medición regular 6 En carga .mm/s", OVarchar.class).setSize(12);

    //carga gE
    OColumn mrcargage1 = new OColumn("Medición regular 1 En carga .gE", OVarchar.class).setSize(12);
    OColumn mrcargage2 = new OColumn("Medición regular 2 En carga .gE", OVarchar.class).setSize(12);
    OColumn mrcargage3 = new OColumn("Medición regular 3 En carga .gE", OVarchar.class).setSize(12);
    OColumn mrcargage4 = new OColumn("Medición regular 4 En carga .gE", OVarchar.class).setSize(12);
    OColumn mrcargage5 = new OColumn("Medición regular 5 En carga .gE", OVarchar.class).setSize(12);
    OColumn mrcargage6 = new OColumn("Medición regular 6 En carga .gE", OVarchar.class).setSize(12);

    OColumn observacion = new OColumn("Observación", OText.class).setSize(100);
    OColumn valormaximo = new OColumn("Valor máximo medido en mm/s", OVarchar.class).setSize(12).setRequired();

    OColumn firmafieldservice = new OColumn("Field Service Manager", OBlob.class).setDefaultValue(false);
    OColumn firmacliente = new OColumn("Cliente", OBlob.class).setDefaultValue(false);



    public Vibracionregular(Context context, OUser user) {
        super(context, "vibracionregular", user);
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

    public String getCodigocliente(ODataRow row) {
        String add = row.getString("codigocliente");
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
        Vibracionregular  vibracionregular  = new Vibracionregular(getContext(),null);
        vibracionregular.delete("id > ? ",new String[]{type},true);
        Log.e(TAG, "Limpio 2" );

    }


}
