package com.ecommerce.ecomm.exception;

public enum ErrorCode {
	RESOURCE_NOT_FOUND(404, "Not Found", "Recurso não encontrado"),
	NO_PRODUCTS_AVAILABLE(404, "Not Found", "Nenhum produto disponível para listagem"),
	NO_SALES_AVAILABLE(404, "Not Found", "Nenhuma venda disponível para listagem"),
	PRODUTO_INATIVO(400, "Bad Request",
			"Produto não pode ser excluído pois possui vendas associadas. Produto inativado."),
	PRODUTO_INATIVO_SALES(400, "Bad Request", "Não é possível criar uma venda para um produto inativo."),
	INSUFFICIENT_STOCK(400, "Bad Request", "Estoque insuficiente"),
	INVALID_DATE(400, "Bad Request", "Data inválida, verifique novamente"),
	INVALID_PRODUCT_STOCK(400, "Bad Request",
			"Estoque do produto com valor negativo ou inválido, verifique e tente novamente"),
	INVALID_SALES_QUANTITY(400, "Bad Request", "Quantidade inválida, verifique e tente novamente"),
	INVALID_PRODUCT_PRICE(400, "Bad Request", "Preço inválido, verifique e tente novamente."),
	NO_CONTENT(204, "No Content", "Nenhum conteúdo disponível"),
	NO_SALES_IN_PERIOD(404, "Not Found", "Nenhuma venda no período"),
	VENDA_NOT_FOUND(404, "Not Found", "Venda não encontrada"),
	PRODUCT_NOT_FOUND(404, "Not Found", "Produto não encontrado"),
	INTERNAL_SERVER_ERROR(500, "Internal Server Error", "Ocorreu um erro inesperado"),
	UNAUTHORIZED_ACCESS(403,"Forbidden", "Acesso não autorizado"),
	EMAIL_NOT_FOUND(404, "Not Found", "Email não encontrado"),
	INVALID_TOKEN(400, "Bad Request", "Token inválido ou expirado.");

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