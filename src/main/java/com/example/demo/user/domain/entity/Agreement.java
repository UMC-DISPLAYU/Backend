package com.example.demo.user.domain.entity;

import com.example.demo.global.entity.BaseTimeEntity;
import com.example.demo.user.domain.enums.Provider;
import com.example.demo.user.domain.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Agreement")
public class Agreement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreeId")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "isRequired", nullable = false)
    private boolean isRequired;
}