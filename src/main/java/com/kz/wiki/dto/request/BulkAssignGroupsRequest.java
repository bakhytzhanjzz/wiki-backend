package com.kz.wiki.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkAssignGroupsRequest {
    private List<Long> clientIds;
    private List<Long> groupIds;
}


