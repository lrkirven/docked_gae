package com.zarcode.data.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.Query;
import javax.jdo.Transaction;

import com.zarcode.platform.dao.BaseDao;
import com.zarcode.shared.model.OpenIdProviderDO;

public class OpenIdProviderDao extends BaseDao {
	
	private Logger logger = Logger.getLogger(OpenIdProviderDao.class.getName());
	
	public List<OpenIdProviderDO> getAll() {
		OpenIdProviderDao res = null;
		List<OpenIdProviderDO> list = null;
		
		Query query = pm.newQuery(OpenIdProviderDO.class);
		list = (List<OpenIdProviderDO>)query.execute();
		
		return list;
	}
	

	public void addProvider(OpenIdProviderDO provider) {
		Transaction tx = pm.currentTransaction();
		double lat = 0;
		double lng = 0;
		
		try {
			tx.begin();
			pm.makePersistent(provider); 
			//
			// commit changes
			//
			tx.commit();
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}  // addProvider
	
}
