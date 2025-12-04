package com.kz.wiki.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class CreateClientRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    private List<String> phones;
    private String email;
    private LocalDate birthday;
    private String gender; // male, female
    private String maritalStatus; // single, married, divorced, widowed
    private String language; // ru, kk, en
    
    @JsonProperty("groupIds")
    private List<Long> groupIds;
    
    @JsonProperty("tagIds")
    private List<Long> tagIds;
    
    private List<ClientAddressRequest> addresses;
    private Map<String, String> socialNetworks;
    private List<ClientRelativeRequest> relatives;
    private Map<String, Boolean> notifications;
}


