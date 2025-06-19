package com.library.service;

import com.library.dto.request.transactionalRequest.BookActionEvent;
import com.library.dto.request.transactionalRequest.BorrowRequest;
import com.library.entity.enums.ActionType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {


    private final KafkaTemplate<String, BookActionEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, BookActionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookAction(BookActionEvent request ) {
        kafkaTemplate.send("book-actions", request);
    }
}
