package com.micropos.products.web;


import com.micropos.products.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.micropos.products.biz.PosService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import com.micropos.api.dto.OrderDTO;
import com.micropos.api.dto.OrderItemDTO;
import com.micropos.api.dto.ProductDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;


@RestController
public class PosController {

    private PosService posService;
    private RestTemplate restTemplate;
    @Autowired
    public void setPosService(PosService posService) {
        this.posService = posService;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    private ModelMapper modelMapper;
    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @GetMapping("/product")
    //@Cacheable(value = "products")
    public ResponseEntity<List<EntityModel<ProductDTO>>> listProducts() {

        List<Product> products = posService.products();
        List<EntityModel<ProductDTO>> resource = products.stream()
                .map(product -> EntityModel.of(modelMapper.map(product, ProductDTO.class),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).showProductById(product.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).listProducts()).withRel("product")))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(resource);//CollectionModel.of(resource,
                //WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).listProducts()).withSelfRel()));
    }
    @GetMapping("/product/clear")
    public void clearCache(){
        posService.clearCache();
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<EntityModel<ProductDTO>> showProductById(@PathVariable String productId) {
        Product product = posService.getProductById(productId);

        if (product != null) {

            EntityModel<ProductDTO> resource = EntityModel.of(modelMapper.map(product, ProductDTO.class));

        // 添加 self 链接
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).showProductById(productId)).withSelfRel());

            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).listProducts()).withRel("product"));

            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
/*
    @PutMapping("/product/{productId}")
    public ResponseEntity<?> updateProduct(@RequestBody Product product, @PathVariable String productId) {
        Product productToUpdate = product;
        productToUpdate.setId(productId);
        posService.updateProduct(productToUpdate);

        Link newLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).showProductById(productId)).withSelfRel();
        try{
            return ResponseEntity.noContent().location(new URI(newLink.getHref())).build();
        }catch (URISyntaxException e){
            return ResponseEntity.badRequest().body("Unable to update " + productToUpdate);
        }

    }*/
    @PatchMapping("/product/{productId}")
    public ResponseEntity<?> updateProductQuantity(@PathVariable String productId, @RequestParam Map<String, Integer> data) {
        Product productToUpdate = posService.getProductById(productId);
        // String str = data.toString();
        // str = str.substring(str.indexOf(':')+1,str.indexOf('}'));
        // productToUpdate.setQuantity(Integer.parseInt(str));
        // posService.updateProduct(productToUpdate);
        EntityModel<Product> resource = EntityModel.of(productToUpdate);

        // resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).showProductById(productId)).withSelfRel());

        // resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).listProducts()).withRel("product"));

//        Link newLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).showProductById(productId)).withSelfRel();
//        try{
//            return ResponseEntity.noContent().location(new URI(newLink.getHref())).build();
//        }catch (URISyntaxException e){
//            return ResponseEntity.badRequest().body("Unable to update " + productToUpdate);
//        }
        return ResponseEntity.ok(resource);
    }
    /* 
    @PostMapping("/product")
    public ResponseEntity<?> NewProduct(@RequestBody Product product) {
        try{
            Product newproduct = posService.updateProduct(product);

            EntityModel<Product> resource = EntityModel.of(newproduct);

            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PosController.class).showProductById(newproduct.getId())).withSelfRel());

            return ResponseEntity
                    .created(new URI(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
                    .body(resource);
        }catch (URISyntaxException e){
            return ResponseEntity.badRequest().body("Unable to update " + product);
        }

    }
*/
    @GetMapping("/settings")
    public ResponseEntity<List<Settings>> getSettings() {
        List<Settings> settings = posService.getSettings();
        return ResponseEntity.ok().body(settings);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Categories>> getCategories() {
        List<Categories> categories = posService.getCategories();

        return ResponseEntity.ok().body(categories);
    }

    @PatchMapping("/product/charge")
    @CircuitBreaker(name = "products-breaker", fallbackMethod = "fail")
    public ResponseEntity<String> charge(@RequestBody ChargeRequest request) {
        OrderDTO orderDTO = new OrderDTO("user0", 0, new ArrayList<>(), "fin");

        double total = 0;
        for (ProductUpdateRequest p : request.getPur()) {
            System.out.println(p.getProductId() + " " + p.getQuantity());
            Product product = posService.getProductById(p.getProductId());

            if (product == null || product.getQuantity() < p.getQuantity()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("take Product failed!");
            }
            total += Double.parseDouble(product.getPrice()) * p.getQuantity();
            orderDTO.getItems().add(new OrderItemDTO(p.getProductId(), p.getQuantity(), Double.parseDouble(product.getPrice())));
        }
        orderDTO.setAmount(total);

        //RestTemplate HERE
        String ret = restTemplate.postForObject("http://webpos-orders/createOrder", orderDTO, String.class);

        request.getPur().forEach(p -> {posService.updateProduct(p.getProductId(), posService.getProductById(p.getProductId()).getQuantity() - p.getQuantity());});
        return ResponseEntity.ok("Data updated successful! orders return : " + ret);
    }


    public ResponseEntity<String> fail(ChargeRequest request, Throwable throwable) {
        System.out.println("fallback method called.");
        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update data :" + throwable.getMessage());
    }
}
