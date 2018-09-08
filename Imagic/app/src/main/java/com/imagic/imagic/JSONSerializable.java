package com.imagic.imagic;

import android.content.Context;

interface JSONSerializable {

    // Serialize
    String jsonSerialize() throws Exception;

    // Deserialize
    void jsonDeserialize(Context context, String json) throws Exception;
}
