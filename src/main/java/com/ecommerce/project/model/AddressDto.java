package com.ecommerce.project.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
	private Long adddressId;
	private String street;
	private String buildingName;
	private String city;
	private String country;
	private String pincode;
    private String state;

}
