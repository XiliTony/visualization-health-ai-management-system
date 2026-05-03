package com.noitidart.api.utils;

import com.noitidart.api.exception.CustomException;

import java.util.Collection;

/**
 * 断言工具类
 */
public class AssertUtils {

    /**
     * 断言对象不为空，如果传进来的对象是null，抛出异常
     *
     * @param object 待检查对象
     * @param message 错误信息
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new CustomException("500", message);
        }
    }

    /**
     * 断言字符串不为空，如果传进来的字符串是null或者空字符串，抛出异常
     *
     * @param text 待检查字符串
     * @param message 报错信息
     */
    public static void hasText(String text, String message) {
        if (text == null || text.trim().isEmpty()) {
            throw new CustomException("500", message);
        }
    }

    /**
     * 断言条件为true，如果传进来的布尔表达式为false，抛出异常
     *
     * @param expression 布尔表达式
     * @param message 错误信息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new CustomException("500", message);
        }
    }

    /**
     * 断言条件为false，如果传进来的布尔表达式为true，抛出异常
     *
     * @param expression 布尔表达式
     * @param message 错误信息
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new CustomException("500", message);
        }
    }

    /**
     * 断言数值大于0，如果传进来的数值是null或者小于等于0，抛出异常
     *
     * @param number 数值
     * @param message 错误信息
     */
    public static void greaterThanZero(Number number, String message) {
        if (number == null || number.doubleValue() <= 0) {
            throw new CustomException("500", message);
        }
    }

    /**
     * 断言数值大于等于0，如果传进来的数值是null或者小于0，抛出异常
     *
     * @param number 数值
     * @param message 错误信息
     */
    public static void notNegative(Number number, String message) {
        if (number == null || number.doubleValue() < 0) {
            throw new CustomException("500", message);
        }
    }

    /**
     * 断言对象相等，如果传进来的期待值为空或实际值与期待值不相等，抛出异常
     *
     * @param expected 期待值
     * @param actual 实际值
     * @param message 错误信息
     */
    public static void equals(Object expected, Object actual, String message) {
        if (expected == null || !expected.equals(actual)) {
            throw new CustomException("500", message);
        }
    }

    /**
     * 断言对象不相等，如果传进来的期待值为空或实际值与期待值相等，抛出异常
     *
     * @param unexpected 期待值
     * @param actual 实际值
     * @param message 错误信息
     */
    public static void notEquals(Object unexpected, Object actual, String message) {
        if (unexpected != null && unexpected.equals(actual)) {
            throw new CustomException("500", message);
        }
    }

    /**
     * 断言集合不为空（集合不能为null，也不能没有元素）
     *
     * @param collection 集合
     * @param message    错误信息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new CustomException("500", message);
        }
    }
}
