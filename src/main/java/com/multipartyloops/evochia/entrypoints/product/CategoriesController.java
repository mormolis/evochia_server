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
@RequestMapping(value = "/product/categories")
public class CategoriesController {

    private final ProductCategoryService productCategoryService;

    public CategoriesController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @RequestMapping(method = RequestMethod.GET)
//    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<List<ProductCategoryDto>> allCategories(@RequestHeader Map<String,String> headers) {

        List<ProductCategoryDto> categories = productCategoryService.getAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @RequestMapping(value="/category/add", method = RequestMethod.POST)
    //    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<CreateProductCategoryResponse> add(@RequestHeader Map<String,String> headers, @RequestBody CreateProductCategoryRequestBody body){
        String id = productCategoryService.addNewCategory(new ProductCategoryDto(null, body.getProductCategoryName(), body.isEnabled()));
        return new ResponseEntity<>(new CreateProductCategoryResponse(id), HttpStatus.CREATED);
    }

    @RequestMapping(value="/category", method = RequestMethod.DELETE)
    //    @AuthRequirement(allow = {Roles.ADMIN})
    public ResponseEntity<Void> delete(@RequestHeader Map<String,String> headers, @RequestBody DeleteProductCategoryRequestBody body){
        productCategoryService.delete(body.getProductCategoryId());
        return new ResponseEntity<>( HttpStatus.NO_CONTENT);
    }
}
