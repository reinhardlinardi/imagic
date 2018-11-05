package com.imagic.imagic;

import org.json.JSONObject;

/**
 * A class representing a menu.
 */
class Menu implements JSONSerializable {

    /* Constants */

    private static final String MENU_KEY = "menu";
    private static final String FRAGMENT_CLASS_KEY = "fragmentClass";

    /* Properties */

    String menu; // Name of menu
    String fragmentClass; // Name of fragment class associated with this menu

    /* Methods */

    // Constructors
    Menu() {}

    Menu(String menu, String fragmentClass) {
        this.menu = menu;
        this.fragmentClass = fragmentClass;
    }

    // Serialization
    @Override
    public String jsonSerialize() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(MENU_KEY, menu);
        jsonObject.put(FRAGMENT_CLASS_KEY, fragmentClass);

        return jsonObject.toString();
    }

    // Deserialization
    @Override
    public void jsonDeserialize(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);

        menu = jsonObject.getString(MENU_KEY);
        fragmentClass = jsonObject.getString(FRAGMENT_CLASS_KEY);
    }
}
