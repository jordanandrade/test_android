package com.example.vgxchange.api.controllers;

import com.example.vgxchange.api.dto.BuyingPropositionToCreate;
import com.example.vgxchange.api.dto.RentalPropositionToCreate;
import com.example.vgxchange.model.BuyingProp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PropositionApi {

    @POST("/api/proposal/buying")
    Call<BuyingProp> add(@Body BuyingPropositionToCreate buyingPropositionToCreate);

    @POST("/api/proposal/rental")
    Call<BuyingProp> add(@Body RentalPropositionToCreate rentalPropositionToCreate);

    @GET("/api/proposal/{idUser}")
    Call<List<BuyingProp>> getAllBuyingProposalsByUser(@Path("idUser") String idUser);
}
