package com.getpet.utilities

import android.content.Context
import android.location.Geocoder
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import java.util.Locale

object LocationUtils {

    fun convertLocationToGeoPoint(context: Context, locationString: String): GeoPoint {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocationName(locationString, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val firstAddress = addresses[0]
                return GeoPoint(firstAddress.latitude, firstAddress.longitude)
            }


        } catch (e: IOException) {
            e.printStackTrace()

            // Handle exception
        }
        return GeoPoint(0.0, 0.0)

}
}

