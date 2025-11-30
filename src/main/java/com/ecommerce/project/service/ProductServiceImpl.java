package com.ecommerce.project.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDto;
import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	CartService cartService;

	private ProductRepository productRepository;
	private CategoryRepository categoryRepository;
	private ModelMapper modelMapper;
	private FileService fileService;

	@Value("{project.image}")
	private String path;

	public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
			ModelMapper modelMapper, FileService fileService) {
		super();
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
		this.fileService = fileService;
	}

	@Override
	public ProductDto addProduct(ProductDto productDto, Long categoryId) {
		// check product is already present or not

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));
		Product product = modelMapper.map(productDto, Product.class);

		boolean isProductNotPresent = true; // assuming product is not present
		List<Product> products = category.getProducts();
		for (Product productvale : products) {
			if (productvale.getProductName().trim().equalsIgnoreCase(product.getProductName().trim())) {
				isProductNotPresent = false;// yes product is present
				break;
			}

		}

		if (isProductNotPresent) {
			product.setImage("default.png");
			product.setCategory(category);

			double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
			product.setSpecialPrice(specialPrice);

			Product savedProduct = productRepository.save(product);

			return modelMapper.map(savedProduct, ProductDto.class);
		} else {
			throw new APIException("Product already exist!!");
		}
	}

	@Override
	public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productsPage = productRepository.findAll(pageable);
		List<Product> products = productsPage.getContent();
		if (products.isEmpty()) {
			throw new APIException("No Products exists");
		}
//		System.out.println(products);

		List<ProductDto> productDtos = products.stream().map(product -> modelMapper.map(product, ProductDto.class))
				.collect(Collectors.toList());
//		System.out.println(productDtos);
		ProductResponse productResponse = new ProductResponse();
		productResponse.setContents(productDtos);
		productResponse.setPageNumber(productsPage.getNumber());
		productResponse.setPageSize(productsPage.getSize());
		productResponse.setTotalElements(productsPage.getTotalElements());
		productResponse.setTotalPages(productsPage.getTotalPages());
		productResponse.setLastPage(productsPage.isLast());
//		System.out.println(productResponse);

		return productResponse;
	}

	@Override
	public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder) {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("category", "categoryId", categoryId));

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		// check apply to check the products available or not
		Page<Product> productsPage = productRepository.findByCategoryOrderByPriceAsc(category, pageable);
		List<Product> products = productsPage.getContent();
		if (products.isEmpty()) {
			throw new APIException(category.getCategoryName() + " category does not have any products");
		}
		List<ProductDto> productDtos = products.stream().map(product -> modelMapper.map(product, ProductDto.class))
				.collect(Collectors.toList());
		ProductResponse productResponse = new ProductResponse();
		productResponse.setContents(productDtos);
		productResponse.setPageNumber(productsPage.getNumber());
		productResponse.setPageSize(productsPage.getSize());
		productResponse.setTotalElements(productsPage.getTotalElements());
		productResponse.setTotalPages(productsPage.getTotalPages());
		productResponse.setLastPage(productsPage.isLast());
		return productResponse;
	}

	@Override
	public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		String keywordWithWildcard = "%" + keyword + "%";
		Page<Product> productsPage = productRepository.findByProductNameLikeIgnoreCase(keywordWithWildcard, pageable);
		List<Product> products = productsPage.getContent();
		if (products.isEmpty()) {
			throw new APIException("Products not found with keyword: " + keyword);
		}
		List<ProductDto> productDtos = products.stream().map(product -> modelMapper.map(product, ProductDto.class))
				.collect(Collectors.toList());
		ProductResponse productResponse = new ProductResponse();
		productResponse.setContents(productDtos);
		productResponse.setPageNumber(productsPage.getNumber());
		productResponse.setPageSize(productsPage.getSize());
		productResponse.setTotalElements(productsPage.getTotalElements());
		productResponse.setTotalPages(productsPage.getTotalPages());
		productResponse.setLastPage(productsPage.isLast());
		return productResponse;

	}

	@Override
	public ProductDto updateProduct(ProductDto productDto, Long productId) {
		// get the product from db
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));

		Product product = modelMapper.map(existingProduct, Product.class);
		// update the product with the request payload

		existingProduct.setProductName(product.getProductName());
		existingProduct.setDescription(product.getDescription());
		existingProduct.setQuantity(product.getQuantity());
		existingProduct.setDiscount(product.getDiscount());
		existingProduct.setPrice(product.getPrice());
		existingProduct.setSpecialPrice(product.getSpecialPrice());

		// save to db
		Product savedProduct = productRepository.save(existingProduct);

		List<Cart> carts = cartRepository.findCartsByProductId(productId);

		List<CartDto> cartDtos = carts.stream().map(cart -> {
			CartDto cartDto = modelMapper.map(cart, CartDto.class);

			List<ProductDto> productDtos = cart.getCartItems().stream()
					.map(p -> modelMapper.map(p.getProduct(), ProductDto.class)).collect(Collectors.toList());

			cartDto.setProducts(productDtos);
			return cartDto;

		}).toList();
		
		cartDtos.forEach(cartDto -> cartService.updateProductInCarts(cartDto.getCartId(), productId) );

		return modelMapper.map(savedProduct, ProductDto.class);
	}

	@Override
	public ProductDto deleteProduct(Long productId) {
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));
		
		List<Cart> carts = cartRepository.findCartsByProductId(productId);
		 carts.forEach(cart -> cartService.deleteProudctFromCart(cart.getCartId(), productId));
		productRepository.delete(existingProduct);

		return modelMapper.map(existingProduct, ProductDto.class);
	}

	@Override
	public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
		// get product from db
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));

		// upload image to server
		// get the file name of uploaded image
		String fileName = fileService.uploadImage(path, image);

		// updating new file name to the product
		existingProduct.setImage(fileName);
		// save the updated product to the db
		Product savedproduct = productRepository.save(existingProduct);
		// return Dto after product to DTO

		return modelMapper.map(savedproduct, ProductDto.class);
	}

}
