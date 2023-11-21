package com.test.pulsar.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * If you DON'T use Lombok you will need to add the constructor like this
 * <snippet>
 * #   public static class User {
 * #    String name;
 * #    int age;
 * #    public User() { }
 * #    public User(String name, int age) { this.name = name; this.age = age; } }
 * #   }
 * </snippet>
 */
public class User1 {
    public String name;
    public int age;
}
