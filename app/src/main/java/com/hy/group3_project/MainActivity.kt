package com.hy.group3_project

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hy.group3_project.Adapters.PropertyAdapter
import com.hy.group3_project.Models.Property
import com.hy.group3_project.ViewActivities.Account.PropertyDetailActivity
import com.hy.group3_project.ViewActivities.BaseActivity
import com.hy.group3_project.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PropertyAdapter

    private var displayedProperties: List<Property> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set option menu
        setSupportActionBar(this.binding.tbOptionMenu)

        // Setup adapter


            adapter = PropertyAdapter(
                propertyDataSource,
                {pos-> addFav(pos) },
                {pos-> removeFav(pos)},
                {pos->viewRowDetail(pos)},
                isLandlord
            )

        // ----- data for recycle view
        binding.rvProperties.adapter = adapter
        binding.rvProperties.layoutManager = LinearLayoutManager(this)
        binding.rvProperties.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )


        // -- filter functionality
        val myPopup = MyPopup(this)
        binding.filterBtn.setOnClickListener(){
            // for popup
            MyPopup(this)
            myPopup.show()
        }

        // -- Search functionality

        binding.searchButton.setOnClickListener(){
            val searchText: String? = binding.searchText.text?.toString()?.trim()

            // Filter the original propertyDataSource based on the search text
            displayedProperties = propertyDataSource.filter { property ->
                property.propertyAddress?.contains(searchText ?: "", ignoreCase = true) == true
            }

            if (myPopup.isApplied) {
                // For functionality with filterSelected
                val filterConfig = myPopup.filterConfig
                val filteredWithConfig = displayedProperties.filter { property ->
                    // Apply additional filters based on the filterConfig
                    val propertyTypeMatch = filterConfig.propertyType?.equals(property.type, ignoreCase = true) ?: true
                    val bedsMatch = filterConfig.beds?.let { it == property.rooms } ?: true
                    val bathsMatch = filterConfig.baths?.let { it == property.bath } ?: true
                    val isPetFriendlyMatch = !filterConfig.isPetFriendly ?: property.isPetFriendly
                    val hasParkingMatch = !filterConfig.hasParking ?: property.hasParking

                    propertyTypeMatch && bedsMatch && bathsMatch && isPetFriendlyMatch && hasParkingMatch
                }

                // Update the adapter with the filtered list
                adapter.updatePropertyDataset(filteredWithConfig)
            }else {
                // for functionality without filterSelected
                adapter.updatePropertyDataset(displayedProperties)
            }


        }

    }

    override fun onResume() {
        super.onResume()

        val propertyListFromSP = sharedPreferences.getString("KEY_PROPERTY_DATASOURCE", "")

        if (propertyListFromSP != "") {
            val gson = Gson()
            val typeToken = object : TypeToken<List<Property>>() {}.type
            val propertiesList = gson.fromJson<List<Property>>(propertyListFromSP, typeToken)

            propertyDataSource.clear()
            propertyDataSource.addAll(propertiesList)
            adapter.notifyDataSetChanged()
        }
    }
}