package com.suning.snfddal.shards.vendor;

import java.io.Serializable;
import java.sql.SQLException;

import com.suning.snfddal.shards.ExceptionSorter;

public class InformixExceptionSorter implements ExceptionSorter, Serializable {

    private static final long serialVersionUID = -5175884111768095263L;

    public boolean isExceptionFatal(SQLException e) {
        switch (e.getErrorCode()) {
            case -710: // Table has been dropped, altered or renamed JBAS-3120
            case -79716: // System or internal error
            case -79730: // Connection noit established
            case -79734: // INFORMIXSERVER has to be specified
            case -79735: // Can't instantiate protocol
            case -79736: // No connection/statement established yet
            case -79756: // Invalid connection URL
            case -79757: // Invalid subprotocol
            case -79758: // Invalid IP address
            case -79759: // Invalid port nnumber
            case -79760: // Invalid database name
            case -79788: // User name must be specified
            case -79811: // Connection without user/password not supported
            case -79812: // User/password does not match with datasource
            case -79836: // Proxy error: no database connection
            case -79837: // Proxy error: IO error
            case -79879: // Unexpected exception

                return true;
        }

        return false;
    }
    

}
