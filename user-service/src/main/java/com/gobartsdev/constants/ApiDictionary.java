package com.gobartsdev.constants;

import java.lang.reflect.Field;
import java.util.List;

public interface ApiDictionary {

    String API_V1 = "/api/v1";
    String LOGIN = "/login";
    String REGISTER = "/register";
    String REFRESH_TOKEN = "/refresh-token";
    String VERIFY = "/verify";
    String ADMIN = "/admin";
    String USERS = "/users";
    String ROLES = "/roles";

    static List<String> getOpenApis(){
        return List.of(
                API_V1 + LOGIN,
                API_V1 + REGISTER,
                API_V1 + REFRESH_TOKEN,
                API_V1 + VERIFY
        );
    }
}
