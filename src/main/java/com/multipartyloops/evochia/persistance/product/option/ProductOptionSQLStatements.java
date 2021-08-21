package com.multipartyloops.evochia.persistance.product.option;

public final class ProductOptionSQLStatements {

    static final String PRODUCT_OPTIONS_SELECT_BY_ID = "SELECT * FROM product_options WHERE option_id=?";
    static final String PRODUCT_OPTIONS_SELECT_BY_PRODUCT_ID = "SELECT * FROM product_options WHERE product_id=?";
    static final String PRODUCT_OPTIONS_INSERTION = "INSERT INTO product_options (option_id, product_id, variation, price) VALUES (?, ?, ?, ?)";
    static final String PRODUCT_OPTIONS_DELETE_BY_OPTION_ID = "DELETE FROM product_options WHERE option_id=?";
    static final String PRODUCT_OPTION_DELETE_ALL_BY_PRODUCT_ID = "DELETE FROM product_options WHERE product_id=?";
    static final String PRODUCT_OPTION_UPDATE = "UPDATE product_options SET variation=?, price=? WHERE option_id=?";

    private ProductOptionSQLStatements() {
    }
}
