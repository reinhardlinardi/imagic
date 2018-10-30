package com.imagic.imagic;

import org.json.JSONObject;

/**
 * A class representing a special effect algorithm.
 */
class SpecialEffectAlgorithm implements JSONSerializable {

    /* Constants */
    private static final String ALGORITHM_KEY = "algorithm";

    /* Properties */
    String algorithm; // Algorithm name

    /* Methods */

    // Constructors
    SpecialEffectAlgorithm() {}

    SpecialEffectAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    // Serialization
    @Override
    public String jsonSerialize() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(ALGORITHM_KEY, algorithm);
        return jsonObject.toString();
    }

    // Deserialization
    @Override
    public void jsonDeserialize(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        algorithm = jsonObject.getString(ALGORITHM_KEY);
    }
}
