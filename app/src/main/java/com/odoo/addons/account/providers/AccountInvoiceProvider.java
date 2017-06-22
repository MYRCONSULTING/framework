package com.odoo.addons.account.providers;
import com.odoo.addons.account.models.AccountInvoice;
import com.odoo.addons.projects.models.ProjectProject;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class AccountInvoiceProvider extends BaseModelProvider {
    public static final String TAG = AccountInvoiceProvider.class.getSimpleName();

    @Override
    public String authority() {
        return AccountInvoice.AUTHORITY;
    }
}
