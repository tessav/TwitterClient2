package com.codepath.apps.restclienttemplate.utils;

/**
 * Created by tessavoon on 10/6/17.
 */

public enum PaginationParamType {
    SINCE("since_id"),
    MAX("max_id");

    private String param;

    PaginationParamType(String param) {
        this.param = param;
    }

    public String param() {
        return param;
    }

}