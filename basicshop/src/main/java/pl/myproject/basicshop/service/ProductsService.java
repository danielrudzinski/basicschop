package pl.myproject.basicshop.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import pl.myproject.basicshop.dto.ProductsDTO;
import pl.myproject.basicshop.dto.UsersDTO;
import pl.myproject.basicshop.mapper.ProductsMapper;
import pl.myproject.basicshop.model.Products;
import pl.myproject.basicshop.model.Users;
import pl.myproject.basicshop.repository.ProductsRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final ProductsMapper productsMapper;

    public ProductsService(ProductsRepository productsRepository, ProductsMapper productsMapper) {
        this.productsRepository = productsRepository;
        this.productsMapper = productsMapper;
    }


    public ResponseEntity<List<ProductsDTO>> getAllProducts() {
        List<ProductsDTO> productsDTOs = productsRepository.findAll().stream()
                .map(productsMapper::apply)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productsDTOs);
    }

    public ResponseEntity<ProductsDTO> getProductsrById(@PathVariable Integer id) {
        return productsRepository.findById(id)
                .map(productsMapper::apply)  // mapujemy Users na UsersDTO
                .map(ResponseEntity::ok)  // opakowanie w ResponseEntity
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    public ResponseEntity<Products> addProducts(@RequestBody Products products) {
        Products savedProducts = productsRepository.save(products);
        UriComponents uriComponents = ServletUriComponentsBuilder.
                fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProducts.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }
    public ResponseEntity<Void>deleteProducts(@PathVariable Integer id ){
        if(!productsRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        productsRepository.deleteById(id);
        return ResponseEntity.noContent().build();

    }
    public ResponseEntity<Products> updateProducts(@PathVariable Integer id, @RequestBody Products updatedProduct) {
        return productsRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setStock(updatedProduct.getStock());

                    return productsRepository.save(existingProduct);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Products> patchProducts(@PathVariable Integer id, @RequestBody Products updatedProduct) {
        return productsRepository.findById(id)
                .map(existingProduct -> {
                    if (updatedProduct.getName() != null) existingProduct.setName(updatedProduct.getName());
                    if (updatedProduct.getDescription() != null) existingProduct.setDescription(updatedProduct.getDescription());
                    if (updatedProduct.getPrice() != null) existingProduct.setPrice(updatedProduct.getPrice());
                    if (updatedProduct.getStock() != null) existingProduct.setStock(updatedProduct.getStock());

                    return productsRepository.save(existingProduct);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



}
