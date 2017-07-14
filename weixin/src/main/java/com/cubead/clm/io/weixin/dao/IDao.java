package com.cubead.clm.io.weixin.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface IDao {
	public <T> void save(T instance);

	public <T> void saveOrUpdate(T instance);
	
	public <T> void update(T instance);
	
	public <T> void batchSaveOrUpdate(Collection<T> instance, Integer batchsize);
	
	public <T> void batchSaveOrUpdate(Collection<T> instance);
	
	public <T> void batchSave(Collection<T> instance, Integer batchsize);

	public <T> void batchSave(Collection<T> instance);
	
	public <T> void batchUpdate(Collection<T> instance, Integer batchsize);
	
	public <T> void batchUpdate(Collection<T> instance);
	
	public <T> void delete(T persistentInstance) throws Exception;

	public <T> T merge(T detachedInstance) throws Exception;

	public <T, PK extends Serializable> T findById(Class<T> cls, PK id) throws Exception;
	
	public <T> List<T> findBySet(Class<T> cls, String propertyName, Collection set) throws Exception;
	
	public <T> List<T> findByExample(T instance) throws Exception;

	public <T> List<T> findAll(Class<T> cls) throws Exception;
	
	public <T> Long findCount(Class<T> cls) throws Exception;
	
	public Integer executeHQL(String hql, HashMap<String, Object> paras) throws Exception;
	
	public <T> List<T> findByHQL(String hql, HashMap<String, Object> paras) throws Exception;
	
	public <T> List<T> findByPage(String hql, HashMap<String, Object> paras, Integer index, Integer count) throws Exception;
}