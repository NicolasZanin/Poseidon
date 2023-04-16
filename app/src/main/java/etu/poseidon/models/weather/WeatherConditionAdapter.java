package etu.poseidon.models.weather;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class WeatherConditionAdapter extends TypeAdapter<WeatherCondition> {

    @Override
    public void write(JsonWriter out, WeatherCondition value) throws IOException {
        out.value(value.name());
    }

    @Override
    public WeatherCondition read(JsonReader in) throws IOException {
        String name = in.nextString();
        return WeatherCondition.valueOf(name);
    }
}
