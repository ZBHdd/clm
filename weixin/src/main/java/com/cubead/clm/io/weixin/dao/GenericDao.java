package com.cubead.clm.io.weixin.dao;

import static org.hibernate.criterion.Example.create;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericDao implements IDao{
	private final static Logger log = LoggerFactory.getLogger(GenericDao.class);
	private SessionFactory sessionFactory;
	
	private Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public <T> void save(T instance) {
		log.debug("save "+instance.getClass()+" instance");
		try {
			getSession().save(instance);
			log.debug("save success");
		} catch (RuntimeException re) {
			log.error("save fail", re);
			throw re;
		}
	}

	public <T> void saveOrUpdate(T instance) {
		log.debug("save or update "+instance.getClass()+" instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("save or update success");
		} catch (RuntimeException re) {
			log.error("save or update ", re);
			throw re;
		}
	}
	
	public <T> void update(T instance) {
		log.debug("update "+instance.getClass()+" instance");
		try {
			getSession().update(instance);
			log.debug("update success");
		} catch (RuntimeException re) {
			log.error("update fail", re);
			throw re;
		}
	}
	
	public <T> void batchSaveOrUpdate(Collection<T> instance, Integer batchsize) {
		log.debug("batch save or update instance");
		if(instance!= null && instance.size() > 0 && batchsize > 0)try {
			Session session = getSession();
			int i = 1;
			for (T inst : instance) {
			    session.saveOrUpdate(inst);
			    if ( i % batchsize == 0 ) {
			        session.flush();
			        session.clear();
			    }
			    i++;
			}
			log.debug("batch save or update success");
		} catch (RuntimeException re) {
			log.error("batch save or update ", re);
			throw re;
		}
	}
	
	public <T> void batchSaveOrUpdate(Collection<T> instance) {
		log.debug("batch save or update instance");
		if(instance!= null && instance.size() > 0)try {
			Session session = getSession();
			for (T inst : instance) session.saveOrUpdate(inst);
			log.debug("batch save or update success");
		} catch (RuntimeException re) {
			log.error("batch save or update ", re);
			throw re;
		}
	}
	
	public <T> void batchSave(Collection<T> instance, Integer batchsize) {
		log.debug("batch save instance");
		if(instance!= null && instance.size() > 0 && batchsize > 0)try {
			Session session = getSession();
			int i = 1;
			for (T inst : instance) {
			    session.save(inst);
			    if ( i % batchsize == 0 ) {
			        session.flush();
			        session.clear();
			    }
			    i++;
			}
			log.debug("batch save success");
		} catch (RuntimeException re) {
			log.error("batch save ", re);
			throw re;
		}
	}

	public <T> void batchSave(Collection<T> instance) {
		log.debug("batch save instance");
		if(instance!= null && instance.size() > 0)try {
			Session session = getSession();
			for (T inst : instance) session.save(inst);
			log.debug("batch save success");
		} catch (RuntimeException re) {
			log.error("batch save ", re);
			throw re;
		}
	}
	
	public <T> void batchUpdate(Collection<T> instance, Integer batchsize) {
		log.debug("batch update instance");
		if(instance!= null && instance.size() > 0 && batchsize > 0)try {
			Session session = getSession();
			int i = 1;
			for (T inst : instance) {
			    session.update(inst);
			    if ( i % batchsize == 0 ) {
			        session.flush();
			        session.clear();
			    }
			    i++;
			}
			log.debug("batch update success");
		} catch (RuntimeException re) {
			log.error("batch update ", re);
			throw re;
		}
	}
	
	public <T> void batchUpdate(Collection<T> instance) {
		log.debug("batch update instance");
		if(instance!= null && instance.size() > 0)try {
			Session session = getSession();
			for (T inst : instance) session.update(inst);
			log.debug("batch update success");
		} catch (RuntimeException re) {
			log.error("batch update ", re);
			throw re;
		}
	}
	
	public <T> void delete(T persistentInstance) throws Exception {
		log.debug("deleting "+persistentInstance.getClass()+" instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (Exception re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public <T> T merge(T detachedInstance) throws Exception {
		log.debug("merging "+detachedInstance.getClass()+" instance");
		try {
			T result = (T) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (Exception re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public <T, PK extends Serializable> T findById(Class<T> cls, PK id) throws Exception {
		log.debug("find by id "+cls+" instance with id: " + id);
		try {
			T instance = (T) getSession().get(cls, id);
			if (instance == null) {
				log.debug("find by id successful, no instance found");
			} else {
				log.debug("find by id successful, instance found");
			}
			return instance;
		} catch (Exception re) {
			log.error("find by id failed", re);
			throw re;
		}
	}
	
	public <T> List<T> findBySet(Class<T> cls, String propertyName, Collection set) throws Exception {
		log.debug("find by set "+cls+" instance");
		try {
			List<T> results = (List<T>) getSession().createCriteria(cls).add(Restrictions.in(propertyName, set)).list();
			log.debug("find by set successful, result size: "
					+ results.size());
			return results;
		} catch (Exception re) {
			log.error("find by set failed", re);
			throw re;
		}
	}
	
	public <T> List<T> findByExample(T instance) throws Exception {
		log.debug("finding "+instance.getClass()+" instance by example");
		try {
			List<T> results = (List<T>) getSession().createCriteria(instance.getClass()).add(create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (Exception re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public <T> List<T> findAll(Class<T> cls) throws Exception {
		log.debug("finding all "+cls+" instance");
		try {
			List<T> results = (List<T>) getSession().createCriteria(cls).list();
			log.debug("find all successful, result size: " + results.size());
			return results;
		} catch (Exception re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	public <T> Long findCount(Class<T> cls) throws Exception {
		log.debug("finding "+cls+" instance count");
		try {
			Long results = (Long)getSession().createCriteria(cls).setProjection(Projections.rowCount()).uniqueResult();
			log.debug("find all count successful, result size: " + results);
			return results;
		} catch (Exception re) {
			log.error("find all count failed", re);
			throw re;
		}
	}
	
	public Integer executeHQL(String hql, HashMap<String, Object> paras) throws Exception {
		log.debug("execute instance by hql");
		try {
			Query query = getSession().createQuery(hql);
			if(paras != null) for (Entry<String, Object> entry : paras.entrySet())
				query.setEntity(entry.getKey(), entry.getValue());
			Integer result = query.executeUpdate();
			log.debug("execute hql successful, result " + result);
			return result;
		} catch (Exception re) {
			log.error("execute hql failed", re);
			throw re;
		}
	}
	
	public <T> List<T> findByHQL(String hql, HashMap<String, Object> paras) throws Exception {
		log.debug("finding instance by hql");
		try {
			Query query = getSession().createQuery(hql);
			if(paras != null) for (Entry<String, Object> entry : paras.entrySet())
				query.setEntity(entry.getKey(), entry.getValue());
			List results = query.list();
			log.debug("find by hql successful, result size: " + results.size());
			return results;
		} catch (Exception re) {
			log.error("find by hql failed", re);
			throw re;
		}
	}
	
	public <T> List<T> findByPage(String hql, HashMap<String, Object> paras, Integer index, Integer count) throws Exception {
		log.debug("finding instance by page");
		try {
			Query query = getSession().createQuery(hql);
			if(paras != null) for (Entry<String, Object> entry : paras.entrySet())
				query.setEntity(entry.getKey(), entry.getValue());
			query.setFirstResult(index);
			query.setMaxResults(count);
			List results = query.list();
			log.debug("find by page successful, result size: " + results.size());
			return results;
		} catch (Exception re) {
			log.error("find by page failed", re);
			throw re;
		}
	}
}