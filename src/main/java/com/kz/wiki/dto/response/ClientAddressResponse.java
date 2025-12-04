package com.kz.wiki.dto.response;

import lombok.Data;

@Data
public class ClientAddressResponse {
    private Long id;
    private String address;
    private String city;
    private String region;
    private String postalCode;
}

