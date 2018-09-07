package com.imagic.imagic;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class JSONSerializer {

    // Serialize single object
    static String serialize(JSONSerializable serializable) throws Exception { return serializable.jsonSerialize(); }

    // Serialize array of objects
    static String serialize(ArrayList<JSONSerializable> serializableArrayList) throws Exception {
        JSONArray array = new JSONArray();
        for(JSONSerializable serializable : serializableArrayList) array.put(serializable.jsonSerialize());

        return array.toString();
    }

    // Deserialize single object
    static void deserialize(Activity activity, String json, JSONSerializable serializable) throws Exception { serializable.jsonDeserialize(activity,json); }

    // Deserialize array of objects
    static void deserialize(Activity activity, String json, ArrayList<JSONSerializable> serializableArrayList) throws Exception {
        JSONArray array = new JSONArray(json);
        for(int idx = 0; idx < array.length(); idx++) serializableArrayList.add((JSONSerializable) array.get(idx));
    }
}
