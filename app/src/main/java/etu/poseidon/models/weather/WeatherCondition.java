package etu.poseidon.models.weather;

import android.os.Parcel;
import android.os.Parcelable;

public enum WeatherCondition implements Parcelable {
    SUN,
    CLOUD,
    THUNDERSTORM,
    WIND,
    STORM,
    RAIN;

    // Parcelable implementation
    public static final Creator<WeatherCondition> CREATOR = new Creator<WeatherCondition>() {
        @Override
        public WeatherCondition createFromParcel(Parcel in) {
            return WeatherCondition.values()[in.readInt()];
        }

        @Override
        public WeatherCondition[] newArray(int size) {
            return new WeatherCondition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ordinal());
    }
}
