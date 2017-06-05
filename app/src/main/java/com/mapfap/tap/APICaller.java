package com.mapfap.tap;

/**
 * Created by mapfap on 6/5/2017 AD.
 */

class APICaller {

    public APICaller() {

    }

    public APIResponse sendCardTap(String nfcId) {
        APIResponse response = new APIResponse();
        response.isError = false;
        response.isNfcRegistered = false;
        response.employeeId = "1234";
        response.employeeName = "Sarun W.";
        response.employeeDepartment = "IT";
        response.employeeIsPreRegistered = true;
        response.timestamp = "30/12/2017 08:32:26";
        return response;
    }

    public APIResponse sendManualTap(String employeeId) {
        APIResponse response = new APIResponse();
        response.isError = false;

        response.isEmployeeFound = true;
        response.employeeId = "1234";
        response.employeeName = "Sarun W.";
        response.employeeDepartment = "IT";
        response.employeeIsPreRegistered = true;
        response.timestamp = "30/12/2017 08:32:26";
        return response;
    }

    public APIResponse findEmployeeByEmployeeId(String employeeId) {
        APIResponse response = new APIResponse();
        response.isError = false;
        response.isEmployeeFound = false;
        response.employeeId = "1234";
        response.employeeName = "Sarun W.";
        response.employeeDepartment = "IT";
        return response;
    }

    public APIResponse registerEmployeeCard(String nfcId, String employeeId) {
        APIResponse response = new APIResponse();
        response.isError = false;
        response.isNfcRegistered = true;
        response.employeeId = "1234";
        response.employeeName = "Sarun W.";
        response.employeeDepartment = "IT";
        response.employeeIsPreRegistered = true;
        return response;
    }

    public APIResponse getActiveEvent() {
        APIResponse response = new APIResponse();
        response.isError = false;
        response.activeEvent = "New Hire Orientation";
        response.activeEventExists = true;
        return response;
    }
}
