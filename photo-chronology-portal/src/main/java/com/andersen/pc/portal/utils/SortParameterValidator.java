package com.andersen.pc.portal.utils;

import com.andersen.pc.common.exception.DomainObjectValidationException;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;

import static com.andersen.pc.common.constant.Constant.Errors.INVALID_SORT_PARAMETER;


@UtilityClass
public class SortParameterValidator {

    public static void checkSortParameter(Class<?> objectClass, Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                String parameterSort = order.getProperty();
                Class<?> checkedClass = objectClass;

                for (String part : parameterSort.split("\\.")) {
                    Optional<Class<?>> fieldClass = getFieldClass(checkedClass, part);
                    if (fieldClass.isEmpty()) {
                        throw new DomainObjectValidationException(INVALID_SORT_PARAMETER);
                    }
                    checkedClass = fieldClass.get();
                }
            }
        }
    }

    private static Optional<Class<?>> getFieldClass(Class<?> objectClass, String fieldName) {
        try {
            Field field = objectClass.getDeclaredField(fieldName);
            return Optional.of(checkField(field));
        } catch (NoSuchFieldException e) {
            Class<?> superClass = objectClass.getSuperclass();
            if (superClass != null) {
                return getFieldClass(superClass, fieldName);
            }
            return Optional.empty();
        }
    }

    private static Class<?> checkField(Field field) {
        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            return (Class<?>) genericType.getActualTypeArguments()[0];
        }
        return field.getType();
    }
}