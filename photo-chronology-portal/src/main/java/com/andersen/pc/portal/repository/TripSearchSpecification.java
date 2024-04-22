package com.andersen.pc.portal.repository;

import com.andersen.pc.common.model.dto.request.TripSearchRequest;
import com.andersen.pc.common.model.entity.Trip;
import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.andersen.pc.common.constant.Constant.Table.DATE;
import static com.andersen.pc.common.constant.Constant.Table.ID;
import static com.andersen.pc.common.constant.Constant.Table.TITLE;
import static com.andersen.pc.common.constant.Constant.Table.USER;

@AllArgsConstructor
public class TripSearchSpecification implements Specification<Trip>, Serializable {

    private transient TripSearchRequest tripSearchRequest;
    private transient Long userId;

    @Override
    public Predicate toPredicate(
            @Nonnull Root<Trip> root,
            @Nonnull CriteriaQuery<?> query,
            @Nonnull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        setTitleParameterPredicate(predicates, criteriaBuilder, root);
        setDatePredicate(predicates, criteriaBuilder, root);
        setUserIdPredicate(predicates, criteriaBuilder, root);
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setTitleParameterPredicate(List<Predicate> predicates, CriteriaBuilder criteriaBuilder, Root<Trip> root) {
        if (StringUtils.isNotBlank(tripSearchRequest.title())) {
            String searchParameter = tripSearchRequest.title().toLowerCase();
            Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(TITLE)), "%" + searchParameter + "%");
            predicates.add(criteriaBuilder.or(namePredicate));
        }
    }

    private void setDatePredicate(List<Predicate> predicates, CriteriaBuilder criteriaBuilder, Root<Trip> root) {
        LocalDate date = tripSearchRequest.dateFrom();
        if (Objects.nonNull(tripSearchRequest.dateFrom())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get(DATE).as(LocalDate.class),
                    date));
        }
        if (Objects.nonNull(tripSearchRequest.dateTo())) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get(DATE).as(LocalDate.class),
                    date));
        }
    }

    private void setUserIdPredicate(List<Predicate> predicates, CriteriaBuilder criteriaBuilder, Root<Trip> root) {
        Predicate userIdPredicate = criteriaBuilder.equal(root.get(USER).get(ID), userId);
        predicates.add(criteriaBuilder.or(userIdPredicate));
    }
}