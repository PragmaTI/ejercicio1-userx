package com.pragma.controller;


import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.pragma.exception.PragmaException;
import com.pragma.model.PragmaModel;
import com.pragma.services.PragmaService;

public abstract class PragmaController<T extends PragmaModel> {

	public static final String PARAMETRO_TIPO_CONSULTA = "tipoConsulta";
	public static final String PARAMETRO_TIPO_ACTUALIZACION = "tipoActualizacion";

	
	protected static Logger log = Logger.getLogger(PragmaController.class);

	
	protected PragmaService<T> service;

	
	public List<T> findAll() {
		return service.findAll();
	}


	public List<T> findByExample(T instance, String tipoConsulta) {
		return service.findByExample(instance, tipoConsulta);
	}



	public T findById(Integer id) {
		return service.findById(getInstanceWithId(id));
	}

	public T save(T instance) {
		return service.save(instance);
	}

	public T update(T instance, Integer id, String tipoActualizacion) {
		((PragmaModel) instance).setId(id);
		return service.update(instance, tipoActualizacion);
	}

	public T delete(Integer id) {
		return service.delete(getInstanceWithId(id));
	}



	protected void setService(PragmaService<T> service) {
		this.service = service;
	}

	
	@SuppressWarnings("unchecked")
	private T getInstanceWithId(Integer id) {
		Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		try {
			PragmaModel instance = clazz.newInstance();
			instance.setId(id);
			return (T) instance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody
	Map<String, String> handleUncaughtException(Exception ex, WebRequest request, HttpServletResponse response) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		if (ex instanceof PragmaException) {
			map.put("code", ((PragmaException) ex).getCodigo());
		} else {
			long ticketId = System.currentTimeMillis();
			log.error("Error - TicketId : " + ticketId);
			map.put("code", ex.getClass().getName());
			map.put("ticketId", String.valueOf(ticketId));
			ex.printStackTrace();
		}
		map.put("error", ex.getMessage());
		log.error(ex);
		return map;
	}
}