package com.imagic.imagic;

import android.content.Context;

import org.json.JSONObject;

class MenuOption implements JSONSerializable {

    // Properties
    public String title;
    public String description;
    public String activityOnClick;

    // Constructors
    MenuOption() {}

    @Override
    public String jsonSerialize() throws Exception {
        JSONObject optionJSON = new JSONObject();

        optionJSON.put("title", title);
        optionJSON.put("description", description);
        optionJSON.put("activityOnClick", activityOnClick);

        return optionJSON.toString();
    }

    @Override
    public void jsonDeserialize(Context context, String json) throws Exception {
        JSONObject optionJSON = new JSONObject(json);

        title = optionJSON.getString("title");
        description = optionJSON.getString("description");
        activityOnClick = optionJSON.getString("activityOnClick");
    }
}
