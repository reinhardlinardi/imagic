package com.imagic.imagic;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A class representing a special effect.
 */
class SpecialEffect implements JSONSerializable {

    /* Constants */
    private static final String EFFECT_KEY = "effect";
    private static final String ALGORITHM_KEY = "algorithms";

    /* Properties */
    String effect; // Effect name
    ArrayList<SpecialEffectAlgorithm> algorithms; // List of algorithms

    /* Methods */

    // Constructors
    SpecialEffect() {}

    SpecialEffect(String effect, ArrayList<SpecialEffectAlgorithm> algorithms) {
        this.effect = effect;
        this.algorithms = algorithms;
    }

    // Serialization
    @Override
    public String jsonSerialize() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(EFFECT_KEY, effect);
        jsonObject.put(ALGORITHM_KEY, JSONSerializer.arrayListSerialize(algorithms));

        return jsonObject.toString();
    }

    // Deserialization
    @Override
    public void jsonDeserialize(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);

        effect = jsonObject.getString(EFFECT_KEY);
        algorithms = JSONSerializer.arrayListDeserialize(jsonObject.getString(ALGORITHM_KEY), SpecialEffectAlgorithm.class);
    }
}
