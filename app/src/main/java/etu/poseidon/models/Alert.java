package etu.poseidon.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import etu.poseidon.models.weather.WeatherCondition;

public class Alert implements Parcelable {

    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("listWeather")
    private List<WeatherCondition> listWeatherCondition;
    @SerializedName("creatorEmail")
    private String creatorEmail;
    @SerializedName("creatorFullname")
    private String creatorFullname;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("enabled")
    private Boolean enabled;

    @SerializedName("perimeter")
    private double perimeter;

    public Alert() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

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

    public List<WeatherCondition> getListWeatherCondition() {
        return listWeatherCondition;
    }

    public void setListWeatherCondition(List<WeatherCondition> listWeatherCondition) {
        this.listWeatherCondition = listWeatherCondition;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getCreatorFullname() {
        return creatorFullname;
    }

    public void setCreatorFullname(String creatorFullname) {
        this.creatorFullname = creatorFullname;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(double perimeter) {
        this.perimeter = perimeter;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled='" + enabled + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", listWeatherCondition=" + listWeatherCondition +
                ", creatorEmail='" + creatorEmail + '\'' +
                ", creatorFullname='" + creatorFullname + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }

    protected Alert(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        listWeatherCondition = in.createTypedArrayList(WeatherCondition.CREATOR);
        creatorEmail = in.readString();
        creatorFullname = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeTypedList(listWeatherCondition);
        dest.writeString(creatorEmail);
        dest.writeString(creatorFullname);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel in) {
            return new Alert(in);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };
}