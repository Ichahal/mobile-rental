package com.hy.group3_project.views.properties


import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hy.group3_project.BaseActivity
import com.hy.group3_project.databinding.ActivityShowPropertyBinding
import com.hy.group3_project.models.adapters.PropertyAdapter

class ShowPropertyActivity : BaseActivity() {

    lateinit var binding: ActivityShowPropertyBinding

    // Mutable list to store properties
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set option menu
        setSupportActionBar(this.binding.tbOptionMenu)

        // Setup adapter
        adapter = PropertyAdapter(
            user!!.showList(),
            {pos-> addFav(pos) },
            {pos-> removeFav(pos)},
            {pos->viewRowDetail(pos)},
            isLandlord,
            isLogin,
            { redirectLogin() },
            user!!.showList()
        )

        // Setup RecyclerView
        binding.rvProperties.adapter = adapter
        binding.rvProperties.layoutManager = LinearLayoutManager(this)
        binding.rvProperties.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )


    }



    // Helper function to retrieve properties from SharedPreferences
    override fun onResume() {
        super.onResume()

        propertyList.clear()
        propertyList.addAll(user!!.showList())
        adapter.notifyDataSetChanged()

    }
}
