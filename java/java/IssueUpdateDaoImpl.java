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
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.csg.cs.core.base.exception.ExceptionContext;
import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.dao.IssueUpdateDao;
import com.csg.cs.micos.entitybean.Collection;
import com.csg.cs.micos.entitybean.Issue;
import com.csg.cs.micos.entitybean.Issuephase;
import com.csg.cs.micos.entitybean.Observation;
import com.csg.cs.micos.entitybean.Recommendation;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.util.SystemUtil;
import com.csg.cs.micos.util.IssuesConstants.IssueUpdate;
import com.csg.cs.micos.util.IssuesConstants.IssuesOverviewConstants;
import com.csg.cs.micos.util.IssuesConstants.ReferenceCodeUtil;

/**
 * 
 * <code>IssueDetailsDaoImpl</code> is used to provide the common methods for
 *  Issue Details
 * 
 * @author Cognizant Technology Solutions

 * @author Last change by $Author: 153146 $
 * @version $Id: IssueUpdateDaoImpl.java,v 1.1 2010/10/25 15:25:36 153146 Exp $
 */
public class IssueUpdateDaoImpl extends BaseDaoImpl implements IssueUpdateDao{
	
	/**
	 * A logger instance with the fully qualified class name as logger name.
	 */
	private static final Logger LOGGER = LoggerHelper
	      .getLogger(ReportDaoImpl.class);
	
	
	/**
	 * get the Issue Header for the Issue Details page.
	 * @param issueId Integer
	 * @return List of Object
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public Object[] getIssuesDetailsHeader(Integer issueId)throws MicosException {
		LOGGER.entering(getClass().getName(), "getIssues");
	 Object[] issueDetailsHeader = null;
		EntityManager em = null;
		try{
			String strQry = getQueryForIssueDetailsHeader(issueId);
			em = getEntityManager();
			Query query = em.createNativeQuery(strQry);
      List<Object[]> issueDetailsHeaderList = query.getResultList();
      if(SystemUtil.isNotNull(issueDetailsHeaderList)) {
        issueDetailsHeader = issueDetailsHeaderList.get(0);
      }
		} catch (Exception exception) {
			LOGGER.error("getIssuesDetailsHeader", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "getIssues");
		}
		return issueDetailsHeader;
	}
	
	
	/**
	 * ouery for the issue details header.
	 * @param issueId Integer
	 * @return String
	 */
	private String getQueryForIssueDetailsHeader(Integer issueId){
		String strQry=" SELECT DISTINCT    h.NAME "
                + "|| '-' "
                + "|| he.NAME "
                + "|| '-' "
                + " || emp.firstname "
                + " || ' ' "
                + " || emp.lastname AS actionowner, "
                + "  i.duedate, rc1.shortdescription, i.phaseable, c.collectionid, "
                + "  obs.title, it.NAME, s.statusdescription, i.noofphases, "
                + "  i.issueid, ip.phaseno, s.statuscode, i.phase1duedate, "
                + "  rc2.shortdescription "
                + "  FROM issue i, "
                + "        HIERARCHY h, "
                + "       hierarchyelement he, "
                + "     employee emp, "
                + "      collection c, "
                + "      observation obs, "
                + "     issuetype it, "
                + "     status s, "
                + "     aorowner ao, "
                + "     referencecode rc1, "
                + "     recommendation rec, "
                + "     issuephase ip, "
                + "      referencecode rc2 "
                + "  WHERE i.issueaorownerid = ao.aorownerid "
                + "    AND ao.hierarchyelementid = he.hierarchyelementid "
                + "    AND he.hierarchyid = h.hierarchyid "
                + "    AND ao.aorowner = emp.employeeid "
                + "    AND i.recommendationid = rec.recommendationid "
                + "    AND c.collectionid = obs.collectionid "
                + "    AND rec.observationid = obs.observationid "
                + "     AND i.issuetypeid = it.issuetypeid "
                + "   AND i.statusid = s.statusid "
                + "   AND rc1.referencecodeid = i.initialstate "
                + "   AND i.issueid = " + issueId
                + "   AND i.issueid = ip.issueid(+) "
                + "   AND i.substatus = rc2.referencecodeid (+)";
		
		return strQry;
	}
 


	
	/**
	 * Provides list of submitted updates
	 * @param issueID Issue ID
	 * @param updatedTexyType String of var args
	 * @return List of SubmittedUpdateView
	 * @throws MicosException MicosException
	 */
  @SuppressWarnings("unchecked")
	public List<Object[]> getSubmittedUpdates(Integer issueID, String...updatedTexyType)throws MicosException{
		LOGGER.entering(getClass().getName(), "getSubmittedUpdates");
		EntityManager em = null;
		String org=null;
		StringBuffer sbIssue=null;
		StringBuffer sbColsComNotes=null;
		boolean isIssueQueryNeedtobeCalled=false;
		List<Object[]> colisscommentsnotes=null;
		List<Object[]> issues=null;		
		try{
			em=getEntityManager();
			org=updatedTexyType[0];	
			if(ReferenceCodeUtil.ADMINISTRATION_NOTES_CODE.equals(org)){
				isIssueQueryNeedtobeCalled=true;
			}
			if(updatedTexyType.length > 1){
				if(ReferenceCodeUtil.ADMINISTRATION_NOTES_CODE.equals(updatedTexyType[1])){
					isIssueQueryNeedtobeCalled=true;
				}
				if(updatedTexyType[1]!=null){
					org=org+","+updatedTexyType[1];
				}
			}
			Issue issue=em.find(Issue.class, issueID);
			Issuephase issuePhase=issue.getIssuephase();			
			if(isIssueQueryNeedtobeCalled){
				sbIssue=new StringBuffer("SELECT ISSUE.CREATEDATE CREATEDATE,ISSUE.ISSUEID ID, ") ;
				sbIssue.append("ISSUE.ISSUETEXT NOTES, ");
				sbIssue.append("EMPLOYEE.FIRSTNAME||' '||EMPLOYEE.LASTNAME||'('||EMPLOYEE.INSTRADIERUNG||'),'||EMPLOYEE.PID as");					
				sbIssue.append(" CREATEDBY, 'Administrator notes' as SHORTDESCRIPTION, 'Y' as fromissue");
				sbIssue.append(" from ISSUE, EMPLOYEE where ISSUE.ISSUEID=");
				sbIssue.append(issueID);
				sbIssue.append(" and ");
				sbIssue.append(" EMPLOYEE.EMPLOYEEID=ISSUE.ISSUECREATOR");
				Query query=em.createNativeQuery(sbIssue.toString());
				issues=query.getResultList();
			}			
			if(null!=issuePhase){							
				sbColsComNotes=new StringBuffer(" SELECT COLISSCOMMENTSNOTES.CREATEDATE CREATEDATE");						
				sbColsComNotes.append(" , COLISSCOMMENTSNOTES.COLISSCOMMENTSNOTESID ID, ");
				sbColsComNotes.append("COLISSCOMMENTSNOTES.COMMENTSNOTES NOTES,");
				sbColsComNotes.append(" EMPLOYEE.FIRSTNAME||' '||EMPLOYEE.LASTNAME||'('||EMPLOYEE.INSTRADIERUNG||'),'||EMPLOYEE.PID as");
				sbColsComNotes.append(" CREATEDBY, REFERENCECODE.SHORTDESCRIPTION SHORTDESCRIPTION, 'N' as fromissue");
				sbColsComNotes.append(" from COLISSCOMMENTSNOTES, EMPLOYEE, REFERENCECODE");
				sbColsComNotes.append(" where COLISSCOMMENTSNOTES.ISSUEPHASEID=");
				sbColsComNotes.append(issuePhase.getIssuephaseid());
				sbColsComNotes.append(" and COLISSCOMMENTSNOTES.CREATEDBY=EMPLOYEE.EMPLOYEEID");
				sbColsComNotes.append(" and COLISSCOMMENTSNOTES.UPDATETEXTTYPE=REFERENCECODE.REFERENCECODEID and");
				sbColsComNotes.append(" REFERENCECODE.CODE in");
				sbColsComNotes.append("("+org+")");
				sbColsComNotes.append(" order by CREATEDATE desc");
				Query query=em.createNativeQuery(sbColsComNotes.toString());
				colisscommentsnotes=query.getResultList();
				if(SystemUtil.isNull(issues)){
					issues=new ArrayList<Object[]>();
				}
				issues.addAll(colisscommentsnotes);
			}						
		}catch (Exception exception) {
			LOGGER.error("getSubmittedUpdates", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			
			LOGGER.exiting(getClass().getName(), "getSubmittedUpdates");
		}
		return issues;
	}
		
	/**
	 * Provide the audit trail for an Issue
	 * @param issueID Issue ID
	 * @return List of Objects
	 * @throws MicosException MicosException
	 */
  @SuppressWarnings("unchecked")
	public List<Object[]> getAuditTrailForIssue(Integer issueID)throws MicosException{
		LOGGER.entering(getClass().getName(), "getAuditTrailForIssue");
		List<Object[]> list=null;
		EntityManager em = null;
		StringBuffer strBuf=null;
		try{
			em = getEntityManager();
			strBuf=new StringBuffer("select AUDITTRAIL.AUDITTRAILID, AUDITTRAIL.CREATEDATE,AUDITTRAIL.NEWVALUE, ");
			strBuf.append("AUDITTRAILTYPE.ATTRIBUTE, ");
			strBuf.append("Employee.FIRSTNAME||' '||Employee.LASTNAME|| '(' ||Employee.INSTRADIERUNG||'), ' ||Employee.PID as employeedesc ");
			strBuf.append("from  AUDITTRAIL, Employee, AUDITTRAILTYPE ");
			strBuf.append("where AUDITTRAIL.CREATEDBY=EMPLOYEE.EMPLOYEEID and ");
			strBuf.append("AUDITTRAILTYPE.AUDITTRAILTYPEID=AUDITTRAIL.AUDITTRAILTYPEID ");
			strBuf.append("and AUDITTRAIL.ISSUEID=");
			strBuf.append(issueID);
			strBuf.append(" and upper(ltrim(rtrim(AUDITTRAILTYPE.STATUSHISTORYRELEVANT)))='Y' order by  AUDITTRAIL.CREATEDATE desc");
			Query query=em.createNativeQuery(strBuf.toString());
			list=query.getResultList();
		}catch (Exception exception) {
			LOGGER.error("getAuditTrailForIssue", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			
			LOGGER.exiting(getClass().getName(), "getAuditTrailForIssue");
		}
		return list;
	}
		
	/**
	 * get the Issue Header for the Issue Details page.
	 * @param issueId Integer
	 * @param var String
	 * @param workingAsUser Working as user
	 * @param role Role
	 * @return List of Object
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getIssuesDetailsAttachmentAndList(Integer issueId,String var, 
	Integer workingAsUser, String role)throws MicosException {
		LOGGER.entering(getClass().getName(), "getIssues");
		List<Object[]> list = new ArrayList<Object[]>();
		EntityManager em = null;
		String strQry=null;
		try{
			em = getEntityManager();
			Issue issue=em.find(Issue.class, issueId);
			if(var.equalsIgnoreCase("attachment")){
				strQry = getQueryForIssueUpdateAttachments(issue,workingAsUser,role);
			}else if(var.equalsIgnoreCase("link")){
				strQry = getQueryForIssueUpdateLinks(issue,workingAsUser,role);
			}			
			Query query = em.createNativeQuery(strQry);
			list = query.getResultList();
		} catch (Exception exception) {
			LOGGER.error("getIssuesDetailsAttachmentAndList", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "getIssues");
		}
		return list;
	}
	
	
	/**
	 * Get All Issue Attachment .
	 * @param issue Issue
     * @param workingAsUser Integer
     * @param role Role
	 * @return Integer
	 */
	private String getQueryForIssueUpdateAttachments(Issue issue, Integer workingAsUser, String role){
		boolean roleIsActionOwnwer=false;
		if(IssuesOverviewConstants.ACTION_OWNER.equalsIgnoreCase(role)
				|| IssuesOverviewConstants.ICU.equalsIgnoreCase(role)
				|| IssuesOverviewConstants.AOR_OWNER.equalsIgnoreCase(role)){
			roleIsActionOwnwer=true;
		}
		StringBuffer sb=new StringBuffer("SELECT MULTIATTACHMENTS.MULTIATTACHMENTSID, ");
		sb.append("ATTACHMENTS.ATTACHMENTID, ATTACHMENTS.ATTACHMENTFILENAME, ATTACHMENTS.CREATEDATE,");
		sb.append("ATTACHMENTS.ATTACHMENTSIZE, ");
		sb.append("MULTIATTACHMENTS.COLLECTIONID as REFERSTOID, MULTIATTACHMENTS.REFERSTO, ");
		if(roleIsActionOwnwer){			
			sb.append("'N'  showdeleteicon, ");
		}else{
			sb.append("case when ATTACHMENTS.LASTUPDATEDBY=");
			sb.append(workingAsUser);
			sb.append(" then 'Y'  else 'N' end as showdeleteicon, ");
		}		
		sb.append("ATTACHMENTS.LASTUPDATE from ATTACHMENTS, ATTACHMENTTYPE, MULTIATTACHMENTS, ");
		sb.append("COLLECTION, ISSUE, OBSERVATION,  RECOMMENDATION where ");
		sb.append("ATTACHMENTS.ATTACHMENTID=MULTIATTACHMENTS.ATTACHMENTID and  ");
		sb.append("ATTACHMENTS.ATTACHMENTTYPE=ATTACHMENTTYPE.ATTACHMENTTYPEID and  ");
		sb.append("MULTIATTACHMENTS.COLLECTIONID=COLLECTION.COLLECTIONID and ");
		sb.append("ISSUE.RECOMMENDATIONID=RECOMMENDATION.RECOMMENDATIONID and ");
		sb.append("RECOMMENDATION.OBSERVATIONID=OBSERVATION.OBSERVATIONID and ");
		sb.append("OBSERVATION.COLLECTIONID=COLLECTION.COLLECTIONID and ");
		sb.append("ISSUE.ISSUEID=");
		sb.append(issue.getIssueid());
		sb.append("union ");
		sb.append("SELECT  MULTIATTACHMENTS.MULTIATTACHMENTSID, ");
		sb.append("ATTACHMENTS.ATTACHMENTID, ATTACHMENTS.ATTACHMENTFILENAME, ");
		sb.append("ATTACHMENTS.CREATEDATE, ATTACHMENTS.ATTACHMENTSIZE, MULTIATTACHMENTS.COLLECTIONID as ");
		sb.append("REFERSTOID, MULTIATTACHMENTS.REFERSTO, ");
		if(roleIsActionOwnwer){			
			sb.append("case when (ATTACHMENTS.LASTUPDATEDBY=");
			sb.append(workingAsUser+")");
			sb.append(" and (MULTIATTACHMENTS.REFERSTO='");
			sb.append(IssueUpdate.ACTION_IMPLEMENTATION+"')");
			sb.append(" then 'Y'  else 'N' end as showdeleteicon, ");
		}else{
			sb.append("case when (ATTACHMENTS.LASTUPDATEDBY=");
			sb.append(workingAsUser+")");
			sb.append(" then 'Y'  else 'N' end as showdeleteicon, ");
		}
		sb.append("ATTACHMENTS.LASTUPDATE from ATTACHMENTS, ATTACHMENTTYPE, MULTIATTACHMENTS, ISSUE, ");
		sb.append("COLLECTION, OBSERVATION,  RECOMMENDATION where ");
		sb.append("ATTACHMENTS.ATTACHMENTID=MULTIATTACHMENTS.ATTACHMENTID and ");
		sb.append("ATTACHMENTS.ATTACHMENTTYPE=ATTACHMENTTYPE.ATTACHMENTTYPEID  and ");
		sb.append("MULTIATTACHMENTS.ISSUEID=ISSUE.ISSUEID and ISSUE.ISSUEID ");
		if(SystemUtil.getBooleanFromString(issue.getRestrictrightao()) && roleIsActionOwnwer){
			sb.append("=");
			sb.append(issue.getIssueid());
		}else{
			sb.append("in (select ISSUEID from ISSUE ");
			sb.append("where ISSUE.RECOMMENDATIONID=RECOMMENDATION.RECOMMENDATIONID and ");
			sb.append("RECOMMENDATION.OBSERVATIONID=OBSERVATION.OBSERVATIONID and ");
			sb.append("COLLECTION.COLLECTIONID=OBSERVATION.COLLECTIONID and COLLECTION.COLLECTIONID=");
			sb.append(issue.getRecommendation().getObservation().getCollection().getCollectionid());
			sb.append(")");
		}
		sb.append("union ");
		sb.append("SELECT MULTIATTACHMENTS.MULTIATTACHMENTSID, ATTACHMENTS.ATTACHMENTID, ");
		sb.append("ATTACHMENTS.ATTACHMENTFILENAME, ATTACHMENTS.CREATEDATE, ATTACHMENTS.ATTACHMENTSIZE, ");
		sb.append("MULTIATTACHMENTS.COLLECTIONID as REFERSTOID, MULTIATTACHMENTS.REFERSTO, ");
		if(roleIsActionOwnwer){			
			sb.append("'N'  showdeleteicon, ");
		}else{
			sb.append("case when (ATTACHMENTS.LASTUPDATEDBY=");
			sb.append(workingAsUser+")");
			sb.append(" then 'Y'  else 'N' end as showdeleteicon, ");
		}
		sb.append("ATTACHMENTS.LASTUPDATE  from ATTACHMENTS, ATTACHMENTTYPE, MULTIATTACHMENTS, ISSUE, ");
		sb.append("COLISSTASK where  ATTACHMENTS.ATTACHMENTID=MULTIATTACHMENTS.ATTACHMENTID and ");
		sb.append("ATTACHMENTS.ATTACHMENTTYPE=ATTACHMENTTYPE.ATTACHMENTTYPEID and ");
		sb.append("MULTIATTACHMENTS.COLLISSTASKID=COLISSTASK.COLLISSTASKID and ");
		sb.append("COLISSTASK.ISSUEID=ISSUE.ISSUEID and  ISSUE.ISSUEID=");
		sb.append(issue.getIssueid());
		return sb.toString();
	}
	
	/**
	 * Get All Issue Lists .
	 * @param issue issue
	 * @param workingAsUser Integer
	 * @param role Role
	 * @return String
	 */
	private String getQueryForIssueUpdateLinks(Issue issue, Integer workingAsUser, String role){
		boolean roleIsActionOwnwer=false;
		if(IssuesOverviewConstants.ACTION_OWNER.equalsIgnoreCase(role)
				|| IssuesOverviewConstants.ICU.equalsIgnoreCase(role)
				|| IssuesOverviewConstants.AOR_OWNER.equalsIgnoreCase(role)){
			roleIsActionOwnwer=true;
		}
		StringBuffer sb=new StringBuffer("SELECT MULTILINKS.MULTILINKSID, ");
		sb.append("LINKS.LINKID, LINKS.LINKNAME, LINKS.CREATEDATE,");
		sb.append("LINKS.LINKURL, ");
		sb.append("MULTILINKS.COLLECTIONID as REFERSTOID, MULTILINKS.REFERSTO, ");
		if(roleIsActionOwnwer){			
			sb.append("'N'  showdeleteicon, ");
		}else{
			sb.append("case when LINKS.LASTUPDATEDBY=");
			sb.append(workingAsUser);
			sb.append(" then 'Y'  else 'N' end as showdeleteicon, ");
		}
		sb.append("LINKS.LASTUPDATE from LINKS, MULTILINKS, ");
		sb.append("COLLECTION, ISSUE, OBSERVATION,  RECOMMENDATION where ");
		sb.append("LINKS.LINKID=MULTILINKS.LINKID and  ");		
		sb.append("MULTILINKS.COLLECTIONID=COLLECTION.COLLECTIONID and ");
		sb.append("ISSUE.RECOMMENDATIONID=RECOMMENDATION.RECOMMENDATIONID and ");
		sb.append("RECOMMENDATION.OBSERVATIONID=OBSERVATION.OBSERVATIONID and ");
		sb.append("OBSERVATION.COLLECTIONID=COLLECTION.COLLECTIONID and ");
		sb.append("ISSUE.ISSUEID=");
		sb.append(issue.getIssueid());
		sb.append(" union ");
		sb.append("SELECT  MULTILINKS.MULTILINKSID, ");
		sb.append("LINKS.LINKID, LINKS.LINKNAME, ");
		sb.append("LINKS.CREATEDATE, LINKS.LINKURL, MULTILINKS.COLLECTIONID as ");
		sb.append("REFERSTOID, MULTILINKS.REFERSTO, ");
		if(roleIsActionOwnwer){			
			sb.append("case when (LINKS.LASTUPDATEDBY=");
			sb.append(workingAsUser+")");
			sb.append(" and (MULTILINKS.REFERSTO='");
			sb.append(IssueUpdate.ACTION_IMPLEMENTATION+"')");
			sb.append(" then 'Y'  else 'N' end as showdeleteicon, ");
		}else{
			sb.append("case when (LINKS.LASTUPDATEDBY=");
			sb.append(workingAsUser+")");
			sb.append(" then 'Y'  else 'N' end as showdeleteicon, ");
		}
		sb.append("LINKS.LASTUPDATE from LINKS, MULTILINKS, ISSUE, ");
		sb.append("COLLECTION, OBSERVATION,  RECOMMENDATION where ");
		sb.append("LINKS.LINKID=MULTILINKS.LINKID and ");		
		sb.append("MULTILINKS.ISSUEID=ISSUE.ISSUEID and ISSUE.ISSUEID ");
		if(SystemUtil.getBooleanFromString(issue.getRestrictrightao()) && roleIsActionOwnwer){
			sb.append("=");
			sb.append(issue.getIssueid());
		}else{
			sb.append("in (select ISSUEID from ISSUE ");
			sb.append("where ISSUE.RECOMMENDATIONID=RECOMMENDATION.RECOMMENDATIONID and ");
			sb.append("RECOMMENDATION.OBSERVATIONID=OBSERVATION.OBSERVATIONID and ");
			sb.append("COLLECTION.COLLECTIONID=OBSERVATION.COLLECTIONID and COLLECTION.COLLECTIONID=");
			sb.append(issue.getRecommendation().getObservation().getCollection().getCollectionid());
			sb.append(")");
		}
		sb.append(" union ");
		sb.append("SELECT MULTILINKS.MULTILINKSID, LINKS.LINKID, ");
		sb.append("LINKS.LINKNAME, LINKS.CREATEDATE, LINKS.LINKURL, ");
		sb.append("MULTILINKS.COLLECTIONID as REFERSTOID, MULTILINKS.REFERSTO, ");
		if(roleIsActionOwnwer){			
			sb.append("'N'  showdeleteicon, ");
		}else{
			sb.append("case when (LINKS.LASTUPDATEDBY=");
			sb.append(workingAsUser+")");
			sb.append(" then 'Y'  else 'N' end as showdeleteicon, ");
		}
		sb.append("LINKS.LASTUPDATE  from LINKS, MULTILINKS, ISSUE, ");
		sb.append("COLISSTASK where  LINKS.LINKID=MULTILINKS.LINKID and ");		
		sb.append("MULTILINKS.COLLISSTASKID=COLISSTASK.COLLISSTASKID and ");
		sb.append("COLISSTASK.ISSUEID=ISSUE.ISSUEID and  ISSUE.ISSUEID=");
		sb.append(issue.getIssueid());
		return sb.toString();
	}
	
	/**
	 * Provide details of isue
	 * @param issueID Issue ID
	 * @return List od issue
	 * @throws MicosException MicosException
	 */
  @SuppressWarnings("unchecked")
	public List<Object[]> getIssueDetails(Integer issueID)throws MicosException{
		LOGGER.entering(getClass().getName(), "getIssueDetails");
		List<Object[]> list=null;
		EntityManager em = null;
		StringBuffer strBuf=null;
		try{
			em = getEntityManager();
			strBuf=new StringBuffer(" select a.DUEDATE,a.PHASEDUEDATE, a.ISSUEID, a.NOWEXPECTEDDATE, a.PHASEABLE, r1.code, r2.code, ");
			strBuf.append(" r3.code ,a.NOOFPHASES,a.PHASENO,a.EARLYACTIONDUEDATEPHASE,a.NEXTPHASEDUEDATEDURATION,a.REFERENCECODEISSUE");
			strBuf.append("	from (select ISSUE.DUEDATE, NVL(ISSUEPHASE.PHASEDUEDATE, ");
			strBuf.append("ISSUE.PHASE1DUEDATE) PHASEDUEDATE,ISSUE.ISSUEID");
			strBuf.append(", ISSUE.NOWEXPECTEDDATE,ISSUE.SUBSTATUS,ISSUE.EXTENDEDSTATUS,ISSUE.INITIALSTATE, ");
			strBuf.append(" ISSUE.PHASEABLE,ISSUE.NOOFPHASES,ISSUEPHASE.PHASENO, ");
			strBuf.append(" case when ISSUETYPE.ENFORCEEARLYACTIONDUEDATE = 'Y' then ISSUETYPE.EARLYACTIONDUEDATEPHASE end EARLYACTIONDUEDATEPHASE,");
			strBuf.append(" case when ISSUETYPE.LIMITNEXTPHASEDUEDATE = 'Y' then ISSUETYPE.NEXTPHASEDUEDATEDURATION end NEXTPHASEDUEDATEDURATION, ");
			strBuf.append(" REFERENCECODE.CODE as REFERENCECODEISSUE from ISSUE,ISSUEPHASE ,ISSUETYPE, REFERENCECODE  " );
			strBuf.append(" where REFERENCECODE.REFERENCECODEID= ISSUE.INITIALSTATE and ISSUE.ISSUETYPEID=ISSUETYPE.ISSUETYPEID and ");
			strBuf.append(" ISSUE.ISSUEID= ISSUEPHASE.ISSUEID(+) and ISSUE.ISSUEID=");
			strBuf.append(issueID);
			strBuf.append(")");
			strBuf.append("a,REFERENCECODE r1, REFERENCECODE r2, REFERENCECODE r3 ");
			strBuf.append("where ");
			strBuf.append("a.SUBSTATUS=r1.REFERENCECODEID(+) ");
			strBuf.append(" and a.EXTENDEDSTATUS=r2.REFERENCECODEID(+)  ");
			strBuf.append("and a.INITIALSTATE=r3.REFERENCECODEID(+) ");						
			Query query=em.createNativeQuery(strBuf.toString());
			list=query.getResultList();
		}catch (Exception exception) {
			LOGGER.error("getIssueDetails", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			
			LOGGER.exiting(getClass().getName(), "getIssueDetails");
		}
		return list;
	}
	
	/**
	 * get issuephase from issue.
	 * @param issueId Integer
	 * @return Issuephase
	 * @throws MicosException MicosException
	 */
	public Issuephase getIssuePhaseFromIssue(Integer issueId)throws MicosException{
		LOGGER.entering(getClass().getName(), "getIssuePhaseFromIssue");
		EntityManager em = null;
		Issuephase issuephase = null;
		try{
			em = getEntityManager();
			Issue issue=(Issue)load(issueId,Issue.class);
			if(null != issue.getIssuephase()){
				issuephase = issue.getIssuephase();
				issuephase.getCreatedate();
			}
			Recommendation rec=issue.getRecommendation();
			if(null!=rec){
				Observation obs=rec.getObservation();
				if(obs!=null){
					obs.getCollection().getAtscollectionid();
				}
			}
		}catch (Exception exception) {
			LOGGER.error("getIssuePhaseFromIssue", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			
			LOGGER.exiting(getClass().getName(), "getIssuePhaseFromIssue");
		}
		return issuephase;
		
	}
	/**
	 * Loads collection details
	 * @param collection Collection
	 * @throws MicosException MicosException
	 */
	public void loadCollectionCheldren(Collection collection)throws MicosException{
		LOGGER.entering(getClass().getName(), "getIssuePhaseFromIssue");
		EntityManager em = null;
		try{
			em = getEntityManager();
			Collection lcollection=em.find(Collection.class, collection.getCollectionid());
			Set<Observation> obs=lcollection.getObservations();
			if(SystemUtil.isNotNull(obs)){
				for(Observation ob: obs){
					ob.getCreatedate();
					Set<Recommendation> recos=ob.getRecommendations();
					if(SystemUtil.isNotNull(recos)){
						for(Recommendation reco: recos){
							reco.getCoding();
						}
					}
				}
			}
		}catch (Exception exception) {
			LOGGER.error("getIssuePhaseFromIssue", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			
			LOGGER.exiting(getClass().getName(), "getIssuePhaseFromIssue");
		}
	}
	
	/**
	 * save after addUpdate button click.
	 * @param issuephase Issuephase
	 * @param issue Issue
	 * @throws MicosException MicosException
	 */
	public void addUpdateClickSave(Issuephase issuephase,Issue issue)throws MicosException{
		LOGGER.entering(getClass().getName(), "addUpdateClickSave");
		EntityManager em = null;
		try{
			em = getEntityManager();
			EntityTransaction et=em.getTransaction();
			et.begin();	
			em.merge(issue);		
			em.merge(issuephase);
			et.commit();
		}catch (Exception exception) {
			LOGGER.error("getIssuePhaseFromIssue", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			
			LOGGER.exiting(getClass().getName(), "addUpdateClickSave");
		}
	}
	
	/**
	 * save after addUpdate button click.
	 * @param issue Issue
	 * @throws MicosException MicosException
	 */
	public void addUpdateClickSave(Issue issue)throws MicosException{
		LOGGER.entering(getClass().getName(), "addUpdateClickSave");
		EntityManager em = null;
		try{
			em = getEntityManager();
			em.getTransaction().begin();
			em.merge(issue);
			em.getTransaction().commit();
		}catch (Exception exception) {
			LOGGER.error("getIssuePhaseFromIssue", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			
			LOGGER.exiting(getClass().getName(), "addUpdateClickSave");
		}
	}
	
	/**
	 * Save collection
	 * @param collection Collection
	 * @throws MicosException MicosException
	 */
	public void completeImplementation(Collection collection)throws MicosException{
		LOGGER.entering(getClass().getName(), "completeImplementation");
		EntityManager em = null;
		try{
			em = getEntityManager();
			em.getTransaction().begin();
			em.merge(collection);
			em.getTransaction().commit();
		}catch (Exception exception) {
			LOGGER.error("completeImplementation", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}			
			LOGGER.exiting(getClass().getName(), "completeImplementation");
		}
	}	
}

/*
 * Check-In History: $Log: IssueUpdateDaoImpl.java,v $
 * Check-In History: Revision 1.1  2010/10/25 15:25:36  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.5  2010/10/15 13:46:13  f542625
 * Check-In History: changed for audittrail
 * Check-In History:
 * Check-In History: Revision 1.4  2010/10/15 07:53:48  f542625
 * Check-In History: AR 1.5 merging
 * Check-In History:
 * Check-In History: Revision 1.3  2010/09/30 05:24:38  g304578
 * Check-In History: Modified for status, substatus issue
 * Check-In History:
 * Check-In History: Revision 1.5  2010/09/29 11:01:22  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.4  2010/08/06 11:45:05  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.2  2010/07/21 16:18:00  f542625
 * Check-In History: defect fixing...
 * Check-In History:
 * Check-In History: Revision 1.1  2010/07/07 08:57:23  g304578
 * Check-In History: changed for package 3
 * Check-In History:
 * Check-In History: Revision 1.1  2010/07/06 13:18:33  g266579
 * Check-In History: For package 3
 * Check-In History:
 * Check-In History: Revision 1.54  2010/07/02 11:21:15  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.53  2010/07/02 07:22:05  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.52  2010/07/01 15:33:13  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.51  2010/07/01 14:39:23  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.50  2010/07/01 07:40:53  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.49  2010/06/30 14:17:01  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.48  2010/06/30 13:16:19  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.47  2010/06/30 13:14:36  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.46  2010/06/30 13:07:45  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.45  2010/06/30 11:36:42  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.44  2010/06/30 10:36:26  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.43  2010/06/29 15:00:31  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.42  2010/06/29 10:56:15  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.41  2010/06/29 10:41:14  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.40  2010/06/29 06:56:14  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.39  2010/06/29 06:48:19  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.38  2010/06/28 13:26:51  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.37  2010/06/28 07:35:38  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.36  2010/06/24 13:55:40  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.35  2010/06/24 09:36:11  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.34  2010/06/24 08:56:35  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.33  2010/06/23 10:11:54  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.32  2010/06/23 07:18:31  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.31  2010/06/22 15:16:23  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.30  2010/06/22 15:09:28  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.29  2010/06/22 15:01:38  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.28  2010/06/22 14:33:58  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.27  2010/06/22 14:12:04  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.26  2010/06/22 12:48:29  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.25  2010/06/22 11:35:44  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.24  2010/06/22 08:38:06  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.23  2010/06/22 07:22:55  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.22  2010/06/22 06:38:58  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.21  2010/06/22 06:08:41  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.20  2010/06/21 13:02:58  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.19  2010/06/21 12:33:29  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.18  2010/06/21 12:18:12  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.17  2010/06/21 11:26:48  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.16  2010/06/21 10:20:46  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.15  2010/06/21 10:14:47  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.14  2010/06/21 08:33:28  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.13  2010/06/21 06:37:44  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.12  2010/06/21 06:20:09  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.11  2010/06/21 05:30:13  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.10  2010/06/18 12:23:27  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.9  2010/06/18 11:23:17  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.8  2010/06/18 10:00:47  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.7  2010/06/18 09:26:38  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.6  2010/06/18 08:56:05  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.5  2010/06/18 08:35:54  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.4  2010/06/18 07:59:54  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.3  2010/06/18 07:42:26  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.2  2010/06/18 06:52:49  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.1  2010/06/18 06:12:37  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 */