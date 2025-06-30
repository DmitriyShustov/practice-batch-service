package ru.axiomatika.batch_service.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "batch_request_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "xml_content")
    private String xml_content;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private BatchItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_request_id")
    private Batch batch;

}
