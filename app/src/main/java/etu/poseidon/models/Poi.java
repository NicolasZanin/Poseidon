package etu.poseidon.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import etu.poseidon.models.weather.WeatherCondition;

public class Poi implements Parcelable {

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
    @SerializedName("creatorEmail")
    private String creatorEmail;
    @SerializedName("creatorFullname")
    private String creatorFullname;

    public Poi() {
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
                ", creatorEmail='" + creatorEmail + '\'' +
                ", creatorFullname='" + creatorFullname + '\'' +
                '}';
    }

    protected Poi(Parcel in) {
        id = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        weatherCondition = in.readParcelable(WeatherCondition.class.getClassLoader());
        createdAt = in.readString();
        updatedAt = in.readString();
        isFinished = in.readByte() != 0;
        perimeter = in.readDouble();
        creatorEmail = in.readString();
        creatorFullname = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(weatherCondition, flags);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeByte((byte) (isFinished ? 1 : 0));
        dest.writeDouble(perimeter);
        dest.writeString(creatorEmail);
        dest.writeString(creatorFullname);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Poi> CREATOR = new Creator<Poi>() {
        @Override
        public Poi createFromParcel(Parcel in) {
            return new Poi(in);
        }

        @Override
        public Poi[] newArray(int size) {
            return new Poi[size];
        }
    };
}