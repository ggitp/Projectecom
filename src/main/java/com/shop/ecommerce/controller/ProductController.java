package com.shop.ecommerce.controller;

import com.shop.ecommerce.dto.ProductUpsertRequest;
import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.service.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  public Product create(@RequestBody ProductUpsertRequest req) {
    return productService.create(req);
  }

  @PutMapping("/{id}")
  public Product update(@PathVariable int id, @RequestBody ProductUpsertRequest req) {
    return productService.update(id, req);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable int id) {
    productService.delete(id);
  }

  @GetMapping("/{id}")
  public Product get(@PathVariable int id) {
    return productService.getById(id);
  }
}