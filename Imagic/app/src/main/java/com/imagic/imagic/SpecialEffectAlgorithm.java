package com.imagic.imagic;

import org.json.JSONObject;

/**
 * A class representing a special effect algorithm.
 */
class SpecialEffectAlgorithm implements JSONSerializable {

    /* Constants */
    private static final String ALGORITHM_NAME_KEY = "algorithmName";

    /* Properties */
    String algorithmName; // Algorithm name

    /* Methods */

    // Constructors
    SpecialEffectAlgorithm() {}

    SpecialEffectAlgorithm(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    // Serialization
    @Override
    public String jsonSerialize() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(ALGORITHM_NAME_KEY, algorithmName);
        return jsonObject.toString();
    }

    // Deserialization
    @Override
    public void jsonDeserialize(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        algorithmName = jsonObject.getString(ALGORITHM_NAME_KEY);
    }
}
