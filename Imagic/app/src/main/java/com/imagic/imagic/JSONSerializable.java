package com.imagic.imagic;

import android.app.Activity;

interface JSONSerializable {

    // Serialize
    String jsonSerialize() throws Exception;

    // Deserialize
    void jsonDeserialize(Activity activity, String json) throws Exception;
}
