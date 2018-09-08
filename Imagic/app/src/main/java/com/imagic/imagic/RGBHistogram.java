package com.imagic.imagic;

import android.content.Context;

import org.json.JSONObject;

class RGBHistogram implements JSONSerializable {

    // Properties
    protected RedHistogram red;
    protected GreenHistogram green;
    protected BlueHistogram blue;

    // Constructor
    RGBHistogram() { resetHistogram(); }

    @Override
    public String jsonSerialize() throws Exception {
        JSONObject RGBHistogramJSON = new JSONObject();

        RGBHistogramJSON.put("red", new JSONObject(red.jsonSerialize()));
        RGBHistogramJSON.put("green", new JSONObject(green.jsonSerialize()));
        RGBHistogramJSON.put("blue", new JSONObject(blue.jsonSerialize()));

        return RGBHistogramJSON.toString();
    }

    @Override
    public void jsonDeserialize(Context context, String json) throws Exception {
        resetHistogram();
        JSONObject RGBHistogramJSON = new JSONObject(json);

        red.jsonDeserialize(context, RGBHistogramJSON.getJSONObject("red").toString());
        green.jsonDeserialize(context, RGBHistogramJSON.getJSONObject("green").toString());
        blue.jsonDeserialize(context, RGBHistogramJSON.getJSONObject("blue").toString());
    }

    // Enable value dependent color for all histogram
    void enableValueDependentColor() {
        red.enableValueDependentColor();
        green.enableValueDependentColor();
        blue.enableValueDependentColor();
    }

    // Is histogram data empty
    boolean isDataEmpty() { return red.isDataEmpty() || green.isDataEmpty() || blue.isDataEmpty(); }

    // Reset histogram
    void resetHistogram() {
        red = new RedHistogram();
        green = new GreenHistogram();
        blue = new BlueHistogram();
    }

    // Update series
    void updateSeries() {
        red.updateSeries();
        green.updateSeries();
        blue.updateSeries();
    }
}
