package com.andersen.pc.portal.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;

@UtilityClass
public class KeyGenerator {

    public String generateKeyForPhoto(String folder, String photoName, Long userId) {
        long epochSeconds = Instant.now().getEpochSecond();
        return folder + "/u" + userId + "/" + epochSeconds + photoName;
    }
}