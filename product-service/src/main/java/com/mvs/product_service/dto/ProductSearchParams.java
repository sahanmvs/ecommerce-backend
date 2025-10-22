package com.mvs.product_service.dto;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Data
public class ProductSearchParams {
    private int page = 0;
    private int size = 10;
    private String sortDirection = "DESC";
    private String sortBy = "updatedAt";
    private String query;
    private String category;
    private String startDate;
    private String endDate;
    private Long minPrice;
    private Long maxPrice;

    public Criteria getProductSearchCriteria() {
        Criteria finalCriteria = new Criteria();

        if (StringUtils.isNotBlank(query)) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("name").regex(query, "i"),
                    Criteria.where("description").regex(query, "i")
            );
            finalCriteria = and(finalCriteria, searchCriteria);
        }

        if (StringUtils.isNotBlank(category)) {
            finalCriteria = and(finalCriteria, Criteria.where("category").is(category));
        }

        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            Criteria dateCriteria = new Criteria().andOperator(
                    Criteria.where("updatedAt")
                            .gte(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(startDate)))
                            .lte(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(endDate)))
            );
            finalCriteria = and(finalCriteria, dateCriteria);
        }

        if (minPrice != null && maxPrice != null) {
            Criteria priceCriteria = new Criteria().andOperator(
                    Criteria.where("price").gte(minPrice).lte(maxPrice)
            );
            finalCriteria = and(finalCriteria, priceCriteria);
        }

        return finalCriteria;
    }

    Criteria and(Criteria existing, Criteria next) {
        if (existing == null || existing.getCriteriaObject().isEmpty()) {
            return next;
        }
        return new Criteria().andOperator(existing, next);
    }

    public Sort.Direction getSortDirection() {
        if (this.sortDirection.equals("ASC")) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }
}
