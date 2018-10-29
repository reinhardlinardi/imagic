package com.imagic.imagic;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A class representing a special effect.
 */
class SpecialEffect implements JSONSerializable {

    /* Constants */
    private static final String EFFECT_NAME_KEY = "effectName";
    private static final String ALGORITHM_NAME_KEY = "algorithms";

    /* Properties */
    String effectName; // Effect name
    ArrayList<SpecialEffectAlgorithm> algorithms; // List of algorithms

    /* Methods */

    // Constructors
    SpecialEffect() {}

    SpecialEffect(String effectName, ArrayList<SpecialEffectAlgorithm> algorithms) {
        this.effectName = effectName;
        this.algorithms = algorithms;
    }

    // Serialization
    @Override
    public String jsonSerialize() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(EFFECT_NAME_KEY, effectName);
        jsonObject.put(ALGORITHM_NAME_KEY, JSONSerializer.arrayListSerialize(algorithms));

        return jsonObject.toString();
    }

    // Deserialization
    @Override
    public void jsonDeserialize(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);

        effectName = jsonObject.getString(EFFECT_NAME_KEY);
        algorithms = JSONSerializer.arrayListDeserialize(jsonObject.getString(ALGORITHM_NAME_KEY), SpecialEffectAlgorithm.class);
    }
}
