package com.gameshop.www.eCommerce.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private UUID id;
    @NotBlank
    @Size(max = 30)
    private String firstName;
    @NotBlank
    @Size(max = 40)
    private String lastName;
    @NotBlank
    @Size(max = 10)
    private String contactNumber;
    @NotBlank
    @Size(max = 60)
    private String country;
    @NotBlank
    @Size(max = 60)
    private String city;
    @NotBlank
    @Size(max = 120)
    private String addressLine;
    @NotBlank
    private Integer postcode;


}
