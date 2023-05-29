package com.group.libraryapp.dto.user.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

public class UserCreateRequest {


  public UserCreateRequest(String name, Integer age) {
    this.name = name;
    this.age = age;
  }

  private String name;
  private Integer age;

  public String getName() {
    return name;
  }

  public Integer getAge() {
    return age;
  }

}
