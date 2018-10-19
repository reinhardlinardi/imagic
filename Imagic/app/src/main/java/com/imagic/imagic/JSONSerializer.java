package com.imagic.imagic;

import android.content.Context;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * A helper class to serialize and deserialize object or ArrayList of object to / from JSON string.
 * Class of that object and all of its subclass(es) must implement JSONSerializable interface.
 */
class JSONSerializer {

    // Serialize an object and return JSON string of the object
    static String serialize(JSONSerializable serializable) throws Exception { return serializable.jsonSerialize(); }

    // Serialize ArrayList of objects and return JSON string of the ArrayList of object
    static String arrayListSerialize(ArrayList<JSONSerializable> serializableArrayList) throws Exception {
        JSONArray array = new JSONArray();
        for(JSONSerializable serializable : serializableArrayList) array.put(serializable.jsonSerialize());

        return array.toString();
    }

    // Deserialize a JSON string and return an object of the given class
    static <T extends JSONSerializable> T deserialize(Context context, String json, Class<T> type) throws Exception {
        T result = type.newInstance();
        result.jsonDeserialize(context, json);
        return result;
    }

    // Deserialize a JSON string and return an ArrayList of object of the given class
    static <T extends JSONSerializable> ArrayList<T> arrayListDeserialize(Context context, String json, Class<T> type) throws Exception {
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