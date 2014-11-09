package com.pragma.services;

import java.util.List;

import com.pragma.dao.PragmaDAO;
import com.pragma.model.PragmaModel;



public abstract class PragmaService<T extends PragmaModel> {

	protected PragmaDAO<T> dao;

	public List<T> findAll() {
		return dao.find(null);
	}

	public List<T> findByExample(T instance, String tipoConsulta) {
		instance.setTipoConsulta(tipoConsulta);
		return dao.find(instance);
	}
	
	public List<T> findByExample(T instance) {		
		return dao.find(instance);
	}

	public T findById(T instance) {
		instance.setTipoConsulta("");
		List<T> res = dao.find(instance);
		return  res != null && res.size() > 0?res.get(0):null;
	}

	
	public T save(T instance) {
		return dao.save(instance);
	}

	public T update(T instance, String tipoActualizacion) {
		instance.setTipoConsulta(tipoActualizacion);
		return dao.update(instance);
	}

	public T delete(T instance) {
		return dao.delete(instance);
	}
	public List<T> report(T instance) {
		return dao.find(instance);
	}
	

	protected void setDao(PragmaDAO<T> dao) {
		this.dao = dao;
	}
	
	
}
