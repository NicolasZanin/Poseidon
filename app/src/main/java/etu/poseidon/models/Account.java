package etu.poseidon.models;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

// The only aim of this class is to be able to provide a demo account.
// In real production, we would not use this class. We would only used the GoogleSignInAccount class for example.
// (We had to do a demo account because if you want to test the application, you will not be able to use
// your Google account (we need to register your Android Studio SHA-1 key to the Google API Console, as we are in development phase for Google)).
public class Account {
    private static GoogleSignInAccount googleSignInAccount;
    private static boolean isLoggedIn = false;

    public static String getEmail(){
        if(googleSignInAccount != null){
            return googleSignInAccount.getEmail();
        }
        return "demo@gmail.com";
    }

    public static String getDisplayName(){
        if(googleSignInAccount != null){
            return googleSignInAccount.getDisplayName();
        }
        return "Demo Account";
    }

    public static void logIn(GoogleSignInAccount account){
        isLoggedIn = true;
        googleSignInAccount = account;
    }

    public static void logIn(){
        isLoggedIn = true;
    }

    public static void logOut(GoogleSignInClient mGoogleSignInClient){
        if(googleSignInAccount != null) mGoogleSignInClient.signOut();
        isLoggedIn = false;
        googleSignInAccount = null;
    }

    public static boolean isLoggedIn(){
        return isLoggedIn;
    }
}
