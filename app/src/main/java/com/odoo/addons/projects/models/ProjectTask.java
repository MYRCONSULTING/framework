package com.odoo.addons.projects.models;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.odoo.R;
import com.odoo.addons.projects.Tasks;
import com.odoo.addons.survey.models.SurveyPage;
import com.odoo.addons.survey.models.SurveySurvey;
import com.odoo.addons.survey.models.SurveyUserInput;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.account.OdooAccountQuickManage;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.notification.ONotificationBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class ProjectTask extends OModel {
    public static final String KEY = ProjectTask.class.getSimpleName();
    public static final String AUTHORITY = "com.odoo.addons.projects.project_tasks";
    private int REQUEST_SIGN_IN_ERROR = 0 ;

    OColumn code = new OColumn("code", OVarchar.class).setSize(100);
    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);
    OColumn project_id = new OColumn("project_id", ProjectProject.class, OColumn.RelationType.ManyToOne);

    OColumn x_survey_id = new OColumn("x_survey_id",SurveySurvey.class,OColumn.RelationType.ManyToOne);
    OColumn description = new OColumn("Description", OText.class);
    OColumn date_deadline = new OColumn("date_deadline", ODate.class);
    OColumn date_start = new OColumn("date_start", ODateTime.class);
    OColumn date_end = new OColumn("date_end", ODateTime.class);
    OColumn color = new OColumn("color",OInteger.class);
    OColumn partner_id = new OColumn("partner_id", ResPartner.class, OColumn.RelationType.ManyToOne);
    @Odoo.Functional(method = "storePartnerName", store = true, depends = {"partner_id"})
    OColumn partner_name = new OColumn("partner_name", OVarchar.class)
            .setLocalColumn();

    OColumn stage_id = new OColumn("stage_id", ProjectTaskType.class,OColumn.RelationType.ManyToOne);

    OColumn priority = new OColumn("priority", OSelection.class)
            .addSelection("0","Baja")
            .addSelection("1","Normal")
            .addSelection("2","Alta");

    OColumn x_recursive = new OColumn("x_recursive", OBoolean.class);
    OColumn x_create_source = new OColumn("x_create_source", OBoolean.class);

    public ProjectTask(Context context, OUser user) {
        super(context, "project.task", user);
        setHasMailChatter(true);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    @Override
    public boolean allowDeleteRecordOnServer(){
        return false;
    }


    @Override
    public void onSyncFinished(){
        ProjectTaskType projectTaskType = new ProjectTaskType(getContext(),null);
        int stage_id_In_Preparation =  projectTaskType.getCodProjectTaskType_Id(TypeTask.IN_PREPARATION.getValue()); //9 En Preparación
        int stage_id_Pending = projectTaskType.getCodProjectTaskType_Id(TypeTask.PENDING.getValue());//10 Pendiente de envio de campo.
        int stage_id_On_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.ON_FIELD.getValue());//11 En Campo
        int stage_id_Return_From_Field = projectTaskType.getCodProjectTaskType_Id(TypeTask.RETURNED_FROM_FIELD.getValue());//12 Retornada de campo
        int stage_id_Cancel = projectTaskType.getCodProjectTaskType_Id(TypeTask.CANCEL.getValue());//13 Cancelada

        ODomain domain = new ODomain();
        ProjectTask projectTask = new ProjectTask(getContext(),null);
        List<ODataRow> rowProjectTaskType = projectTask.select(null,"stage_id = ?",new String[]{String.valueOf(stage_id_Pending)},"id asc");
        for (int i=0; i<rowProjectTaskType.size(); i++) {
            OValues valuesProjectTask = new OValues();
            valuesProjectTask.put("stage_id",stage_id_On_Field);
            projectTask.update(rowProjectTaskType.get(i).getInt(OColumn.ROW_ID),valuesProjectTask);
        }

        //Clean Cancel
        String type = String.valueOf(stage_id_Cancel);
        //projectTask.delete("stage_id = ? and x_recursive = ?",new String[]{type,"false"},true);
        projectTask.delete("stage_id = ? ",new String[]{type},true);
        //showTaskNotification();
    }



    @Override
    public ODomain defaultDomain() {
        /* En este filtro se llama al metodo que devuelve los codigos del servidor*/
        ProjectTaskType projectTaskType = new ProjectTaskType(getContext(),null);
        int stage_id_In_Preparation =  projectTaskType.getCodProjectTaskTypefromServer(TypeTask.IN_PREPARATION.getValue()); //9 En Preparación
        int stage_id_Pending = projectTaskType.getCodProjectTaskTypefromServer(TypeTask.PENDING.getValue());//10 Pendiente de envio de campo.
        int stage_id_On_Field = projectTaskType.getCodProjectTaskTypefromServer(TypeTask.ON_FIELD.getValue());//11 En Campo
        int stage_id_Return_From_Field = projectTaskType.getCodProjectTaskTypefromServer(TypeTask.RETURNED_FROM_FIELD.getValue());//12 Retornada de campo
        int stage_id_Cancel = projectTaskType.getCodProjectTaskTypefromServer(TypeTask.CANCEL.getValue());//13 Cancelada
        ODomain domain = new ODomain();
        domain.add("&");
        domain.add("user_id", "=", getUser().getUserId());
        //domain.add("|");
        //domain.add("|");
        domain.add("|");
        domain.add("|");
        //domain.add("stage_id", "=", projectTaskType.getCodProjectTaskType(TypeTask.IN_PREPARATION.getValue())); //9 En Preparación
        domain.add("stage_id", "=", stage_id_Pending);//10 Pendiente de envio de campo.
        domain.add("stage_id", "=", stage_id_On_Field);//11 En Campo
        //domain.add("stage_id", "=", projectTaskType.getCodProjectTaskType(TypeTask.RETURNED_FROM_FIELD.getValue()));//12 Retornada de campo
        domain.add("stage_id", "=", stage_id_Cancel);//13 Cancelada
        return domain;
    }


    public String storePartnerName(OValues values) {
        try {
            if (!values.getString("partner_id").equals("false")) {
                JSONArray partner_id = new JSONArray(values.getString("partner_id"));
                return partner_id.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    private void showSignInErrorNotification() {
        ONotificationBuilder builder = new ONotificationBuilder(getContext(),
                REQUEST_SIGN_IN_ERROR);
        builder.setTitle(OResource.string(getContext(),R.string.toast_information_saved));
        builder.setBigText("May be you have changed your account " +
                "password or your account does not exists");
        builder.setIcon(R.drawable.ic_action_camera);
        builder.setText(getUser().getAndroidName());
        builder.allowVibrate(true);
        builder.withRingTone(true);
        builder.setOngoing(true);
        builder.withLargeIcon(false);
        builder.setColor(OResource.color(getContext(), R.color.android_orange_dark));
        Bundle extra = getUser().getAsBundle();
        // Actions
        ONotificationBuilder.NotificationAction actionReset = new ONotificationBuilder.NotificationAction(
                R.drawable.ic_action_suppliers,
                "Reset",
                110,
                "reset_password",
                OdooAccountQuickManage.class,
                extra
        );
        ONotificationBuilder.NotificationAction deleteAccount = new ONotificationBuilder.NotificationAction(
                R.drawable.ic_action_navigation_close,
                "Remove",
                111,
                "remove_account",
                OdooAccountQuickManage.class,
                extra
        );
        builder.addAction(actionReset);
        builder.addAction(deleteAccount);
        builder.build().show();
    }
    private void showTaskNotification() {
        ONotificationBuilder builder = new ONotificationBuilder(getContext(),REQUEST_SIGN_IN_ERROR);
        builder.setTitle(OResource.string(getContext(),R.string.title_notification));
        builder.setBigText(OResource.string(getContext(),R.string.title_task_notification));
        builder.setIcon(R.drawable.ic_system_update_black_24dp);
        builder.setText(getUser().getAndroidName());
        builder.allowVibrate(true);
        builder.withRingTone(true);
        builder.setOngoing(true);
        builder.withLargeIcon(false);
        builder.setColor(OResource.color(getContext(), R.color.android_orange_dark));
        Bundle extra = getUser().getAsBundle();
        // Actions
        ONotificationBuilder.NotificationAction actionVerify = new ONotificationBuilder.NotificationAction(
                R.drawable.ic_action_notes_content,
                OResource.string(getContext(),R.string.notificacion_verifid_tasks),
                110,
                "verify_task",
                Tasks.class,
                extra
        );

        builder.addAction(actionVerify);
        builder.build().show();
    }

    public static List<Integer> getProjectTaskOnField(Context context) {
        ProjectTask projectTask = new ProjectTask(context,null);
        List<Integer> rowProjectTask = projectTask.getServerIds();
        return rowProjectTask;
    }


}
