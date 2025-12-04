package com.kz.wiki.dto.response;

import lombok.Data;

@Data
public class ClientCardResponse {
    private Long id;
    private String type;
    private String number;
    private String fileUrl;
}


