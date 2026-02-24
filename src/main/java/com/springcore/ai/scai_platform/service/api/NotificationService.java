package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.dto.NotificationDTO;
import com.springcore.ai.scai_platform.entity.Notification;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;

import java.util.List;

public interface NotificationService {
    Flux<NotificationDTO> getNotificationStream(Long userId);

    void sendToUser(Long userId, NotificationDTO payload);

    void sendToUsers(List<Long> userIds, NotificationDTO payload);

    List<Notification> getHistory(Long userId);

    void markAsRead(Long id);

    Integer countUnread(Long userId);

    void markAllAsRead(Long userId);

}
