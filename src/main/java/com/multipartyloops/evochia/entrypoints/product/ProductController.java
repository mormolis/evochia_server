package com.multipartyloops.evochia.entrypoints.product;

import com.multipartyloops.evochia.core.product.ProductService;
import com.multipartyloops.evochia.core.product.dto.ProductDto;
import com.multipartyloops.evochia.entrypoints.product.entities.ProductCategoryChangeRequestBody;
import com.multipartyloops.evochia.entrypoints.product.entities.ProductPreferredTerminalRequestBody;
import com.multipartyloops.evochia.entrypoints.product.entities.ProductUpdateRequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(method = RequestMethod.GET)
    //    @AuthRequirement
    public ResponseEntity<List<ProductDto>> allProducts(@RequestHeader Map<String, String> headers) {
        List<ProductDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @RequestMapping(value = "/product/{product_id}", method = RequestMethod.GET)
    public ResponseEntity<ProductDto> getProductById(@RequestHeader Map<String, String> headers, @PathVariable("product_id") String productId) {
        ProductDto productDto = productService.getProductById(productId);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/product/add", method = RequestMethod.POST)
    public ResponseEntity<ProductDto> addProduct(@RequestHeader Map<String, String> headers, @RequestBody ProductDto body) {

        ProductDto productDto = productService.addProduct(body);
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/product/delete/{product_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteProduct(@RequestHeader Map<String, String> headers, @PathVariable("product_id") String productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/product/update/{product_id}", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateProduct(@RequestHeader Map<String, String> headers, @PathVariable("product_id") String productId, @RequestBody ProductUpdateRequestBody body) {

        productService.updateProduct(
                productId,
                body.getName(),
                body.getDescription(),
                body.getPrice(),
                body.isEnabled(),
                body.getPreferredTerminalId(),
                body.getProductOptions()
        );

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/by-category/{category_id}", method = RequestMethod.GET)
    public ResponseEntity<List<ProductDto>> getByCategoryId(@RequestHeader Map<String, String> headers, @PathVariable("category_id") String categoryId) {
        List<ProductDto> allProductsByCategory = productService.getAllProductsByCategory(categoryId);
        return new ResponseEntity<>(allProductsByCategory, HttpStatus.OK);
    }

    @RequestMapping(value = "/enabled/by-category/{category_id}", method = RequestMethod.GET)
    public ResponseEntity<List<ProductDto>> getEnabledProductsByCategoryId(@RequestHeader Map<String, String> headers, @PathVariable("category_id") String categoryId) {
        List<ProductDto> allProductsByCategory = productService.getEnabledProductsByCategory(categoryId);
        return new ResponseEntity<>(allProductsByCategory, HttpStatus.OK);
    }

    @RequestMapping(value = "/product/category/update", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updateCategoryId(@RequestHeader Map<String, String> headers, @RequestBody ProductCategoryChangeRequestBody body) {
        productService.changeCategoryOfAProduct(body.getProductId(), body.getDestinationCategoryId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/product/terminal/update", method = RequestMethod.PATCH)
    public ResponseEntity<Void> updatePreferredTerminalId(@RequestHeader Map<String, String> headers, @RequestBody ProductPreferredTerminalRequestBody body) {
        productService.changePreferredTerminalOfAProduct (body.getProductId(), body.getDestinationPreferredTerminalId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
