package com.jhcs.booklore.application.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @NotEmpty(message = "O primeiro nome é obrigatório") @NotNull(message = "O primeiro nome é obrigatório") String firstName,
        @NotEmpty(message = "O sobrenome é obrigatório") @NotNull(message = "O sobrenome é obrigatório") String lastName,
        @Email(message = "O e-mail não está bem formatado") @NotEmpty(message = "O e-mail é obrigatório") @NotNull(message = "O e-mail é obrigatório") String email,
        @NotEmpty(message = "A senha é obrigatória") @NotNull(message = "A senha é obrigatória") @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String password) {}