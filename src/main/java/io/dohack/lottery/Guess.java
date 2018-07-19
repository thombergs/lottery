package io.dohack.lottery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class Guess {

  @Id
  @GeneratedValue
  private Long id;

  private int guessedNumber;

  @Column(unique = true)
  private String email;

  private Boolean emailVerified;

}
