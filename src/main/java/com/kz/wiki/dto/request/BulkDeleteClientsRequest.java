package com.kz.wiki.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkDeleteClientsRequest {
    private List<Long> clientIds;
}

