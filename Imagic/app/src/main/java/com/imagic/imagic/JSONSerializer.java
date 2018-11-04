package com.imagic.imagic;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * A helper class to serialize and deserialize object or ArrayList of object to / from JSON string.
 * Class of that object must implement JSONSerializable interface.
 */
class JSONSerializer {

    // Serialize an object and return JSON string of the object
    static <T extends JSONSerializable> String serialize(T serializable) throws Exception { return serializable.jsonSerialize(); }

    // Serialize ArrayList of objects and return JSON string of the ArrayList of object
    static <T extends JSONSerializable> String arrayListSerialize(ArrayList<T> serializableArrayList) throws Exception {
        JSONArray array = new JSONArray();
        for(JSONSerializable serializable : serializableArrayList) array.put(serializable.jsonSerialize());

        return array.toString();
    }

    // Deserialize a JSON string and return an object of the given class
    static <T extends JSONSerializable> T deserialize(String json, Class<T> type) throws Exception {
        T result = type.newInstance();
        result.jsonDeserialize(json);
        return result;
    }

    // Deserialize a JSON string and return an ArrayList of object of the given class
    static <T extends JSONSerializable> ArrayList<T> arrayListDeserialize(String json, Class<T> type) throws Exception {
        JSONArray serializableArrayList = new JSONArray(json);
        ArrayList<T> resultArrayList = new ArrayList<>();

        for(int idx = 0; idx < serializableArrayList.length(); idx++) {
            T result = type.newInstance();
            result.jsonDeserialize(serializableArrayList.getString(idx));
            resultArrayList.add(result);
        }

        return resultArrayList;
    }
}