package com.hy.group3_project.views.properties


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.hy.group3_project.BaseActivity
import com.hy.group3_project.R
import com.hy.group3_project.controllers.properties.PropertyRepository
import com.hy.group3_project.databinding.ActivityPropertyDetailBinding
import com.hy.group3_project.models.enums.Roles
import com.hy.group3_project.models.properties.Property
import kotlinx.coroutines.launch
import java.text.NumberFormat


class PropertyDetailActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityPropertyDetailBinding
    private var mMap: GoogleMap? = null
    private var selectedProperty: Property? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.propertyRepository = PropertyRepository(applicationContext)
//set option menu


        //set option menu

        setSupportActionBar(this.binding.tbOptionMenu)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val propertyId = intent.getStringExtra("PROPERTY_ID")!!
        lifecycleScope.launch {
            selectedProperty = propertyRepository.findProperty(propertyId)!!

            if (selectedProperty != null) {
                if (user != null &&
                    user?.role == Roles.Landlord.toString() &&
                    user!!.showList().contains(selectedProperty?.id)
                ) {
                    // Do something with the extra item, e.g., make a button invisible
                    binding.btnUpdate.visibility = View.VISIBLE
                    binding.btnDelete.visibility = View.VISIBLE
                }

                val formattedPrice =
                    NumberFormat.getCurrencyInstance().format(selectedProperty!!.price)
                binding.propertyPrice.text = formattedPrice
                binding.propertyType.text = selectedProperty!!.type
                binding.bedText.text = selectedProperty!!.beds
                binding.bathText.text = selectedProperty!!.baths
                binding.petFriendlyText.text =
                    if (!selectedProperty!!.petFriendly) "Pets" else "No Pets"
                binding.parkingText.text =
                    if (!selectedProperty!!.canParking) "Parking" else "No Parking"
                binding.addressCity.text =
                    "${selectedProperty!!.address}, ${selectedProperty!!.city}"
                binding.propertyDescription.text = selectedProperty!!.desc
                binding.contactInfo.text = "Contact: ${selectedProperty!!.contactInfo}"


                if (selectedProperty!!.availability) {
                    binding.availability.text = "Available"
                } else {
                    binding.availability.text = "Not available"
                }
                addMarker(selectedProperty!!)
            }
        }


        binding.btnUpdate.setOnClickListener {

            val propertyUpdateIntent =
                Intent(this@PropertyDetailActivity, UpdatePropertyActivity::class.java)

            propertyUpdateIntent.putExtra("PROPERTY_ID", propertyId)

            startActivity(propertyUpdateIntent)
        }

        binding.btnDelete.setOnClickListener {
            val propertyId = intent.getStringExtra("PROPERTY_ID")!!
            lifecycleScope.launch {
                userRepository.removeUserListWithPropertyId(propertyId)
                propertyRepository.deleteProperty(propertyId)
                val gson = Gson()
                val userJson = gson.toJson(user)
                prefEditor.putString("KEY_USER", userJson)
                prefEditor.apply()
                loadUserData()
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun addMarker(property: Property) {
        if (mMap != null) {
            val address = getAddress(property.address)
            val latLng = address?.let { LatLng(it.latitude, it.longitude) }

            if (latLng != null) {
                Log.d(TAG, "Add marker $latLng")
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
                mMap!!.addMarker(MarkerOptions().position(latLng).title(property.address))
            } else {
                Log.e(TAG, "Failed to get valid LatLng for the address")
            }
        } else {
            // Handle the case when mMap is null
            Log.e(TAG, "Google Map is not available")
            // You can display a message to the user or perform any other action here
        }
    }
}


