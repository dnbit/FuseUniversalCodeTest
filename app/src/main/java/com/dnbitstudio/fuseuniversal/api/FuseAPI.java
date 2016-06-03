package com.dnbitstudio.fuseuniversal.api;

import com.dnbitstudio.fuseuniversal.model.Company;

import retrofit.Call;
import retrofit.http.GET;

public interface FuseAPI
{
    @GET(" ")
    Call<Company> getCompany();
}
