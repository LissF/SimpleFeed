package ua.org.tenletters.simplefeed;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

public final class Utils {

    private static final String TAG = "Utils";

    private Utils() {
        // Empty
    }

    public static boolean isInternetAvailable(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Logging
    ///////////////////////////////////////////////////////////////////////////

    public static void logV(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void logD(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void logI(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void logE(final String tag, final String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void logE(final String tag, final String message, final Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Null-safe unboxing and comparing
    ///////////////////////////////////////////////////////////////////////////

    public static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public static double unbox(@Nullable final Double d) {
        return d == null ? 0d : d;
    }

    public static int unbox(@Nullable final Integer integer) {
        return integer == null ? 0 : integer;
    }

    public static boolean unbox(@Nullable final Boolean bool) {
        return bool == null ? false : bool;
    }
}