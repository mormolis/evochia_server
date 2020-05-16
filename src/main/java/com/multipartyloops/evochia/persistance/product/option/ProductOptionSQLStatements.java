package com.multipartyloops.evochia.persistance.product.option;

public final class ProductOptionSQLStatements {

    public static final String PRODUCT_OPTIONS_SELECT_BY_ID = "SELECT * FROM product_options WHERE option_id=?";
    public static final String PRODUCT_OPTIONS_SELECT_BY_PRODUCT_ID = "SELECT * FROM product_options WHERE product_id=?";
    public static final String PRODUCT_OPTIONS_INSERTION = "INSERT INTO product_options (option_id, product_id, variation, price) VALUES (?, ?, ?, ?)";
    public static final String PRODUCT_OPTIONS_DELETE_BY_OPTION_ID = "DELETE FROM product_options WHERE option_id=?";
    public static final String PRODUCT_OPTION_DELETE_ALL_BY_PRODUCT_ID = "DELETE FROM product_options WHERE product_id=?";


    private ProductOptionSQLStatements() {
    }
}
