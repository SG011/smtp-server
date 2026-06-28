package com.smtp.tracking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryTrackerIT {

    @Mock
    DeliveryEventRepository repo;

    @InjectMocks
    DeliveryTracker tracker;

    @Test
    void record_persists_toDatabase() {
        tracker.record("msg-test-1", "DELIVERED");

        ArgumentCaptor<DeliveryEvent> captor = ArgumentCaptor.forClass(DeliveryEvent.class);
        verify(repo).save(captor.capture());

        DeliveryEvent saved = captor.getValue();
        assertThat(saved.getMessageId()).isEqualTo("msg-test-1");
        assertThat(saved.getStatus()).isEqualTo("DELIVERED");
        assertThat(saved.getRecordedAt()).isNotNull();
    }

    @Test
    void record_withSoftBounce_persistsCorrectStatus() {
        tracker.record("msg-test-2", "SOFT_BOUNCE");

        ArgumentCaptor<DeliveryEvent> captor = ArgumentCaptor.forClass(DeliveryEvent.class);
        verify(repo).save(captor.capture());

        assertThat(captor.getValue().getStatus()).isEqualTo("SOFT_BOUNCE");
    }
}
