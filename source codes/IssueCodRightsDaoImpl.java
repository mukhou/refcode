/******************************************************************************
 * Copyright(c) 2003-2010 CREDIT SUISSE Financial Services. All Rights Reserved.
 *
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/
package com.csg.cs.micos.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.csg.cs.core.base.exception.ExceptionContext;
import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.dao.IssueCodRightsDao;
import com.csg.cs.micos.entitybean.Aorowner;
import com.csg.cs.micos.entitybean.Country;
import com.csg.cs.micos.entitybean.Employee;
import com.csg.cs.micos.entitybean.Icrightregion;
import com.csg.cs.micos.entitybean.Issuecoordinatorright;
import com.csg.cs.micos.entitybean.Issuecreatorgroup;
import com.csg.cs.micos.entitybean.Referencecode;
import com.csg.cs.micos.entitybean.Referencecodegroup;
import com.csg.cs.micos.entitybean.Region;
import com.csg.cs.micos.entitybean.Reportqueue;
import com.csg.cs.micos.entitybean.Reporttype;
import com.csg.cs.micos.entitybean.Status;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.util.DateUtil;
import com.csg.cs.micos.util.MicosConstantValues;
import com.csg.cs.micos.util.SystemUtil;


/**
 * Used as a DAO layer for accessing the data for issues.
 *
 * @author Cognizant Technology Solutions
 * @author Last change by $Author: 153146 $
 * @version $Id: IssueCodRightsDaoImpl.java,v 1.1 2010/10/25 15:25:32 153146 Exp $
 */
public class IssueCodRightsDaoImpl extends IssueTypeDaoImpl implements IssueCodRightsDao{
	
	/**
	 * A logger instance with the fully qualified class name as logger name.
	 */
	private static final Logger LOGGER = LoggerHelper
			.getLogger(IssueTypeDaoImpl.class);
	/**
	 * 
	 * @return List
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Country> loadListOfCountries() throws MicosException{
		LOGGER.entering(getClass().getName(), "loadListOfCountries");
		List<Country> countryList = null;
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery("select * from Country order by COUNTRYNAME",
					Country.class);
			countryList = qry.getResultList();
			if(SystemUtil.isNotNull(countryList)){
				for (Country country : countryList) {
					country.getCountryname();
					
				}
			}
		} catch (Exception exception) {
			LOGGER.error("loadReferenceCode", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(em!= null){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadReferenceCode");

		}
		return countryList;
	}
	
	/**
	 * loads all IssueTypeGroup.
	 * @param userId Integer
	 * @return List Issuecreatorgroup
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Issuecreatorgroup> loadICGForIAA(Integer userId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "loadICGForIAA");
		List<Issuecreatorgroup> issuetypeGroup = null;
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery("select * from Issuecreatorgroup order by NAME ",
					Issuecreatorgroup.class);
			issuetypeGroup = qry.getResultList();
			

		} catch (Exception exception) {
			LOGGER.error("loadICGForIAA", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(em != null){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadICGForIAA");
		}

		return issuetypeGroup;
	}
	
	
	/**
	 * loads all IssueTypeGroup.
	 * @param userId Integer
	 * @return List Issuecreatorgroup
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Issuecreatorgroup> loadICGForIGA(Integer userId) throws MicosException {
		LOGGER.entering(getClass().getName(), "loadICGForIGA");
		List<Issuecreatorgroup> icglist=new ArrayList<Issuecreatorgroup>();
		EntityManager em = null;
		try{
			em = getEntityManager();
			String queryString = "select icg.* from ISSUEGROUPADMINISTRATOR iga, issuetype it, issuecreatorgroup icg  "
					+ " where icg.ISSUETYPEID = it.ISSUETYPEID "
					+ " and it.ISSUETYPEID = iga.ISSUETYPEID "
					+ " and iga.ISSUEGROUPADMINISTRATOR = "+ userId;
			Query query = em.createNativeQuery(queryString, Issuecreatorgroup.class);
			icglist = query.getResultList();
			
		} catch (Exception exception) {
		LOGGER.error("loadICGForIGA", exception);
		throw new MicosException(ExceptionContext.ERROR, exception,
				SystemUtil.getErrorMessage(exception));
		} finally {
		if(em != null){
			em.close();
		}
		LOGGER.exiting(getClass().getName(), "loadICGForIGA");
		}

		return icglist;
	}
	
	/**
	 * loads all Reference.
	 * 
	 * @return List Referencecode
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Referencecode> loadReferenceForIAA()
			throws MicosException {
		LOGGER.entering(getClass().getName(), "loadReferenceForIAA");
		List<Referencecode> referencecode = null;
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery("select * " +
					"from REFERENCECODEGROUP t where t.GROUPNAME = '"+"IssueTypeGroup"+"'",
					Referencecodegroup.class);
			Referencecodegroup referencecodegroup = (Referencecodegroup)qry.getSingleResult();
			qry = em.createNativeQuery("select * from REFERENCECODE" +
					" t where t.REFERENCECODEGROUPID = "+ 
					referencecodegroup.getReferencecodegroupid()+"order by SHORTDESCRIPTION",
					Referencecode.class);
			referencecode = qry.getResultList();
	
		} catch (Exception exception) {
			LOGGER.error("loadReferenceForIAA", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(em != null){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadReferenceForIAA");
		}
		return referencecode;
	}
	
	/**
	 * @param workingasuserid workingasuserid
	 * @return list of issue coordinator assignment
	 * @throws MicosException
	 *             MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Issuecoordinatorright> getIssueCodAssignment(Integer workingasuserid)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "getIssueCodAssignment");
		List<Issuecoordinatorright> issueCoordinatorList = new ArrayList<Issuecoordinatorright>();
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery(
					"select * from issuecoordinatorright ",Issuecoordinatorright.class);
			issueCoordinatorList = qry.getResultList();
			if (SystemUtil.isNotNull(issueCoordinatorList)) {
				for (Issuecoordinatorright issuecoordinatorright : issueCoordinatorList) {
					Employee employee = issuecoordinatorright.getEmployee();
					if(employee != null){
						employee.getFirstname();
					}
					Aorowner aorowner = issuecoordinatorright.getAorowner();
					getAorOwner(aorowner);
					Country country =  issuecoordinatorright.getCountry();
					getCountry(country);
					Referencecode referencecode = issuecoordinatorright.getIssuetypegroup();
					getReference(referencecode);
					Issuecreatorgroup issuecreatorgroup = issuecoordinatorright.
						getIssuecreatorgroup();
					getIssueCreatorGrp(issuecreatorgroup);
					Status status = issuecoordinatorright.getStatus();
					getStatus(status);
					Set<Icrightregion> list=issuecoordinatorright.getIcrightregions();
					if(null != list){
						for(Icrightregion icrightregion: list){
							if(null!=icrightregion.getRegionid()){
								icrightregion.getRegionid().getDescription();
							}
						}
					}
				}
			}

		} catch (Exception exception) {
			LOGGER.error("getIssueCodAssignment", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "getIssueCodAssignment");
		}
		return issueCoordinatorList;
	}
	
	/**
	 * 
	 * @param aorowner Aorowner
	 */
	private void getAorOwner(Aorowner aorowner){
		if(aorowner != null){
			aorowner.getDefernotifications();
		}
	}
	
