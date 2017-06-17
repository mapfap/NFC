package com.mapfap.tap;

/**
 * Created by mapfap on 6/5/2017 AD.
 */

class APIResponse {

    public UserState userState;
    public boolean isError = true;
    public String errorDetails;

    public boolean isNfcRegistered;
    public String employeeId;
    public String employeeName;
    public String employeeDepartment;
    public boolean employeeIsPreRegistered;
    public boolean isDuplicateTap;

    public String activeEvent;
    public boolean activeEventExists;

    public boolean isEmployeeFound;

//    public String timestamp;

    public APIResponse() {

    }


    public void copy(APIResponse o) {
        this.isError = o.isError;
        this.errorDetails = o.errorDetails;
        this.isNfcRegistered = o.isNfcRegistered;
        this.employeeId = o.employeeId;
        this.employeeName = o.employeeName;
        this.employeeDepartment = o.employeeDepartment;
        this.employeeIsPreRegistered = o.employeeIsPreRegistered;
        this.activeEvent = o.activeEvent;
        this.activeEventExists = o.activeEventExists;
        this.isEmployeeFound = o.isEmployeeFound;
        this.isDuplicateTap = o.isDuplicateTap;
    }
}
