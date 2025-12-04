package com.kz.wiki.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ClientResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private String phone;
    private List<String> phones;
    private String email;
    private LocalDate birthday;
    private String gender;
    private String maritalStatus;
    private String language;
    private List<ClientGroupResponse> groups;
    private List<ClientTagResponse> tags;
    private BigDecimal totalPurchases;
    private LocalDateTime lastPurchaseDate;
    private LocalDateTime registrationDate;
    private Long registrationStoreId;
    private String registrationStoreName;
    private BigDecimal debtAmount;
    private Integer loyaltyPoints;
    private String loyaltyLevel;
    private List<ClientAddressResponse> addresses;
    private Map<String, String> socialNetworks;
    private List<ClientRelativeResponse> relatives;
    private Map<String, Boolean> notifications;
    private List<ClientCardResponse> cards;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

