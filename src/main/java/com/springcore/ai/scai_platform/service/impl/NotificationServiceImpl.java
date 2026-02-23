package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.config.RabbitConfig;
import com.springcore.ai.scai_platform.dto.NotificationDTO;
import com.springcore.ai.scai_platform.entity.Notification;
import com.springcore.ai.scai_platform.repository.api.NotificationRepository;
import com.springcore.ai.scai_platform.service.api.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository notificationRepository;
    // ยังต้องมี Map Sinks สำหรับพ่นออก SSE ให้ Browser ที่เกาะอยู่กับเครื่องนี้
    private final Map<Long, Sinks.Many<NotificationDTO>> userSinks = new ConcurrentHashMap<>();

    public NotificationServiceImpl(RabbitTemplate rabbitTemplate, NotificationRepository notificationRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Flux<NotificationDTO> getNotificationStream(Long userId) {
        return userSinks.computeIfAbsent(userId, k ->
                Sinks.many().multicast().onBackpressureBuffer()
        ).asFlux();
    }

    @Override
    public void sendToUser(Long userId, NotificationDTO payload) {

        Notification entity = Notification.builder()
                .userId(userId)
                .title(payload.getTitle())
                .message(payload.getMessage())
                .type(payload.getType())
                .parentId(payload.getParentId())
                .url(payload.getUrl())
                .isRead(false)
                .build();
        notificationRepository.save(entity);

        // ส่งเข้า RabbitMQ: "มีงานส่งถึง User ID นี้นะ"
        rabbitTemplate.convertAndSend(
                RabbitConfig.NOTI_EXCHANGE,
                "noti.user." + userId,
                payload
        );
    }

    @RabbitListener(queues = RabbitConfig.NOTI_QUEUE)
    public void receiveFromRabbit(NotificationDTO payload, @Header("amqp_receivedRoutingKey") String routingKey) {
        try {
            // ตรวจสอบว่า routingKey มีรูปแบบที่ถูกต้องก่อน split
            if (routingKey != null && routingKey.startsWith("noti.user.")) {
                String[] parts = routingKey.split("\\.");
                if (parts.length >= 3) {
                    Long userId = Long.parseLong(parts[2]);
                    Sinks.Many<NotificationDTO> sink = userSinks.get(userId);
                    if (sink != null) {
                        // ใช้ emitNext พร้อม Handler เพื่อจัดการกรณีท่อเต็มหรือไม่มีคนฟัง
                        sink.emitNext(payload, Sinks.EmitFailureHandler.FAIL_FAST);
                    }
                }
            }
        } catch (Exception e) {
            // แนะนำให้ log error ไว้ด้วยครับ เผื่อ debug ตอนรันบน M3 Pro
            System.err.println("Error processing notification from RabbitMQ: " + e.getMessage());
        }
    }

    @Override
    public void sendToUsers(List<Long> userIds, NotificationDTO payload) {
        userIds.forEach(id -> sendToUser(id, payload));
    }

    @Override
    public List<Notification> getHistory(Long userId) {
        return notificationRepository.findTop20ByUserIdOrderByCreateDateDesc(userId);
    }

    @Override
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
