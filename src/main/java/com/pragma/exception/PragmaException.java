package com.pragma.exception;

public class PragmaException extends RuntimeException {
	private String codigo;
	private static final long serialVersionUID = 1L;

	public PragmaException(String codigo, String message, Throwable cause) {
		super(message, cause);
		this.codigo = codigo;
	}

	public PragmaException(String codigo, String message) {
		super(message);
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}
}
