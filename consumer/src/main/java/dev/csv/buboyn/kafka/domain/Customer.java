package dev.csv.buboyn.kafka.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class Customer {
    private String name;
    private String address;
}
