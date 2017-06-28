package com.mapfap.tap;

/**
 * Created by mapfap on 6/8/2017 AD.
 */

public class APIWrapper {

    public boolean error = true;
    public String message;

    public boolean nfc_registered;
    public String code;
    public String name;
    public String department;
    public boolean pre_registered;

//    public String activeEvent;
    public boolean active_event_exists;
    public String event_name;

    public boolean employee_found;

    public boolean duplicate;
//    public String timestamp;

    public String active_event_check_mode;

    public  APIWrapper() {

    }

    public APIResponse toAPIResponse() {
        APIResponse response = new APIResponse();
        response.isError = error;
        response.errorDetails = message;
        response.isNfcRegistered = nfc_registered;
        response.employeeId = code;
        response.employeeName = name;
        response.employeeDepartment = department;
        response.employeeIsPreRegistered = pre_registered;
        response.activeEventExists = active_event_exists;
        response.isEmployeeFound = employee_found;
        response.activeEvent = event_name;
        response.isDuplicateTap = duplicate;
        response.activeEventCheckMode = active_event_check_mode;
        return response;
    }
}
