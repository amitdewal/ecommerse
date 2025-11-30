package com.ecommerce.project.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.AddressDto;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.util.AuthUtil;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AuthUtil authUtil;
	@Autowired
	private UserRepository userRepository;

	@Override
	public AddressDto createAddress(AddressDto addressDto, User user) {

		Address address = modelMapper.map(addressDto, Address.class);

		List<Address> addresses = user.getAddresses();
		addresses.add(address);
		user.setAddresses(addresses);

		address.setUser(user);

		Address savedAddress = addressRepository.save(address);

		return modelMapper.map(savedAddress, AddressDto.class);
	}

	@Override
	public List<AddressDto> getAllAddressess() {
		List<Address> listOfAddresses = addressRepository.findAll();
		System.out.println(listOfAddresses);

		List<AddressDto> listOfAddressDtos = listOfAddresses.stream()
				.map(address -> modelMapper.map(address, AddressDto.class)).toList();
		return listOfAddressDtos;
	}

	@Override
	public AddressDto getAddressById(Long adddressId) {
		Address address = addressRepository.findById(adddressId)
				.orElseThrow(() -> new ResourceNotFoundException("adddress", "adddressId", adddressId));

		return modelMapper.map(address, AddressDto.class);
	}

	@Override
	public List<AddressDto> getUserAddressess(User user) {

		List<Address> addresses = user.getAddresses();
		List<AddressDto> listOfAddressDtos = addresses.stream()
				.map(address -> modelMapper.map(address, AddressDto.class)).toList();
		return listOfAddressDtos;
	}

	@Override
	public AddressDto updateAddress(Long adddressId, AddressDto addressDto) {

		Address existingaddress = addressRepository.findById(adddressId)
				.orElseThrow(() -> new ResourceNotFoundException("adddress", "adddressId", adddressId));

		Address address = modelMapper.map(addressDto, Address.class);

		// upadting address
//		existingaddress.setAdddressId(address.getAdddressId());
		existingaddress.setBuildingName(address.getBuildingName());
		existingaddress.setCity(address.getCity());
		existingaddress.setCountry(address.getCountry());
		existingaddress.setPincode(address.getPincode());
		existingaddress.setState(address.getState());
		existingaddress.setStreet(address.getStreet());

		Address addresSavedToDb = addressRepository.save(existingaddress);

		User user = existingaddress.getUser();
		user.getAddresses().removeIf(addres -> addres.getAdddressId().equals(adddressId));
		user.getAddresses().add(addresSavedToDb);
		userRepository.save(user);
		return modelMapper.map(addresSavedToDb, AddressDto.class);
	}

	@Override
	public String deleteAddress(Long adddressId) {
		Address existingaddress = addressRepository.findById(adddressId)
				.orElseThrow(() -> new ResourceNotFoundException("adddress", "adddressId", adddressId));
		User user = existingaddress.getUser();
		user.getAddresses().removeIf(addres -> addres.getAdddressId().equals(adddressId));
		userRepository.save(user);
		addressRepository.delete(existingaddress);

		return "Address delete successfully with addressId: " + adddressId;

	}

}
