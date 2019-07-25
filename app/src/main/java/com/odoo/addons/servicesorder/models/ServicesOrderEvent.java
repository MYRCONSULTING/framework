package com.odoo.addons.servicesorder.models;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Ricardo Livelli on 20/11/2017.
 */

public class ServicesOrderEvent extends OModel {
    public static final String KEY = ServicesOrderEvent.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.servicesorder.services_order_event";

    OColumn comment = new OColumn("Observaciones", OVarchar.class).setSize(100);
    OColumn display_name = new OColumn("display_name", OVarchar.class).setSize(100);
    OColumn state = new OColumn("Tipo de Evento", ServicesOrderEventType.class,OColumn.RelationType.ManyToOne);
    OColumn os_id = new OColumn("Orden de servicio", ServicesOrder.class,OColumn.RelationType.ManyToOne);
    //OColumn x_attachment_ids = new OColumn("Attachments", IrAttachment.class,OColumn.RelationType.ManyToMany);
    //OColumn x_date_create_user = new OColumn("date_create_user", OVarchar.class).setDefaultValue(ODateUtils.getUTCDate());
    OColumn decoration = new OColumn("Decoracion", OSelection.class)
            .addSelection("danger","Rojo")
            .addSelection("success","Verde")
            .addSelection("info","Azul")
            .addSelection("it","Italic")
            .addSelection("warning","Amarillo")
            .addSelection("bf","Bold");



    public ServicesOrderEvent(Context context, OUser user) {
        super(context, "services.order.events", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    @Override
    public ODomain defaultDomain() {
        /*
        List<Integer> list = new ArrayList<Integer>();
        ResPartner resPartner = new ResPartner(getContext(), null);
        int idUser = resPartner.selectRowId(getUser().getPartnerId());
        ODataRow row = resPartner.browse(new String[]{"driver"}, "id = ?", new String[]{String.valueOf(getUser().getPartnerId())});
        ServicesOrder servicesOrder = new ServicesOrder(getContext(), null);
        if (row.get("driver") == "true") { //chofer
            List<ODataRow> listOS_ids = servicesOrder.select(new String[]{"id"},"driver_id = ?",new String[]{String.valueOf(idUser)},"id asc");
            if (!listOS_ids.isEmpty()){
                ListIterator<ODataRow> iterator = listOS_ids.listIterator();
                // Printing the iterated value
                System.out.println("\nUsing ListIterator Chofer:\n");
                Log.i(TAG,  " Iter X1 >> " );
                while (iterator.hasNext()) {
                    int xid = iterator.next().getInt("id");
                    Log.i(TAG,  " Iter X2 >> " + xid );
                    list.add(xid);

                }
                Log.i(TAG,  " Usuario XXX4 >> " + getUser().getUserId());
                Log.i(TAG,  " Usuario XXX4777777 >> " + list);
            }
        }else{ //cliente
            List<ODataRow> listOS_ids = servicesOrder.select();
            if (!listOS_ids.isEmpty()){
                ListIterator<ODataRow> iterator = listOS_ids.listIterator();
                // Printing the iterated value
                System.out.println("\nUsing ListIterator Cliente:\n");
                Log.i(TAG,  " Iter X1 >> " );
                while (iterator.hasNext()) {
                    int xid = iterator.next().getInt("id");
                    Log.i(TAG,  " Iter X2 >> " + xid );
                    list.add(xid);

                }
                Log.i(TAG,  " Usuario XXX4 >> " + getUser().getUserId());
                Log.i(TAG,  " Usuario XXX4777777 >> " + list);
            }

        }
        */
        List<Integer> list = new ArrayList<Integer>();
        ServicesOrder servicesOrder = new ServicesOrder(getContext(), null);
        List<ODataRow> listOS_ids = servicesOrder.select();
        if (!listOS_ids.isEmpty()){
            ListIterator<ODataRow> iterator = listOS_ids.listIterator();
            // Printing the iterated value
            System.out.println("\nUsing ListIterator Cliente:\n");
            Log.i(TAG,  " Iter X1 >> " );
            while (iterator.hasNext()) {
                //int xid = iterator.next().getInt("id");
                int xid = iterator.next().getInt("_id");
                Log.i(TAG,  " Iter X2 >> " + xid );
                list.add(xid);

            }
            Log.i(TAG,  " Usuario XXX4 >> " + getUser().getUserId());
            Log.i(TAG,  " Usuario XXX4777777 >> " + list);
        }



        ODomain domain = new ODomain();

        ServicesOrderEventType servicesOrderEventType = new ServicesOrderEventType(getContext(), null);
        int stage_id_In_Preparation = servicesOrderEventType.getCodEventType_Id("RUTA");
        //domain.add("state","=",stage_id_In_Preparation);

        //domain.add("|");
        //domain.add("|");
        //domain.add("os_id", "in", list);
        //domain.add("os_id", "=", 2);
        //domain.add("os_id", "=", 3);
        return domain;
    }



    public String getOrder_Ref(ODataRow row) {
        String add = row.getString("os_id");
        return add;
    }

    @Override
    public boolean allowCreateRecordOnServer() {
        return true;
    }


}
