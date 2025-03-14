package com.example.product_service.service;

import com.example.product_service.dto.ProductRequest;
import com.example.product_service.dto.ProductResponse;
import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository ProductRepository;

    public void createProduct(ProductRequest ProductRequest){
        Product product = Product.builder()
                .name(ProductRequest.getName())
                .description((ProductRequest.getDescription()))
                .price(ProductRequest.getPrice())
                .build();

        ProductRepository.save(product);
        log.info("product {} created", product.getId() );
    }

    public List <ProductResponse> getAllProducts(){
        List<Product> products = ProductRepository.findAll();
        return products.stream().map( this:: mapToProductResponse).toList();
    }

    public ProductResponse mapToProductResponse(Product product){

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
