package com.ecommerce.project.service;

import java.util.List;

import com.ecommerce.project.model.AddressDto;
import com.ecommerce.project.model.User;

public interface AddressService {

	AddressDto createAddress(AddressDto addressDto, User user);

	List<AddressDto> getAllAddressess();

	AddressDto getAddressById(Long adddressId);

	List<AddressDto> getUserAddressess(User user);

	AddressDto updateAddress(Long adddressId, AddressDto addressDto);

	String deleteAddress(Long adddressId);

}
