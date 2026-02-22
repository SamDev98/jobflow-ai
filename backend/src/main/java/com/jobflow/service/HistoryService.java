package com.jobflow.service;

import com.jobflow.dto.request.CreateHistoryRequest;
import com.jobflow.dto.response.HistoryResponse;
import com.jobflow.entity.HistoryItem;
import com.jobflow.entity.User;
import com.jobflow.entity.enums.HistoryType;
import com.jobflow.exception.ResourceNotFoundException;
import com.jobflow.repository.HistoryRepository;
import com.jobflow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final SecurityUtils securityUtils;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<HistoryResponse> getAllHistory(HistoryType type, Pageable pageable) {
        User user = userService.getOrCreateUser(securityUtils.getCurrentUserId());
        Page<HistoryItem> items;
        if (type != null) {
            items = historyRepository.findAllByUserIdAndType(user.getId(), type, pageable);
        } else {
            items = historyRepository.findAllByUserId(user.getId(), pageable);
        }
        return items.map(this::mapToResponse);
    }

    @Transactional
    public HistoryResponse createHistory(CreateHistoryRequest request) {
        User user = userService.getOrCreateUser(securityUtils.getCurrentUserId());
        HistoryItem item = HistoryItem.builder()
                .user(user)
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .metadata(request.getMetadata())
                .build();
        return mapToResponse(historyRepository.save(item));
    }

    @Transactional
    public void deleteHistory(UUID id) {
        User user = userService.getOrCreateUser(securityUtils.getCurrentUserId());
        HistoryItem item = historyRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("History item not found"));
        item.setDeletedAt(Instant.now());
        historyRepository.save(item);
    }

    private HistoryResponse mapToResponse(HistoryItem item) {
        return HistoryResponse.builder()
                .id(item.getId())
                .type(item.getType())
                .title(item.getTitle())
                .content(item.getContent())
                .metadata(item.getMetadata())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
