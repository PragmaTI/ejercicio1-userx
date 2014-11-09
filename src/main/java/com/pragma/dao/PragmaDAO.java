package com.pragma.dao;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;

import com.pragma.exception.PragmaException;

import com.pragma.model.PragmaModel;
import com.pragma.util.PragmaConstants;


public abstract class PragmaDAO<T extends PragmaModel> extends JdbcDaoSupport {


	private DataSource productionDataSource;
	

	


	public DataSource getProductionDataSource() {
		return productionDataSource;
	}



	public void setProductionDataSource(DataSource productionDataSource) {
		this.productionDataSource = productionDataSource;
	}




	
	public abstract T getRow(java.sql.ResultSet rs, int currentRow, String procName, Map<String, Object> inParams) throws SQLException;
	
	public abstract Map<String, PragmaSPDef> getSPMap();
	
	public abstract Map<String, Object> preProcess(T domain, String procName);
	
	public abstract Map<String, Object> postProcess(Map<String, Object> results, String procName);
	
	public Map<String, Object> executeProcedure(String procName, T domain)  throws PragmaException{	
		
		
		Map<String, Object> inParams = preProcess(domain, procName);
		
		Map<String, PragmaSPDef> procDefs = getSPMap();
		
		if(procDefs == null){
			throw new PragmaException("ERR","No existen definiciones de procedures " + procName);
		}
		PragmaSPDef sspd = procDefs.get(procName);
		
		if(sspd == null){
			throw new PragmaException("ERR","No existe la definicion para el procedure " + procName);
		}
		
		SevenStoredProcedure sp = new SevenStoredProcedure(getJdbcTemplate().getDataSource(), sspd, inParams, procName);
		Map<String, Object> results = sp.execute(getInParams(inParams));
		
		Map<String, Object> tmpresults = postProcess(results, procName);
		
		if(tmpresults != null){
			results = tmpresults;
		}
			
		return results;
	}
	
	private Map<String, Object> getInParams(Map<String, Object> inParams) {
		Map<String, Object> tmpInParams = new HashMap<String, Object>();
		
		Iterator<String> it = null; 
		
		it = inParams.keySet().iterator();
		
		String key = null;
		while(it.hasNext()){
			key = it.next();
			tmpInParams.put(getProcedureParamPrefix() + key, inParams.get(key));
		}
		
		return tmpInParams;
	}

	protected  String getProcedureParamPrefix(){
		return PragmaConstants.PROCEDURE_PARAM_PREFIX;
	}

	private class SevenStoredProcedure extends StoredProcedure { 
		 
		@SuppressWarnings("unchecked")
		public SevenStoredProcedure(DataSource datasource, PragmaSPDef procDefinition, final Map<String, Object> inParams, final String procName) throws PragmaException{ 
			super(datasource, procDefinition.getName());				
			
			try{
				
				Iterator<String> it = null;
				String key = null;
				if(procDefinition.getInParams().size() > 0){
					it = procDefinition.getInParams().keySet().iterator();
					
					Integer type = null;
					
					while(it.hasNext()){
						key = it.next().toString();
						type = (Integer) procDefinition.getInParams().get(key);
																				
						declareParameter( new SqlParameter(getProcedureParamPrefix() + key, type.intValue()));													
					}
					
				}else{
					it = inParams.keySet().iterator();
					
					key = null;
					while(it.hasNext()){
						key = it.next().toString();
						
						if("class".equals(key) || "beanName".equals(key)){
							continue;
						}
						
						 Class<?> type = PropertyUtils.getPropertyType(((Class<T>)inParams.get("class")).newInstance(), key);
						
						
						if(type.toString().equals(Integer.class.toString())){
							declareParameter( new SqlParameter(PragmaConstants.PROCEDURE_PARAM_PREFIX + key, Types.INTEGER) );							
						}else
							if(type.toString().equals(String.class.toString())){
								declareParameter( new SqlParameter(PragmaConstants.PROCEDURE_PARAM_PREFIX + key, Types.VARCHAR) );
							}
						
						
					
					}
				}
				
		
				
				
								
				if(procDefinition.getOutParams().size() > 0){
					it = procDefinition.getOutParams().keySet().iterator();
					
					Integer	type = null;
					key = null;
					while(it.hasNext()){
						key = it.next().toString();
						type = (Integer) procDefinition.getOutParams().get(key);
						
						declareParameter( new SqlOutParameter(key, type.intValue(), new RowMapper<T>(){
							
							public T mapRow(java.sql.ResultSet rs, int currentRow) throws java.sql.SQLException {
									
								 	return getRow(rs, currentRow, procName, inParams);
							}
					 						
						}
					)
					);
					}
				}else{
					//declaring sql out parameter
					if(procDefinition.isReturnsCursor()){
						declareParameter( new SqlOutParameter( procDefinition.getCursorName(), -10, new RowMapper<T>(){
							
								public T mapRow(java.sql.ResultSet rs, int currentRow) throws java.sql.SQLException {
										
									 	return getRow(rs, currentRow, procName, inParams);
								}
						 						
							}
						)
						);
					}else if(procDefinition.isReturnsId()){
						
						declareParameter( new SqlOutParameter( procDefinition.getGeneratedIdName(), 2, new RowMapper<PragmaModel>(){
							
								public PragmaModel mapRow(java.sql.ResultSet rs, int currentRow) throws java.sql.SQLException {
										
									 	return getRow(rs, currentRow, procName, inParams);
								}
						 						
							}
						)
						);
						}
				}

					 						
					
					compile();
			}catch(Exception e){
				throw new PragmaException("ERR",e.getMessage(),e);
			}
		} 
			
	}
	
	

	public List<T> find(T instance) throws PragmaException {
		Map<String, Object> result = null;			
		
			 if(instance.getId() == null){
				 result = executeProcedure(PragmaConstants.SP_CONSULTAR, instance);
			 }else if(instance.getId() != null && instance.getId() != 0){				 
				 result = executeProcedure(PragmaConstants.SP_CONSULTAR_BY_ID, instance);
			 }

		
		 return (List<T>) result.get("c_resultados");
	    
		
}



public T update(T instance) throws PragmaException {
	Map<String, Object>  result = null;
	 try {
		result = executeProcedure(PragmaConstants.SP_MODIFICAR, instance);
	} catch (PragmaException e) {			
		e.printStackTrace();
		throw new PragmaException("ERR",e.getMessage(),e);
	}
	 return result.get("c_resultados") != null?((List<T>) result.get("c_resultados")).get(0):instance;
}


public T save(T instance) throws PragmaException {
	Map<String, Object>  result = null;
	
	 try {			 
		result = executeProcedure(PragmaConstants.SP_GRABAR, instance);		
	} catch (PragmaException e) {					
		throw e;
	}
	 
	 return result.get("c_resultados") != null?((List<T>) result.get("c_resultados")).get(0):instance;

}




public T delete(T instance) throws PragmaException {
	Map<String, Object>  result = null;
	 try {
		result = executeProcedure(PragmaConstants.SP_ELIMINAR, instance);
	} catch (PragmaException e) {			
		e.printStackTrace();
		throw new PragmaException("ERR",e.getMessage(),e);
	}
	 return result.get("c_resultados") != null?((List<T>) result.get("c_resultados")).get(0):instance;
}

	

	@Autowired
	public void initDataSource(@Qualifier("productionDataSource") DataSource productionDataSource) {
		
		this.productionDataSource = productionDataSource;
		setDataSource(productionDataSource);
	}
}