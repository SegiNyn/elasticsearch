/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.test;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TestMatchers extends Matchers {

    public static Matcher<Path> pathExists(Path path, LinkOption... options) {
        return new CustomMatcher<Path>("Path " + path + " exists") {
            @Override
            public boolean matches(Object item) {
                return Files.exists(path, options);
            }
        };
    }

    public static <T> Matcher<Predicate<T>> predicateMatches(T value) {
        return new CustomMatcher<Predicate<T>>("Matches " + value) {
            @Override
            public boolean matches(Object item) {
                if (Predicate.class.isInstance(item)) {
                    return ((Predicate<T>) item).test(value);
                } else {
                    return false;
                }
            }
        };
    }

    public static Matcher<String> matchesPattern(String regex) {
        return matchesPattern(Pattern.compile(regex));
    }

    public static Matcher<String> matchesPattern(Pattern pattern) {
        return predicate("Matches " + pattern.pattern(), String.class, pattern.asPredicate());
    }

    private static <T> Matcher<T> predicate(String description, Class<T> type, Predicate<T> predicate) {
        return new CustomMatcher<T>(description) {
            @Override
            public boolean matches(Object item) {
                if (type.isInstance(item)) {
                    return predicate.test(type.cast(item));
                } else {
                    return false;
                }
            }
        };
    }

}