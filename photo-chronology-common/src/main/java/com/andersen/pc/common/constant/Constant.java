package com.andersen.pc.common.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constant {
    @UtilityClass
    public static class Service {
        public static final String PC_PORTAL = "PC_PORTAL";

        @UtilityClass
        public static class Token {
            public static final String TOKEN_TYPE = "bearer";
            public static final String TOKEN_CLAIM_USERNAME = "name";
            public static final String TOKEN_CLAIM_EMAIL = "email";
            public static final String TOKEN_CLAIM_ROLES = "role";
            public static final String AUTHORIZATION = "Authorization";
            public static final String TOKEN_HEADER = "Bearer ";
        }

        @UtilityClass
        public static class Logging {
            public static final String RESULT_ERROR_MESSAGE = "Result of call ==> method: {}, path: {}, {}: {}, " +
                    "result: Failure\nError cause: {}";
            public static final String RESULT_ERROR_MESSAGE_WITH_TRACE = """
                    Result of call ==> method: {}, path: {}, {}: {}, result: Failure
                    Error cause: {}
                    {}
                    """;
            public static final String RESULT_MESSAGE = "Result of call ==> method: {}, path: {}, {}: {}, " +
                    "result: Success";
            public static final String CALL_MESSAGE = "Call endpoint ==> method: {}, path: {}, {}: {}";
        }
    }

    @UtilityClass
    public static class Errors {
        public static final String INVALID_NAME_SIZE = "name.should.contain.from.2.to.50.symbols";
        public static final String INVALID_EMAIL_SIZE = "email.should.be.less.256.symbols";
        public static final String INVALID_EMAIL_FORMAT = "invalid.email.format";
        public static final String NAME_MUST_BE_SET = "name.must.be.set";
        public static final String TITLE_MUST_BE_SET = "title.must.be.set";
        public static final String SEARCH_PARAMETER_SHOULD_BE_SET = "search.parameter.should.be.set";
        public static final String DATE_MUST_BE_SET = "date.must.be.set";
        public static final String INVALID_TITLE_SIZE = "invalid.title.size";
        public static final String DONT_HAVE_PERMISSION_FOR_THAT = "dont.have.permission.for.that";
        public static final String EMAIL_MUST_BE_SET = "email.must.be.set";
        public static final String ERROR_OCCURRED_WHILE_SAVING_PHOTO = "error_occurred_while_saving_photo";
        public static final String SEARCH_PARAMETER_SHOULD_BE_GREATER_2_SYMBOLS = "search.parameter.should.be.greater.2.symbols";
        public static final String PASSWORD_MUST_BE_SET = "password.must.be.set";
        public static final String USER_PASSWORD_NOT_FOUND = "user.password.not.found";
        public static final String USER_NOT_FOUND = "user.not.found";
        public static final String PHOTO_NOT_FOUND = "photo.not.found";
        public static final String TRIP_NOT_FOUND = "trip.not.found";
        public static final String USER_WITH_SUCH_EMAIL_IS_ALREADY_EXISTS = "user.with.such.email.is.already.exists";
        public static final String PASSWORD_NOT_FOUND = "password.not.found";
        public static final String INVALID_CREDENTIALS = "invalid.credentials";
        public static final String PASSWORD_VALIDATION_FAILED = "password.validation.failed";
        public static final String OLD_PASSWORD_MUST_BE_SET = "old.password.must.be.set";
        public static final String NEW_PASSWORD_MUST_BE_SET = "new.password.must.be.set";
        public static final String INCORRECT_OLD_PASSWORD = "incorrect.old.password";
        public static final String UNAUTHORIZED = "unauthorized";
        public static final String INVALID_SORT_PARAMETER = "invalid.sort.parameter";
        public static final String USER_IS_NOT_ACTIVE = "user.is.not.active";
    }

    @UtilityClass
    public static class Configuration {
        public static final String PACKAGES_TO_SCAN = "com.andersen";
    }

    @UtilityClass
    public static class Table {
        public static final String USER = "user";
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DATE = "date";
        public static final String COUNTER = "counter";
    }
}
