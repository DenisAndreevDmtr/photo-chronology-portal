package com.andersen.pc.common.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = "trip")
@EqualsAndHashCode(exclude = {"trip"}, callSuper = false)
@Table(name = "photos")
public class Photo extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "awsKey", length = 200, nullable = false)
    private String awsKey;
    @Column(name = "title", length = 100, nullable = false)
    private String title;
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
}