package com.multipartyloops.evochia.entrypoints.product;

import com.multipartyloops.evochia.core.product.category.ProductCategoryService;
import com.multipartyloops.evochia.core.product.entities.ProductCategoryDto;
import com.multipartyloops.evochia.entrypoints.product.entities.CreateProductCategoryRequestBody;
import com.multipartyloops.evochia.entrypoints.product.entities.CreateProductCategoryResponse;
import com.multipartyloops.evochia.entrypoints.product.entities.DeleteProductCategoryRequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/products/categories")
public class CategoriesController {

    private final ProductCategoryService productCategoryService;

    public CategoriesController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @RequestMapping(method = RequestMethod.GET)
//    @AuthRequirement
    public ResponseEntity<List<ProductCategoryDto>> allCategories(@RequestHeader Map<String, String> headers) {

        List<ProductCategoryDto> categories = productCategoryService.getAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @RequestMapping(value = "/category/{product_category_id}", method = RequestMethod.GET)
//    @AuthRequirement
    public ResponseEntity<ProductCategoryDto> getById(@RequestHeader Map<String, String> headers, @PathVariable("product_category_id") String productCategoryId) {

        ProductCategoryDto productCategoryDto = productCategoryService.getById(productCategoryId);
        return new ResponseEntity<>(productCategoryDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/enabled", method = RequestMethod.GET)
//    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<List<ProductCategoryDto>> allEnabledCategories(@RequestHeader Map<String, String> headers) {

        List<ProductCategoryDto> categories = productCategoryService.getAllEnabledCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @RequestMapping(value = "/category/update", method = RequestMethod.PATCH)
    //    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<Void> update(@RequestHeader Map<String, String> headers, @RequestBody ProductCategoryDto body) {
        productCategoryService.update(body.getProductCategoryId(), body.getProductCategoryName(), body.isEnabled());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/category/enable/{product_category_id}", method = RequestMethod.PATCH)
    //    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<Void> enableCategory(@RequestHeader Map<String, String> headers, @PathVariable("product_category_id") String productCategoryId) {
        productCategoryService.enableById(productCategoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/category/disable/{product_category_id}", method = RequestMethod.PATCH)
    //    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<Void> disableCategory(@RequestHeader Map<String, String> headers, @PathVariable("product_category_id") String productCategoryId) {
        productCategoryService.disableById(productCategoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @RequestMapping(value = "/category/add", method = RequestMethod.POST)
    //    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<CreateProductCategoryResponse> add(@RequestHeader Map<String, String> headers, @RequestBody CreateProductCategoryRequestBody body) {
        String id = productCategoryService.addNewCategory(new ProductCategoryDto(null, body.getProductCategoryName(), body.isEnabled()));
        return new ResponseEntity<>(new CreateProductCategoryResponse(id), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/category", method = RequestMethod.DELETE)
    //    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<Void> delete(@RequestHeader Map<String, String> headers, @RequestBody DeleteProductCategoryRequestBody body) {
        productCategoryService.delete(body.getProductCategoryId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
