package com.wokconns.customer.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeDataDTO implements Serializable {
    ArrayList<HomeBannerDTO> banner = new ArrayList<>();
    ArrayList<HomeNearByArtistsDTO> near_by_artist = new ArrayList<>();
    ArrayList<UserBooking> active_booking = new ArrayList<>();
    ArrayList<HomeRecomendedDTO> recommended = new ArrayList<>();
    ArrayList<HistoryDTO> invoice = new ArrayList<>();
    ArrayList<HomeCategoryDTO> category = new ArrayList<>();

    public ArrayList<HomeBannerDTO> getBanner() {
        return banner;
    }

    public void setBanner(ArrayList<HomeBannerDTO> banner) {
        this.banner = banner;
    }

    public ArrayList<HomeNearByArtistsDTO> getNear_by_artist() {
        return near_by_artist;
    }

    public void setNear_by_artist(ArrayList<HomeNearByArtistsDTO> near_by_artist) {
        this.near_by_artist = near_by_artist;
    }

    public ArrayList<UserBooking> getActive_booking() {
        return active_booking;
    }

    public void setActive_booking(ArrayList<UserBooking> active_booking) {
        this.active_booking = active_booking;
    }

    public ArrayList<HomeRecomendedDTO> getRecommended() {
        return recommended;
    }

    public void setRecommended(ArrayList<HomeRecomendedDTO> recommended) {
        this.recommended = recommended;
    }

    public ArrayList<HistoryDTO> getInvoice() {
        return invoice;
    }

    public void setInvoice(ArrayList<HistoryDTO> invoice) {
        this.invoice = invoice;
    }

    public ArrayList<HomeCategoryDTO> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<HomeCategoryDTO> category) {
        this.category = category;
    }
}
