package bank.recommendationservice.fintech.other;

import bank.recommendationservice.fintech.exception.UnknownProductTypeException;

public enum ProductType {
    DEBIT("DEBIT"),
    CREDIT("CREDIT"),
    INVEST("INVEST"),
    SAVING("SAVING");

    private final String type;

    ProductType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ProductType fromString(String productType) {
        for (ProductType type : ProductType.values()) {
            if (type.getType().equals(productType)) {
                return type;
            }
        }
        throw new UnknownProductTypeException("Неизвестный тип продукта: " + productType);
    }

}