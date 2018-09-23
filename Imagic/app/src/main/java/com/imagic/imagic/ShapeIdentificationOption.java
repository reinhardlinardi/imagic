package com.imagic.imagic;

import android.content.Context;

import org.json.JSONObject;

class ShapeIdentificationOption implements JSONSerializable {

    // Properties
    public String algorithm;

    @Override
    public String jsonSerialize() throws Exception {
        JSONObject optionJSON = new JSONObject();
        optionJSON.put("algorithm", algorithm);

        return optionJSON.toString();
    }

    @Override
    public void jsonDeserialize(Context context, String json) throws Exception {
        JSONObject optionJSON = new JSONObject(json);
        algorithm = optionJSON.getString("algorithm");
    }
}
