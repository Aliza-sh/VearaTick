package com.vearad.vearatick.fgmSub

import ApiService
import BottomMarginItemDecoration
import CustomBottomMarginItemDecoration
import CustomTopMarginItemDecoration
import android.content.Context
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

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.105:8081/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val sharedPreferencesAccessToken = requireActivity().getSharedPreferences(ACCESSTOKEN, Context.MODE_PRIVATE)
        val accessToken = sharedPreferencesAccessToken.getString(KEYACCESSTOKEN, null)

        val sharedPreferencesLoginStep24 =
            requireActivity().getSharedPreferences(SHAREDLOGINSTEP24, Context.MODE_PRIVATE)
        val user = sharedPreferencesLoginStep24.getString(LOGINSTEP24, null)

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
                            val events: List<Events.Event> = eventData.events
                            Log.v("loginapp", "events: ${events}")
                            companyEventAdapter =
                                CompanyEventAdapter(
                                    events,
                                    this,
                                )
                            binding.rcvEvent.layoutManager = GridLayoutManager(context, 2)
                            binding.rcvEvent.adapter = companyEventAdapter
                            topMargin()
                            bottomMargin(events)
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

//    private fun POSTInfoUrl() {
//
//
////        Log.d(TAG, "POSTInfoUrl type: " + type);
//
////        pg_register.setVisibility(View.VISIBLE);
//        val jsonObject = JSONObject()
//        try {
//            Log.d(TAG, "name: " + name)
//            //            jsonObject.put("csrf", "code_here");
//            jsonObject.put("name", name)
//            //            jsonObject.put("number", number);
//        } catch (e: JSONException) {
//            // handle exception
//        }
//        val requestQueue = Volley.newRequestQueue(context)
//        val postRequest: StringRequest = object : StringRequest(
//            Request.Method.GET,
//            get_folders_question_url + "?type=" + name,
//            object : com.android.volley.Response.Listener<String?> {
//                fun onResponse(`object`: String?) {
//
////                        Log.d(TAG, "object: " + object);
////                        tx_level.setText(level+"");
////                        Log.d(TAG, "Success "+object);
//                    try {
//                        val jsonArr = JSONArray(`object`)
//                        for (i in 0 until jsonArr.length()) {
//                            val jsonObj = jsonArr.getJSONObject(i)
//                            val name_en = jsonObj.getString("name_en")
//                            val name_fa = jsonObj.getString("name_fa")
//                            val status = jsonObj.getString("status")
//
////                                Log.d(TAG, "name_en: " + name_en);
//                            itemFolderQuestions.add(
//                                ItemFolderQuestion(
//                                    "",
//                                    status,
//                                    name_en,
//                                    name_fa
//                                )
//                            )
//                            if (i == 0) {
//                                SSSP.getInstance(this@ActivityFolderQus).putBoolean(name_en, true)
//                            }
//                        }
//                        adapter = AdapterFolderQuestion(this@ActivityFolderQus, itemFolderQuestions)
//                        // Attach the adapter to the recyclerview to populate items
//                        recyclerView.setAdapter(adapter)
//                        val mLayoutManager: RecyclerView.LayoutManager =
//                            GridLayoutManager(this@ActivityFolderQus, 1)
//                        // Set layout manager to position the items
//                        recyclerView.setLayoutManager(mLayoutManager)
//                        ll_pb.setVisibility(View.GONE)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                }
//            },
//            object : ErrorListener() {
//                fun onErrorResponse(error: VolleyError) {
//                    Log.d(TAG, "Error response $error")
//                    //                        pg_register.setVisibility(View.GONE);
//                }
//            }) {
//            //This is for Headers If You Needed
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                val params: MutableMap<String, String> = HashMap()
//                params["Accept"] = "application/json"
//                return params
//            }
//        }
//        requestQueue.add(postRequest)
////        MySingleton.getInstance(this).addToRequestQueue(requestQueue);
//    }

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