package com.dicoding.android.zsstoryapp.view.maps

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.dicoding.android.zsstoryapp.R
import com.dicoding.android.zsstoryapp.databinding.ActivityMapsBinding
import com.dicoding.android.zsstoryapp.utils.AppConstants
import com.dicoding.android.zsstoryapp.view.main.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var lStoryMap: ArrayList<LatLng>? = null
    private var lStoryMapName: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.title = "ZS Story User - Maps View"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        lStoryMap = intent.getParcelableArrayListExtra(AppConstants.EXTRA_MAP)
        lStoryMapName = intent.getStringArrayListExtra(AppConstants.EXTRA_MAP_NAME)

        for (i in lStoryMap!!.indices) {
            mMap.addMarker(
                MarkerOptions()
                    .position(lStoryMap!![i])
                    .title(lStoryMapName!![i])
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lStoryMap!![i], 15f))
        }

        // Custom
        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("New Marker")
                    .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
                    .icon(vectorToBitmap(R.drawable.ic_pin, Color.parseColor("#3DDC84")))
            )
        }

        // POI
        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }
        setMapStyle()
    }

    // Custom Pin
    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // Custom Map Style
    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    // Option Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        val map = menu.findItem(R.id.menu_maps)
        map.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_localization -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menu_list -> {
                Intent(this@MapsActivity, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}