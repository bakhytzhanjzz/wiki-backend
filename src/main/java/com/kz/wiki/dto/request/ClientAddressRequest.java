package com.kz.wiki.dto.request;

import lombok.Data;

@Data
public class ClientAddressRequest {
    private String address;
    private String city;
    private String region;
    private String postalCode;
}


