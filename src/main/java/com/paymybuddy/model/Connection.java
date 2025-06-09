package com.paymybuddy.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "connections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Connection {

}
