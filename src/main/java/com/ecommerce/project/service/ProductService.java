package com.ecommerce.project.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponse;

public interface ProductService {

	ProductDto addProduct(ProductDto productDto, Long categoryId);

	ProductResponse getAllProducts(Integer pageNumber,Integer pageSize,String sortBy, String sortOrder);

	ProductResponse searchByCategory(Long categoryId,Integer pageNumber,Integer pageSize,String sortBy, String sortOrder);

	ProductResponse searchProductByKeyword(String keyword,Integer pageNumber,Integer pageSize,String sortBy, String sortOrder);

	ProductDto updateProduct(ProductDto productDto, Long productId);

	ProductDto deleteProduct(Long productId);

	ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException;

}
