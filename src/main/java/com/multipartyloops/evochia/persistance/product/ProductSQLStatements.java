package com.multipartyloops.evochia.persistance.product;

public final class ProductSQLStatements {

    public static final String PRODUCT_INSERTION = "INSERT INTO product (product_id, category_id, name, description, price, enabled) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String PRODUCT_DELETE_BY_ID = "DELETE FROM product WHERE product_id=?";
    public static final String PRODUCTS_SELECT_ALL = "SELECT * FROM product";
    public static final String PRODUCT_SELECT_ALL_BY_CATEGORY = "SELECT * FROM product WHERE category_id=?";
    public static final String PRODUCT_SELECT_ALL_ENABLED_BY_CATEGORY = "SELECT * FROM product WHERE category_id=? AND enabled=?";
    public static final String PRODUCT_SELECT_BY_PRODUCT_ID = "SELECT * FROM product WHERE product_id=?";

    private ProductSQLStatements() {
    }
}
