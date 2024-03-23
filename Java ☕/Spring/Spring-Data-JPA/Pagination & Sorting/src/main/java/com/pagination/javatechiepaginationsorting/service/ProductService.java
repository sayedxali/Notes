package com.pagination.javatechiepaginationsorting.service;

import com.pagination.javatechiepaginationsorting.model.Product;
import com.pagination.javatechiepaginationsorting.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @PostConstruct
    public void initDB() {
        List<Product> productList = IntStream.rangeClosed(1, 200)
                .mapToObj(value -> new Product("product" + value, new Random().nextInt(100), new Random().nextLong(50000)))
                .toList();
        this.productRepository.saveAll(productList);
    }

    public List<Product> findAllProducts() {
        return this.productRepository.findAll();
    }


    // dynamic sorting
    public List<Product> findProductsWithSorting(String field) {
        return this.productRepository.findAll(Sort.by(
                Sort.Direction.DESC,
                field
        ));
    }


    // pagination
    // pageSize: item per page / the number of record you want to view per page
    // offset: the next element / the next page
    public Page<Product> findProductsWithPagination(int offset, int pageSize) {
        return this.productRepository.findAll(PageRequest.of(
                offset,
                pageSize
        ));
    }

    // pagination & sorting
    public Page<Product> findProductsWithPaginationAndSorting(int offset, int pageSize, String field) {
        return this.productRepository.findAll(
                PageRequest.of(
                        offset,
                        pageSize
                ).withSort(Sort.by(field))
        );
    }

}
