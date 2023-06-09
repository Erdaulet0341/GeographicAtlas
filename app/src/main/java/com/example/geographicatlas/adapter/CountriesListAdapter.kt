package com.example.geographicatlas.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.geographicatlas.R
import com.example.geographicatlas.data.Country
import com.example.geographicatlas.databinding.CountriesListItemBinding
import com.example.geographicatlas.fragments.CountryDetails
import java.math.RoundingMode
import kotlin.math.ceil

class CountriesListAdapter(private val context: Context, private val fragmentManager: FragmentManager): RecyclerView.Adapter<CountriesListAdapter.CountryHolder>() {

    var newsList = ArrayList<Country>()

    fun setList(arr: ArrayList<Country>){
        this.newsList = arr

    }

    class CountryHolder(item: View): RecyclerView.ViewHolder(item){
        val binding = CountriesListItemBinding.bind(item)
        val expandableLayout = binding.expandableLayout
        val collapsed = binding.collapsed
        val constrLayoutTop = binding.constrLayoutTop
        val learnMoreBtn = binding.learnMoreText

        fun bind(country: Country){
            binding.nameOfCountry.text = country.name.common

            binding.capital.text =  country.capital?.get(0)

            if(country.capital?.get(0) == null){
                binding.capital.text =  "None capital"
            }

            if(country.area >= 1000000){
                val area = country.area / 1000000
                val roundedArea = area.toBigDecimal().setScale(3, RoundingMode.UP).toDouble()
                binding.areaList.text = "$roundedArea mln km\u00B2"
            }
            else if(country.area<1000000 && country.area>=1000){
                val last = country.area.toInt() % 1000
                val first = country.area.toInt() / 1000
                if(last.toString().length==2) binding.areaList.text = "$first 0$last km\u00B2"
                else if(last.toString().length==1) binding.areaList.text = "$first 00$last km\u00B2"
                else binding.areaList.text = "$first $last km\u00B2"
            }
            else{
                binding.areaList.text = "${country.area.toInt()} km\u00B2"
            }

            if(country.population >= 1000000){
                val population  = country.population.toDouble()/1000000
                val populationRounded = ceil(population)
                binding.populationList.text = "${populationRounded.toInt()} mln"
            }
            else if(country.population in 1001..999999){
                val last = country.population % 1000
                val first = country.population / 1000
                binding.populationList.text = "$first $last"
            }
            else{
                binding.populationList.text = country.population.toString()
            }

            val img = binding.imgList
            val url = country.flags.png
            Glide.with(img)
                .load(url)
                .placeholder(R.drawable.ic_baseline_access_time_24)
                .error(R.drawable.ic_baseline_hide_image_24)
                .fallback(R.drawable.ic_baseline_access_time_24)
                .into(img)


            var currencyText = ""
            if(country.currencies?.toString() != null && country.currencies.values?.toString() != null){
                country.currencies.values.forEachIndexed { index, element ->
                    val matchingKey = country.currencies.entries.find { it.value.name == element.name }?.key
                    if(country.currencies.values.size == 1){
                        currencyText += "${element.name} (${element.symbol ?: "None symbol"}) ($matchingKey) "
                    }
                    else if(index == country.currencies.values.size-1){
                        currencyText += "${element.name} (${element.symbol ?: "None symbol"}) ($matchingKey) "
                    }
                    else {
                        currencyText += "${element.name} (${element.symbol ?: "None symbol"}) ($matchingKey), "
                    }
                }
                binding.currencyList.text = currencyText
            }
            else{
                binding.currencyList.text = "None currency"
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.countries_list_item, parent, false)
        return CountryHolder(view)
    }

    override fun onBindViewHolder(holder: CountryHolder, position: Int) {
        val country = newsList[position]
        holder.bind(country)

        if(country.unMember){
            holder.expandableLayout.visibility = View.GONE
            holder.collapsed.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_expand_more_24))
        }
        else{
            holder.expandableLayout.visibility = View.VISIBLE
            holder.collapsed.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_expand_less_24))
        }

        holder.constrLayoutTop.setOnClickListener {
            country.unMember = !country.unMember
            notifyItemChanged(position)
        }

        holder.learnMoreBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("cca2", country.cca2)
            val fragment = CountryDetails()
            fragment.arguments = bundle
            fragmentManager?.beginTransaction()?.replace(R.id.fragmentList, fragment)?.commit()
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}