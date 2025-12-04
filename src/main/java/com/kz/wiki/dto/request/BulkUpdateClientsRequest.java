package com.kz.wiki.dto.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BulkUpdateClientsRequest {
    private List<Long> clientIds;
    private Map<String, Object> updates; // language, notifications, etc.
}

