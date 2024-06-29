package com.project.salebe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "mygen")
    @GenericGenerator(name = "mygen", strategy = "com.project.salebe.util.IdGenerator")
    @Column(name="id")
    private String id;
    @Column(name = "name", nullable = false)
    private String name;

    public static String ADMIN = "ADMIN";
    public static String USER = "USER";
}