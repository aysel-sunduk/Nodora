package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank(message = "Ad boş olamaz")
    @Size(max = 20, message = "Ad en fazla 20 karakter olabilir")
    private String name;

    @NotBlank(message = "Soyad boş olamaz")
    @Size(max = 30, message = "Soyad en fazla 30 karakter olabilir")
    private String surname;

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(max = 20, message = "Kullanıcı adı en fazla 20 karakter olabilir")
    private String memberName;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
}