	/**
	 * 
	 * @param country Country
	 */
	private void getCountry(Country country){
		if(country != null){
			country.getCountryname();
		}
	}
	/**
	 * 
	 * @param status Status
	 */
	private void getStatus(Status status){
		if(status != null){
			status.getStatuscode();
		}
	}
	
	/**
	 * 
	 * @param issuecreatorgroup Issuecreatorgroup
	 */
	private void getIssueCreatorGrp(Issuecreatorgroup issuecreatorgroup){
		if(issuecreatorgroup != null){
			issuecreatorgroup.getName(); 
		}
	}
	
	/**
	 * 
	 * @param referencecode Referencecode.
	 */
	private void getReference(Referencecode referencecode){
		if(referencecode != null){
			referencecode.getShortdescription();
		}
	}
	
	/**
	 * change the status and expiry date of issueviewingrights.
	 * @param issueCodAssignmentId Integer
	 * @throws MicosException MicosException
	 */
	public void deleteIssueCodAssignment(Integer issueCodAssignmentId)
			throws MicosException {
		EntityManager em = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Issuecoordinatorright issuecoordinatorright = (Issuecoordinatorright) em.find(
					Issuecoordinatorright.class, issueCodAssignmentId);
			Status status = getStatusByCode(MicosConstantValues.Status.INACTIVE);
			issuecoordinatorright.setStatus(status);
			issuecoordinatorright.setExpirydate(new java.sql.Date(DateUtil.getCurrentDate().getTime()));
			em.merge(issuecoordinatorright);
			em.getTransaction().commit();

		} catch (Exception exception) {
			LOGGER.error("deleteIssueCodAssignment", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "deleteIssueCodAssignment");
		}
	}
	
	/**
	 * @param issueCoordId Integer
	 * @return list of issue viewing rights
	 * @throws MicosException MicosException
	 *             
	 */
	@SuppressWarnings("unchecked")
	public Issuecoordinatorright getIssueAssignmentDetailsById(Integer issueCoordId)
			throws MicosException {		
		Issuecoordinatorright issuecoordinatorright = null;		
		EntityManager em = null;
		try{
			em = getEntityManager();
			issuecoordinatorright = (Issuecoordinatorright) em.find(
					Issuecoordinatorright.class, issueCoordId);
			if(null != issueCoordId){				
				Aorowner aorowner = issuecoordinatorright.getAorowner();
				if(null != aorowner){
					aorowner.getDefernotifications();
				}								
				Employee employee = issuecoordinatorright.getLastupdatedby();
				if(null != employee){
					employee.getFirstname();
				}
				Country country =  issuecoordinatorright.getCountry();
				if(null != country){
					country.getCountryname();
				}
				Set<Icrightregion> list=issuecoordinatorright.getIcrightregions();
				if(null != list){
					for(Icrightregion icrightregion: list){
						if(null!=icrightregion.getRegionid()){
							icrightregion.getRegionid().getDescription();
						}
					}
				}
				Issuecreatorgroup issuecreatorgroup = issuecoordinatorright.getIssuecreatorgroup();
				if(null != issuecreatorgroup){
					issuecreatorgroup.getName();
				}				
				Referencecode referencecode = issuecoordinatorright.getIssuetypegroup();
				if(null != referencecode){
					referencecode.getShortdescription();
				}				
				em.refresh(issuecoordinatorright);
			}
		}catch (Exception exception) {
			LOGGER.error("getIssueAssignmentDetailsById", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "getIssueAssignmentDetailsById");
		}
		return issuecoordinatorright;
	}


	/**
	 * @return list of regions
	 * @throws MicosException MicosException
	 *             
	 */
	@SuppressWarnings("unchecked")
	public List<Region> fetchRegion()
			throws MicosException {	
		LOGGER.entering(getClass().getName(), "fetchRegion");
		List<Region> regionList = new ArrayList<Region>();
		EntityManager em = null;
		try{
			em = getEntityManager();
			Query qry = em.createNativeQuery(
					"select * from region reg where reg.code <> 0 ", Region.class);
			regionList = qry.getResultList();
		}catch (Exception exception) {
			LOGGER.error("fetchRegion", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "fetchRegion");
		}
		
		return regionList;
	}
	

	/**
	 * save issuecoordinatorright.
	 * @param issuecoordinatorright Issuecoordinatorright
	 * @return issuecoordinatorright id
	 * @throws MicosException MicosException
	 */
	
	public Integer createIssueCodAssignment(Issuecoordinatorright issuecoordinatorright)
			throws MicosException{
		
		EntityManager em = null;
		Integer issueCoordId = 0;
		try{
			em = getEntityManager();
			em.getTransaction().begin();
			issuecoordinatorright.setLastupdate(DateUtil.getCurrentTimestamp());
			if(null == issuecoordinatorright.getIssuecoordinatorrightid()){
				issuecoordinatorright.setCreatedate(DateUtil.getCurrentTimestamp());
				em.persist(issuecoordinatorright);
			}else{
				em.merge(issuecoordinatorright);
			}
			em.getTransaction().commit();
			issueCoordId = issuecoordinatorright.getIssuecoordinatorrightid();
		}catch (Exception exception) {
			LOGGER.error("getIssueCreatorGroup", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getIssueCreatorGroup");
		}
		return issueCoordId;
	}
	
	/**
	 * loads  Reference.
	 * @param issueTypeGrpId Integer
	 * @return referencecode Referencecode
	 * @throws MicosException MicosException
	 */
	
	public Referencecode loadIssueTypeGroup(Integer issueTypeGrpId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "loadIssueTypeGroup");
		Referencecode referencecode = null;
		EntityManager em = null;
		try {
			em = getEntityManager();
				Query query = em.createNativeQuery("select * " +
						"from REFERENCECODEGROUP t where t.GROUPNAME = " +
						"'"+"IssueTypeGroup"+"'",
                        Referencecodegroup.class);
            Referencecodegroup referencecodegroup = (Referencecodegroup)query.getSingleResult();
            query = em.createNativeQuery("select * from REFERENCECODE t where t.REFERENCECODEID = '"+
                        issueTypeGrpId+"' and referencecodegroupid = '"
                        + referencecodegroup.getReferencecodegroupid()+"'",
                        Referencecode.class);   
            if(query.getResultList().size() != 0){
            	referencecode= (Referencecode)query.getSingleResult();
            }            

	
		} catch (Exception exception) {
			LOGGER.error("loadReferenceForIAA", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(em != null){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadIssueTypeGroup");
		}
		return referencecode;
	}
	
	
	/**
	 * load the issuecreatorgroup.
	 * @param issueCreatorGrpId Integer
	 * @return issuecreatorgroup Issuecreatorgroup
	 * @throws MicosException MicosException
	 */
	
	public Issuecreatorgroup loadIssueCreatorGroup(Integer issueCreatorGrpId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "loadIssueCreatorGroup");
		Issuecreatorgroup issuecreatorgroup = null;
		EntityManager em = null;
		try {
			em = getEntityManager();
			String str = "select * from Issuecreatorgroup i where " +
					"i.issuecreatorgroupid ='" +issueCreatorGrpId+"'";
			Query query = em.createNativeQuery(str, Issuecreatorgroup.class);
			if(query.getResultList().size() != 0){
				issuecreatorgroup = (Issuecreatorgroup)query.getSingleResult();	
			}
			
		} catch (Exception exception) {
			LOGGER.error("loadReferenceForIAA", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(em != null){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadIssueCreatorGroup");
		}
		return issuecreatorgroup;
	}
	
	

	/**
	 * save excel.
	 * @param reportqueue Reportqueue
	 * @throws MicosException MicosException
	 */
	public void saveExcel(Reportqueue reportqueue)
			throws MicosException{
		
		EntityManager em = null;
		try{
			em = getEntityManager();
			em.getTransaction().begin();
			em.persist(reportqueue);
			em.getTransaction().commit();
		}catch (Exception exception) {
			LOGGER.error("getIssueCreatorGroup", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getIssueCreatorGroup");
		}
	}
	
	/**
	 * load report type.
	 * @param reportTypeCode String
	 * @return Reporttype
	 * @throws MicosException Micos Exception
	 */
	public Reporttype loadReportType(String reportTypeCode) throws MicosException {
		LOGGER.entering(getClass().getName(), "loadCountry");
		Reporttype reporttype = null;
		EntityManager em = null;
		try {
				if (null != reportTypeCode) {
					em = getEntityManager();
					Query qry = em.createNativeQuery("select * from Reporttype t where t.REPORTCODE = '" +reportTypeCode  +"'", Reporttype.class);
					reporttype = (Reporttype) qry.getSingleResult();			
				}	
			
		} catch (Exception exception) {
			LOGGER.error("loadCountry", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadCountry");

		}
		return reporttype;
	}
	
	/**
	 * method for loading Region.
	 * @param regionId Integer
	 * @return Region  object
	 * @exception MicosException MicosException
	 */
	public Region loadRegion(Integer regionId) throws MicosException {
		LOGGER.entering(getClass().getName(), "loadRegion");
		Region region = null;
		EntityManager em = null;
		try {
			if (null != regionId) {
				em = getEntityManager();
				region = (Region) em.find(Region.class, regionId);				
			}
		} catch (Exception exception) {
			LOGGER.error("loadRegion", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadRegion");
		}
		return region;
	}
	
	
	/**
	 * method for loading Country.
	 * @param countryId Integer
	 * @return country Country
	 * @exception MicosException Micos Exception
	 */
	public Country loadCountry(long countryId) throws MicosException {
		LOGGER.entering(getClass().getName(), "loadCountry");
		Country country = null;
		EntityManager em = null;
		try {
			
				em = getEntityManager();
				country = (Country) em.find(Country.class, countryId);				
			
		} catch (Exception exception) {
			LOGGER.error("loadCountry", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadCountry");

		}
		return country;
	}
}

/*
 * Check-In History: $Log: IssueCodRightsDaoImpl.java,v $
 * Check-In History: Revision 1.1  2010/10/25 15:25:32  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.6  2010/05/26 11:28:11  f542625
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.5  2010/05/12 10:43:14  f542625
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.4  2010/05/06 13:30:12  f542625
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.3  2010/04/23 14:57:56  f542625
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.2  2010/04/22 12:20:04  g304578
 * Check-In History: changed for package 5
 * Check-In History:
 * Check-In History: Revision 1.3  2010/04/22 12:04:59  163376
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.2  2010/04/22 11:59:42  163376
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.1  2010/04/22 09:18:28  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.1  2010/04/22 08:48:09  g304578
 * Check-In History: changed for package 5
 * Check-In History:
 * Check-In History: Revision 1.5  2010/04/21 11:21:28  163376
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.4  2010/04/21 09:53:05  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.3  2010/04/21 09:08:20  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.2  2010/04/20 07:48:35  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.1  2010/04/20 04:34:44  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 */