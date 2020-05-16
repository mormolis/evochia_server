package com.multipartyloops.evochia.persistance.product.category;

public final class ProductCategorySQLStatements {

    public static final String PRODUCT_CATEGORIES_SELECT_ALL = "SELECT * FROM product_categories";
    public static final String PRODUCT_CATEGORIES_SELECT_ALL_BY_ENABLED = "SELECT * FROM product_categories WHERE enabled=?";
    public static final String PRODUCT_CATEGORIES_SELECT_BY_CATEGORY_ID = "SELECT * FROM product_categories WHERE category_id=?";
    public static final String PRODUCT_CATEGORIES_INSERTION = "INSERT INTO product_categories (category_id, name, enabled) VALUES (?, ?, ?)";
    public static final String PRODUCT_CATEGORIES_DELETE_CATEGORY = "DELETE FROM product_categories WHERE category_id=?";


    private ProductCategorySQLStatements(){
    }
}
