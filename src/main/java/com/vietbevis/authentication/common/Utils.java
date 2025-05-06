package com.vietbevis.authentication.common;

import java.util.Random;

public class Utils {

  public static String generateRandomNumber(int length) {
    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(random.nextInt(10));
    }
    return sb.toString();
  }
}
