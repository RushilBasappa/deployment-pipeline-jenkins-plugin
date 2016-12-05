package com.pearson.deployment.validation

import java.lang.reflect.*
import java.util.regex.Pattern

class Validator {
  protected void validateField(String fieldName, Object value) {
    Field field = this.class.getDeclaredField(fieldName)
    if (field.isAnnotationPresent(ValidString.class)) {
      checkValidString(field, value)
    }
  }

  private void checkValidString(Field field, Object value) {
    def pa = field.getAnnotation(ValidString.class)
    String regexp = pa.regexp()
    if (!value) {
      return
    }
    if (value.class == String && Pattern.matches(regexp, value) ) {
      return
    } else {
      throw new IllegalArgumentException(pa.message())
    }
  }
}
