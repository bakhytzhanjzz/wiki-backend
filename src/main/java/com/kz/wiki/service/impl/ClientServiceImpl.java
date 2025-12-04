package com.kz.wiki.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.dto.request.*;
import com.kz.wiki.dto.response.*;
import com.kz.wiki.entity.*;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.*;
import com.kz.wiki.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final CustomerRepository customerRepository;
    private final ClientAddressRepository addressRepository;
    private final ClientRelativeRepository relativeRepository;
    private final ClientCardRepository cardRepository;
    private final CustomerGroupRepository customerGroupRepository;
    private final CustomerTagRepository customerTagRepository;
    private final ClientGroupRepository groupRepository;
    private final ClientTagRepository tagRepository;
    private final StoreRepository storeRepository;
    private final SaleRepository saleRepository;
    private final CustomerDebtRepository debtRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_CLIENT", entityType = "CLIENT")
    public ClientResponse create(CreateClientRequest request, String tenantId) {
        // Check phone uniqueness
        if (request.getPhone() != null && customerRepository.findByPhoneAndTenantId(request.getPhone(), tenantId).isPresent()) {
            throw new BadRequestException("Customer with phone " + request.getPhone() + " already exists");
        }
        
        // Check email uniqueness
        if (request.getEmail() != null && customerRepository.findByEmailAndTenantId(request.getEmail(), tenantId).isPresent()) {
            throw new BadRequestException("Customer with email " + request.getEmail() + " already exists");
        }

        Customer customer = new Customer();
        customer.setTenantId(tenantId);
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setMiddleName(request.getMiddleName());
        customer.setName(buildFullName(request.getLastName(), request.getFirstName(), request.getMiddleName()));
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setBirthday(request.getBirthday());
        customer.setGender(request.getGender());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setLanguage(request.getLanguage());
        customer.setRegistrationDate(LocalDateTime.now());
        
        // Store phones as JSON
        if (request.getPhones() != null && !request.getPhones().isEmpty()) {
            try {
                customer.setPhones(objectMapper.writeValueAsString(request.getPhones()));
            } catch (Exception e) {
                log.error("Error serializing phones", e);
            }
        } else if (request.getPhone() != null) {
            try {
                customer.setPhones(objectMapper.writeValueAsString(List.of(request.getPhone())));
            } catch (Exception e) {
                log.error("Error serializing phone", e);
            }
        }
        
        // Store social networks as JSON
        if (request.getSocialNetworks() != null) {
            try {
                customer.setSocialNetworks(objectMapper.writeValueAsString(request.getSocialNetworks()));
            } catch (Exception e) {
                log.error("Error serializing social networks", e);
            }
        }
        
        // Store notifications as JSON
        if (request.getNotifications() != null) {
            try {
                customer.setNotifications(objectMapper.writeValueAsString(request.getNotifications()));
            } catch (Exception e) {
                log.error("Error serializing notifications", e);
            }
        }
        
        // Set registration store
        if (request.getAddresses() != null && !request.getAddresses().isEmpty()) {
            // Use first address's store if available, or default to storeId 1
            customer.setRegistrationStoreId(1L); // Default, can be enhanced
        }
        
        Customer saved = customerRepository.save(customer);
        
        // Save addresses
        if (request.getAddresses() != null) {
            for (ClientAddressRequest addrReq : request.getAddresses()) {
                ClientAddress address = new ClientAddress();
                address.setTenantId(tenantId);
                address.setClientId(saved.getId());
                address.setAddress(addrReq.getAddress());
                address.setCity(addrReq.getCity());
                address.setRegion(addrReq.getRegion());
                address.setPostalCode(addrReq.getPostalCode());
                addressRepository.save(address);
            }
        }
        
        // Save relatives
        if (request.getRelatives() != null) {
            for (ClientRelativeRequest relReq : request.getRelatives()) {
                ClientRelative relative = new ClientRelative();
                relative.setTenantId(tenantId);
                relative.setClientId(saved.getId());
                relative.setName(relReq.getName());
                relative.setRelation(relReq.getRelation());
                relative.setPhone(relReq.getPhone());
                relativeRepository.save(relative);
            }
        }
        
        // Assign groups
        if (request.getGroupIds() != null) {
            for (Long groupId : request.getGroupIds()) {
                if (groupRepository.existsByIdAndTenantId(groupId, tenantId)) {
                    CustomerGroup cg = new CustomerGroup();
                    cg.setTenantId(tenantId);
                    cg.setCustomerId(saved.getId());
                    cg.setGroupId(groupId);
                    customerGroupRepository.save(cg);
                }
            }
        }
        
        // Assign tags
        if (request.getTagIds() != null) {
            for (Long tagId : request.getTagIds()) {
                if (tagRepository.existsByIdAndTenantId(tagId, tenantId)) {
                    CustomerTag ct = new CustomerTag();
                    ct.setTenantId(tenantId);
                    ct.setCustomerId(saved.getId());
                    ct.setTagId(tagId);
                    customerTagRepository.save(ct);
                }
            }
        }
        
        return toResponse(saved, tenantId);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_CLIENT", entityType = "CLIENT")
    public ClientResponse update(Long id, CreateClientRequest request, String tenantId) {
        Customer customer = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        // Check phone uniqueness if changed
        if (request.getPhone() != null && !request.getPhone().equals(customer.getPhone()) &&
            customerRepository.findByPhoneAndTenantId(request.getPhone(), tenantId).isPresent()) {
            throw new BadRequestException("Customer with phone " + request.getPhone() + " already exists");
        }

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setMiddleName(request.getMiddleName());
        customer.setName(buildFullName(request.getLastName(), request.getFirstName(), request.getMiddleName()));
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setBirthday(request.getBirthday());
        customer.setGender(request.getGender());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setLanguage(request.getLanguage());
        customer.setUpdatedAt(LocalDateTime.now());
        
        // Update phones
        if (request.getPhones() != null) {
            try {
                customer.setPhones(objectMapper.writeValueAsString(request.getPhones()));
            } catch (Exception e) {
                log.error("Error serializing phones", e);
            }
        }
        
        // Update social networks
        if (request.getSocialNetworks() != null) {
            try {
                customer.setSocialNetworks(objectMapper.writeValueAsString(request.getSocialNetworks()));
            } catch (Exception e) {
                log.error("Error serializing social networks", e);
            }
        }
        
        // Update notifications
        if (request.getNotifications() != null) {
            try {
                customer.setNotifications(objectMapper.writeValueAsString(request.getNotifications()));
            } catch (Exception e) {
                log.error("Error serializing notifications", e);
            }
        }
        
        Customer saved = customerRepository.save(customer);
        
        // Update addresses
        addressRepository.deleteByClientIdAndTenantId(id, tenantId);
        if (request.getAddresses() != null) {
            for (ClientAddressRequest addrReq : request.getAddresses()) {
                ClientAddress address = new ClientAddress();
                address.setTenantId(tenantId);
                address.setClientId(saved.getId());
                address.setAddress(addrReq.getAddress());
                address.setCity(addrReq.getCity());
                address.setRegion(addrReq.getRegion());
                address.setPostalCode(addrReq.getPostalCode());
                addressRepository.save(address);
            }
        }
        
        // Update relatives
        relativeRepository.deleteByClientIdAndTenantId(id, tenantId);
        if (request.getRelatives() != null) {
            for (ClientRelativeRequest relReq : request.getRelatives()) {
                ClientRelative relative = new ClientRelative();
                relative.setTenantId(tenantId);
                relative.setClientId(saved.getId());
                relative.setName(relReq.getName());
                relative.setRelation(relReq.getRelation());
                relative.setPhone(relReq.getPhone());
                relativeRepository.save(relative);
            }
        }
        
        // Update groups
        customerGroupRepository.deleteByCustomerIdAndTenantId(id, tenantId);
        if (request.getGroupIds() != null) {
            for (Long groupId : request.getGroupIds()) {
                if (groupRepository.existsByIdAndTenantId(groupId, tenantId)) {
                    CustomerGroup cg = new CustomerGroup();
                    cg.setTenantId(tenantId);
                    cg.setCustomerId(saved.getId());
                    cg.setGroupId(groupId);
                    customerGroupRepository.save(cg);
                }
            }
        }
        
        // Update tags
        customerTagRepository.deleteByCustomerIdAndTenantId(id, tenantId);
        if (request.getTagIds() != null) {
            for (Long tagId : request.getTagIds()) {
                if (tagRepository.existsByIdAndTenantId(tagId, tenantId)) {
                    CustomerTag ct = new CustomerTag();
                    ct.setTenantId(tenantId);
                    ct.setCustomerId(saved.getId());
                    ct.setTagId(tagId);
                    customerTagRepository.save(ct);
                }
            }
        }
        
        return toResponse(saved, tenantId);
    }

    @Override
    public ClientResponse findById(Long id, String tenantId) {
        Customer customer = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return toResponse(customer, tenantId);
    }

    @Override
    public Page<ClientResponse> findAll(String tenantId, Pageable pageable,
                                       String search, List<Long> groupIds, List<Long> tagIds,
                                       LocalDate birthdayFrom, LocalDate birthdayTo,
                                       BigDecimal purchaseAmountFrom, BigDecimal purchaseAmountTo,
                                       LocalDate lastPurchaseFrom, LocalDate lastPurchaseTo,
                                       Integer noPurchaseDays,
                                       LocalDate registrationFrom, LocalDate registrationTo,
                                       List<Long> registrationStoreIds, String gender) {
        // For now, implement basic filtering
        // TODO: Implement complex filtering with all parameters
        List<Customer> allCustomers = customerRepository.findByTenantId(tenantId);
        
        // Apply filters
        List<Customer> filtered = allCustomers.stream()
                .filter(c -> search == null || search.isEmpty() || 
                        (c.getFirstName() != null && c.getFirstName().toLowerCase().contains(search.toLowerCase())) ||
                        (c.getLastName() != null && c.getLastName().toLowerCase().contains(search.toLowerCase())) ||
                        (c.getPhone() != null && c.getPhone().contains(search)))
                .filter(c -> gender == null || gender.isEmpty() || gender.equals(c.getGender()))
                .filter(c -> birthdayFrom == null || (c.getBirthday() != null && !c.getBirthday().isBefore(birthdayFrom)))
                .filter(c -> birthdayTo == null || (c.getBirthday() != null && !c.getBirthday().isAfter(birthdayTo)))
                .filter(c -> purchaseAmountFrom == null || (c.getTotalPurchases() != null && c.getTotalPurchases().compareTo(purchaseAmountFrom) >= 0))
                .filter(c -> purchaseAmountTo == null || (c.getTotalPurchases() != null && c.getTotalPurchases().compareTo(purchaseAmountTo) <= 0))
                .collect(Collectors.toList());
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Customer> paged = start < filtered.size() ? filtered.subList(start, end) : Collections.emptyList();
        
        List<ClientResponse> responses = paged.stream()
                .map(c -> toResponse(c, tenantId))
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, filtered.size());
    }

    @Override
    public Map<String, Object> getStatistics(String tenantId) {
        List<Customer> allCustomers = customerRepository.findByTenantId(tenantId);
        long totalClients = allCustomers.size();
        
        // Calculate new clients last week
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long newClientsLastWeek = allCustomers.stream()
                .filter(c -> c.getRegistrationDate() != null && c.getRegistrationDate().isAfter(weekAgo))
                .count();
        
        // Calculate non-returning clients (no purchase in last 30 days)
        LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
        long nonReturningClients = allCustomers.stream()
                .filter(c -> c.getLastPurchaseDate() == null || c.getLastPurchaseDate().isBefore(monthAgo))
                .count();
        
        // Calculate birthdays today
        LocalDate today = LocalDate.now();
        long birthdaysToday = allCustomers.stream()
                .filter(c -> c.getBirthday() != null && 
                        c.getBirthday().getMonth() == today.getMonth() &&
                        c.getBirthday().getDayOfMonth() == today.getDayOfMonth())
                .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClients", totalClients);
        stats.put("newClientsLastWeek", newClientsLastWeek);
        stats.put("nonReturningClients", nonReturningClients);
        stats.put("birthdaysToday", birthdaysToday);
        
        return stats;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_CLIENT", entityType = "CLIENT")
    public void delete(Long id, String tenantId) {
        Customer customer = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        // Delete related data
        addressRepository.deleteByClientIdAndTenantId(id, tenantId);
        relativeRepository.deleteByClientIdAndTenantId(id, tenantId);
        cardRepository.deleteByClientIdAndTenantId(id, tenantId);
        customerGroupRepository.deleteByCustomerIdAndTenantId(id, tenantId);
        customerTagRepository.deleteByCustomerIdAndTenantId(id, tenantId);
        
        customerRepository.delete(customer);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "BULK_UPDATE_CLIENTS", entityType = "CLIENT")
    public Map<String, Integer> bulkUpdate(BulkUpdateClientsRequest request, String tenantId) {
        int updated = 0;
        for (Long clientId : request.getClientIds()) {
            Customer customer = customerRepository.findByIdAndTenantId(clientId, tenantId).orElse(null);
            if (customer != null) {
                if (request.getUpdates().containsKey("language")) {
                    customer.setLanguage((String) request.getUpdates().get("language"));
                }
                if (request.getUpdates().containsKey("notifications")) {
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Boolean> notifications = (Map<String, Boolean>) request.getUpdates().get("notifications");
                        customer.setNotifications(objectMapper.writeValueAsString(notifications));
                    } catch (Exception e) {
                        log.error("Error updating notifications", e);
                    }
                }
                customer.setUpdatedAt(LocalDateTime.now());
                customerRepository.save(customer);
                updated++;
            }
        }
        return Map.of("updated", updated);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "BULK_ASSIGN_GROUPS", entityType = "CLIENT")
    public Map<String, Integer> bulkAssignGroups(BulkAssignGroupsRequest request, String tenantId) {
        int updated = 0;
        for (Long clientId : request.getClientIds()) {
            for (Long groupId : request.getGroupIds()) {
                if (!customerGroupRepository.findByCustomerIdAndTenantId(clientId, tenantId).stream()
                        .anyMatch(cg -> cg.getGroupId().equals(groupId))) {
                    CustomerGroup cg = new CustomerGroup();
                    cg.setTenantId(tenantId);
                    cg.setCustomerId(clientId);
                    cg.setGroupId(groupId);
                    customerGroupRepository.save(cg);
                }
            }
            updated++;
        }
        return Map.of("updated", updated);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "BULK_REMOVE_GROUPS", entityType = "CLIENT")
    public Map<String, Integer> bulkRemoveGroups(BulkAssignGroupsRequest request, String tenantId) {
        int updated = 0;
        for (Long clientId : request.getClientIds()) {
            for (Long groupId : request.getGroupIds()) {
                customerGroupRepository.deleteByCustomerIdAndGroupIdAndTenantId(clientId, groupId, tenantId);
            }
            updated++;
        }
        return Map.of("updated", updated);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "BULK_ASSIGN_TAGS", entityType = "CLIENT")
    public Map<String, Integer> bulkAssignTags(BulkAssignTagsRequest request, String tenantId) {
        int updated = 0;
        for (Long clientId : request.getClientIds()) {
            for (Long tagId : request.getTagIds()) {
                if (!customerTagRepository.findByCustomerIdAndTenantId(clientId, tenantId).stream()
                        .anyMatch(ct -> ct.getTagId().equals(tagId))) {
                    CustomerTag ct = new CustomerTag();
                    ct.setTenantId(tenantId);
                    ct.setCustomerId(clientId);
                    ct.setTagId(tagId);
                    customerTagRepository.save(ct);
                }
            }
            updated++;
        }
        return Map.of("updated", updated);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "BULK_REMOVE_TAGS", entityType = "CLIENT")
    public Map<String, Integer> bulkRemoveTags(BulkAssignTagsRequest request, String tenantId) {
        int updated = 0;
        for (Long clientId : request.getClientIds()) {
            for (Long tagId : request.getTagIds()) {
                customerTagRepository.deleteByCustomerIdAndTagIdAndTenantId(clientId, tagId, tenantId);
            }
            updated++;
        }
        return Map.of("updated", updated);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "BULK_DELETE_CLIENTS", entityType = "CLIENT")
    public Map<String, Integer> bulkDelete(BulkDeleteClientsRequest request, String tenantId) {
        int deleted = 0;
        for (Long clientId : request.getClientIds()) {
            try {
                delete(clientId, tenantId);
                deleted++;
            } catch (Exception e) {
                log.error("Error deleting client " + clientId, e);
            }
        }
        return Map.of("deleted", deleted);
    }

    @Override
    @AuditLoggable(action = "IMPORT_CLIENTS", entityType = "CLIENT")
    public Map<String, Object> importClients(org.springframework.web.multipart.MultipartFile file, String tenantId) {
        // TODO: Implement Excel/CSV import
        // For now, return placeholder
        Map<String, Object> result = new HashMap<>();
        result.put("imported", 0);
        result.put("failed", 0);
        result.put("errors", Collections.emptyList());
        return result;
    }

    @Override
    public List<ClientResponse> searchForDebts(String search, String tenantId, Boolean hasDebt) {
        List<Customer> customers;
        if (search != null && !search.isEmpty()) {
            customers = customerRepository.searchByTenantId(tenantId, search);
        } else {
            customers = customerRepository.findByTenantId(tenantId);
        }
        
        if (hasDebt == null || hasDebt) {
            customers = customers.stream()
                    .filter(c -> c.getDebtAmount() != null && c.getDebtAmount().compareTo(BigDecimal.ZERO) > 0)
                    .collect(Collectors.toList());
        }
        
        return customers.stream()
                .map(c -> toResponse(c, tenantId))
                .collect(Collectors.toList());
    }

    private ClientResponse toResponse(Customer customer, String tenantId) {
        ClientResponse response = new ClientResponse();
        response.setId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setMiddleName(customer.getMiddleName());
        
        // Build full name
        StringBuilder fullName = new StringBuilder();
        if (customer.getLastName() != null) fullName.append(customer.getLastName());
        if (customer.getFirstName() != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(customer.getFirstName());
        }
        if (customer.getMiddleName() != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(customer.getMiddleName());
        }
        response.setFullName(fullName.toString());
        
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setBirthday(customer.getBirthday());
        response.setGender(customer.getGender());
        response.setMaritalStatus(customer.getMaritalStatus());
        response.setLanguage(customer.getLanguage());
        response.setTotalPurchases(customer.getTotalPurchases());
        response.setLastPurchaseDate(customer.getLastPurchaseDate());
        response.setRegistrationDate(customer.getRegistrationDate());
        response.setRegistrationStoreId(customer.getRegistrationStoreId());
        response.setDebtAmount(customer.getDebtAmount());
        response.setLoyaltyPoints(customer.getLoyaltyPoints());
        response.setLoyaltyLevel(customer.getLoyaltyLevel());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        
        // Parse phones
        if (customer.getPhones() != null) {
            try {
                List<String> phones = objectMapper.readValue(customer.getPhones(), new TypeReference<List<String>>() {});
                response.setPhones(phones);
            } catch (Exception e) {
                log.error("Error parsing phones", e);
                response.setPhones(Collections.singletonList(customer.getPhone()));
            }
        } else if (customer.getPhone() != null) {
            response.setPhones(Collections.singletonList(customer.getPhone()));
        }
        
        // Parse social networks
        if (customer.getSocialNetworks() != null) {
            try {
                Map<String, String> socialNetworks = objectMapper.readValue(customer.getSocialNetworks(), new TypeReference<Map<String, String>>() {});
                response.setSocialNetworks(socialNetworks);
            } catch (Exception e) {
                log.error("Error parsing social networks", e);
            }
        }
        
        // Parse notifications
        if (customer.getNotifications() != null) {
            try {
                Map<String, Boolean> notifications = objectMapper.readValue(customer.getNotifications(), new TypeReference<Map<String, Boolean>>() {});
                response.setNotifications(notifications);
            } catch (Exception e) {
                log.error("Error parsing notifications", e);
            }
        }
        
        // Load addresses
        List<ClientAddress> addresses = addressRepository.findByClientIdAndTenantId(customer.getId(), tenantId);
        response.setAddresses(addresses.stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList()));
        
        // Load relatives
        List<ClientRelative> relatives = relativeRepository.findByClientIdAndTenantId(customer.getId(), tenantId);
        response.setRelatives(relatives.stream()
                .map(this::toRelativeResponse)
                .collect(Collectors.toList()));
        
        // Load cards
        List<ClientCard> cards = cardRepository.findByClientIdAndTenantId(customer.getId(), tenantId);
        response.setCards(cards.stream()
                .map(this::toCardResponse)
                .collect(Collectors.toList()));
        
        // Load groups
        List<Long> groupIds = customerGroupRepository.findGroupIdsByCustomerIdAndTenantId(customer.getId(), tenantId);
        response.setGroups(groupIds.stream()
                .map(groupId -> {
                    ClientGroup group = groupRepository.findByIdAndTenantId(groupId, tenantId).orElse(null);
                    if (group != null) {
                        ClientGroupResponse gr = new ClientGroupResponse();
                        gr.setId(group.getId());
                        gr.setName(group.getName());
                        return gr;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        
        // Load tags
        List<Long> tagIds = customerTagRepository.findTagIdsByCustomerIdAndTenantId(customer.getId(), tenantId);
        response.setTags(tagIds.stream()
                .map(tagId -> {
                    ClientTag tag = tagRepository.findByIdAndTenantId(tagId, tenantId).orElse(null);
                    if (tag != null) {
                        ClientTagResponse tr = new ClientTagResponse();
                        tr.setId(tag.getId());
                        tr.setName(tag.getName());
                        return tr;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        
        // Load store name
        if (customer.getRegistrationStoreId() != null) {
            Store store = storeRepository.findByIdAndTenantId(customer.getRegistrationStoreId(), tenantId).orElse(null);
            if (store != null) {
                response.setRegistrationStoreName(store.getName());
            }
        }
        
        return response;
    }

    private ClientAddressResponse toAddressResponse(ClientAddress address) {
        ClientAddressResponse response = new ClientAddressResponse();
        response.setId(address.getId());
        response.setAddress(address.getAddress());
        response.setCity(address.getCity());
        response.setRegion(address.getRegion());
        response.setPostalCode(address.getPostalCode());
        return response;
    }

    private ClientRelativeResponse toRelativeResponse(ClientRelative relative) {
        ClientRelativeResponse response = new ClientRelativeResponse();
        response.setId(relative.getId());
        response.setName(relative.getName());
        response.setRelation(relative.getRelation());
        response.setPhone(relative.getPhone());
        return response;
    }

    private ClientCardResponse toCardResponse(ClientCard card) {
        ClientCardResponse response = new ClientCardResponse();
        response.setId(card.getId());
        response.setType(card.getType());
        response.setNumber(card.getNumber());
        response.setFileUrl(card.getFileUrl());
        return response;
    }

    private String buildFullName(String lastName, String firstName, String middleName) {
        StringBuilder fullName = new StringBuilder();
        if (lastName != null && !lastName.trim().isEmpty()) {
            fullName.append(lastName.trim());
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(firstName.trim());
        }
        if (middleName != null && !middleName.trim().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName.trim());
        }
        // Return non-null string (empty if all parts are null/empty, but this should not happen in practice)
        return fullName.length() > 0 ? fullName.toString() : "";
    }
}

