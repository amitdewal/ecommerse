package com.ecommerce.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDto;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Category API", description = "Operations related to managing product categories")

@RestController
@RequestMapping("/api")
public class CategoryController {

	private CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}

	@GetMapping("/echo")
	public ResponseEntity<String> echoMessage(@RequestParam(name = "message", required = false) String message) {
		return new ResponseEntity<>("Echoed message " + message, HttpStatus.OK);
	}

	@Operation(
		    summary = "Get All Categories",
		    description = "Fetches the complete list of product categories available in the system.",
		    responses = {
		        @ApiResponse(
		            responseCode = "200",
		            description = "Successfully fetched all categories",
		            content = @Content(mediaType = "application/json",
		                schema = @Schema(implementation = CategoryResponse.class)
		            )
		        ),
		        @ApiResponse(responseCode = "401", description = "Unauthorized"),
		        @ApiResponse(responseCode = "500", description = "Internal Server Error")
		    }
		)
	@GetMapping("/public/categories")
	public ResponseEntity<CategoryResponse> getAllCategories(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
		CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
		return new ResponseEntity<>(categoryResponse, HttpStatus.OK);

	}

	@Operation(summary = "Create Category",description = "API to create new category")
	@PostMapping("/public/categories")
	public ResponseEntity<CategoryDto> createCategbory(@Valid @RequestBody CategoryDto categoryDto) {
		CategoryDto savedCategoryDto = categoryService.createCategory(categoryDto);
		return new ResponseEntity<>(savedCategoryDto, HttpStatus.CREATED);

	}

	@DeleteMapping("/admin/categories/{categoryId}")
	public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
		CategoryDto deleteCategoryDto = categoryService.deleteCategory(categoryId);
		return new ResponseEntity<>(deleteCategoryDto, HttpStatus.OK);

	}

	@PutMapping("/public/categories/{categoryId}")
	public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto,
			@PathVariable Long categoryId) {

		CategoryDto updateCategoryDto = categoryService.updateCategory(categoryDto, categoryId);
		return new ResponseEntity<>(updateCategoryDto, HttpStatus.OK);

	}

}
