package com.lucafaggion.thesis.common.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "associated_account", uniqueConstraints = @UniqueConstraint(columnNames = { "username",
    "service_id" }))
@EqualsAndHashCode(exclude = { "user"}) // This,
@ToString(exclude = { "user"}) // and this
public class UserAssociatedAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String username;
  private String email;
  private String token;

  @OneToOne
  @JoinColumn(name="service_id")
  private ExternalService service;
}
