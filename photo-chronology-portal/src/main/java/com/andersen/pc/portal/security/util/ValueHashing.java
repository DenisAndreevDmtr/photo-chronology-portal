package com.andersen.pc.portal.security.util;

import org.apache.commons.codec.digest.DigestUtils;

public class ValueHashing {
    public static String getHash(String value) {
        return DigestUtils.sha256Hex(value);
    }
}
