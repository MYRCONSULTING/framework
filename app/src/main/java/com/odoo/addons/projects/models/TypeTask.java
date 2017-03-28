package com.odoo.addons.projects.models;

/**
 * Created by Ricardo Livelli on 27/03/2017.
 */

public enum TypeTask {
    IN_PREPARATION,PENDING,ON_FIELD,RETURNED_FROM_FIELD,CANCEL,OTHER;

    public int getValue() {
        switch (this) {
            case IN_PREPARATION:
                return 0;
            case PENDING:
                return 1;
            case ON_FIELD:
                return 2;
            case RETURNED_FROM_FIELD:
                return 3;
            case CANCEL:
                return 4;
            case OTHER:
                return 5;
            default:
                return -1;
        }
    }
}
