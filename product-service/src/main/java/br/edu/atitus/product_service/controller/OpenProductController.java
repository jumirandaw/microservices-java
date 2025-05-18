package br.edu.atitus.product_service.controller;

import br.edu.atitus.product_service.entities.Product;
import br.edu.atitus.product_service.repositories.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/products")
public class OpenProductController {

    private final ProductRepository repository;

    @Value("${server.port}")
    private String port;

    public OpenProductController(ProductRepository repository) {
        this.repository = repository;
    }

    // Adiciona produto de teste se o banco estiver vazio
    @PostConstruct
    public void initData() {
        if (repository.count() == 0) {
            Product p = new Product();
            p.setName("Camiseta Preta");
            p.setPrice(49.99);
            repository.save(p);
        }
    }

    @GetMapping("/{id}/{currency}")
    public Product findProduct(@PathVariable Long id, @PathVariable String currency) {
        Optional<Product> product = repository.findById(id);

        if (product.isPresent()) {
            Product p = product.get();
            p.setEnvironment("Product-Service PORT: " + port);
            p.setConvertedPrice(p.getPrice()); // Simulação de conversão
            return p;
        }

        // Retorna 404 se não encontrar
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id: " + id);
    }
}
