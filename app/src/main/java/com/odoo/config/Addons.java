/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 30/12/14 3:11 PM
 */
package com.odoo.config;

import com.odoo.addons.account.AccountInvoice;
import com.odoo.addons.account.AccountPayment;
import com.odoo.addons.customers.Customers;
import com.odoo.addons.projects.Project;
import com.odoo.addons.projects.Tasks;
import com.odoo.core.support.addons.AddonsHelper;
import com.odoo.core.support.addons.OAddon;
import com.odoo.addons.enel.*;

public class Addons extends AddonsHelper {

    /**
     * Declare your required module here
     * NOTE: For maintain sequence use object name in asc order.
     * Ex.:
     * OAddon partners = new OAddon(Partners.class).setDefault();
     * for maintain sequence call withSequence(int sequence)
     * OAddon partners = new OAddon(Partners.class).withSequence(2);
     */
/*
    OAddon tasks = new OAddon(Tasks.class).setDefault();
    OAddon project = new OAddon(Project.class);
    OAddon customers = new OAddon(Customers.class);
    OAddon account = new OAddon(AccountInvoice.class);
    OAddon payment = new OAddon(AccountPayment.class);
*/
    OAddon encuesta = new OAddon(EncuestaF.class).setDefault();
    //OAddon surveysurvey = new OAddon(SurveySurvey.class);
    //OAddon surveypage = new OAddon(SurveyPage.class);
    //OAddon surveyquestion = new OAddon(SurveyQuestion.class);
    //OAddon survey = new OAddon(Project.class);
}
