package com.imagic.imagic;

import org.json.JSONObject;

/**
 * A class representing a menu.
 */
class Menu implements JSONSerializable {

    /* Constants */

    private static final String MENU_NAME_KEY = "menuName";
    private static final String FRAGMENT_CLASS_NAME_KEY = "fragmentClassName";

    /* Properties */

    String menuName; // Name of menu
    String fragmentClassName; // Name of fragment class associated with this menu

    /* Methods */

    // Constructors
    Menu() {}

    Menu(String menuName, String fragmentClassName) {
        this.menuName = menuName;
        this.fragmentClassName = fragmentClassName;
    }

    // Serialization
    @Override
    public String jsonSerialize() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(MENU_NAME_KEY, menuName);
        jsonObject.put(FRAGMENT_CLASS_NAME_KEY, fragmentClassName);

        return jsonObject.toString();
    }

    // Deserialization
    @Override
    public void jsonDeserialize(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);

        menuName = jsonObject.getString(MENU_NAME_KEY);
        fragmentClassName = jsonObject.getString(FRAGMENT_CLASS_NAME_KEY);
    }
}
