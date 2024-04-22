package com.andersen.pc.portal.repository;

import com.andersen.pc.common.model.dto.request.UserSearchRequest;
import com.andersen.pc.common.model.entity.User;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.andersen.pc.common.model.entity.QUser.user;

@AllArgsConstructor
@Repository
public class UserSearchRepository implements QueryDslRepository {

    @Qualifier("jpaQueryFactoryBean")
    private final JPAQueryFactory queryFactory;

    public QueryResults<User> getUsersBySearchParameter(
            UserSearchRequest userSearchRequest,
            Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(
                pageable.getSort(),
                user.id.asc());
        return queryFactory.select(user)
                .from(user)
                .where(setSearchParameterPredicate(userSearchRequest))
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
    }

    private BooleanExpression setSearchParameterPredicate(UserSearchRequest userSearchRequest) {
        String searchParameter = userSearchRequest.searchParameter();
        if (StringUtils.isNotEmpty(searchParameter)) {
            return user.name.like("%" + searchParameter + "%").or(user.email.like("%" + searchParameter + "%"));
        }
        return Expressions.TRUE;
    }
}