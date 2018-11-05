package com.imagic.imagic;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class representing a convolution kernel.
 */
class ConvolutionKernel implements JSONSerializable {

    /* Constants */

    private static final String NAME_KEY = "name";
    private static final String KERNEL_KEY = "kernel";

    /* Properties */

    String name; // Author
    double[][][] kernel; // Kernel

    /* Methods */

    // Constructors
    ConvolutionKernel() {}

    ConvolutionKernel(String name, double[][][] kernel) {
        this.name = name;
        this.kernel = kernel;
    }

    @Override
    public String jsonSerialize() throws Exception {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(NAME_KEY, name);
        jsonObject.put(KERNEL_KEY, kernel);

        return jsonObject.toString();
    }

    @Override
    public void jsonDeserialize(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        name = jsonObject.getString(NAME_KEY);

        JSONArray layers = jsonObject.getJSONArray(KERNEL_KEY);
        kernel = new double[layers.length()][][];

        for(int layer = 0; layer < layers.length(); layer++) {
            JSONArray rows = layers.getJSONArray(layer);
            kernel[layer] = new double[rows.length()][];

            for(int row = 0; row < rows.length(); row++) {
                JSONArray cols = rows.getJSONArray(row);
                kernel[layer][row] = new double[cols.length()];

                for(int col = 0; col < cols.length(); col++) kernel[layer][row][col] = cols.getDouble(col);
            }
        }
    }
}
