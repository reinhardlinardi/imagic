package com.imagic.imagic;

import android.content.Context;

import org.json.JSONArray;

import java.util.ArrayList;

class JSONSerializer {

    // Serialize single object
    static String serialize(JSONSerializable serializable) throws Exception { return serializable.jsonSerialize(); }

    // Serialize array of objects
    static String arraySerialize(ArrayList<JSONSerializable> serializableArrayList) throws Exception {
        JSONArray array = new JSONArray();
        for(JSONSerializable serializable : serializableArrayList) array.put(serializable.jsonSerialize());

        return array.toString();
    }

    // Deserialize single object or array of objects
    static <T extends JSONSerializable> T deserialize(Context context, String json, Class<T> type) throws Exception {
        T result = type.newInstance();
        result.jsonDeserialize(context, json);
        return result;
    }

    // Deserialize array of objects
    static <T extends JSONSerializable> ArrayList<T> arrayDeserialize(Context context, String json, Class<T> type) throws Exception {
        JSONArray serializableArrayList = new JSONArray(json);
        ArrayList<T> resultArrayList = new ArrayList<>();

        for(int idx = 0; idx < serializableArrayList.length(); idx++) {
            T result = type.newInstance();
            result.jsonDeserialize(context, serializableArrayList.getString(idx));
            resultArrayList.add(result);
        }

        return resultArrayList;
    }
}
