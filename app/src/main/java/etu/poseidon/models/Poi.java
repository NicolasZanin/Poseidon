package etu.poseidon.models;

import com.google.gson.annotations.SerializedName;

import etu.poseidon.models.weather.WeatherCondition;

public class Poi {

    @SerializedName("_id")
    private String id;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("weather")
    private WeatherCondition weatherCondition;
    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("finished")
    private boolean isFinished;
    @SerializedName("perimeter")
    private double perimeter;

    public Poi() {
    }

    public Poi(String id, double latitude, double longitude, WeatherCondition weatherCondition, String createdAt, String updatedAt, boolean isFinished, double perimeter) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.weatherCondition = weatherCondition;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isFinished = isFinished;
        this.perimeter = perimeter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(WeatherCondition weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(double perimeter) {
        this.perimeter = perimeter;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Poi{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", weatherCondition='" + weatherCondition + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", isFinished=" + isFinished +
                ", perimeter=" + perimeter +
                '}';
    }
}
