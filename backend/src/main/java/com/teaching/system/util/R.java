package com.teaching.system.util;

import java.util.HashMap;

public class R extends HashMap<String, Object> {

    public static R ok() {
        R r = new R();
        r.put("code", 200);
        r.put("message", "ok");
        return r;
    }

    public static R ok(Object data) {
        R r = ok();
        r.put("data", data);
        return r;
    }

    public static R error(int code, String message) {
        R r = new R();
        r.put("code", code);
        r.put("message", message);
        return r;
    }

    public static R error(String message) {
        return error(500, message);
    }
}
