package com.imagic.imagic;

/**
 * An interface to ensure all class implemented this interface can be serialized and deserialized by JSONSerializer.
 * Every class implemented this interface must provide a parameterless constructor for the purpose of deserialization.
 */
interface JSONSerializable {

    // Serialize method
    String jsonSerialize() throws Exception;

    // Deserialize method
    void jsonDeserialize(String json) throws Exception;
}