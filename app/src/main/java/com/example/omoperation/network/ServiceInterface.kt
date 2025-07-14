package com.example.omoperation.network

import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.CommonRespS
import com.example.omoperation.model.PunchInMod
import com.example.omoperation.model.PunchInResp
import com.example.omoperation.model.PunchOutMod
import com.example.omoperation.model.PunchOutResp
import com.example.omoperation.model.VersionResp
import com.example.omoperation.model.audit.AuditMod
import com.example.omoperation.model.avr.AvrMod
import com.example.omoperation.model.avr.AvrResp
import com.example.omoperation.model.branches.BranchesResp
import com.example.omoperation.model.calculate_charge.CalculateMod
import com.example.omoperation.model.checklist.VehicleContainerEntry
import com.example.omoperation.model.checklist.VehicleContainerEntryResp
import com.example.omoperation.model.clientbox.ClientBoxResp
import com.example.omoperation.model.cn_create_eway.CNCreateEwayResp
import com.example.omoperation.model.cn_create_eway.CnCreateEwayMod
import com.example.omoperation.model.cn_enquery.Myquery
import com.example.omoperation.model.cncreation.CnCreationMod
import com.example.omoperation.model.cnvalidate.CnValidateResp
import com.example.omoperation.model.cnvaridate.CnValidateMod
import com.example.omoperation.model.dispactch.DispatchResp
import com.example.omoperation.model.employess.EmployeeResp
import com.example.omoperation.model.empty.EmptyMod
import com.example.omoperation.model.eway.EwayMod
import com.example.omoperation.model.eway.EwayResp
import com.example.omoperation.model.eway_dropdown.Eway_DropDownMod
import com.example.omoperation.model.eway_dropdown.Eway_Drop_Down_Resp
import com.example.omoperation.model.findcustomer.CustomerMod
import com.example.omoperation.model.findcustomer.CustomerResp
import com.example.omoperation.model.findlorry.LorryMod
import com.example.omoperation.model.findlorry.LorryTypeResp
import com.example.omoperation.model.freight.FreightResp
import com.example.omoperation.model.gatepass.GatePassMod
import com.example.omoperation.model.gatepass.GatePassResp
import com.example.omoperation.model.gatepass.submitResp
import com.example.omoperation.model.gatepassin.GatePassInMod
import com.example.omoperation.model.gatepassin.GatePassInResp
import com.example.omoperation.model.generatetally.GenerateTallYMod
import com.example.omoperation.model.generatetally.GenerateTallyResp
import com.example.omoperation.model.loading.LoadingResp
import com.example.omoperation.model.login.LoginMod
import com.example.omoperation.model.login.LoginResp
import com.example.omoperation.model.oda.OdaResp
import com.example.omoperation.model.offline.Offline_C_Resp
import com.example.omoperation.model.pod.PodMod
import com.example.omoperation.model.pod.getpoddetails
import com.example.omoperation.model.print.PrintBarcodeMod
import com.example.omoperation.model.print.PrintCNMod
import com.example.omoperation.model.rewarehouse.ReDetailResp
import com.example.omoperation.model.rewarehouse.ReWarehouseMod
import com.example.omoperation.model.savegatepass.SaveDataPassMod
import com.example.omoperation.model.submission.SubmissionResp
import com.example.omoperation.model.tally.TallyResp
import com.example.omoperation.model.vehcleimage.VehcleImageMod
import com.example.omoperation.model.vehicleload.VehcleUnloadResp
import com.example.omoperation.model.vehicleloadunload.VehcleLoadUnloadMod
import com.example.omoperation.model.verifyCNE.VerifyCneMod
import com.example.omoperation.model.verifyCNE.VerifyCneResp
import com.omlogistics.deepak.omlogistics.model.calculate_charge.CalculateResp
import com.omlogistics.deepak.omlogistics.model.print.PrintResp
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ServiceInterface   {
     companion object{
        val omapi: String by lazy {
            "https://scmomsanchar.omlogistics.co.in/oracle/android_api/"
            //"https://qa.omlogistics.co.in/oracle/android_api/"
        }

        val omsanchar: String by lazy {
            //"https://omsanchar.omlogistics.co.in/"
            "https://scmomsanchar.omlogistics.co.in/"
        }

        val omapp: String by lazy {
           // "https://omapp.omlogistics.co.in/"
            "https://scm.omlogistics.co.in/"
        }
     val omsl: String by lazy { "https://scmomsanchar.omlogistics.co.in/"
        }

    }


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("challanUnloading.php")
    fun challanUnloading(
        @HeaderMap headers: Map<String, String?>,
        @Body mod: CommonMod
    ): Call<CommonRespS>


    @POST("challanUnloading.php?status=unloading")
    fun AVR_SUBMIT_CHALLAN(
        @HeaderMap headers: Map<String, String>,
        @Body mod: AvrMod?
    ): Call<AvrResp>

    //https://api.omlogistics.co.in/cn_validate1.php
    @POST("cn_validate1.php")
    fun cn_validate1(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CnValidateMod
    ): Call<CnValidateResp>

    @POST("cn_validate1_bajaj.php")
    fun cn_validate1_bajaj(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CnValidateMod
    ): Call<CnValidateResp>

    @POST
    fun cn_validateurl(
        @Url url : String,
        @HeaderMap headers: Map<String, String>,
        @Body mod: CnValidateMod
    ): Call<CnValidateResp>

    @POST("login.php")
    suspend fun LogIN(@Body mod: LoginMod): Response<LoginResp>?

    @POST("branch_networkdir.php")
    suspend fun branch_networkdir(@HeaderMap headers: Map<String, String>,@Body mod: CommonMod): Response<BranchesResp>
    @POST("vehicle_validate_checklist.php")
    suspend fun vehicle_validate_checklist(@HeaderMap headers: Map<String, String>,@Body mod: Any): Response<CommonRespS>

    @POST
    suspend fun loading_barcode(@Url url : String,@HeaderMap headers: Map<String, String>,@Body mod: Any): Response<CommonRespS>
@POST
    suspend fun find_client_missing_box(@Url url : String,@HeaderMap headers: Map<String, String>,@Body mod: Any): Response<ClientBoxResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("cn_validate.php")
    fun cn_validate(
        @HeaderMap headers: Map<String, String>,
        @Body mod: Any
    ): Call<CommonRespS>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("cn_validate_bajaj.php")
    fun cn_validate_bajaj(
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
    @POST("image_upload_lorry.php")
    suspend fun image_upload_lorry(
        @HeaderMap headers: Map<String, String>,
        @Body mod: VehcleImageMod
    ): Response<CommonRespS>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("cnentry.php")
    suspend fun findcustomer(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CustomerMod
    ): Response<CustomerResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("cnentry.php")
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

    @POST("oracle/android_api/tally.php")
    suspend fun GenerateTally(
        @HeaderMap headers: Map<String?, String?>?,
        @Body mod: GenerateTallYMod?
    ): Response<GenerateTallyResp?>?


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("cn_detail.php")
    suspend fun cn_detail(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CommonMod
    ): Response<getpoddetails>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("cn_pod_upload1.php")
    suspend fun podupload(
        @HeaderMap headers: Map<String, String>,
        @Body mod: PodMod
    ): Response<CommonRespS>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("submission_enq.php")
    suspend fun submission_enq(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CommonMod
    ): Response<SubmissionResp>
@Headers("Content-Type: application/json;charset=UTF-8")
    @POST("cnValidateRewh.php")
    suspend fun cnValidateRewh(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CommonMod
    ): Response<ReDetailResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("cnRewh.php")
    suspend fun cnRewh(
        @HeaderMap headers: Map<String, String>,
        @Body mod: ReWarehouseMod
    ): Response<ReDetailResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("offline_challan.php")
    suspend fun offline_challan(
        @HeaderMap headers: Map<String, String?>,
        @Body mod: CommonMod?
    ): Response<Offline_C_Resp>

    @POST("oracle/android_api/vehGatePass.php")
    fun GetePass(
        @HeaderMap headers: Map<String, String>,
        @Body mod: GatePassInMod
    ): Call<GatePassInResp>

    @POST("oracle/android_api/vehGatePass.php")
    fun GetePassSubmit(
        @HeaderMap headers: Map<String, String>,
        @Body mod: SaveDataPassMod
    ): Call<submitResp>
    @POST("/oracle/android_api/vehGatePass.php")
    fun GetePass(
        @HeaderMap headers: Map<String, String>,
        @Body mod: GatePassMod
    ): Call<GatePassResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oracle/android_api/vehGatePass.php")
    suspend fun searchGetPaper(
        @HeaderMap headers: Map<String, String>,
        @Body mod: GatePassInMod
    ): Response<GatePassInResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oracle/android_api/ewaybill/ewbSingle.php")
    suspend fun searchEwb(
        @HeaderMap headers: Map<String, String>,
        @Body mod: EwayMod
    ): Response<EwayResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oracle/android_api/ewaybill/ewbSingle.php")
    suspend fun cftWt(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CalculateMod
    ): Response<CalculateResp>
@Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oracle/android_api/ewaybill/ewbSingle.php")
    suspend fun createCn(
        @HeaderMap headers: Map<String, String>,
        @Body mod: CnCreateEwayMod
    ): Response<CNCreateEwayResp>
@Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oracle/android_api/ewaybill/ewbSingle.php")
    suspend fun checkCNE(
        @HeaderMap headers: Map<String, String>,
        @Body mod: VerifyCneMod
    ): Response<VerifyCneResp>

@Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oracle/android_api/ewaybill/ewbSingle.php")
    suspend fun checkBilling(
        @HeaderMap headers: Map<String, String>,
        @Body mod: VerifyCneMod
    ): Response<VerifyCneResp>
@Headers("Content-Type: application/json;charset=UTF-8")
    @POST("oracle/android_api/ewaybill/ewbSingle.php")
    suspend fun dropdowns(
        @HeaderMap headers: Map<String, String>,
        @Body mod: Eway_DropDownMod
    ): Response<Eway_Drop_Down_Resp>

@Headers("Content-Type: application/json;charset=UTF-8")
    @POST("barcode_print.php")
    suspend fun barcode_print(
        @HeaderMap headers: Map<String, String>,
        @Body mod: PrintBarcodeMod
    ): Response<CommonRespS>

    @POST("omstaffAppVersion.php")//https://api.omlogistics.co.in/omstaffAppVersion.php
    suspend fun omstaffAppVersion(
        @Body mod: CommonMod
    ): Response<VersionResp>


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("vluimage.php")
    suspend fun vluimage(
        @HeaderMap headers: Map<String, String?>,
        @Body mod: CommonMod
    ): Response<VehcleUnloadResp>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("vluimage.php")
    suspend fun vluimage(
        @HeaderMap headers: Map<String, String?>,
        @Body mod: VehcleLoadUnloadMod
    ): Response<VehcleUnloadResp?>?


    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("online_audit_scanning1.php?status=audit")
    suspend fun online_audit_scanning(
        @HeaderMap headers: Map<String, String>,
        @Body mod: AuditMod
    ): Response<CommonRespS>

    @POST("empty_challan.php")
    suspend fun emptrychallan(
        @Body mod: EmptyMod
    ): Response<CommonRespS?>?

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIxMDAzOSIsIm5hbWUiOiJLYXNoaW5hdGggVGhhbGthciIsImp0aSI6ImM4YTk0YTEwLTk4NzgtNGZlMy04MzdmLWEyZDRjNzFmMmVmZiIsImV4cCI6MTczNTgwNDU1NywiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3Q6NDQzNjgvIiwiYXVkIjoiaHR0cHM6Ly9sb2NhbGhvc3Q6NDQzNjgvIn0.d0amPI5N3yknZhVoOW4qhFS7lWbSOODKxqty1Gd9ueI"
    )
    @POST("api/HeadCodeType/GetHeadCodeByUserID")
    fun getHeadCodeByUserID(
        @Query("ZPID") zpid: Int,
        @Query("DeptID") deptId: Int,
        @Query("UserID") userId: Int,
       // @HeaderMap headers: Map<String, String?>
    ): Call<ResponseBody>

    @POST("oracle/android_api/ewaybill/ewbSingle.php")
    fun printvalue(
        @HeaderMap headers: Map<String?, String?>?,
        @Body mod: PrintCNMod?
    ): Call<PrintResp>?
 @POST("office-entry-form.php")
    fun punchin(

        @Body mod: PunchInMod?
    ): Call<PunchInResp>?

    @POST("emp_visit.php")
    fun punchout(
        @Body mod: PunchOutMod
    ): Call<PunchOutResp>


    @POST("trackdeviceid.php")
    suspend fun trackdeviceid(): Response<CommonRespS>

    @POST("savedeviceid.php")
    suspend fun savedeviceid(): Response<CommonRespS>

    @POST("api/cncreation/VehicleContainerEntry")
    fun Container_Chek_List(@Body mod: VehicleContainerEntry): Call<VehicleContainerEntryResp>
//https://scmomsanchar.omlogistics.co.in/api/cncreation/VehicleContainerEntry
    //https://scm.omlogistics.co.in/api/cncreation/VehicleContainerEntry
//https://api.omlogistics.co.in/emp_networkdir.php
    //https://api.omlogistics.co.in/oda_station.php
    //{"bcode":"7500","status":"challan"}
//{"branch":"7500","status":"getTallyNo"}
    //https://api.omlogistics.co.in/branch_networkdir.php

}