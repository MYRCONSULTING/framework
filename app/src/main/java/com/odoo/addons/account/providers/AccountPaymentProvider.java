package com.odoo.addons.account.providers;
import com.odoo.addons.account.models.AccountInvoice;
import com.odoo.addons.account.models.AccountPayment;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Ricardo Livelli on 09/02/2017.
 */

public class AccountPaymentProvider extends BaseModelProvider {
    public static final String TAG = AccountPaymentProvider.class.getSimpleName();

    @Override
    public String authority() {
        return AccountPayment.AUTHORITY;
    }
}
