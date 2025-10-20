package com.example.demoToDo.model;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {
     @NotBlank
     private String email;
     @NotBlank
     private String password;
     private String name;
     
     public String getEmail() {
         return email;
     }
     public void setEmail(String email) {
         this.email = email;
     }
     public String getPassword() {
         return password;
     }
     public void setPassword(String password) {
         this.password = password;
     }

     public void setName(String name) { this.name = name; }
     public String getName() { return name; }
}
