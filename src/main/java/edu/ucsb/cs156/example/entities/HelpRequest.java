package edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 
 * This is a JPA entity that represents a HelpReques
 * 
 * A HelpRequest is a request for help, e.g. those on the #help-lecture-discussion channel of the course slack
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "helprequest")
public class HelpRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String requesterEmail;
  private String teamId;
  private String tableOrBreakoutRoom;
  private LocalDateTime requestTime;
  private String explanation;
  private boolean solved;
}