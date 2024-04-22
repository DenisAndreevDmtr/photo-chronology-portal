package com.andersen.pc.portal.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.andersen.pc.common.constant.Constant.Table.COUNTER;

interface QueryDslRepository {

    default List<OrderSpecifier<?>> getOrderSpecifiers(Sort sort, OrderSpecifier<?> defaultOrderSpecifier) {
        if (sort.isSorted()) {
            return getOrderSpecifiersFromSort(sort);
        } else {
            return getOrderSpecifiersWithDefault(defaultOrderSpecifier);
        }
    }

    private <T> List<OrderSpecifier<?>> getOrderSpecifiersFromSort(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if (property.toLowerCase().contains(COUNTER)) {
                continue;
            }
            Expression<?> propertyPath = Expressions.path(Objects.class, property);
            OrderSpecifier<?> orderSpecifier = new OrderSpecifier(
                    order.isAscending() ? com.querydsl.core.types.Order.ASC :
                            com.querydsl.core.types.Order.DESC, propertyPath
            );
            orderSpecifiers.add(orderSpecifier);
        }
        return orderSpecifiers;
    }

    private List<OrderSpecifier<?>> getOrderSpecifiersWithDefault(OrderSpecifier<?> defaultOrderSpecifier) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        orderSpecifiers.add(defaultOrderSpecifier);
        return orderSpecifiers;
    }
}
