package com.ecommerce.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.model.AddressDto;
import com.ecommerce.project.model.User;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
@Tag(
	    name = "Address API",
	    description = "Manage customer addresses including add, update, delete and fetch."
	)
@RestController
@RequestMapping("/api")
public class AddressController {

	@Autowired
	private AddressService addressService;

	@Autowired
	AuthUtil authUtil;

	// save address

	@PostMapping("/addresses")
	public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressDto addressDto) {
		User user = authUtil.loggedInUser();

		AddressDto savedAddressDto = addressService.createAddress(addressDto, user);

		return new ResponseEntity<>(savedAddressDto, HttpStatus.CREATED);

	}

	@GetMapping("/addresses")
	public ResponseEntity<List<AddressDto>> getAllAddressess() {
		List<AddressDto> listOfAddressessDto = addressService.getAllAddressess();
		return new ResponseEntity<>(listOfAddressessDto, HttpStatus.OK);

	}

	@GetMapping("/addresses/{adddressId}")
	public ResponseEntity<AddressDto> getProductByCategory(@PathVariable Long adddressId) {

		AddressDto addressDto = addressService.getAddressById(adddressId);
		return new ResponseEntity<>(addressDto, HttpStatus.OK);
	}

	@GetMapping("/users/addresses")
	public ResponseEntity<List<AddressDto>> getUserAddressess() {
		User user = authUtil.loggedInUser();

		List<AddressDto> listOfAddressessDto = addressService.getUserAddressess(user);
		return new ResponseEntity<>(listOfAddressessDto, HttpStatus.OK);

	}

	@PutMapping("/addresses/{adddressId}")
	public ResponseEntity<AddressDto> updateAddressById(@PathVariable Long adddressId,
			@RequestBody AddressDto addressDto) {
		AddressDto responseAddressDto = addressService.updateAddress(adddressId, addressDto);

		return new ResponseEntity<>(responseAddressDto, HttpStatus.OK);

	}

	@DeleteMapping("/addresses/{adddressId}")
	public ResponseEntity<String> deleteAddressById(@PathVariable Long adddressId) {
		String status = addressService.deleteAddress(adddressId);

		return new ResponseEntity<>(status, HttpStatus.OK);

	}

}
