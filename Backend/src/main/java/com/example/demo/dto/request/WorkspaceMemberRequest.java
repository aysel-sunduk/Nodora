package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkspaceMemberRequest {

    // Workspace ID'sinin boş (null) olamayacağını belirtir.
    @NotNull(message = "Workspace ID boş bırakılamaz.")
    private Integer workspaceId;

    // Davet edilecek e-posta adresinin boş olamayacağını ve geçerli bir e-posta formatında olmasını sağlar.
    @NotBlank(message = "E-posta adresi boş bırakılamaz.")
    @Email(message = "Geçerli bir e-posta adresi girin.")
    private String memberEmail;
}
