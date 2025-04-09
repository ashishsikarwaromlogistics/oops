package com.example.omoperation.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.omoperation.CustomProgress
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.model.cn_enquery.Myquery
import com.example.omoperation.model.dispactch.DispatchResp
import com.example.omoperation.model.freight.FreightResp
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import retrofit2.Call
import retrofit2.Response


class CnEnqueryRepository {
    val _livedata= MutableLiveData<Myquery>()
    val _dispatchdetails= MutableLiveData<DispatchResp>()
    val _freightdetails= MutableLiveData<FreightResp>()
   lateinit var cp: CustomProgress
    fun getCNDetails(context : Context, value : String){
       if(Utils.haveInternet(context)){
           cp= CustomProgress(context)
           cp.show()
            val URL = ServiceInterface.omapi+"cn_enquiry.php?status=omstaff&cn_no=" + value

           ApiClient.getClient().create(ServiceInterface::class.java).cnenquery(URL,Utils.getheaders()).enqueue(object :
               retrofit2.Callback<Myquery> {
               override fun onResponse(
                   call: Call<Myquery>,
                   response: Response<Myquery>
               ) {
                   cp.dismiss()
                   if(response.isSuccessful){
                       response.body()?.let {
                           _livedata.postValue(response.body())
                       }?:run  {
                          Utils.showDialog(context,"error","Response body is null",R.drawable.ic_error_outline_red_24dp)
                       }
                   }
                   else  Utils.showDialog(
                       context,
                       "HTTP Error ${response.code()}",
                       response.message(),
                       R.drawable.ic_error_outline_red_24dp
                   )

               }

               override fun onFailure(call: Call<Myquery>, t: Throwable) {
                 cp.dismiss()
                   Utils.showDialog(context,"onFailure ",t.message,
                       R.drawable.ic_error_outline_red_24dp)
               }

           })

       }
    }
    fun getDispatch(context : Context, value : String){
        if(Utils.haveInternet(context)){
            cp= CustomProgress(context)
            cp.show()
            val URL = ServiceInterface.omapi+"cn_enquiry_detail.php?cn_no=" + value

            ApiClient.getClient().create(ServiceInterface::class.java).dispatchdetails(URL,Utils.getheaders()).enqueue(object :
                retrofit2.Callback<DispatchResp> {
                override fun onResponse(
                    call: Call<DispatchResp>,
                    response: Response<DispatchResp>
                ) {
                    cp.dismiss()
                    if(response.body()!=null){
                        _dispatchdetails.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<DispatchResp>, t: Throwable) {
                    cp.dismiss()
                  Utils.showDialog(context,"onFailure ",t.message,
                        R.drawable.ic_error_outline_red_24dp)
                }

            })

        }
    }
    fun getFreight(context : Context, value : String){
        if(Utils.haveInternet(context)){
            cp= CustomProgress(context)
            cp.show()
            val URL = ServiceInterface.omapi+"cn_enquiry_fright.php?cn_no=" + value

            ApiClient.getClient().create(ServiceInterface::class.java).freightDetails(URL,Utils.getheaders()).enqueue(object :
                retrofit2.Callback<FreightResp> {
                override fun onResponse(
                    call: Call<FreightResp>,
                    response: Response<FreightResp>
                ) {
                    cp.dismiss()
                    if(response.body()!=null){
                        _freightdetails.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<FreightResp>, t: Throwable) {
                    cp.dismiss()
                    Utils.showDialog(context,"onFailure ",t.message,
                        R.drawable.ic_error_outline_red_24dp)
                }

            })

        }
    }
}