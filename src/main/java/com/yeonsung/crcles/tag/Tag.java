package com.yeonsung.crcles.tag;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class Tag {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true,nullable = false)
    private String title;
}
