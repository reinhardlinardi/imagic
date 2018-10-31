package com.imagic.imagic;

import org.json.JSONObject;

/**
 * A class representing an OCR method.
 */
class OCRMethod implements JSONSerializable {

    /* Constants */
    private static final String METHOD_KEY = "method";

    /* Properties */
    String method; // Method name

    /* Methods */

    // Constructors
    OCRMethod() {}

    OCRMethod(String method) { this.method = method; }

    @Override
    public String jsonSerialize() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(METHOD_KEY, method);
        return jsonObject.toString();
    }

    @Override
    public void jsonDeserialize(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        method = jsonObject.getString(METHOD_KEY);
    }
}
