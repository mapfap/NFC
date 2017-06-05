package com.mapfap.tap;

/**
 * Created by mapfap on 6/5/2017 AD.
 */

class APIResponse {

    public boolean isError;
    public String errorDetails;

    public boolean isNfcRegistered;
    public String employeeId;
    public String employeeName;
    public String employeeDepartment;
    public boolean employeeIsPreRegistered;

    public String activeEvent;
    public boolean activeEventExists;

    public boolean isEmployeeFound;

    public String timestamp;

    public APIResponse() {

    }


}
