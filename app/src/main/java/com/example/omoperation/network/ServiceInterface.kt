package com.example.omoperation.network

import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.CommonRespS
import com.example.omoperation.model.avr.AvrMod
import com.example.omoperation.model.avr.AvrResp
import com.example.omoperation.model.branches.BranchesResp
import com.example.omoperation.model.cn_enquery.Myquery
import com.example.omoperation.model.cncreation.CnCreationMod
import com.example.omoperation.model.cnvalidate.CnValidateResp
import com.example.omoperation.model.cnvaridate.CnValidateMod
import com.example.omoperation.model.dispactch.DispatchResp
import com.example.omoperation.model.employess.EmployeeResp
import com.example.omoperation.model.findcustomer.CustomerMod
import com.example.omoperation.model.findcustomer.CustomerResp
import com.example.omoperation.model.findlorry.LorryMod
import com.example.omoperation.model.findlorry.LorryTypeResp
import com.example.omoperation.model.freight.FreightResp
import com.example.omoperation.model.generatetally.GenerateTallYMod
import com.example.omoperation.model.generatetally.GenerateTallyResp
import com.example.omoperation.model.loading.LoadingResp
import com.example.omoperation.model.login.LoginMod
import com.example.omoperation.model.login.LoginResp
import com.example.omoperation.model.oda.OdaResp
import com.example.omoperation.model.offline.Offline_C_Resp
import com.example.omoperation.model.pod.PodMod
import com.example.omoperation.model.pod.getpoddetails
import com.example.omoperation.model.rewarehouse.ReDetailResp
import com.example.omoperation.model.rewarehouse.ReWarehouseMod
import com.example.omoperation.model.submission.SubmissionResp
import com.example.omoperation.model.tally.TallyResp
import com.example.omoperation.model.vehcleimage.VehcleImageMod
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface ServiceInterface   {
    companion object{
        val omapi: String by lazy { "https://api.omlogistics.co.in/"
        }

        val omsanchar: String by lazy { "https://omsanchar.omlogistics.co.in/"
        }

        val omapp: String by lazy { "https://omapp.omlogistics.co.in/"
        }

    }

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/challanUnloading.php")
    fun challanUnloading(
        @HeaderMap headers: Map<String, String?>,
        @Body mod: CommonMod
    ): Call<CommonRespS>


    @POST("challanUnloading.php?status=unloading")
    fun AVR_SUBMIT_CHALLAN(
        @HeaderMap headers: Map<String, String>,
        @Body mod: AvrMod?
    ): Call<AvrResp>

//https://api.omlogistics.co.in//cn_validate1.php
    @POST("cn_validate1.php")
    fun cn_validate1(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CnValidateMod
    ): Call<CnValidateResp>
    @POST("/login.php")
    suspend fun LogIN(@Body mod: LoginMod): Response<LoginResp>

    @POST("/branch_networkdir.php")
    suspend fun branch_networkdir(@HeaderMap headers: Map<String, String>,@Body mod: CommonMod): Response<BranchesResp>
   @POST("vehicle_validate_checklist.php")
    suspend fun vehicle_validate_checklist(@HeaderMap headers: Map<String, String>,@Body mod: Any): Response<CommonRespS>

    @POST
    suspend fun loading_barcode(@Url url : String,@HeaderMap headers: Map<String, String>,@Body mod: Any): Response<CommonRespS>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cn_validate.php")
    fun cn_validate(
        @HeaderMap headers: Map<String, String>,
        @Body mod: Any
    ): Call<CommonRespS>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oracle/android_api/tally.php")
    fun loadingplan(
        @HeaderMap headers: Map<String, String>,
        @Body mod: Any
    ): Call<LoadingResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oda_station.php")
    fun oda_station(
        @HeaderMap headers: Map<String, String>,
        @Body mod: Any
    ): Call<OdaResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST
    fun cnenquery(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
    ): Call<Myquery>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST
    fun dispatchdetails(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
    ): Call<DispatchResp>
 @Headers("Content-Type: application/json;charset=UTF-8")
    @POST
    fun freightDetails(
     @Url url: String,
     @HeaderMap headers: Map<String, String>,
 ): Call<FreightResp>
 @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("emp_networkdir.php")
   suspend fun employeedirect(
        @HeaderMap headers: Map<String, String>
    ): Response<EmployeeResp>



    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/image_upload_lorry.php")
    suspend fun image_upload_lorry(
        @HeaderMap headers: Map<String, String>,
        @Body mod: VehcleImageMod
    ): Response<CommonRespS>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cnentry.php")
    suspend fun findcustomer(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CustomerMod
    ): Response<CustomerResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cnentry.php")
    suspend fun CNCreation(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CnCreationMod
    ): Response<CommonRespS>

    @POST("/oracle/android_api/tally.php")
    suspend fun LorryType(
        @HeaderMap headers: Map<String?, String?>?,
        @Body mod: LorryMod
    ): Response<LorryTypeResp>

    @POST("/oracle/android_api/tally.php")
    suspend fun Tall_Search(
        @HeaderMap headers: Map<String?, String?>?,
        @Body mod: LorryMod
    ): Response<TallyResp>

    @POST("/oracle/android_api/tally.php")
    suspend fun GenerateTally(
        @HeaderMap headers: Map<String?, String?>?,
        @Body mod: GenerateTallYMod?
    ): Response<GenerateTallyResp?>?


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cn_detail.php")
    suspend fun cn_detail(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CommonMod
    ): Response<getpoddetails>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cn_pod_upload1.php")
    suspend fun podupload(
        @HeaderMap headers: Map<String, String>,
        @Body mod: PodMod
    ): Response<CommonRespS>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/submission_enq.php")
    suspend fun submission_enq(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CommonMod
    ): Response<SubmissionResp>
@Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cnValidateRewh.php")
    suspend fun cnValidateRewh(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CommonMod
    ): Response<ReDetailResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/cnRewh.php.php")
    suspend fun cnRewh(
        @HeaderMap headers: Map<String, String>,
        @Body mod: ReWarehouseMod
    ): Response<ReDetailResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("/offline_challan.php")
    suspend fun offline_challan(
        @HeaderMap headers: Map<String, String?>,
        @Body mod: CommonMod?
    ): Response<Offline_C_Resp>


//https://api.omlogistics.co.in/emp_networkdir.php
    //https://api.omlogistics.co.in/oda_station.php
    //{"bcode":"7500","status":"challan"}
//{"branch":"7500","status":"getTallyNo"}
    //https://api.omlogistics.co.in/branch_networkdir.php

}