package com.ecommerce.project.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	public String uploadImage(String path, MultipartFile file) throws IOException;

}
