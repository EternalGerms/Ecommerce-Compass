package com.ecommerce.ecomm.exception;

public enum ErrorCode {
    RESOURCE_NOT_FOUND(404, "NOT_FOUND", "Recurso não encontrado"),
    NO_PRODUCTS_AVAILABLE(404, "NOT_FOUND", "Nenhum produto disponível para listagem"),
    NO_SALES_AVAILABLE(404, "NOT_FOUND", "Nenhuma venda disponível para listagem"),
    PRODUTO_INATIVO(400, "BAD_REQUEST", "Produto não pode ser excluído pois possui vendas associadas. Produto inativado."),
    PRODUTO_INATIVO_SALES(400, "BAD_REQUEST", "Não é possível criar uma venda para um produto inativo."),
    INSUFFICIENT_STOCK(400, "BAD_REQUEST", "Estoque insuficiente"),
    INVALID_DATE(400, "BAD_REQUEST", "Data inválida, verifique novamente"),
    INVALID_PRODUCT_STOCK(400, "BAD_REQUEST", "Estoque do produto com valor negativo ou inválido, verifique e tente novamente"),
    INVALID_SALES_QUANTITY(400, "BAD_REQUEST", "Quantidade inválida, verifique e tente novamente"),
    INVALID_PRODUCT_PRICE(400, "BAD_REQUEST", "Preço inválido, verifique e tente novamente."),
    NO_CONTENT(204, "NO_CONTENT", "Nenhum conteúdo disponível"),
    NO_SALES_IN_PERIOD(404, "NOT_FOUND", "Nenhuma venda no período"),
    VENDA_NOT_FOUND(404, "NOT_FOUND", "Venda não encontrada"),
    PRODUCT_NOT_FOUND(404, "NOT_FOUND", "Produto não encontrado"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "Ocorreu um erro inesperado"),
    UNAUTHORIZED_ACCESS(403, "FORBIDDEN", "Acesso não autorizado"),
    EMAIL_NOT_FOUND(404, "NOT_FOUND", "Email não encontrado"),
    INVALID_TOKEN(400, "BAD_REQUEST", "Token inválido ou expirado."),
    INVALID_LOGIN(401, "UNAUTHORIZED", "Login inválido"),
    USERNAME_ALREADY_EXISTS(409, "CONFLICT", "Nome de usuário já existente/utilizado"),
    INVALID_REGISTRATION(400, "BAD_REQUEST", "Registro inválido"),
    USER_ROLE_NO_PERMISSION(403, "FORBIDDEN", "Usuário com role USER sem permissão para criar um produto"),
    USER_NOT_AUTHENTICATED(401, "UNAUTHORIZED", "Usuário não autenticado"), // Added line
    USER_NOT_FOUND(404, "NOT_FOUND", "Usuário não encontrado"), // Added line
    ROLE_NOT_FOUND(404, "NOT_FOUND", "Role não encontrada"), // Added line
    TOKEN_EXPIRED(400, "BAD_REQUEST", "Token expirado"); // Added line

    private final int code;
    private final String status;
    private final String message;

    ErrorCode(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
