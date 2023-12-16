package com.vearad.vearatick.fgmSub

import ApiService
import BottomMarginItemDecoration
import CustomBottomMarginItemDecoration
import CustomTopMarginItemDecoration
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.vearad.vearatick.ACCESSTOKEN
import com.vearad.vearatick.DataBase.EmployeeDao
import com.vearad.vearatick.KEYACCESSTOKEN
import com.vearad.vearatick.LOGINSTEP24
import com.vearad.vearatick.SHAREDLOGINSTEP24
import com.vearad.vearatick.adapter.CompanyEventAdapter
import com.vearad.vearatick.databinding.FragmentCompanyEventBinding
import com.vearad.vearatick.model.Events
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.MalformedURLException


class CompanyEventFragment : Fragment(), CompanyEventAdapter.CompanyEventEvent {

    lateinit var binding: FragmentCompanyEventBinding
    lateinit var companyEventAdapter: CompanyEventAdapter
    lateinit var companyEmployeeResumeDao: EmployeeDao
    lateinit var companyEventData: List<Events.Event>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyEventBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddNewEvent.setOnClickListener {
            var createEventUrl = "https://step24.ir/events/create"
            try {
                val modifiedUrl = Uri.parse(createEventUrl)
                    .buildUpon()
                    .appendQueryParameter("appOrigin", "android")
                    .build()

                val intent = Intent(Intent.ACTION_VIEW, modifiedUrl)
                startActivity(intent)
            } catch (e: MalformedURLException) {
                // Handle URL exception
            } catch (e: IOException) {
                // Handle connection exception
            }
        }

        val sharedPreferencesAccessToken = requireActivity().getSharedPreferences(ACCESSTOKEN, Context.MODE_PRIVATE)
        val accessToken = sharedPreferencesAccessToken.getString(KEYACCESSTOKEN, null)

        val sharedPreferencesLoginStep24 =
            requireActivity().getSharedPreferences(SHAREDLOGINSTEP24, Context.MODE_PRIVATE)
        val user = sharedPreferencesLoginStep24.getString(LOGINSTEP24, null)

        val apiService = createApiService()
        getEvent(user,accessToken,apiService)

    }

    private fun createApiService(): ApiService {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.107:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    private fun getEvent(user: String?, accessToken: String?, apiService: ApiService) {

        if (user != null && accessToken != null) {
            val call = apiService.getData("${user}/events", "Bearer $accessToken")
            call.enqueue(object : Callback<Events>, CompanyEventAdapter.CompanyEventEvent {
                override fun onResponse(call: Call<Events>, response: Response<Events>) {
                    if (response.isSuccessful) {
                        //companyEventData = response.body()!!.event
                        Log.v("loginapp", "response: ${response}")
                        Log.v("loginapp", "call: ${call}")

                        val eventData: Events? = response.body()
                        Log.v("loginapp", "eventData: ${eventData}")

                        if (eventData != null) {
                            //val events: List<Events.Event> = eventData.events
                            companyEventData = eventData.events
                            Log.v("loginapp", "events: ${companyEventData}")
                            companyEventAdapter =
                                CompanyEventAdapter(
                                    companyEventData,
                                    this,
                                )
                            binding.rcvEvent.layoutManager = GridLayoutManager(context, 2)
                            binding.rcvEvent.adapter = companyEventAdapter
                            topMargin()
                            bottomMargin(companyEventData)
                            // Process the events data as needed
                        } else {
                            // Handle the case where the response body is null
                        }
                        // Process the data and update the UI
                    } else {
                        // Handle unsuccessful response
                    }
                }
                override fun onFailure(call: Call<Events>, t: Throwable) {
                    Log.e("RequestError", "Error: ${t.message}")
                    // Handle the error
                }

                override fun onEventClicked(companyEvent: Events.Event, position: Int) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    private fun topMargin() {
        val topMargin = 270 // اندازه مارجین بالا را از منابع دریافت کنید
        val itemDecoratio = CustomTopMarginItemDecoration(topMargin)
        binding.rcvEvent.addItemDecoration(itemDecoratio)
    }

    private fun bottomMargin(companyEventData: List<Events.Event>) {
        val itemCount = companyEventData.size // تعداد آیتم‌های موجود در لیست را دریافت کنید
        if (itemCount % 2 == 0) {
            val bottomMargin = 220 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = CustomBottomMarginItemDecoration(bottomMargin)
            binding.rcvEvent.addItemDecoration(itemDecoration)
        } else {
            val bottomMargin = 220 // اندازه مارجین پایین را از منابع دریافت کنید
            val itemDecoration = BottomMarginItemDecoration(bottomMargin)
            binding.rcvEvent.addItemDecoration(itemDecoration)
        }
    }

    override fun onEventClicked(companyEvent: Events.Event, position: Int) {
        TODO("Not yet implemented")
    }
}