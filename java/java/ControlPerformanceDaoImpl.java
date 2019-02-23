/******************************************************************************
 * Copyright(c) 2003-2007 CREDIT SUISSE Financial Services. All Rights Reserved.
 *
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/
package com.csg.cs.micos.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.csg.cs.core.base.exception.ExceptionContext;
import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.bean.Aorowner;
import com.csg.cs.micos.bean.Attachment;
import com.csg.cs.micos.bean.Attachmenttype;
import com.csg.cs.micos.bean.Control;
import com.csg.cs.micos.bean.ControlRemWarEsc;
import com.csg.cs.micos.bean.Controlobjective;
import com.csg.cs.micos.bean.Controlreference;
import com.csg.cs.micos.bean.Controlrfc;
import com.csg.cs.micos.bean.Controltype;
import com.csg.cs.micos.bean.Employee;
import com.csg.cs.micos.bean.Executionschedule;
import com.csg.cs.micos.bean.Executionschema;
import com.csg.cs.micos.bean.Executiontask;
import com.csg.cs.micos.bean.Executor;
import com.csg.cs.micos.bean.Executoraor;
import com.csg.cs.micos.bean.Executorpid;
import com.csg.cs.micos.bean.Goal;
import com.csg.cs.micos.bean.Goalscontrolobjective;
import com.csg.cs.micos.bean.Goalspolicy;
import com.csg.cs.micos.bean.Hierarchy;
import com.csg.cs.micos.bean.Hierarchyelement;
import com.csg.cs.micos.bean.Hierarchyrole;
import com.csg.cs.micos.bean.Link;
import com.csg.cs.micos.bean.MicosUserBean;
import com.csg.cs.micos.bean.Multiattachment;
import com.csg.cs.micos.bean.Multilanguagetranslation;
import com.csg.cs.micos.bean.Multilink;
import com.csg.cs.micos.bean.Notification;
import com.csg.cs.micos.bean.Notificationtype;
import com.csg.cs.micos.bean.Periodtype;
import com.csg.cs.micos.bean.Policy;
import com.csg.cs.micos.bean.Qaschema;
import com.csg.cs.micos.bean.Rating;
import com.csg.cs.micos.bean.Ratinggrouprating;
import com.csg.cs.micos.bean.Referencecode;
import com.csg.cs.micos.bean.RemWarEscOtherRecipient;
import com.csg.cs.micos.bean.Rfc;
import com.csg.cs.micos.bean.Schedule;
import com.csg.cs.micos.bean.Scheduleadhocdate;
import com.csg.cs.micos.bean.Schedulewhen;
import com.csg.cs.micos.bean.Sequenceorder;
import com.csg.cs.micos.bean.Setup;
import com.csg.cs.micos.bean.Signoffschema;
import com.csg.cs.micos.bean.Status;
import com.csg.cs.micos.bean.Step;
import com.csg.cs.micos.bean.Supportedlanguage;
import com.csg.cs.micos.bean.Supportschema;
import com.csg.cs.micos.bean.Translation;
import com.csg.cs.micos.dao.ControlPerformanceDao;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.util.DateUtil;
import com.csg.cs.micos.util.MicosCommonQuery;
import com.csg.cs.micos.util.MicosConstantValues;
import com.csg.cs.micos.util.SystemUtil;
import com.csg.cs.micos.util.MicosConstantValues.TaskSelectionType;
import com.csg.cs.micos.view.AttachmentView;
import com.csg.jsf.bean.UserBean;

/**
 * <code>ControlPerformanceDaoImpl</code> provides methods for executing database
 * queries using hibernate.
 *
 * @author Cognizant Technology Solutions
 * @author Last change by $Author: 153146 $
 * @version $Id: ControlPerformanceDaoImpl.java,v 1.1 2010/10/25 15:25:29 153146 Exp $
 */
public class ControlPerformanceDaoImpl extends HibernateDaoSupport implements
		ControlPerformanceDao {

	/**
	 * A logger instance with the fully qualified class name as logger name.
	 */
	private static final Logger LOGGER = LoggerHelper
			.getLogger(ControlPerformanceDaoImpl.class);

	/**
	 * Query for retrieving rating
	 */
	public static final String QRY_RATING = "from com.csg.cs.micos.bean.Rating rating where rating.ratingid=:ratingId";

	/**
	 * Query for retrieving rating
	 */
	public static final String QRY_STATUS = "from com.csg.cs.micos.bean.Status status where status.statuscode=:statuscode";
	
	/**
	 * Get count of all Goals for which the user is owner.
	 * 
	 * @param 
	 * 
	 * @return size Integer
	 * @throws MicosException
	 *             in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	public Integer getGoalCount() throws MicosException {
		LOGGER.entering(getClass().getName(), "getGoalCount");
		Session hibernateSession = getSession();
		Set<Goal> setOfGoal = null;
		Employee employee = null;
		Integer size = null;
		
		try {
			MicosUserBean user = (MicosUserBean) UserBean.getUserBean();
			String userId = user.getWorkingAsUser().getEmployeeid().toString();
			Query query = hibernateSession
					.createQuery("from com.csg.cs.micos.bean.Employee employee "
							+ "where employee.employeeid="
							+ Integer.parseInt(userId));
			if (null != query) {
				employee = (Employee) query.uniqueResult();
				if (null != employee) {
					setOfGoal = employee.getGoalsByGoalowner();
				}
			}

			if (!SystemUtil.isNull(setOfGoal)) {
				size = setOfGoal.size();
			}
		} catch (Exception exception) {
      LOGGER.error("getGoalCount", exception);
      throw new MicosException(ExceptionContext.ERROR, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getGoalCount");
		}
		return size;
	}

	/**
	 * Get count of all Controls for which the user is owner.
	 * 
	 * @param 
	 * 
	 * @return size Integer
	 * @throws MicosException
	 *             in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	public Integer getControlCount() throws MicosException {
		LOGGER.entering(getClass().getName(), "getControlCount");
		Session hibernateSession = getSession();
		Integer controlCount = 0;
		try {
			MicosUserBean user = (MicosUserBean) UserBean.getUserBean();
			String userId = "("+user.getWorkingAsUser().getEmployeeid().toString()+")";
			
			Query query = hibernateSession.createSQLQuery(MicosCommonQuery.getControlCountQuery(userId));
			BigDecimal count = (BigDecimal) query.uniqueResult();
			if(count != null) {
				controlCount = count.intValue();
			}
			
		} catch (Exception exception) {
			LOGGER.error("getControlCount", exception);
			throw new MicosException(ExceptionContext.ERROR, SystemUtil
					.getErrorMessage(exception));
		} finally {
			releaseSession(hibernateSession);
			LOGGER.exiting(getClass().getName(), "getControlCount");
		}
		return controlCount;
	}

	/**
	 * Get count of all Hierarchies for which the user is owner.
	 * 
	 * @param 
	 * 
	 * @return size Integer
	 * @throws MicosException
	 *             in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	public Integer getHierarchyCount() throws MicosException {
		LOGGER.entering(getClass().getName(),"getHierarchyCount");
		Session hibernateSession = getSession();
		Set<Hierarchy> setOfHierarchy = null;
		//Set<Hierarchyadmin> setOfHierarchyAdmin = null;
		Employee employee = null;
		Integer size = null;
		
		try {
			MicosUserBean user = (MicosUserBean) UserBean.getUserBean();
			String userId = user.getWorkingAsUser().getEmployeeid().toString();
			Query query = hibernateSession
					.createQuery("from com.csg.cs.micos.bean.Employee employee "
							+ "where employee.employeeid="
							+ Integer.parseInt(userId));
			if (null != query) {
				employee = (Employee) query.uniqueResult();
				if (null != employee) {
					setOfHierarchy = employee.getHierarchiesByHierarchyowner();
					//setOfHierarchyAdmin = employee.getHierarchyadminsByHierarchyadmin();
				}
			}

			if (setOfHierarchy != null) {
				size = setOfHierarchy.size();
			}
			/*if(setOfHierarchyAdmin != null) {
				size += setOfHierarchyAdmin.size();
			}*/
		} catch (Exception exception) {
      LOGGER.error("getHierarchyCount", exception);
      throw new MicosException(ExceptionContext.ERROR, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getHierarchyCount");
		}
		return size;
	}
	
	
	/**
	 * load ControlRemWarEsc By PowerOptionId.
	 * @param powerOptionId Integer
	 * @return Set ControlRemWarEsc
	 * @throws MicosException MicosException
	 */
	public Set<ControlRemWarEsc> loadControlRemWarEscByPowerOptionId(Integer powerOptionId) throws MicosException{
		LOGGER.entering(getClass().getName(),"loadControlRemWarEscByPowerOptionId");
		Session hibernateSession = getSession();
		Set<ControlRemWarEsc> setControlRemWarEsc = new HashSet<ControlRemWarEsc>();
		List<BigDecimal>  list = null;
		try {
			Query query = hibernateSession.createSQLQuery("select CONTROLREMWARESCID from CONTROLREMWARESC where POWEROPTIONID="+powerOptionId);
			
			if (null != query) {
				list = query.list();
				if(SystemUtil.isNotNull(list)){
					for(BigDecimal controlRemWarEscId:list){
						ControlRemWarEsc controlRemWarEsc = (ControlRemWarEsc) hibernateSession.get(
								ControlRemWarEsc.class, Integer.parseInt(controlRemWarEscId.toString()));
						controlRemWarEsc.getControlRemWarEscId();
						if(null != controlRemWarEsc.getRecipient()){
							controlRemWarEsc.getRecipient().getCode();
						}
						if(null != controlRemWarEsc.getFrequency()){
							controlRemWarEsc.getFrequency().getCode();
						}
						if(null != controlRemWarEsc.getPeriodType()){
							controlRemWarEsc.getPeriodType().getName();
						}
						if(null != controlRemWarEsc.getNotType()){
							controlRemWarEsc.getNotType().getCode();
						}
						if(SystemUtil.isNotNull(controlRemWarEsc.getOtherRecipients())){
							for(RemWarEscOtherRecipient escOtherRecipient : controlRemWarEsc.getOtherRecipients()){
								escOtherRecipient.getEmployee().getEmployeeName();
							}
						}
						setControlRemWarEsc.add(controlRemWarEsc);
					}
					
				}
			}
		}catch (Exception exception) {
		      LOGGER.error("getHierarchyCount", exception);
		      throw new MicosException(ExceptionContext.ERROR, SystemUtil
		          .getErrorMessage(exception));
		} finally {
		      releaseSession(hibernateSession);
		      LOGGER.exiting(getClass().getName(), "getHierarchyCount");
		 }
		return setControlRemWarEsc;
	}

	/**
	   * Fetches task count on the basis of selection
	   * @param userId executor
	   * @param taskType String
	   * @param hibernateSession hibernateSession 
	   * @return count 
	   * 
	   * @throws MicosException
	   *           in the following cases:
	   *           <ul>
	   *           <li>if unable to retrieve data e.g. when looking up specific
	   *           data via a known identifier</li>
	   *           <li>if unable to access data for varying reasons like permission
	   *           denied, data source lookup failure, data integrity violations etc
	   *           </li>
	   *           </ul>
	   */
	public int getTaskCount(Integer userId, String taskType, Session hibernateSession) throws MicosException {
		LOGGER.entering(getClass().getName(),"getTaskCount");
		int result = 0;
		try {
			
			StringBuilder queryBuf = new StringBuilder();
	    	
	    	queryBuf.append("SELECT count(*) FROM EXECUTIONTASK t ");
	    	queryBuf.append("WHERE t.Executor = " + userId + "   ");
	    	queryBuf.append("AND t.Statusid = 2 ");
	    	queryBuf.append("AND t.CONTROLCOLLECTIONTASK != 'C' ");
	    	if(taskType != null){
	    		if(taskType.equals(TaskSelectionType.RED)){
	    			queryBuf.append(" AND TO_DATE(t.executionenddate, 'DD.MM.YYYY')  < TO_DATE(sysdate, 'DD.MM.YYYY') ");
	    		} else if(taskType.equals(TaskSelectionType.YELLOW)) {
	    			queryBuf.append(" AND (TO_DATE(t.executionenddate, 'DD.MM.YYYY') >= TO_DATE(sysdate, 'DD.MM.YYYY') ");
					queryBuf.append(" AND TO_DATE(t.executionenddate, 'DD.MM.YYYY') <= TO_DATE((sysdate + 4), 'DD.MM.YYYY'))");
	    		} else if(taskType.equals(TaskSelectionType.GREEN)) {
	    			queryBuf.append(" AND TO_DATE(t.executionenddate, 'DD.MM.YYYY') > TO_DATE((sysdate + 4), 'DD.MM.YYYY') ");
	    		}
	    	}
	    			    	
	    	Query query = hibernateSession.createSQLQuery(queryBuf.toString());
	    	BigDecimal dbResult = (BigDecimal)query.uniqueResult();
	    	if(dbResult != null) {
	    		result = dbResult.intValue();
	    	}

		} catch (Exception exception) {
			LOGGER.error("getTaskCount", exception);
			throw new MicosException(ExceptionContext.ERROR, SystemUtil
					.getErrorMessage(exception));
		} finally {
			LOGGER.exiting(getClass().getName(),
					"getTaskCount");
		}
		return result;
	}

	/**
	 * Loads a execution task against a task id.
	 * @param taskId Task ID
	 * @return Executiontask A Executiontask
	 * @exception MicosException Micos Exception
	 */
	public Executiontask getTaskDetails(Integer taskId) throws MicosException {
		LOGGER.entering(getClass().getName(), "getTaskDetails");
		Executiontask lExecutiontask = null;
		Session hibernateSession = getSession();

		List<Executiontask> listExecutiontasks = null;
		try {
			lExecutiontask = (Executiontask) hibernateSession.get(
					Executiontask.class, taskId);
			if (null != lExecutiontask) {
				if (lExecutiontask.getStatus() != null) {
					lExecutiontask.getStatus().getStatuscode();
				}
				Rating rating = lExecutiontask.getRating();
				lExecutiontask.getExecutor().getPid();
				if (rating != null) {
					rating.getRatingcode();
				}
				// Enhancement 1.2 Amended Sign-off process start
				Rating signoffRating = lExecutiontask.getSignoffrating();
				if (signoffRating != null) {
					signoffRating.getRatingid();
				}
				Hierarchyelement executionHe = lExecutiontask.getExecutionHierarchyElement();
				if(executionHe != null) {
					executionHe.getName();
					executionHe.getHierarchy().getName();
				}
				Hierarchyrole executionRole = lExecutiontask.getExecutionRole();
				if(executionRole != null) {
					executionRole.getName();
				}
        		// 1.4.1
        		lExecutiontask.getGloballyvisible();
        		lExecutiontask.getCountry();

				lExecutiontask.getSignoffcomment();
				// Enhancement 1.2 Amended Sign-off process end
				Executionschedule executionSchedule = lExecutiontask
						.getExecutionschedule();
				if (null != executionSchedule) {
					Step step = executionSchedule.getStep();
					if (null != step) {
						Setup setup = step.getSetup();
						if (null != setup) {
							Control control = setup.getControl();
							if (null != control) {
								// Loading scor aor for control
								Aorowner aorOwner = control.getAorowner();
								if (null != aorOwner) {
									Employee employee = aorOwner
											.getEmployeeByAorowner();
									employee.getEmployeeName();
								}
								lazyLoadMultilanguagetranslation(control
										.getMultilanguagetranslationByTitle());
								lazyLoadMultilanguagetranslation(control
										.getMultilanguagetranslationByDescription());
								lazyLoadMultilanguagetranslation(control
										.getMultilanguagetranslationByDetaildescription());
								lazyLoadAttachemnts(control
										.getMultiattachments());
								lazyLoadLinks(control.getMultilinks());
								Signoffschema signoffSchema = lExecutiontask
										.getSignoffschema();
								if (null != signoffSchema) {
									listExecutiontasks = getRollingSignoff(
											signoffSchema, hibernateSession);
									if (!SystemUtil.isNull(listExecutiontasks)) {
										Iterator it = listExecutiontasks
												.iterator();
										while (it.hasNext()) {
											Executiontask aExecutiontask = (Executiontask) it
													.next();
											if (aExecutiontask != null) {
												aExecutiontask.getDescription();
												Employee employee = aExecutiontask
														.getExecutor();
												if (employee != null) {
													employee.getPid();
												}
												Rating aRating = aExecutiontask
														.getRating();
												if (aRating != null) {
													aRating.getRatingname();
												}
											}
										}
									}
								}
								lazyLoadAttachemnts(lExecutiontask
										.getMultiattachments());
								lazyLoadLinks(lExecutiontask.getMultilinks());
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("getTaskDetails", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			releaseSession(hibernateSession);
			LOGGER.exiting(getClass().getName(), "getTaskDetails");
		}
		return lExecutiontask;
	}

	/**
	 * Get a execution task base on the PK.
	 * @param executionTaskId Execution task ID
	 * @return Execution Task
	 * @throws MicosException Micos Exception
	 */
	public Executiontask loadExecutionTask(Integer executionTaskId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"loadAExecutionTask");
		Executiontask executiontask = null;
		Session session = null;
		try {
			if (null != executionTaskId) {
				session = getSession();
				executiontask = (Executiontask) session.get(
						Executiontask.class, executionTaskId);
        if(executiontask != null){
          executiontask.getExecutionschedule();
          // Use for lazy loading in group view.
          executiontask.getStatus().getStatuscode();
        }
			}
		} catch (Exception exception) {
      LOGGER.error("loadAExecutionTask", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "loadAExecutionTask");
		}
		return executiontask;
	}

	/**
	 * Load a rating base on rating code.
	 * @param ratingId Rating Id
	 * @return Rating
	 * @throws MicosException Micos Exception
	 */
	public Rating loadRating(String ratingId) throws MicosException {
		LOGGER.entering(getClass().getName(),"loadRating");
		Rating rating = null;
		Session session = null;
		try {
			if (!SystemUtil.isNull(ratingId)) {
				session = getSession();
				Query query = session.createQuery(QRY_RATING);
				query.setString("ratingId", ratingId);
				rating = (Rating) query.uniqueResult();
			}
		} catch (Exception exception) {
      LOGGER.error("loadRating", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "loadRating");
		}
		return rating;
	}

	/**
	 * Loads the schima based on schima type.
	 * @param schemaId Schima ID
	 * @param schemaType Schema Type
	 * @return Object
	 * @throws MicosException Micos Exception
	 */
	public Object loadSchema(Integer schemaId, String schemaType)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"loadSchema");
		Object schemaObj = null;
		Session session = null;
		try {
			if (null != schemaId && !SystemUtil.isNull(schemaType)) {
				session = getSession();
				if (MicosConstantValues.SchemaType.SUPPORT.equals(schemaType)) {
					schemaObj = (Supportschema) session.get(
							Supportschema.class, schemaId);
				} else if (MicosConstantValues.SchemaType.SIGNOFF.equals(schemaType)) {
					schemaObj = (Signoffschema) session.get(
							Signoffschema.class, schemaId);
				} else if (MicosConstantValues.SchemaType.QA.equals(schemaType)) {
					schemaObj = (Qaschema) session
							.get(Qaschema.class, schemaId);
				} else if (MicosConstantValues.SchemaType.EXECUTION.equals(schemaType)) {
					schemaObj = (Executionschema) session.get(
							Executionschema.class, schemaId);
				}
			}
		} catch (Exception exception) {
      LOGGER.error("loadSchema", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "loadSchema");
		}
		return schemaObj;
	}

	/**
	 * Load an employee based on PK.
	 * @param employeeId Employe ID
	 * @return Emoployee 
	 * @throws MicosException Micos Exception
	 */
	public Employee loadEmployee(Integer employeeId) throws MicosException {
		LOGGER.entering(getClass().getName(),"loadEmployee");
		Employee employee = null;
		Session session = null;
		try {
			if (null != employeeId) {
				session = getSession();
				employee = (Employee) session.load(Employee.class, employeeId);
				employee.getPid();
			}
		} catch (Exception exception) {
      LOGGER.error("loadEmployee", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "loadEmployee");
		}
		return employee;
	}

	/**
	 * Save a execution task.
	 * @param schemaObj Schima Object
	 * @throws MicosException Micos Exception
	 */
	public void saveExecutionTask(Object schemaObj) throws MicosException {
		LOGGER.entering(getClass().getName(),"saveExecutionTask");
		Session session = null;
		try {
			if (null != schemaObj) {
				session = getSession();
				if (schemaObj instanceof Supportschema) {
					Supportschema supportSchema = (Supportschema) schemaObj;
					session.saveOrUpdate(supportSchema);
				} else if (schemaObj instanceof Signoffschema) {
					Signoffschema signOfftSchema = (Signoffschema) schemaObj;
					session.saveOrUpdate(signOfftSchema);
				} else if (schemaObj instanceof Qaschema) {
					Qaschema qaSchema = (Qaschema) schemaObj;
					session.saveOrUpdate(qaSchema);
				} else if (schemaObj instanceof Executionschema) {
					Executionschema executionSchema = (Executionschema) schemaObj;
					session.saveOrUpdate(executionSchema);
				}
				session.flush();
			}
		} catch (Exception exception) {
      LOGGER.error("saveExecutionTask", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "saveExecutionTask");
		}
	}

	/**
	 * Retrive the Goal And Control Objective for Control Information
	 * @return Executiontask 
	 * @throws MicosException MicosException
	 * @param taskId Integer
	 *             in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	public Executiontask retriveGoalAndControlObjective(Integer taskId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "retriveGoalAndControlObjective");
		Session hibernateSession = getSession();
		Executiontask aExecutiontask = null;
		Set<Controlrfc> setControlrfc = null;
		Controlrfc aControlrfc = null;
		Set<Goalscontrolobjective> setGoalscontrolobjective = null;
		Goalscontrolobjective aGoalscontrolobjective = null;
		Executionschedule aExecutionschedule = null;
		Step aStep = null;
		Setup aSetup = null;
		Control aControl = null;
		Controlobjective aControlObjective = null;
		Aorowner aAorowner = null;
		Employee aEmployee = null;
		Hierarchyelement aHierarchyelement = null;
		Goal aGoal = null;

		try {
			aExecutiontask = (Executiontask) hibernateSession.get(
					Executiontask.class, taskId);
			if (aExecutiontask != null) {
				aExecutionschedule = aExecutiontask.getExecutionschedule();
				if (aExecutionschedule != null) {
					aStep = aExecutionschedule.getStep();
					if (aStep != null) {
						aSetup = aStep.getSetup();
						if (aSetup != null) {
							aControl = aSetup.getControl();
							setControlrfc = aControl.getControlrfcs();
						}
					}
				}
			}

			if (!SystemUtil.isNull(setControlrfc)) {
				Iterator itControlrfc = setControlrfc.iterator();
				while (itControlrfc.hasNext()) {
					aControlrfc = (Controlrfc) itControlrfc.next();
				}
			}

			if (aControlrfc != null) {
				Rfc aRfc = aControlrfc.getRfc();
				if (aRfc != null) {
					aAorowner = aRfc.getAorowner();
					if (aAorowner != null) {
						aEmployee = aAorowner.getEmployeeByAorowner();
						aHierarchyelement = aAorowner.getHierarchyelement();
						aEmployee.getEmployeeName();
						aHierarchyelement.getDescription();
					}

					aControlObjective = aRfc.getControlobjective();
					if (aControlObjective != null) {
						lazyLoadMultilanguagetranslation(aControlObjective
								.getMultilanguagetranslationByTitle());
						lazyLoadMultilanguagetranslation(aControlObjective
								.getMultilanguagetranslationByDescription());
						setGoalscontrolobjective = aControlObjective
								.getGoalscontrolobjectives();
					}
				}
			}

			if (!SystemUtil.isNull(setGoalscontrolobjective)) {
				Iterator itGoalscontrolobjective = setGoalscontrolobjective
						.iterator();
				while (itGoalscontrolobjective.hasNext()) {
					aGoalscontrolobjective = (Goalscontrolobjective) itGoalscontrolobjective
							.next();
				}
			}

			if (aGoalscontrolobjective != null) {
				aGoal = aGoalscontrolobjective.getGoal();
				if (aGoal != null) {
					lazyLoadMultilanguagetranslation(aGoal
							.getMultilanguagetranslationByTitle());
					lazyLoadMultilanguagetranslation(aGoal
							.getMultilanguagetranslationByDescription());
					Employee employee = aGoal.getEmployeeByGoalowner();
					if (employee != null) {
						employee.getEmployeeName();
					}
					// Enhancement 1.4 code changes start
					if (aGoal.getMultiattachments() != null) {
						lazyLoadAttachemnts(aGoal.getMultiattachments());
					}
					if (aGoal.getMultilinks() != null) {
						lazyLoadLinks(aGoal.getMultilinks());
					}
					if (aGoal.getGoalspolicies() != null) {
						Set goalpolicySet = aGoal.getGoalspolicies();
						Iterator itPolicy = goalpolicySet.iterator();
						while (itPolicy.hasNext()) {
							Goalspolicy goalpolicy = (Goalspolicy) itPolicy
									.next();
							if (goalpolicy != null) {
								Policy policy = goalpolicy.getPolicy();
								if (policy != null) {
									policy.getLastupdate();
									policy.getPolicyid();
									policy.getPolicyname();
									policy.getPolicyurl();
								}
							}
						}
					}
					// Enhancement 1.4 code changes end
				}
			}

		} catch (Exception exception) {
			LOGGER.error("retriveGoalAndControlObjective", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			releaseSession(hibernateSession);
			LOGGER.exiting(getClass().getName(),
					"retriveGoalAndControlObjective");
		}
		return aExecutiontask;
	}

	/**
	 * Retrive the Execution Output for Control Information
	 * 
	 * @param taskId
	 *            Integer
	 * @param taskType
	 *            String
	 * @param fromTaskArchive
	 *            boolean
	 * @return Set of Taks
	 * @throws MicosException
	 *             MicosException in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	@SuppressWarnings("unchecked")
	public List<Executiontask> getExecutionOutputOfCurrentSchedule(Integer taskId, String taskType, boolean fromTaskArchive) throws MicosException {
		LOGGER.entering(getClass().getName(), "getExecutionOutputOfCurrentSchedule");
    Session hibernateSession = getSession();
    List<Executiontask> setExecutiontaskForSchedule = null;
    Executiontask aExecutiontask = null;
    Rating aRating = null;
    Status status = null;
    Sequenceorder seqOrder = null;
    try {     
      StringBuffer queryBuffer = new StringBuffer(
          "from com.csg.cs.micos.bean.Executiontask as executiontask where ");
      if (MicosConstantValues.SchemaType.EXECUTION.equalsIgnoreCase(taskType)) {
        queryBuffer.append(" executiontask.supportschema is not null ");
      } else if (MicosConstantValues.SchemaType.QA.equalsIgnoreCase(taskType)) {
        queryBuffer.append(" (executiontask.supportschema is not null "
            + " or executiontask.executionschema is not null) ");
      } else if (MicosConstantValues.SchemaType.SIGNOFF
          .equalsIgnoreCase(taskType)) {
        queryBuffer.append(" (executiontask.supportschema is not null "
            + " or executiontask.executionschema is not null "
            + " or executiontask.qaschema is not null) ");
      } else {
        queryBuffer.append(" executiontask.supportschema is not null "
            + " and executiontask.executionschema is not null "
            + " and executiontask.qaschema is not null ");
      }
      queryBuffer
          .append(" and executiontask.executionschedule.executionscheduleid in ( select "
              + " task.executionschedule.executionscheduleid from com.csg.cs.micos.bean.Executiontask "
              + " as task where task.executiontaskid = " + taskId + ")");
      Query query = hibernateSession.createQuery(queryBuffer.toString());
      if (query != null) {
        setExecutiontaskForSchedule = query.list();
      
        /*
         * Adding to the list incase of dependent tasks exists
         */ 
        if (setExecutiontaskForSchedule != null) {
          setExecutiontaskForSchedule
              .addAll(getTriggeredByScheduleExecutionTasks(taskId,
                  hibernateSession));
          if(fromTaskArchive){
            setExecutiontaskForSchedule.add((Executiontask)hibernateSession.get(Executiontask.class, taskId));
          }          
        }
       
        if (SystemUtil.isNotNull(setExecutiontaskForSchedule)) {
          Iterator itrExecutionTasks = setExecutiontaskForSchedule.iterator();
          while (itrExecutionTasks.hasNext()) {
            aExecutiontask = (Executiontask)itrExecutionTasks.next();
            if (aExecutiontask != null) {
              aExecutiontask.getSupportschema();
              aExecutiontask.getExecutionschema();
              aExecutiontask.getQaschema();
              aExecutiontask.getSignoffschema();
              aExecutiontask.getDescription();
              aExecutiontask.getExecutor();
              aExecutiontask.getExecutor().getPid();
              aRating = aExecutiontask.getRating();
              if (aRating != null) {
                aRating.getRatingname();
              }      
              status = aExecutiontask.getStatus();
              if(status != null){
                status.getStatuscode();
              }
              seqOrder = aExecutiontask.getSequenceorder();
              if(seqOrder != null){
                seqOrder.getOrdernumber();
              }
              lazyLoadAttachemnts(aExecutiontask.getMultiattachments());
              lazyLoadLinks(aExecutiontask.getMultilinks());
            }
          }
        }
      }
    } catch (Exception exception) {
      LOGGER.error("getExecutionOutputOfCurrentSchedule", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(),
          "getExecutionOutputOfCurrentSchedule");
    }
    return setExecutiontaskForSchedule;

	}

  /**
   * Get triggered execution schedule
   * 
   * @param taskId
   *          Execution task id
   * @param hibernateSession
   *          Session
   * @throws MicosException Exception
   * @return triggered by execution schedule task list
   */
	@SuppressWarnings("unchecked")
  private List<Executiontask> getTriggeredByScheduleExecutionTasks(
      Integer taskId, Session hibernateSession) throws MicosException {
    LOGGER.entering(getClass().getName(), "getTriggeredBySchedule");
    List<Executiontask> triggeredTaskList = null;
    Query query = null;
    try {
      query = hibernateSession
          .createSQLQuery(
              "select * from Executiontask executiontask where "
                  + " executiontask.executionscheduleid in ( "
                  + " select executionschedule.executionscheduleid from Executionschedule executionschedule "
                  + " connect by prior executionschedule.triggeredbyscheduleid = executionschedule.executionscheduleid "
                  + " start with executionschedule.executionscheduleid = (select triggeredschedule.triggeredbyscheduleid "
                  + " from Executiontask task, Executionschedule triggeredschedule where "
                  + " triggeredschedule.executionscheduleid = task.executionscheduleid "
                  + " and task.executiontaskid = " + taskId + "))").addEntity(
              Executiontask.class);
      if (query != null) {
        triggeredTaskList = query.list();
      }
    } catch (Exception exception) {
      LOGGER.error("getTriggeredByScheduleExecutionTasks", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    }
    LOGGER.exiting(getClass().getName(), "getTriggeredBySchedule");
    return triggeredTaskList;
  }
	
	/**
	 * lazy load links
	 * 
	 * @param multiLinkSet
	 *            link set
	 */
	private void lazyLoadLinks(Set multiLinkSet){
		if (!SystemUtil.isNull(multiLinkSet)) {
			Iterator itMultiLink = multiLinkSet.iterator();
			while (itMultiLink.hasNext()) {
				Multilink multiLink = (Multilink) itMultiLink.next();
				if (null != multiLink) {
					Link link = multiLink.getLink();
					if (link != null) {
						link.getLinkid();
						link.getLinkname();
					}
				}
			}
		}
	}
	
	/**
	 * lazy load attachments
	 * @param multiAttachmentSet attachment set
	 */
	private void lazyLoadAttachemnts(Set multiAttachmentSet){
		if (!SystemUtil.isNull(multiAttachmentSet)) {
			Iterator itmultiattachment = multiAttachmentSet.iterator();
			while (itmultiattachment.hasNext()) {
				Multiattachment multiAttachment = (Multiattachment) itmultiattachment
						.next();
				if (null != multiAttachment) {
					Attachment attachment = multiAttachment
							.getAttachment();
					if (null != attachment) {
						attachment.getAttachmentid();
						attachment.getAttachmentfilename();
					}
				}
			}
		}
	}

	/**
	 * Retrive the Execution Output for ControlSetupDetailedInformation
	 * 
	 * @return List ControlInformationView
	 * @param taskId Integer
	 * @throws MicosException
	 *             in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	public Executiontask getControlSetupDetailedInformation(Integer taskId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),
					"getControlSetupDetailedInformation");
		Session hibernateSession = getSession();
		Executiontask aExecutiontask = null;
		Executionschedule aExecutionschedule = null;
		Step aStep = null;
		Setup aSetup = null;
		Control aControl = null;
		Aorowner aAorowner = null;
		Employee aEmployee = null;
		Set<Controlrfc> setControlrfc = null;
		Controlrfc aControlrfc = null;
		Rfc aRfc = null;
		Status aStatus = null;
		Set<Controlreference> setControlreference = null;
		Controlreference aControlreference = null;
		Controltype aControltype = null;
		
		try {
			aExecutiontask = (Executiontask) hibernateSession.get(
					Executiontask.class, taskId);
			if (aExecutiontask != null) {

				aExecutionschedule = aExecutiontask.getExecutionschedule();
				if (aExecutionschedule != null) {
					aStep = aExecutionschedule.getStep();
					if (aStep != null) {
						aSetup = aStep.getSetup();
						if (aSetup != null) {
							aControl = aSetup.getControl();
							if (aControl != null) {
								aControltype = aControl.getControltype();
								if (aControltype != null) {
									aControltype.getName();
								}
								aControl.getEnddate();

								aAorowner = aControl.getAorowner();
								setControlrfc = aControl.getControlrfcs();
								if (aAorowner != null) {
									aEmployee = aAorowner
											.getEmployeeByAorowner();
									if (aEmployee != null) {
										aEmployee.getEmployeeName();
									}
								}
								lazyLoadMultilanguagetranslation(aControl.getMultilanguagetranslationByTitle());
								lazyLoadMultilanguagetranslation(aControl.getMultilanguagetranslationByDescription());
								lazyLoadMultilanguagetranslation(aControl.getMultilanguagetranslationByDetaildescription());
								
								setControlreference = aControl
										.getControlreferencesByTocontrol();
								if (!SystemUtil.isNull(setControlreference)) {

									Iterator iterator = setControlreference
											.iterator();
									while (iterator.hasNext()) {
										aControlreference = (Controlreference) iterator
												.next();
										if (aControlreference != null) {
											Control control = aControlreference
													.getControlByFromcontrol();
											if (control != null) {
												control.getControlid();
                        control.getMastercontrolid();
												lazyLoadMultilanguagetranslation(control.getMultilanguagetranslationByTitle());
												aControltype = control .getControltype();
												if (aControltype != null) {
													aControltype.getName();
												}

												aStatus = aControl.getStatus();
												if (aStatus != null) {
													aStatus.getStatusdescription();
												}

											}

										}

									}

								}

							}
						}
					}
				}
			}

			if (!SystemUtil.isNull(setControlrfc)) {
				Iterator itControlrfc = setControlrfc.iterator();
				while (itControlrfc.hasNext()) {
					aControlrfc = (Controlrfc) itControlrfc.next();
				}
			}

			if (aControlrfc != null) {
				aRfc = aControlrfc.getRfc();
				if (aRfc != null) {
					aRfc.getRfccomment();

				}
			}
		} catch (Exception exception) {
      LOGGER.error("getControlSetupDetailedInformation", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(hibernateSession);
      LOGGER
          .exiting(getClass().getName(), "getControlSetupDetailedInformation");
		}
    return aExecutiontask;
	}

	/**
	 * Provides list of ratings.
	 * @return List Rating List
   * @param taskId Execution task id
	 * @exception MicosException Micos Exception
	 */
	@SuppressWarnings("unchecked")
  public List<Rating> getPerformanceRatings(Integer taskId) throws MicosException {
		LOGGER.entering(getClass().getName(),"getPerformanceRatings");
		List<Rating> ratingList = null;
		Session session = null;
		try {
			session = getSession();
			Query qry = session.createSQLQuery("SELECT * FROM RATING WHERE RATINGID IN ("
          + "SELECT RGR.RATINGID FROM RATINGGROUPRATING RGR,EXECUTIONTASK ET, " 
          + "EXECUTIONSCHEDULE ES, STEP S WHERE "
          + "RGR.RATINGGROUPID = S.EXECUTIONRATINGGROUP "
          + "AND S.STEPID = ES.STEPID "
          + "AND ES.EXECUTIONSCHEDULEID = ET.EXECUTIONSCHEDULEID "
          + "AND ET.EXECUTIONTASKID =" + taskId +")" 
          + " ORDER BY RATINGCODE").addEntity(Rating.class);
			if (qry != null) {
				ratingList = qry.list();
				if (!SystemUtil.isNull(ratingList)) {
					Iterator itRating = ratingList.iterator();
					while (itRating.hasNext()) {
						Rating rating = (Rating) itRating.next();
						if (rating != null) {
							rating.getRatingname();
						}
					}
				}
			}
		} catch (Exception exception) {
      LOGGER.error("getPerformanceRatings", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "getPerformanceRatings");
		}
    return ratingList;
	}

	/**
	 * Retrive the Execution Output for ControlSepOverview
	 * @return List ControlInformationView
	 * @throws MicosException MicosException
	 * @param taskId Integer
	 *             in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	public Executiontask getControlSepOverview(Integer taskId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),
					"getControlSepOverview");
		Session hibernateSession = getSession();
		Executiontask aExecutiontask = null;
		Executionschedule aExecutionschedule = null;
		Step aStep = null;
		Setup aSetup = null;
		Control aControl = null;
		Set<Setup> setSetup = null;
		Set<Step> setStep = null;
		Supportschema aSupportschema = null;
		Signoffschema aSignoffschema = null;
		Qaschema aQaschema = null;
		Executionschema aExecutionschema = null;
		Set<Executor> setExecutorsforSupportschema = null;
		Set<Executor> setExecutorsforSignoffschema = null;
		Set<Executor> setExecutorsforExecutionschema = null;

		
		try {
			aExecutiontask = (Executiontask) hibernateSession.get(
					Executiontask.class, taskId);
			if (aExecutiontask != null) {
				aExecutionschedule = aExecutiontask.getExecutionschedule();
				if (aExecutionschedule != null) {
					aStep = aExecutionschedule.getStep();
					if (aStep != null) {
						aSetup = aStep.getSetup();
						if (aSetup != null) {
							aControl = aSetup.getControl();
							if (aControl != null) {
								setSetup = aControl.getSetups();
							}
						}
					}
				}
			}

			Iterator iterator = setSetup.iterator();
			while (iterator.hasNext()) {
				aSetup = (Setup) iterator.next();
				if (aSetup != null) {
					setStep = aSetup.getSteps();
					if (!SystemUtil.isNull(setStep)) {
						Iterator itstep = setStep.iterator();
						while (itstep.hasNext()) {
							aStep = (Step) itstep.next();
							if (aStep != null) {
								if (aStep != null) {
									//getEsclationProperties(aStep);
									getStepProperties(aStep);
									aSupportschema = aStep.getSupportschema();
									aSignoffschema = aStep.getSignoffschema();
									aQaschema = aStep.getQaschema();
									aExecutionschema = aStep
											.getExecutionschema();
									if (aSupportschema != null) {
										setExecutorsforSupportschema = aSupportschema
												.getExecutors();
										if (setExecutorsforSupportschema != null) {
											setExecutorsforSupportschema = setHierarchyDetails(setExecutorsforSupportschema);
										}
									}
									if (aSignoffschema != null) {
										setExecutorsforSignoffschema = aSignoffschema
												.getExecutors();
										if (setExecutorsforSignoffschema != null) {
											setExecutorsforSignoffschema = setHierarchyDetails(setExecutorsforSignoffschema);
										}
									}
									if (aQaschema != null) {
										setExecutorsforSupportschema = aQaschema
												.getExecutors();
										if (setExecutorsforSupportschema != null) {
											setExecutorsforSupportschema = setHierarchyDetails(setExecutorsforSupportschema);
										}
									}
									if (aExecutionschema != null) {
										setExecutorsforExecutionschema = aExecutionschema
												.getExecutors();
										if (setExecutorsforExecutionschema != null) {
											setExecutorsforExecutionschema = setHierarchyDetails(setExecutorsforExecutionschema);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception exception) {
      LOGGER.error("getControlSepOverview", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getControlSepOverview");
		}
    return aExecutiontask;
	}

	/**
	 * sets HierarchyDetails
	 * @param setExecutor set
	 * @return finalExecutor
	 * @throws MicosException MicosException
	 */
	private Set<Executor> setHierarchyDetails(Set<Executor> setExecutor)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"setHierarchyDetails");
		Executor aExecutor = null;
		Set<Executor> finalExecutor = null;
		Session hibernateSession = getSession();

		try {
			Iterator iterator = setExecutor.iterator();
			while (iterator.hasNext()) {
				aExecutor = (Executor) iterator.next();
				if (aExecutor != null) {
					Set<Executoraor> setExecutoraors = aExecutor.getExecutoraors();
					if (!SystemUtil.isNull(setExecutoraors)) {
						Iterator iteratorExecutaor = setExecutoraors.iterator();
						while (iteratorExecutaor.hasNext()) {
							Executoraor aExecutoraor = (Executoraor) iteratorExecutaor.next();
							if (aExecutoraor != null) {
								aExecutoraor.getLevelsdown();
								aExecutoraor.getHierarchyelement().getName();
								aExecutoraor.getHierarchyelement().getHierarchy().getName();
								if(aExecutoraor.getHierarchyrole() != null) {
									aExecutoraor.getHierarchyrole().getHierarchyroleid();
									aExecutoraor.getHierarchyrole().getName();
								}
							}
						}
					}

					Set<Executorpid> setExecutorpid = aExecutor.getExecutorpids();
					Iterator itExecutorpid = setExecutorpid.iterator();
					while (itExecutorpid.hasNext()) {
						Executorpid aExecutorpid = (Executorpid) itExecutorpid.next();
						if (aExecutorpid != null) {
							aExecutorpid.getExecutorpidid();
							aExecutorpid.getInclude();
							Employee aEmployee = aExecutorpid.getEmployeeByEmployeeid();
							if (aEmployee != null) {
								aEmployee.getEmployeeName();
							}
						}
					}

				}
				finalExecutor = setExecutor;
			}
		} catch (Exception exception) {
      LOGGER.error("setHierarchyDetails", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(hibernateSession);
		}
    return finalExecutor;
	}

	/**
	 * Retrive the Step Properties
	 * @return List ControlInformationView
	 * @param aStep
	 *    the Step
	 * @throws MicosException
	 *             in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	private Step getStepProperties(Step aStep)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"getStepProperties");
		Session hibernateSession = getSession();
		Set<Schedule> setSchedule = null;
		Schedule aSchedule = null;
		Periodtype aPeriodtype = null;
		Periodtype supportinitiateperiodtype = null;
		Schedulewhen supportschedulewhen = null;
		Periodtype supportdurationperiodtype = null;
		Supportschema aSupportschema = null;
		Executionschema aExecutionschema = null;
		Signoffschema aSignoffschema = null;
		Qaschema aQaschema = null;
		Set<Executiontask> setExecutiontasks = null;
		Executiontask aExecutiontasks = null;
		Iterator itExecutiontasks = null;

		try {
			if (aStep != null) {
				setSchedule = aStep.getSchedules();
				Set<ControlRemWarEsc> setOfControlRemWarEsc = aStep.getControlRemWarEscStep();
				if(SystemUtil.isNotNull(setOfControlRemWarEsc)){
					for(ControlRemWarEsc remWarEsc:setOfControlRemWarEsc){
						Set<RemWarEscOtherRecipient> setRemWarEscOtherRecipient = remWarEsc.getOtherRecipients();
						if(SystemUtil.isNotNull(setRemWarEscOtherRecipient)){
							for(RemWarEscOtherRecipient otherRecipient:setRemWarEscOtherRecipient){
								otherRecipient.getEmployee().getFirstname();
							}
						}
						remWarEsc.getPeriodType().getName();
						if(remWarEsc.getRecipient() != null) {
							remWarEsc.getRecipient().getCode();
						}
						remWarEsc.getNotType().getCode();
						remWarEsc.getFrequency().getCode();
					}
				}
				if (!SystemUtil.isNull(setSchedule)) {
					Iterator itSchedule = setSchedule.iterator();
					while (itSchedule.hasNext()) {
						aSchedule = (Schedule) itSchedule.next();
						if (aSchedule != null) {
							aSchedule.getStartdate();//Start Date
							aSchedule.getEnddate();//End date
							aSchedule.getPatternqty();//pattern quantity
							aPeriodtype = aSchedule
									.getPeriodtypeByPatternfrequency();
							if (aPeriodtype != null) {
								aPeriodtype.getName(); //Month
							}
		                  // Adhoc date list
		                  Set<Scheduleadhocdate> adhocDateSet = aSchedule
		                  .getScheduleadhocdates();
		                  
		                  if (adhocDateSet != null) {
		                    for (Scheduleadhocdate date : adhocDateSet) {
		                      date.getAdhocdate();
		                    }
		                  }                  
									aSupportschema = aStep.getSupportschema();
									aExecutionschema = aStep
											.getExecutionschema();
									aSignoffschema = aStep.getSignoffschema();
									aQaschema = aStep.getQaschema();

									//For Start Support  
									aSchedule.getSupportinitiateqty(); //First column
									supportinitiateperiodtype = aSchedule
											.getPeriodtypeBySupportinitiateperiodtype();
									if (supportinitiateperiodtype != null) {
										supportinitiateperiodtype.getName();//Second column
									}
									supportschedulewhen = aSchedule
											.getSchedulewhenBySupportschedulewhen();
									if (supportschedulewhen != null) {
										supportschedulewhen.getName();//Third column
									}
									aSchedule.getSupportdurationqty();//4th column
									supportdurationperiodtype = aSchedule
											.getPeriodtypeBySupportdurationperiodtype();
									if (supportdurationperiodtype != null) {
										supportdurationperiodtype.getName();//5th column
									}

									if(aSupportschema!=null){
									setExecutiontasks = aSupportschema
											.getExecutiontasks();
									if (!SystemUtil.isNull(setExecutiontasks)) {
										itExecutiontasks = setExecutiontasks
												.iterator();
										while (itExecutiontasks.hasNext()) {
											aExecutiontasks = (Executiontask) itExecutiontasks
													.next();

											if (aExecutiontasks != null) {
												aExecutiontasks
														.getExecutionenddate();//for end date
											}
										}

									}
									}
									//End of Start Support  

									//For Start Performance  
									aSchedule.getExecutioninitiateqty(); //First column
									supportinitiateperiodtype = aSchedule
											.getPeriodtypeByExecutorinitiateperiodtype();
									if (supportinitiateperiodtype != null) {
										supportinitiateperiodtype.getName();//Second column
									}
									supportschedulewhen = aSchedule
											.getSchedulewhenByExecutionschedulewhen();
									if (supportschedulewhen != null) {
										supportschedulewhen.getName();//Third column
									}
									aSchedule.getExecutiondurationqty();//4th column
									supportdurationperiodtype = aSchedule
											.getPeriodtypeByExecutiondurationperiodtype();
									if (supportdurationperiodtype != null) {
										supportdurationperiodtype.getName();//5th column
									}

									if(aExecutionschema!=null){
									setExecutiontasks = aExecutionschema
											.getExecutiontasks();
									if (!SystemUtil.isNull(setExecutiontasks)) {
										itExecutiontasks = setExecutiontasks
												.iterator();
										while (itExecutiontasks.hasNext()) {
											aExecutiontasks = (Executiontask) itExecutiontasks
													.next();

											if (aExecutiontasks != null) {
												aExecutiontasks
														.getExecutionenddate();//for end date
											}
										}
									}
									}
									//End of Start Performance 

										//For Start QA  
										aSchedule.getQainitiateqty(); //First column
										supportinitiateperiodtype = aSchedule
												.getPeriodtypeByQainitiateperiodtype();
										if (supportinitiateperiodtype != null) {
											supportinitiateperiodtype.getName();//Second column
										}
										supportschedulewhen = aSchedule
												.getSchedulewhenByQaschedulewhen();
										if (supportschedulewhen != null) {
											supportschedulewhen.getName();//Third column
										}
										aSchedule.getQadurationqty();//4th column
										supportdurationperiodtype = aSchedule
												.getPeriodtypeByQadurationperiodtype();
										if (supportdurationperiodtype != null) {
											supportdurationperiodtype.getName();//5th column
										}

										if(aQaschema!=null){
										setExecutiontasks = aQaschema
												.getExecutiontasks();
										if (!SystemUtil
												.isNull(setExecutiontasks)) {
											itExecutiontasks = setExecutiontasks
													.iterator();
											while (itExecutiontasks.hasNext()) {
												aExecutiontasks = (Executiontask) itExecutiontasks
														.next();

												if (aExecutiontasks != null) {
													aExecutiontasks
															.getExecutionenddate();//for end date
												}
											}
										}
										}
											//End of Start QA

											//	For Start Sign-off   
											aSchedule.getSignoffinitiateqty(); //First column
											supportinitiateperiodtype = aSchedule
													.getPeriodtypeBySignoffinitiateperiodtype();
											if (supportinitiateperiodtype != null) {
												supportinitiateperiodtype
														.getName();//Second column
											}
											supportschedulewhen = aSchedule
													.getSchedulewhenBySignoffschedulewhen();
											if (supportschedulewhen != null) {
												supportschedulewhen.getName();//Third column
											}
											aSchedule.getSignoffdurationqty();//4th column
											supportdurationperiodtype = aSchedule
													.getPeriodtypeBySignoffdurationperiodtype();
											if (supportdurationperiodtype != null) {
												supportdurationperiodtype
														.getName();//5th column
											}

											if(aSignoffschema!=null){
											setExecutiontasks = aSignoffschema
													.getExecutiontasks();
											if (!SystemUtil
													.isNull(setExecutiontasks)) {
												itExecutiontasks = setExecutiontasks
														.iterator();
												while (itExecutiontasks
														.hasNext()) {
													aExecutiontasks = (Executiontask) itExecutiontasks
															.next();

													if (aExecutiontasks != null) {
														aExecutiontasks
																.getExecutionenddate();//for end date
													}
												}
												 
											}
										}
//											End of Start Sign-off 
										}
									}
								
						}
					}
						
					
						/*}
			}*/
		} catch (Exception exception) {
      LOGGER.error("getStepProperties", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getStepProperties");
		}
    return aStep;
	}
	

	/**
	 * Provides the status for status code.
	 * @param statusCode Status Code
	 * @return Status
	 * @exception MicosException Micos Exception
	 */
	public Status getStatusByStatusCode(String statusCode)throws MicosException{
		LOGGER.entering(getClass().getName(),"getStatusByStatusCode");
		Status status=null;
		Session session=null;
		try{
			session=getSession();
			Query qry=session.createQuery(QRY_STATUS);
			qry.setString("statuscode", statusCode);
			status=(Status)qry.uniqueResult();
		} catch (Exception exception) {
      LOGGER.error("getStatusByStatusCode", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "getStatusByStatusCode");
		}
    return status;
	}

	
	/**
	 * get the Rolling Signoff
	 * 
	 * @return List <Executiontask>
	 * @throws MicosException MicosException
	 * @param hibernateSession Session
	 * @param signoffschema Signoffschema
	 *             in the following cases:
	 *             <ul>
	 *             <li>if unable to retrieve data e.g. when looking up specific
	 *             data via a known identifier</li>
	 *             <li>if unable to access data for varying reasons like
	 *             permission denied, data source lookup failure, data integrity
	 *             violations etc </li>
	 *             </ul>
	 */
	private List<Executiontask> getRollingSignoff(Signoffschema signoffschema,
			Session hibernateSession) throws MicosException {
		LOGGER.entering(getClass().getName(), "getRollingSignoff");
		Executiontask aExecutiontask = null;
		List<Executiontask> listExecutiontasks = new ArrayList<Executiontask>();
		Set<Executiontask> allExecutiontasks = null;

		try {
			if (signoffschema != null) {
				allExecutiontasks = signoffschema.getExecutiontasks();
				Iterator it = allExecutiontasks.iterator();
				while (it.hasNext()) {
					aExecutiontask = (Executiontask) it.next();
					if (aExecutiontask != null
							&& aExecutiontask.getRollingsignoff() != null
							&& aExecutiontask.getSignedoff() != null) {
						if (aExecutiontask.getRollingsignoff()
								.equalsIgnoreCase("N")
								&& aExecutiontask.getSignedoff()
										.equalsIgnoreCase("N")) {
							listExecutiontasks.add(aExecutiontask);
						}
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("getRollingSignoff", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		}
		return listExecutiontasks;
	}
	
	/** 
	 * Execution Task For Signoff
	 * @return objExecutiontask
	 * @param taskId Integer
	 * @throws MicosException
	 *    the MicosException
	 */
	public List executionTaskForSignoff(Integer taskId) throws MicosException{
		LOGGER.entering(getClass().getName(),"executionTaskForSignoff");
		Session hibernateSession = null;
    List executiontaskLst = null;
    Executiontask exeTask = null;
    try {
      hibernateSession = getSession();
      Query query = hibernateSession
          .createQuery("from com.csg.cs.micos.bean.Executiontask "
              + " as executiontask where executiontask.executionschema is not null "
              + " and executiontask.executionschedule in ( select "
              + " task.executionschedule from com.csg.cs.micos.bean.Executiontask "
              + " as task where task.executiontaskid=" + taskId + ") " 
              + " order by executiontask.executiontaskid desc");
      if (query != null) {
        executiontaskLst = query.list();
        if (SystemUtil.isNotNull(executiontaskLst)) {
          Iterator itrExecutionTask = executiontaskLst.iterator();
          while (itrExecutionTask.hasNext()) {
            exeTask = (Executiontask)itrExecutionTask.next();
            if (exeTask != null) {
              Rating rating = exeTask.getRating();
              if (rating != null) {
                rating.getRatingname();
              }
              Rating singoffRating = exeTask.getSignoffrating();
              if (singoffRating != null) {
                singoffRating.getRatingname();
              }
              Sequenceorder seqOrder = exeTask.getSequenceorder();
              if(seqOrder != null){
                seqOrder.getOrdernumber();
              }
              if(exeTask.getReperformanceoftask() != null){
                exeTask.getReperformanceoftask();
              }
              Employee emplyee = exeTask.getExecutor();
              if (emplyee != null) {
                emplyee.getPid();
                emplyee.getFirstname();
                emplyee.getLastname();
                emplyee.getInstradierung();
              }
              Status status = exeTask.getStatus();
              if (status != null) {
                status.getStatuscode();
              }
              lazyLoadAttachemnts(exeTask.getMultiattachments());
              lazyLoadLinks(exeTask.getMultilinks());
            }
          }
        }
      }

    } catch (Exception exception) {
      LOGGER.error("executionTaskForSignoff", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "executionTaskForSignoff");
    }
    return executiontaskLst;
	}

	
	/**
	 * gets AttachmentTypeforAttachment
	 * @return attachType
	 * @param extension String
	 * @throws MicosException Micos Exception
	 */
	public Attachmenttype getAttachmentTypeforAttachment(String extension) throws MicosException{
		 LOGGER.entering(getClass().getName(),"getAttachmentTypeforAttachment");
		 Attachmenttype attachType = new Attachmenttype();
		 Session hibernateSession = null;
		 try {
			 hibernateSession = getSession();
			 Query query = hibernateSession
	          .createQuery("from com.csg.cs.micos.bean.Attachmenttype as attachType"
	              + " where attachType.extension=:selectedExtension");
	      query.setString("selectedExtension", extension);
		      if (null != query) {
		        attachType = (Attachmenttype)query.uniqueResult();
		      }
		 } catch (Exception exception) {
      LOGGER.error("getAttachmentTypeforAttachment", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		 } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getAttachmentTypeforAttachment");
		 }
    return attachType;
	}

	/** 
	 * getTaskIdForSignoff
	 * @return taskIdForSignoff
	 * @param taskId 
	 *    the Integer
	 * @throws MicosException
	 *    the MicosException  
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getTaskIdForSignoff(Integer taskId) throws MicosException{
		LOGGER.entering(getClass().getName(),"getTaskIdForSignoff");
		Session hibernateSession = null;
		List<Object> taskIdForSignoff = null;
		
		try {
			hibernateSession = getSession();
			Query query = hibernateSession.createSQLQuery("Select EXECUTIONTASK.EXECUTIONTASKID as TaskID"
					+ " from EXECUTIONTASK,EXECUTIONSCHEDULE,STEP,EMPLOYEE,STATUS"
					+ " where EXECUTIONTASK.EXECUTIONSCHEDULEID = EXECUTIONSCHEDULE.EXECUTIONSCHEDULEID"
					+ " and EXECUTIONSCHEDULE.STEPID = STEP.STEPID"
					+ " and EXECUTIONTASK.STATUSID = STATUS.STATUSID"
					+ " and EXECUTIONTASK.EXECUTOR = EMPLOYEE.EMPLOYEEID"
					+ " and EXECUTIONTASK.SIGNOFFSCHEMAID is not null"
					+ " and STEP.STEPID ="
					+ " (Select STEP.STEPID from EXECUTIONTASK,EXECUTIONSCHEDULE,STEP"
					+ " where EXECUTIONTASK.EXECUTIONSCHEDULEID = EXECUTIONSCHEDULE.EXECUTIONSCHEDULEID"
					+ " and EXECUTIONSCHEDULE.STEPID = STEP.STEPID"
					+ " and EXECUTIONTASK.EXECUTIONTASKID = "
					+ taskId + ")"
					+ " and EXECUTIONTASK.EXECUTIONTASKID <>  "
					+ taskId );
			
			if (query != null){
				taskIdForSignoff = query.list();
			}
		} catch (Exception exception) {
      LOGGER.error("getTaskIdForSignoff", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getTaskIdForSignoff");
		}
    return taskIdForSignoff;
	}

	/**
	   * Called from Controller to get attachment content
	   * @param str string
	   * @return AttachmentView
	   * @see com.csg.cs.micos.comp.GoalManager#getSelectedAttachment(java.lang.String)
	   * @throws MicosException Micos Exception
	   */
	  public Attachment getSelectedAttachment(String str) throws MicosException {
    LOGGER.entering(getClass().getName(), "getSelectedAttachment");
    Session hibernateSession = null;
    Attachment attachment = new Attachment();
    try {
      hibernateSession = getSession();
      Query query = hibernateSession
          .createQuery("from com.csg.cs.micos.bean.Attachment as attachment"
              + " where attachment.attachmentid=:selectedAttachmentId");
      query.setString("selectedAttachmentId", str);
      if (null != query) {
        attachment = (Attachment)query.uniqueResult();
        Attachmenttype aAttachmenttype = attachment.getAttachmenttype();
        if (aAttachmenttype != null) {
          aAttachmenttype.getMimetype();
        }
      }

    } catch (Exception exception) {
      LOGGER.error("getSelectedAttachment", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getSelectedAttachment");
    }
    return attachment;
	  }
	  
	/**
   * gets Status code By Task Id
   * 
   * @return statusCode
   * @param taskId
   *          the Integer
   * @throws MicosException
   *           the MicosException
   */
	public String getStatuscodeByTaskId(Integer taskId) throws MicosException {
    LOGGER.entering(getClass().getName(), "getStatuscodeByTaskId");
    Session hibernateSession = null;
    String statusCode = "";
    try {
      hibernateSession = getSession();
      Query query = hibernateSession
          .createSQLQuery("Select STATUSCODE from STATUS s,EXECUTIONTASK e "
              + " where s.STATUSID = e.STATUSID and e.EXECUTIONTASKID= "
              + taskId);

      if (query != null) {
        statusCode = query.uniqueResult().toString();
      }
    } catch (Exception exception) {
      LOGGER.error("getStatuscodeByTaskId", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getStatuscodeByTaskId");
    }
    return statusCode;
	}
	  
/*	*//**
	 * saves Rolling Signoff
	 * @param rollingSignoff 
	 *    the Rollingsignoff
	 * @throws MicosException
	 *     the MicosException 
	 * 
	 *//*
	public void saveRollingSignoff(Rollingsignoff rollingSignoff) throws MicosException{
			LOGGER.entering(getClass().getName(),"saveRollingSignoff");
			Session hibernateSession = null;
			try {
				hibernateSession = getSession();
				hibernateSession.saveOrUpdate(rollingSignoff);
				hibernateSession.flush();
			} catch (HibernateException hexp) {
				LOGGER.error("saveRollingSignoff", hexp);
				throw new MicosException(ExceptionContext.ERROR, hexp,
						"MICOS.Hibernateexception");
			} finally {
				releaseSession(hibernateSession);
				LOGGER.exiting(getClass().getName(), "saveRollingSignoff");
				
			}

	}*/
	
	/**
	   * Method deletes attachements
	   * @param selectedAttachmentId attachment id 
	   * @throws MicosException MicosException
	   */
	public void deleteAttachments(Integer selectedAttachmentId)
									throws MicosException {
		  LOGGER.entering(getClass().getName(),"deleteAttachments");
		  Session hibernateSession = null;
		  Attachment attachment = null;
		  try {
			  	hibernateSession = getSession();
				attachment = (Attachment) hibernateSession.get(Attachment.class,
						selectedAttachmentId);
				if (null != attachment) {
					hibernateSession.delete(attachment);
					hibernateSession.flush();
				}
		  } catch (Exception exception) {
      LOGGER.error("deleteAttachments", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "deleteAttachments");
    }
	}
	
	
	/**
	 * 
	 * delete A Link
	 * 
	 * @param linkId
	 *            Integer
	 * @exception MicosException
	 *                Micos Exception
	 */
	public void deleteALink(Integer linkId) throws MicosException {
		LOGGER.entering(getClass().getName(),"deleteALink");
		Session hibernateSession = null;
		Link link = null;
		try {
			hibernateSession = getSession();
			link = (Link) hibernateSession.get(Link.class, linkId);
			if (null != link) {
				hibernateSession.delete(link);
				hibernateSession.flush();
			}
		} catch (Exception exception) {
      LOGGER.error("deleteALink", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
		} finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "deleteALink");
		}
	}

  /**
   * Lazy loading of multilanguage translation
   * @param multilanguagetranslation Multilanguage
   */
	private void lazyLoadMultilanguagetranslation(Multilanguagetranslation multilanguagetranslation) {
		LOGGER.entering(getClass().getName(), "lazyLoadMultilanguagetranslation");
		if (multilanguagetranslation != null) {
			Set<Translation> translations = multilanguagetranslation.getTranslations();
			for (Translation translation : translations) {
				Supportedlanguage suptdLang = translation.getSupportedlanguage();
				if (suptdLang != null) {
					suptdLang.getLanguagecode();
					translation.getTranslatedtext();
				}
			}
		}
	}

  /**
   * Change status
   * @param taskId Integer
   * @exception MicosException
   *              Micos Exception
   */
  public void changeStatus(Integer taskId) throws MicosException {
    LOGGER.entering(getClass().getName(), "changeStatus");
    Session hibernateSession = null;
    Executiontask task = null;
    Status status = null;
    Query query = null;
    try {
      hibernateSession = getSession();
      task = (Executiontask)hibernateSession.get(Executiontask.class, taskId);
      query = hibernateSession
          .createQuery("from com.csg.cs.micos.bean.Status where statuscode = 'AS'");
      if (query != null) {
        status = (Status)query.uniqueResult();
      }
      if (task != null) {
        task.setStatus(status);
        hibernateSession.saveOrUpdate(task);
        hibernateSession.flush();
      }
    } catch (Exception exception) {
      LOGGER.error("changeStatus", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "changeStatus");
    }
  }

  /**
   * Fetches the archived tasks based on the parameters passed.
   * 
   * @param hibernateSession
   *          Session
   * @param selectedArchivePeriod
   *          String
   * @param checkSupportTasks
   *          boolean
   * @param checkPerformanceTasks
   *          boolean
   * @param checkQATasks
   *          boolean
   * @param checkSignoffTasks
   *          boolean
   * @return List
   * @throws MicosException
   *           Exception
   */
  @SuppressWarnings("unchecked")
  public List<Executiontask> getArchivedTasks(Session hibernateSession,
      String selectedArchivePeriod, boolean checkSupportTasks,
      boolean checkPerformanceTasks, boolean checkQATasks,
      boolean checkSignoffTasks) throws MicosException {
    LOGGER.entering(getClass().getName(), "getArchivedTasks");
    List<Executiontask> task = null;
    Query query = null;
    GregorianCalendar dateTo = new GregorianCalendar();
    GregorianCalendar dateFrom = new GregorianCalendar();
    try {
      MicosUserBean user = (MicosUserBean)UserBean.getUserBean();
      String userId = user.getWorkingAsUser().getEmployeeid().toString();      
      String schemaQuery = generateSchemaQuery(selectedArchivePeriod, checkSupportTasks,checkPerformanceTasks,checkQATasks,checkSignoffTasks);

      StringBuilder queryBuf = new StringBuilder();
      
      if(!selectedArchivePeriod.equalsIgnoreCase("LE")){
        //calculateFromDate(selectedArchivePeriod, dateFrom);
        SystemUtil.calculateFromDate(selectedArchivePeriod, dateFrom);
        Timestamp timeFrom = new java.sql.Timestamp(dateFrom.getTime().getTime());
        Timestamp timeTo = new java.sql.Timestamp(dateTo.getTime().getTime());
        
        queryBuf.append("select * from executiontask a "
                       + "where a.EXECUTOR = " + userId + " and a.STATUSID = 3 ");
        queryBuf.append("and ("+ schemaQuery +") ");
        queryBuf.append("and a.LASTUPDATE >= :betweenDateFrom and a.LASTUPDATE <= :betweenDateTo ");
        queryBuf.append("order by a.executiontaskid");
        query = hibernateSession.createSQLQuery(queryBuf.toString()).addEntity(Executiontask.class);
        query.setTimestamp("betweenDateFrom", timeFrom);
        query.setTimestamp("betweenDateTo", timeTo);
      } else {
       boolean firstcheck = true;
       queryBuf.append("select * from executiontask where executiontaskid in (");
       if(checkSupportTasks) {
        queryBuf.append("     SELECT   ");
        queryBuf.append("    EXECUTIONTASKID");
        queryBuf.append("          from");
        queryBuf.append("(");
        queryBuf.append("select a.EXECUTIONTASKID, c.MASTERCONTROLID,  c.CONTROLID, s.TITLE,");
        queryBuf.append("    last_value(c.controlid) over (");
        queryBuf.append("   partition by c.mastercontrolid order by mastercontrolid");
        queryBuf.append("    ) as rn");
        queryBuf.append("        FROM executiontask a, executionschedule es, step s, setup st, control c ");
        queryBuf.append(" WHERE a.executiontaskid IN (SELECT   MAX (x.executiontaskid) FROM executiontask x");
        queryBuf.append(" WHERE x.executor = " + userId + " ");
        queryBuf.append(" AND x.statusid = 3 ");
        queryBuf.append("and x.supportschemaid is not null ");
        queryBuf.append(" GROUP BY x.supportschemaid)");
        queryBuf.append(" and a.EXECUTIONSCHEDULEID = es.EXECUTIONSCHEDULEID");
        queryBuf.append("  and es.STEPID = s.STEPID");
        queryBuf.append("  and s.SETUPID = st.SETUPID");
        queryBuf.append("  and st.controlid = c.controlid    ");
        queryBuf.append("   ) where rn = controlid");
        firstcheck= false;
       }
       if(checkPerformanceTasks) {
         if(!firstcheck) {
           queryBuf.append("    union all "); 
         } else {
           firstcheck= false;
         }
         queryBuf.append("     SELECT   ");
         queryBuf.append("    EXECUTIONTASKID");
         queryBuf.append("          from");
         queryBuf.append("(");
         queryBuf.append("select a.EXECUTIONTASKID, c.MASTERCONTROLID,  c.CONTROLID, s.TITLE,");
         queryBuf.append("    last_value(c.controlid) over (");
         queryBuf.append("   partition by c.mastercontrolid order by mastercontrolid");
         queryBuf.append("    ) as rn");
         queryBuf.append("        FROM executiontask a, executionschedule es, step s, setup st, control c ");
         queryBuf.append(" WHERE a.executiontaskid IN (SELECT   MAX (x.executiontaskid) FROM executiontask x");
         queryBuf.append(" WHERE x.executor = " + userId + " ");
         queryBuf.append(" AND x.statusid = 3 ");
         queryBuf.append("and x.executionschemaid is not null ");
         queryBuf.append(" GROUP BY x.executionschemaid )");
         queryBuf.append(" and a.EXECUTIONSCHEDULEID = es.EXECUTIONSCHEDULEID");
         queryBuf.append("  and es.STEPID = s.STEPID");
         queryBuf.append("  and s.SETUPID = st.SETUPID");
         queryBuf.append("  and st.controlid = c.controlid    ");
         queryBuf.append("   ) where rn = controlid");
       }  
       if(checkQATasks) {
          if(!firstcheck) {
            queryBuf.append("    union all "); 
          } else {
            firstcheck= false;
          }
         queryBuf.append("     SELECT   ");
         queryBuf.append("    EXECUTIONTASKID");
         queryBuf.append("          from");
         queryBuf.append("(");
         queryBuf.append("select a.EXECUTIONTASKID, c.MASTERCONTROLID,  c.CONTROLID, s.TITLE,");
         queryBuf.append("    last_value(c.controlid) over (");
         queryBuf.append("   partition by c.mastercontrolid order by mastercontrolid");
         queryBuf.append("    ) as rn");
         queryBuf.append("        FROM executiontask a, executionschedule es, step s, setup st, control c ");
         queryBuf.append(" WHERE a.executiontaskid IN (SELECT   MAX (x.executiontaskid) FROM executiontask x");
         queryBuf.append(" WHERE x.executor = " + userId + " ");
         queryBuf.append(" AND x.statusid = 3 ");
         queryBuf.append("and  x.qaschema is not null ");
         queryBuf.append("GROUP BY  x.qaschema)");
         queryBuf.append(" and a.EXECUTIONSCHEDULEID = es.EXECUTIONSCHEDULEID");
         queryBuf.append("  and es.STEPID = s.STEPID");
         queryBuf.append("  and s.SETUPID = st.SETUPID");
         queryBuf.append("  and st.controlid = c.controlid    ");
         queryBuf.append("   ) where rn = controlid");
       }
       
       if(checkSignoffTasks) {
         if(!firstcheck) {
           queryBuf.append("    union all "); 
         }
         queryBuf.append("     SELECT   ");
         queryBuf.append("    EXECUTIONTASKID");
         queryBuf.append("          from");
         queryBuf.append("(");
         queryBuf.append("select a.EXECUTIONTASKID, c.MASTERCONTROLID,  c.CONTROLID, s.TITLE,");
         queryBuf.append("    last_value(c.controlid) over (");
         queryBuf.append("   partition by c.mastercontrolid order by mastercontrolid");
         queryBuf.append("    ) as rn");
         queryBuf.append("        FROM executiontask a, executionschedule es, step s, setup st, control c ");
         queryBuf.append(" WHERE a.executiontaskid IN (SELECT   MAX (x.executiontaskid) FROM executiontask x");
         queryBuf.append(" WHERE x.executor = " + userId + " ");
         queryBuf.append(" AND x.statusid = 3 ");
         queryBuf.append("and x.signoffschemaid is not null ");
         queryBuf.append("GROUP BY x.signoffschemaid )");
         queryBuf.append(" and a.EXECUTIONSCHEDULEID = es.EXECUTIONSCHEDULEID");
         queryBuf.append("  and es.STEPID = s.STEPID");
         queryBuf.append("  and s.SETUPID = st.SETUPID");
         queryBuf.append("  and st.controlid = c.controlid    ");
         queryBuf.append("   ) where rn = controlid");
         
       }
       queryBuf.append(" )  order by executiontask.executiontaskid"); 
        query = hibernateSession.createSQLQuery(queryBuf.toString()).addEntity(Executiontask.class);
      }  
      
      if (query != null) {
        task = query.list();
      }
    } catch (Exception exception) {
      LOGGER.error("getArchivedTasks", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      LOGGER.exiting(getClass().getName(), "getArchivedTasks");
    }
    return task;
  }

  /**
   * Generates a part of query based on schemas selected for task archive selection criteria.
   * @param selectedArchivePeriod String
   * @param checkSupportTasks boolean
   * @param checkPerformanceTasks boolean
   * @param checkQATasks boolean
   * @param checkSignoffTasks boolean
   * @return String
   */
  private String generateSchemaQuery(String selectedArchivePeriod,
      boolean checkSupportTasks, boolean checkPerformanceTasks,
      boolean checkQATasks, boolean checkSignoffTasks) {
    StringBuilder queryBuf = new StringBuilder();
    boolean firstClause = true;
    if(checkSupportTasks){
      if(!selectedArchivePeriod.equalsIgnoreCase("LE")){
        queryBuf.append("a.supportschemaid is not null");
      } else {
        queryBuf.append("x.supportschemaid is not null");
      }      
      firstClause = false;
    }
    if(checkPerformanceTasks){
      if(!selectedArchivePeriod.equalsIgnoreCase("LE")){
        queryBuf.append(firstClause ? "a.executionschemaid is not null" : " or a.executionschemaid is not null");
      } else {
        queryBuf.append(firstClause ? "x.executionschemaid is not null" : " or x.executionschemaid is not null");
      }      
      firstClause = false;
    }
    if(checkQATasks){
      if(!selectedArchivePeriod.equalsIgnoreCase("LE")){
        queryBuf.append(firstClause ? "a.qaschema is not null" : " or a.qaschema is not null");
      } else {
        queryBuf.append(firstClause ? "x.qaschema is not null" : " or x.qaschema is not null");
      }      
      firstClause = false;
    }
    if(checkSignoffTasks){
      if(!selectedArchivePeriod.equalsIgnoreCase("LE")){
        queryBuf.append(firstClause ? "a.signoffschemaid is not null" : " or a.signoffschemaid is not null");
      } else {
        queryBuf.append(firstClause ? "x.signoffschemaid is not null" : " or x.signoffschemaid is not null");
      }
      
    }    
    return queryBuf.toString();
  }

  
  /**
   * Retrieves the detailed description for performance task.
   * 
   * @param taskid
   *          selected task id
   * @param hibernateSession
   *          hibernateSession
   * @return List of tasks
   * @throws MicosException
   *           Exception
   * 
   */
  @SuppressWarnings("unchecked")
  public List<Executiontask> retrieveTaskForDetailedDescription(Integer taskid,
      Session hibernateSession) throws MicosException {
    LOGGER.entering(getClass().getName(), "retrieveTaskForDetailedDescription");
    Query query = null;
    List<Executiontask> tasks = null;
    try {
      String qryStr = "Select * from EXECUTIONTASK "
          + "START WITH EXECUTIONTASKID = " + taskid
          + " CONNECT BY PRIOR REPERFORMANCEOFTASKID = EXECUTIONTASKID"
          + " order by EXECUTIONTASK.LASTUPDATE DESC";
      query = hibernateSession.createSQLQuery(qryStr).addEntity(
          Executiontask.class);
      if (query != null) {
        tasks = query.list();
      }
    } catch (Exception exception) {
      LOGGER.error("retrieveTaskForDetailedDescription", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      LOGGER
          .exiting(getClass().getName(), "retrieveTaskForDetailedDescription");
    }
    return tasks;
  }
  
  /**
   * Retrieve list of Signoff ratings.
   * @param taskId taskId
   * @return List Rating List
   * @exception MicosException
   *              Micos Exception
   */
  @SuppressWarnings("unchecked")
  public List<Rating> getSignoffRatings(Integer taskId) throws MicosException {
    LOGGER.entering(getClass().getName(), "getSignoffRatings");
    List<Rating> ratingList = null;
    Session session = null;
    try {
      session = getSession();
      Query qry = session
          .createSQLQuery(
                    "SELECT * FROM RATING WHERE RATINGID IN ("
                  + "SELECT RGR.RATINGID FROM RATINGGROUPRATING RGR,EXECUTIONTASK ET, "
                  + "EXECUTIONSCHEDULE ES, STEP S WHERE "
                  + "RGR.RATINGGROUPID = S.SIGNOFFRATINGGROUP "
                  + "AND S.STEPID = ES.STEPID "
                  + "AND ES.EXECUTIONSCHEDULEID = ET.EXECUTIONSCHEDULEID "
                  + "AND ET.EXECUTIONTASKID =" + taskId + ")"
                  + " ORDER BY RATINGCODE").addEntity(Rating.class);
      if (qry != null) {
        ratingList = qry.list();
        if (!SystemUtil.isNull(ratingList)) {
          Iterator itRating = ratingList.iterator();
          while (itRating.hasNext()) {
            Rating rating = (Rating)itRating.next();
            if (rating != null) {
              rating.getRatingname();
            }
          }
        }
      }
    } catch (Exception exception) {
      LOGGER.error("getSignoffRatings", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "getSignoffRatings");
    }
    return ratingList;
  }  
  
  /**
   * Retrieve Sign-off rating group rating based on taskId and rating id
   * 
   * @param taskId Execution
   *          task Id
   * @param ratingId Signoff
   *          Rating Id
   * @return RatingGroupRating object
   * @throws MicosException Exception
   */
  public Ratinggrouprating getRatingGroupRating(Integer taskId,
      Integer ratingId) throws MicosException {
    LOGGER.entering(getClass().getName(), "getRatingGroupRating");
    Ratinggrouprating rgRating = null;
    Session session = null;
    try {
      session = getSession();
      Query query = session.createSQLQuery(
          "SELECT * FROM RATINGGROUPRATING WHERE RATINGID = " + ratingId
              + " AND RATINGGROUPID IN (SELECT S.SIGNOFFRATINGGROUP "
              + " FROM EXECUTIONTASK ET, EXECUTIONSCHEDULE ES, STEP S "
              + " WHERE S.STEPID = ES.STEPID "
              + " AND ES.EXECUTIONSCHEDULEID = ET.EXECUTIONSCHEDULEID "
              + " AND ET.EXECUTIONTASKID = " + taskId + ")").addEntity(
          Ratinggrouprating.class);
      if (query != null) {
        rgRating = (Ratinggrouprating)query.uniqueResult();
        if(rgRating != null){
          rgRating.getRating();
        }
      }
    } catch (Exception exception) {
      LOGGER.error("getRatingGroupRating", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "getRatingGroupRating");
    }
    return rgRating;
  }
  
  /**
   * Retrieve Performance rating group rating based on taskId and rating id
   * 
   * @param taskId Execution
   *          task Id
   * @param ratingId Performance
   *          Rating Id
   * @return RatingGroupRating object
   * @throws MicosException Exception
   */
  public Ratinggrouprating getPerformanceRatingGroupRating(Integer taskId,
      Integer ratingId) throws MicosException {
    LOGGER.entering(getClass().getName(), "getPerformanceRatingGroupRating");
    Ratinggrouprating rgRating = null;
    Session session = null;
    try {
      session = getSession();
      Query query = session.createSQLQuery(
          "SELECT * FROM RATINGGROUPRATING WHERE RATINGID = " + ratingId
              + " AND RATINGGROUPID IN (SELECT S.EXECUTIONRATINGGROUP "
              + " FROM EXECUTIONTASK ET, EXECUTIONSCHEDULE ES, STEP S "
              + " WHERE S.STEPID = ES.STEPID "
              + " AND ES.EXECUTIONSCHEDULEID = ET.EXECUTIONSCHEDULEID "
              + " AND ET.EXECUTIONTASKID = " + taskId + ")").addEntity(
          Ratinggrouprating.class);
      if (query != null) {
        rgRating = (Ratinggrouprating)query.uniqueResult();
        if(rgRating != null){
          rgRating.getRating();
        }
      }
    } catch (Exception exception) {
      LOGGER.error("getPerformanceRatingGroupRating", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(session);
      LOGGER.exiting(getClass().getName(), "getPerformanceRatingGroupRating");
    }
    return rgRating;
  }  
  
  /**
   * Load Notification Type
   * 
   * @param notificationTypeCode
   *          the String
   * @return Notificationtype
   * @throws MicosException
   *           Micos Exception
   */
  public Notificationtype loadNotificationType(String notificationTypeCode)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "loadNotificationType");
    Notificationtype notificationtype = null;
    Session hibernateSession = null;
    try {
      hibernateSession = getSession();
      Query qry = hibernateSession
          .createQuery("from com.csg.cs.micos.bean.Notificationtype as notificationtype "
              + "where notificationtype.notificationtypecode=:notificationTypeCode");
      qry.setString("notificationTypeCode", notificationTypeCode);
      notificationtype = (Notificationtype)qry.uniqueResult();
      notificationtype.getNotificationtypeid();
    } catch (Exception exception) {
      LOGGER.error("loadNotificationType", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "loadNotificationType");
    }
    return notificationtype;
  }  
  
  /**
   * Get all signers as part of the current schedule based on task id
   * 
   * @return lstSigners Signers list
   * @param taskId
   *          the Integer
   * @throws MicosException
   *           the MicosException
   */
  @SuppressWarnings("unchecked")
  public List<Executiontask> getSigners(Integer taskId) throws MicosException {
    LOGGER.entering(getClass().getName(), "getSigners");
    Session hibernateSession = null;
    List<Executiontask> lstSigners = null;
    try {
      hibernateSession = getSession();
      Query query = hibernateSession
          .createSQLQuery(" SELECT * FROM EXECUTIONTASK E WHERE"
              + " E.SIGNOFFSCHEMAID IS NOT NULL AND E.EXECUTIONSCHEDULEID = "
              + " (SELECT EXECUTIONSCHEDULEID FROM EXECUTIONTASK WHERE EXECUTIONTASKID = "
              + taskId + ")").addEntity(Executiontask.class);
      if (query != null) {
        lstSigners = query.list();
        if (!SystemUtil.isNull(lstSigners)) {
          Iterator itrSigners = lstSigners.iterator();
          while (itrSigners.hasNext()) {
            Executiontask task = (Executiontask)itrSigners.next();
            if (task != null) {
              task.getExecutor();
            }
          }
        }        
      }
    } catch (Exception exception) {
      LOGGER.error("getSigners", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getSigners");
    }
    return lstSigners;
  }
  
  /**
   * Save Notification
   * 
   * @param notification
   *          the Notification
   * @throws MicosException
   *           the Micos Exception
   */
  public void saveNotification(Notification notification) throws MicosException {
    LOGGER.entering(getClass().getName(), "saveNotification");
    Session hibernateSession = null;
    try {
      hibernateSession = getSession();
      hibernateSession.saveOrUpdate(notification);
      hibernateSession.flush();
    } catch (Exception exception) {
      LOGGER.error("saveNotification", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "saveNotification");
    }
  }  
  
  /**
   * Execution task status list
   * 
   * @throws MicosException
   *           exception
   * @return statusList
   */
  @SuppressWarnings("unchecked")
  public List<Status> getExecutionStatusList() throws MicosException {
    LOGGER.entering(getClass().getName(), "getExecutionStatusList");
    Session hibernateSession = null;
    List<Status> statusList = new ArrayList<Status>();
    Query query = null;
    try {
      hibernateSession = getSession();
      query = hibernateSession
          .createQuery("from com.csg.cs.micos.bean.Status where statuscode in "
              + "('AS','CMP','PND') ORDER BY statusdescription");
      if (query != null) {
        statusList = query.list();
      }
    } catch (Exception exception) {
      LOGGER.error("getExecutionStatusList", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "getExecutionStatusList");
    }
    return statusList;
  }

  /**
   * Search execution tasks based on the selected criteria
   * @param hibernateSession Session
   * @param executionTaskId
   *          execution task id
   * @param employeePID
   *          employee pid
   * @param statusCode
   *          status code
   * @param mapCount Count
   * @return executionTaskList
   * @throws MicosException Exception
   */
  @SuppressWarnings("unchecked")
  public List<Executiontask> searchReassignmentTasks(Session hibernateSession,
      Integer executionTaskId, String employeePID, String statusCode,
      Map<String, Integer> mapCount) throws MicosException {
    LOGGER.entering(getClass().getName(), "searchReassignmentTasks");
    List<Executiontask> taskList = null;
    Criteria criteria = null;
    Criteria countCriteria = null;
    try {
      criteria = hibernateSession.createCriteria(Executiontask.class).addOrder(
          Order.desc("executiontaskid"));
      if (executionTaskId != null) {
        criteria.add(Restrictions.eq("executiontaskid", executionTaskId));
      }
      if (SystemUtil.isNotNull(employeePID)) {
        // get employee id
        criteria.createCriteria("executor").add(
            Restrictions.ilike("pid", employeePID, MatchMode.ANYWHERE));
      }
      if (SystemUtil.isNotNull(statusCode)) {
        // get status code
        criteria.createCriteria("status").add(
            Restrictions.like("statuscode", statusCode));
      } 
      if (criteria != null) {
        countCriteria = criteria;
        criteria.setFirstResult(0);
        criteria.setMaxResults(MicosConstantValues.TASK_RE_ASSIGN_REC_COUNT);
        taskList = criteria.list();
        
        countCriteria.setProjection(Projections.rowCount());
        mapCount.put("maxCount", (Integer)countCriteria.uniqueResult());        
      }
    } catch (Exception exception) {
      LOGGER.error("getExecutionStatusList", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    }
    LOGGER.exiting(getClass().getName(), "searchReassignmentTasks");
    return taskList;
  }
  
  /**
   * Re-Assign the selected tasks to the selected employee/status
   * 
   * @param reAssignmentList
   *          Re-Assignment Task List
   * 
   * @throws MicosException micosexception
   */
  public void changeTaskAssignments(List<Executiontask> reAssignmentList)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "changeTaskAssignments");
    Session hibernateSession = null;
    try {
      hibernateSession = getSession();
      for (Executiontask task : reAssignmentList) {
        hibernateSession.saveOrUpdate(task);
        hibernateSession.flush();        
      }
    } catch (Exception exception) {
      LOGGER.error("changeTaskAssignments", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "changeTaskAssignments");
    }

    LOGGER.exiting(getClass().getName(), "changeTaskAssignments");
  }

    /**
	 * It will fetch all attachments for a selected task id.
	 * 
	 * @param taskID
	 *            Integer
	 * @return List
	 * @throws MicosException
	 *             MicosException
	 */
  @SuppressWarnings("unchecked")
	public List<Attachment> populateAttachmentforTask(Integer taskID)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateAttachmentforTask");
		Session hibernateSession = null;
		Criteria criteria = null;
		List<Attachment> listAttachment = new ArrayList<Attachment>();
		try {
			hibernateSession = getSession();
			criteria = hibernateSession.createCriteria(Multiattachment.class);
			if (taskID != null) {
				criteria.add(Restrictions.eq("executiontask.executiontaskid",
						taskID));
			}

			if (criteria != null) {
				List<Multiattachment> listMultiattachment = criteria.list();
				if (SystemUtil.isNotNull(listMultiattachment)) {
					for (Multiattachment aMultiattachment : listMultiattachment) {
						Attachment attachment = aMultiattachment
								.getAttachment();
						attachment.getAttachmentfilename();
						attachment.getAttachmenttype().getExtension();
						listAttachment.add(attachment);
					}
				}

			}
		} catch (Exception exception) {
			LOGGER.error("populateAttachmentforTask", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			releaseSession(hibernateSession);
			LOGGER.exiting(getClass().getName(), "populateAttachmentforTask");
		}
		return listAttachment;
  }

/**
 * It will fetch all attachments for a selected task id.
 * 
 * @param taskID
 *            Integer
 * @return List
 * @throws MicosException
 *             MicosException
 */
 @SuppressWarnings("unchecked")
 public List<Link> populateLinkforTask(Integer taskID) throws MicosException {
		LOGGER.entering(getClass().getName(), "populateLinkforTask");
		Session hibernateSession = null;
		Criteria criteria = null;
		List<Link> listLink = new ArrayList<Link>();
		try {
			hibernateSession = getSession();
			criteria = hibernateSession.createCriteria(Multilink.class);
			if (taskID != null) {
				criteria.add(Restrictions.eq("executiontask.executiontaskid",
						taskID));
			}

			if (criteria != null) {
				List<Multilink> listMultilink = criteria.list();
				if (SystemUtil.isNotNull(listMultilink)) {
					for (Multilink aMultilink : listMultilink) {
						Link aLink = aMultilink.getLink();
						aLink.getLinkname();
						listLink.add(aLink);
					}
				}

			}
		} catch (Exception exception) {
			LOGGER.error("populateLinkforTask", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			releaseSession(hibernateSession);
			LOGGER.exiting(getClass().getName(), "populateLinkforTask");
		}
		return listLink;
	}
/*	*//**
	 * submit the Execution Task collection
	 * 
	 * @param executionTask
	 *            Executiontask
	 * @param loggedinuser loggedinuser           
	 * @throws MicosException
	 *             exception
	 * 
	 *//*
 	public void submitExecutionTaskFromChecklist(ControlPerformanceTaskView executionTask, Integer loggedinuser) throws MicosException {
 		LOGGER.entering(getClass().getName(), "submitExecutionTaskFromChecklist");
 	    Session hibernateSession = null;
 	    try {
 	      hibernateSession = getSession();
 	      Executiontask executiontask = (Executiontask) hibernateSession.get(
					Executiontask.class, executionTask.getId());

 	      Criteria criteria = hibernateSession.createCriteria(Status.class).add(Restrictions.eq("statuscode", MicosConstantValues.Status.COMPLETED));
 	      Status status = (Status)criteria.uniqueResult();
 	      executiontask.setStatus(status); 	
 	      
 	      if(executionTask.getTaskType().equals(
					MicosConstantValues.SchemaType.TASKTYPE_EXE)) {
 	    	 executiontask.setRating(loadRating(executionTask.getCode()));
 	      }
 	     
 	      executiontask.setPerfSubmitDate(DateUtil.getCurrentTimestamp()); 
	      executiontask.setLastupdate(DateUtil.getCurrentTimestamp());
 	     
	      Employee loggedinemployee = loadEmployee(loggedinuser);
	      executiontask.setTaskSubmittedby(loggedinemployee);
	      executiontask.setEmployee(loggedinemployee);
	      
 	      hibernateSession.saveOrUpdate(executiontask);
 	      hibernateSession.flush();        
 	    } catch (Exception exception) {
 	      LOGGER.error("submitExecutionTaskFromChecklist", exception);
 	      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
 	          .getErrorMessage(exception));
 	    } finally {
 	      releaseSession(hibernateSession);
 	      LOGGER.exiting(getClass().getName(), "submitExecutionTask");
 	    }
 	}*/
 /**
  * save Attachment And Link for task id
  * @param setMultiattachment Set
  * @param setMultilink Set
  * @param setDeletedMultiattachment Set
  * @param setDeletedMultiLink Set
  * @param taskId Integer
  * @throws MicosException exception
  */
  public void saveAttachmentAndLink(Set<Multiattachment> setMultiattachment,Set<Multilink> setMultilink,
		  List<AttachmentView> attachmentView,	Set<Multilink> setDeletedMultiLink, Integer taskId)throws MicosException{
	  	 LOGGER.entering(getClass().getName(), "saveAttachmentAndLink");
		 Session hibernateSession = null;
		 Executiontask task=null;
		 try {
			 hibernateSession = getSession();
			 task = (Executiontask)hibernateSession.get(Executiontask.class, taskId);
			 task=addMultiattachment(setMultiattachment,task,hibernateSession);
			 task=addMultiLink(setMultilink,task,hibernateSession);
				
			 deleteAttachment(attachmentView,hibernateSession);
			 deleteLink(setDeletedMultiLink,hibernateSession);
			 
			 hibernateSession.saveOrUpdate(task);
			 hibernateSession.flush();
		 }catch (Exception exception) {
		      LOGGER.error("saveAttachmentAndLink", exception);
		      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
		          .getErrorMessage(exception));
		 } finally {
		      releaseSession(hibernateSession);
		      LOGGER.
		      exiting(getClass().getName(), "saveAttachmentAndLink");
		 }
  }
  
  /**
   * add a attachment with a Collection.
   * @param setMultiattachment Set
   * @param task Executiontask
   * @param hibernateSession Session
   * @return Collection
   * @throws MicosException exception
   */
  private Executiontask addMultiattachment(
              Set<Multiattachment> setMultiattachment, Executiontask task, Session hibernateSession) throws MicosException{
	    LOGGER.entering(getClass().getName(), "addMultiattachment"); 
        Attachmenttype attachmenttype=null;
        if (SystemUtil.isNotNull(setMultiattachment)) {
              Set<Multiattachment> newsetMultiattachment = new HashSet<Multiattachment>();
              for (Multiattachment aMultiattachment : setMultiattachment) {
                    if (aMultiattachment.getMultiattachmentsid() == null) {
                          Employee newEmployee = (Employee) hibernateSession.get(
                                      Employee.class, aMultiattachment.getEmployee().getEmployeeid());
                          aMultiattachment.setExecutiontask(task);
                          aMultiattachment.setEmployee(newEmployee);
                          aMultiattachment.setCreatedate(DateUtil.getCurrentTimestamp());
                          aMultiattachment.setLastupdate(DateUtil.getCurrentTimestamp());
                          Attachment attachment=aMultiattachment.getAttachment();
                          
                          Query query = hibernateSession
                        .createQuery("from com.csg.cs.micos.bean.Attachmenttype as attachType"
                            + " where attachType.extension=:selectedExtension");
                    query.setString("selectedExtension", attachment.getAttachmenttype().getExtension());
                          if (null != query) {
                            attachmenttype = (Attachmenttype)query.uniqueResult();
                          }
                          attachment.setAttachmenttype(attachmenttype);
                          Employee employee=(Employee)hibernateSession
                          .get(Employee.class,aMultiattachment.getAttachment().getEmployee().getEmployeeid());
                          attachment.setEmployee(employee);
                          aMultiattachment.setAttachment(attachment);
                          newsetMultiattachment.add(aMultiattachment);
                    }
              }
              task.setMultiattachments(newsetMultiattachment);
        }
        LOGGER.exiting(getClass().getName(), "addMultiattachment");
        return task;
  }

	
	/**
	 * add a attachment with a Collection.
	 * @param setMultilink Set
	 * @param task Collection
	 * @param hibernateSession hibernateSession
	 * @return Collection
	 */
	private Executiontask addMultiLink(
			Set<Multilink> setMultilink,  Executiontask task,  Session hibernateSession) {
		LOGGER.entering(getClass().getName(), "addMultiLink"); 
		if (SystemUtil.isNotNull(setMultilink)) {
			Set<Multilink> newsetMultilink = new HashSet<Multilink>();
			for (Multilink aMultilink : setMultilink) {
				if (aMultilink.getMultilinksid() == null) {
					Employee newEmployee = (Employee) hibernateSession.get(
							Employee.class, aMultilink.getEmployee().getEmployeeid());
					aMultilink.setExecutiontask(task);
					aMultilink.setEmployee(newEmployee);
					aMultilink.setCreatedate(DateUtil.getCurrentTimestamp());
					aMultilink.setLastupdate(DateUtil.getCurrentTimestamp());
					Link link=aMultilink.getLink();
					Employee employee=(Employee)hibernateSession
					.get(Employee.class,aMultilink.getLink().getEmployee().getEmployeeid());
					link.setEmployee(employee);
					link.setCreatedate(DateUtil.getCurrentTimestamp());
					link.setLastupdate(DateUtil.getCurrentTimestamp());
					aMultilink.setLink(link);
					newsetMultilink.add(aMultilink);
				}
			}
			task.setMultilinks(newsetMultilink);
		}
		LOGGER.exiting(getClass().getName(), "addMultiLink");
		return task;
	}
	
  /**
   * delete an attachment from a Collection.
   * @param setDeletedMultiattachment Set
   * @param hibernateSession Session
   */
	
	 private void deleteAttachment(
			 List<AttachmentView> attachmentView,
			Session hibernateSession) {
		LOGGER.entering(getClass().getName(), "deleteAttachment"); 
	    if (!SystemUtil.isNull(attachmentView)) {
	        Iterator itAttach = attachmentView.iterator();
	        while (itAttach.hasNext()) {
	          AttachmentView lattachView = (AttachmentView)itAttach.next();
	          if (null != lattachView) {
	            Attachment attach = (Attachment)hibernateSession.get(
	                Attachment.class, lattachView.getId());
	            if(attach == null) {
	              continue;
	            }
	            attach.setLastupdate(lattachView.getLastUpdate());
	            hibernateSession.delete(attach);
	          }
	        }
	      }
		 LOGGER.exiting(getClass().getName(), "deleteAttachment");
	} 
	
	
	/**
	 * delete a link.
	 * @param setDeletedMultiLink List
	 * @param hibernateSession Session
	 * 
	 */
	private void deleteLink(Set<Multilink> setDeletedMultiLink,Session hibernateSession){
		LOGGER.entering(getClass().getName(), "deleteLink"); 
		if (SystemUtil.isNotNull(setDeletedMultiLink)) {
			for (Multilink aMultilink : setDeletedMultiLink) {
				Multilink newMultilink = (Multilink) hibernateSession
						.get(Multilink.class, aMultilink.getMultilinksid());
				newMultilink.setLastupdate(aMultilink.getLastupdate());
				Link aLink=newMultilink.getLink();
				Link newLink = (Link) hibernateSession
				.get(Link.class, aLink.getLinkid());
				newLink.setLastupdate(aLink.getLastupdate());
				newMultilink.setLink(newLink);
				hibernateSession.delete(newMultilink);
				hibernateSession.delete(newLink);
			}
		}
		 LOGGER.exiting(getClass().getName(), "deleteLink");
	}
	
	/**
	   * Fetches task Details on the basis of for Check List View.
	   * @param languageCode String
	   * @param tabname String
	   * @return List
	   * 
	   * @throws MicosException
	   *           in the following cases:
	   *           <ul>
	   *           <li>if unable to retrieve data e.g. when looking up specific
	   *           data via a known identifier</li>
	   *           <li>if unable to access data for varying reasons like permission
	   *           denied, data source lookup failure, data integrity violations etc
	   *           </li>
	   *           </ul>
	   */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllScheduledTasksForChecklist(String languageCode, String tabname) throws MicosException {
		  LOGGER.entering(getClass().getName(), "getAllTaskDetailsForControlPerformance");
		    Session hibernateSession = null;
		    List<Object[]> objExecutiontask = null;
		    Query query = null;
		    MicosUserBean user = (MicosUserBean) UserBean.getUserBean();
			Integer userId = user.getWorkingAsUser().getEmployeeid();
		    try {
		    	StringBuilder queryBuf = new StringBuilder();
		    	
		    	queryBuf.append("SELECT distinct mytable.EXECUTIONTASKID,mytable.SUPPORTSCHEMAID,");
		    	queryBuf.append("mytable.EXECUTIONSCHEMAID, mytable.SIGNOFFSCHEMAID, mytable.QASCHEMA,");
		    	queryBuf.append("mytable.EXECUTIONENDDATE, mytable.DESCRIPTION, mytable.COVERAGEPERIODSTARTDATE,");
		    	queryBuf.append("mytable.COVERAGEPERIODENDDATE, mytable.TITLE, mytable.CONTROLID,TO_CHAR(mytable.w1) as w1,TO_CHAR(mytable.w2) as w2,");
		    	queryBuf.append("RATING.RATINGID,MULTIATTACHMENTS.ATTACHMENTID,MULTILINKS.LINKID, mytable.executionhename,");
          		queryBuf.append("mytable.COUNTRY,mytable.GLOBALLYVISIBLE,mytable.name as periodicpattern, ");
          		queryBuf.append("mytable.checklisttaskstate, mytable.SIGNOFF, mytable.EXECUTION, ");
          		queryBuf.append("mytable.monday,mytable.tuesday,mytable.wednesday,mytable.thursday,mytable.friday,mytable.saturday, mytable.sunday, mytable.checklisttaskstatedesc, mytable.MANDATORYCOMMENT, ");
          		queryBuf.append("mytable.steptype, mytable.poperiodicpattern, mytable.pomonday, mytable.potuesday, mytable.powednesday, mytable.pothursday, mytable.pofriday, ");
          		queryBuf.append("mytable.posaturday, mytable.posunday ");
          		queryBuf.append("FROM (");
		    	queryBuf.append("SELECT EXECUTIONTASK.EXECUTIONTASKID,  EXECUTIONTASK.SUPPORTSCHEMAID, EXECUTIONTASK.EXECUTIONSCHEMAID, EXECUTIONTASK.QASCHEMA, EXECUTIONTASK.SIGNOFFSCHEMAID, ");
		    	queryBuf.append("EXECUTIONTASK.EXECUTIONENDDATE, EXECUTIONTASK.DESCRIPTION, EXECUTIONSCHEDULE.COVERAGEPERIODSTARTDATE, EXECUTIONSCHEDULE.COVERAGEPERIODENDDATE, ");
          		queryBuf.append("EXECUTIONTASK.COUNTRY, EXECUTIONTASK.GLOBALLYVISIBLE, periodtype.name, (select statuscode from status where statusid = executiontask.checklisttaskstate) as checklisttaskstate, ");
		    	queryBuf.append("(select statusdescription from status where statusid = executiontask.checklisttaskstate) as checklisttaskstatedesc, ");
          		queryBuf.append("STEP.TITLE, CONTROL.CONTROLID, t1.translatedtext w1, t2.translatedtext w2, EXECUTIONTASK.RATINGID, HierarchyElement.name as executionhename, step.SIGNOFF, step.EXECUTION, ");
		    	queryBuf.append("schedule.monday, schedule.tuesday, schedule.wednesday, schedule.thursday, schedule.friday, schedule.saturday, schedule.sunday, RATINGGROUPRATING.MANDATORYCOMMENT, ");
		    	queryBuf.append("step.steptype, tempperiodtype.NAME as poperiodicpattern, poschedule.monday as pomonday, poschedule.tuesday as potuesday, poschedule.wednesday as powednesday,");
		    	queryBuf.append("poschedule.thursday as pothursday, poschedule.friday as pofriday, poschedule.saturday as posaturday, poschedule.sunday as posunday ");
		    	queryBuf.append("FROM EXECUTIONTASK, EXECUTIONSCHEDULE, RATING, STEP, SETUP, CONTROL, multilanguagetranslation m1, multilanguagetranslation m2, ");
		    	queryBuf.append("Translation t1, Translation t2, Supportedlanguage, HierarchyElement, schedule, periodtype, status, RATINGGROUPRATING,  poweroption, poschedule, periodtype tempperiodtype ");
		    	queryBuf.append("WHERE EXECUTIONTASK.Executor = " + userId + " AND (EXECUTIONTASK.Statusid = 2 OR EXECUTIONTASK.Statusid = 4) AND EXECUTIONTASK.CONTROLCOLLECTIONTASK != 'C' ");
		    	queryBuf.append("AND EXECUTIONTASK.executionscheduleid = executionschedule.executionscheduleid ");
		    	queryBuf.append("AND executionschedule.stepid = step.stepid AND step.setupid = setup.setupid ");
		    	queryBuf.append("AND setup.controlid = control.controlid ");
		    	queryBuf.append("AND CONTROL.title = m1.multilanguagetranslationid ");
		    	queryBuf.append("AND m1.multilanguagetranslationid = t1.multilanguagetranslationid ");
		    	queryBuf.append("AND t1.SUPPORTEDLANGUAGEID = SUPPORTEDLANGUAGE.SUPPORTEDLANGUAGEID ");
		    	queryBuf.append("AND SUPPORTEDLANGUAGE.LANGUAGECODE= '" + languageCode + "'");
		    	queryBuf.append("AND CONTROL.description = m2.multilanguagetranslationid ");
		    	queryBuf.append("AND m2.multilanguagetranslationid = t2.multilanguagetranslationid ");
		    	queryBuf.append("AND t2.SUPPORTEDLANGUAGEID = SUPPORTEDLANGUAGE.SUPPORTEDLANGUAGEID ");
		    	queryBuf.append("AND SUPPORTEDLANGUAGE.LANGUAGECODE= '" + languageCode + "'");
		    	
		    	if(tabname != null && tabname.equalsIgnoreCase(TaskSelectionType.PASTDUE)){ 
		    		queryBuf.append(" AND TO_DATE(EXECUTIONTASK.executionenddate, 'DD.MM.YYYY') < TO_DATE(sysdate , 'DD.MM.YYYY') ");
		    	}
		    	
		    	queryBuf.append(" and HIERARCHYELEMENT.HIERARCHYELEMENTID = EXECUTIONTASK.EXECUTIONHE");
		    	queryBuf.append(" and step.STEPID = schedule.STEPID");
		    	queryBuf.append(" and schedule.PATTERNFREQUENCY = periodtype.PERIODTYPEID(+)");
		    	queryBuf.append(" and executiontask.CHECKLISTTASKSTATE = status.STATUSID(+)");
		    	queryBuf.append(" and EXECUTIONTASK.RATINGID = RATINGGROUPRATING.RATINGID(+)");
		    	queryBuf.append(" and step.poweroptionid = poweroption.poweroptionid(+)");
		    	queryBuf.append(" and poweroption.poweroptionid = poschedule.poweroptionid(+)");
		    	queryBuf.append(" and poschedule.patternfrequency = tempperiodtype.periodtypeid(+)");
		    	queryBuf.append(" ) mytable ");
		    	queryBuf.append("left outer join MULTIATTACHMENTS on (MULTIATTACHMENTS.executiontaskid = mytable.executiontaskid) ");
		    	queryBuf.append("left outer join MULTILINKS on (MULTILINKS.executiontaskid = mytable.executiontaskid) ");
		    	queryBuf.append("left outer join RATING on (RATING.ratingid = mytable.ratingid) ");
		    	queryBuf.append("order by mytable.EXECUTIONENDDATE, mytable.EXECUTIONTASKID, w1");		    	
		    	
		    	hibernateSession = getSession();
		    	query = hibernateSession.createSQLQuery(queryBuf.toString());
		    	
		      if (query != null) {
		    	  objExecutiontask = query.list();
		      }
		      
		    } catch (Exception exception) {
		      LOGGER.error("getAllTaskDetailsForControlPerformance", exception);
		      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
		          .getErrorMessage(exception));
		    } finally {
		      releaseSession(hibernateSession);
		      LOGGER.exiting(getClass().getName(), "getAllTaskDetailsForControlPerformance");
		    }
		  return objExecutiontask;
 } 
	
	/**
	   * Fetches Scheduled task Details on the basis of selection
	   * @param languageCode String
	   * @param tabname String
	   * @return List
	   * 
	   * @throws MicosException
	   *           in the following cases:
	   *           <ul>
	   *           <li>if unable to retrieve data e.g. when looking up specific
	   *           data via a known identifier</li>
	   *           <li>if unable to access data for varying reasons like permission
	   *           denied, data source lookup failure, data integrity violations etc
	   *           </li>
	   *           </ul>
	   */
	@SuppressWarnings("unchecked")
	public List<Object[]> getAllScheduledTasks(String languageCode, String tabname) throws MicosException {
		  LOGGER.entering(getClass().getName(), "getAllScheduledTasks");
		    Session hibernateSession = null;
		    List<Object[]> objExecutiontask = null;
		    Query query = null;
		    MicosUserBean user = (MicosUserBean) UserBean.getUserBean();
			Integer userId = user.getWorkingAsUser().getEmployeeid();
		    try {
		    	StringBuilder queryBuf = new StringBuilder();
		    	
		    	queryBuf.append("SELECT distinct t.EXECUTIONTASKID, t.SUPPORTSCHEMAID, t.EXECUTIONSCHEMAID, t.QASCHEMA,");
		    	queryBuf.append("t.SIGNOFFSCHEMAID, t.EXECUTIONENDDATE, s.TITLE, dbms_lob.substr(Translation.translatedtext), Status.STATUSDESCRIPTION, ");
		    	queryBuf.append("he.Name FROM EXECUTIONTASK t, EXECUTIONSCHEDULE es, STEP s, SETUP, CONTROL c, Status, multilanguagetranslation ml, ");
		    	queryBuf.append("Translation, Supportedlanguage, HierarchyElement he ");
		    	queryBuf.append("WHERE t.Executor = " + userId + " AND t.Statusid = 2 AND t.CONTROLCOLLECTIONTASK != 'C' ");
		    	queryBuf.append("AND t.executionscheduleid = es.executionscheduleid ");
		    	queryBuf.append("AND es.stepid = s.stepid AND s.setupid = setup.setupid ");
		    	queryBuf.append("AND setup.controlid = c.controlid ");
		    	queryBuf.append("AND c.title = ml.multilanguagetranslationid ");
		    	queryBuf.append("AND ml.multilanguagetranslationid = Translation.multilanguagetranslationid ");
		    	queryBuf.append("AND Translation.SUPPORTEDLANGUAGEID = SUPPORTEDLANGUAGE.SUPPORTEDLANGUAGEID ");
		    	queryBuf.append("AND SUPPORTEDLANGUAGE.LANGUAGECODE= '" + languageCode + "' ");
		    	queryBuf.append("AND status.statusid = t.statusid");
		    	if(tabname != null && tabname.equalsIgnoreCase(TaskSelectionType.PASTDUE)){ 
		    		queryBuf.append(" AND TO_DATE(t.executionenddate, 'DD.MM.YYYY') < TO_DATE(sysdate , 'DD.MM.YYYY') ");
		    	}
		    	
		    	
		    	queryBuf.append(" and he.hierarchyelementid = t.executionhe ");
		    	queryBuf.append(" order by t.EXECUTIONENDDATE, t.EXECUTIONTASKID ");
		    	hibernateSession = getSession();
		    	query = hibernateSession.createSQLQuery(queryBuf.toString());
		    	
		      if (query != null) {
		    	  objExecutiontask = query.list();
		      }
		      
		    } catch (Exception exception) {
		      LOGGER.error("getAllScheduledTasks", exception);
		      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
		          .getErrorMessage(exception));
		    } finally {
		      releaseSession(hibernateSession);
		      LOGGER.exiting(getClass().getName(), "getAllScheduledTasks");
		    }
		  return objExecutiontask;
 } 

  /**
   * Fetches the control title for the given control id.
   * 
   * @param languageCode
   *            the language code for the user
   * @param controlId
   *            the control id for which title is needed
   * @return the Control Title
   * @throws MicosException
   *             if
   *             <ul>
   *             <li>failure in data retrival</li>
   *             <li>failure in data access</li>
   *             </ul>
   */
  public String findControlTitleForControl(String languageCode,
      Integer controlId) throws MicosException {
    LOGGER.entering(getClass().getName(),"findControlTitleForControl");
    String controlTitle = null;
    Session hibernateSession = getSession();
    String qryForControlTitle = "SELECT translation.TRANSLATEDTEXT as Title "
        + "FROM multilanguagetranslation, control, translation, supportedlanguage "
        + "WHERE control.title = multilanguagetranslation.multilanguagetranslationid "
        + "AND multilanguagetranslation.multilanguagetranslationid = translation.multilanguagetranslationid "
        + "AND supportedlanguage.supportedlanguageid = translation.supportedlanguageid "
        + "AND SUPPORTEDLANGUAGE.LANGUAGECODE = '"
        + languageCode
        + "'"
        + " AND control.CONTROLID = " + controlId;
    try {
      Query query = hibernateSession.createSQLQuery(qryForControlTitle);
      if (null != query) {
        List listOfObj = query.list();
        if (!SystemUtil.isNull(listOfObj)) {
          for (int i = 0; i < listOfObj.size(); i++) {
            org.hibernate.lob.SerializableClob rowValue = (org.hibernate.lob.SerializableClob) listOfObj
                .get(i);
            controlTitle = SystemUtil.getClobValueAsString(rowValue);
            break;
          }
        }
      }
    } catch (Exception exception) {
      LOGGER.error("findControlTitleForControl", exception);
      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
          .getErrorMessage(exception));
    } finally {
      releaseSession(hibernateSession);
      LOGGER.exiting(getClass().getName(), "findControlTitleForControl");
    }
    return controlTitle;
  }

    /**
	 * used to update task detail in case of save and submit
	 * 
	 * @param task  Executiontask
	 * @return true, if task updated succeesfully
	 * @throws MicosException MicosException
	 */
	public boolean updateTask(Executiontask task) throws MicosException {
		LOGGER.entering(getClass().getName(), "updateTask");
		boolean success = false;
 	    Session hibernateSession = null;
 	    try {
 	      hibernateSession = getSession();
 	      hibernateSession.saveOrUpdate(task);
 	      hibernateSession.flush();
 	      success = true;
 	    } catch (Exception exception) {
 	      LOGGER.error("updateTask", exception);
 	      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
 	          .getErrorMessage(exception));
 	    } finally {
 	      releaseSession(hibernateSession);
 	      LOGGER.exiting(getClass().getName(), "updateTask");
 	    }
 	    
 	    return success;
	}
	
	
	/**
	 * Gets the Referencecode object by code
	 * 
	 * @param refcode referencecode code.	
	 * @param refCodegroup referencecodegroup group name.	
	 * @exception MicosException Micos Exception.
	 * @return status Status a status .
	 */
	public Referencecode getReferenceCodeByCode(String refcode, String refCodegroup) throws MicosException {
		LOGGER.entering(getClass().getName(), "getReferenceCodeByCode");
		Referencecode referencecode = null;
		if(SystemUtil.isNotNull(refcode)) {
			Session session = null;
			try {
				session = getSession();
				Query query=session.createQuery("from com.csg.cs.micos.bean.Referencecode rfcode " +
				"where rfcode.code=:refcode and rfcode.referencecodegroup.groupname=:groupname");		
				query.setString("refcode", refcode);
				query.setParameter("groupname", refCodegroup);
				referencecode = (Referencecode) query.uniqueResult();		
			} catch (Exception exception) {
				LOGGER.error("getReferenceCodeBycode", exception);
				throw new MicosException(ExceptionContext.ERROR, exception,
						SystemUtil.getErrorMessage(exception));
			} finally {
				releaseSession(session);
				LOGGER.exiting(getClass().getName(), "getReferenceCodeByCode");
			}
		}
		
		return referencecode;
	}
	
	/**
	 * Gets all performance task for sign of task.
	 * 
	 * @param taskId
	 *    the Integer
	 * @return List
	 * @throws MicosException
	 *      the Micos Exception
	 */
	
	@SuppressWarnings("unchecked")
	 public List<Object[]> getPerformanceTasksForSignoff(Integer taskId) throws MicosException{
		LOGGER.entering(getClass().getName(),"getPerformanceTasksForSignoff");
		Session hibernateSession = null;
		List<Object[]> taskForSignoff = null;
		
		try {
			hibernateSession = getSession();
			Query query = hibernateSession.createSQLQuery("Select distinct EXECUTIONTASK.EXECUTIONTASKID,"
					+ " EXECUTIONTASK.SIGNOFFRATINGID, EXECUTIONTASK.SIGNOFFCOMMENT, STATUS.STATUSCODE, RATINGGROUPRATING.MANDATORYCOMMENT"
					+ " from EXECUTIONTASK,EXECUTIONSCHEDULE,STEP,EMPLOYEE,STATUS, RATINGGROUPRATING"
					+ " where EXECUTIONTASK.EXECUTIONSCHEDULEID = EXECUTIONSCHEDULE.EXECUTIONSCHEDULEID"
					+ " and EXECUTIONSCHEDULE.STEPID = STEP.STEPID"
					+ " and EXECUTIONTASK.STATUSID = STATUS.STATUSID"
					+ " and EXECUTIONTASK.EXECUTOR = EMPLOYEE.EMPLOYEEID"
					+ " and EXECUTIONTASK.SIGNOFFRATINGID = RATINGGROUPRATING.RATINGID(+)"
					+ " and EXECUTIONTASK.executionschemaid is not null"
					+ " and STEP.STEPID ="
					+ " (Select STEP.STEPID from EXECUTIONTASK,EXECUTIONSCHEDULE,STEP"
					+ " where EXECUTIONTASK.EXECUTIONSCHEDULEID = EXECUTIONSCHEDULE.EXECUTIONSCHEDULEID"
					+ " and EXECUTIONSCHEDULE.STEPID = STEP.STEPID"
					+ " and EXECUTIONTASK.EXECUTIONTASKID = "
					+ taskId + ")"
					+ " and EXECUTIONTASK.EXECUTIONTASKID <>  "
					+ taskId );
			
			if (query != null){
				taskForSignoff = query.list();
			}
		} catch (Exception exception) {
	      LOGGER.error("getPerformanceTasksForSignoff", exception);
	      throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil
	          .getErrorMessage(exception));
		} finally {
	      releaseSession(hibernateSession);
	      LOGGER.exiting(getClass().getName(), "getPerformanceTasksForSignoff");
		}
    return taskForSignoff;
	}  
	

}

/*
 * Check-In History: $Log: ControlPerformanceDaoImpl.java,v $
 * Check-In History: Revision 1.1  2010/10/25 15:25:29  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.119  2010/10/15 07:53:48  f542625
 * Check-In History: AR 1.5 merging
 * Check-In History:
 * Check-In History: Revision 1.118.2.10  2010/10/12 15:41:57  f535955
 * Check-In History: Updated for defect - 1191
 * Check-In History:
 * Check-In History: Revision 1.118.2.9  2010/10/11 13:54:09  f542625
 * Check-In History: chnaged for logic of "last executiion" in task archive (defect 1025).
 * Check-In History:
 * Check-In History: Revision 1.118.2.8  2010/10/07 20:51:34  f535955
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.118.2.7  2010/10/05 14:44:39  f535955
 * Check-In History: Updated for It Defect
 * Check-In History:
 * Check-In History: Revision 1.118.2.6  2010/09/29 14:20:20  f535955
 * Check-In History: Checklist portal groupView update
 * Check-In History:
 * Check-In History: Revision 1.118.2.5  2010/09/28 09:55:52  f542625
 * Check-In History: changed for defect 1038.
 * Check-In History:
 * Check-In History: Revision 1.118.2.4  2010/09/17 16:20:33  f542625
 * Check-In History: fixed alignment problem
 * Check-In History:
 * Check-In History: Revision 1.118.2.3  2010/09/17 15:46:41  f542625
 * Check-In History: changed for task count
 * Check-In History:
 * Check-In History: Revision 1.118.2.2  2010/09/15 14:06:19  f542625
 * Check-In History: removed schedule tasks tab and task container for AR 1.5
 * Check-In History:
 * Check-In History: Revision 1.118  2010/06/16 11:57:26  g304578
 * Check-In History: changed for AR 1.5
 * Check-In History:
 * Check-In History: Revision 1.117  2010/06/09 15:06:13  g304578
 * Check-In History: changed for AR 1.5
 * Check-In History:
 * Check-In History: Revision 1.5  2010/06/09 09:46:38  160134
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.4  2010/06/07 11:31:21  212346
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.3  2010/06/07 05:44:04  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.2  2010/06/03 09:31:21  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.1  2010/05/27 07:00:47  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.116  2010/03/10 09:43:12  f542625
 * Check-In History: code merging after 1.4.3 and AR 2 package 1
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.13  2010/02/03 09:48:48  f542625
 * Check-In History: changed for task selection
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.12  2010/01/08 10:23:14  f542625
 * Check-In History: used assignment views queries to get my assignments data.
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.11  2010/01/04 15:50:20  f542625
 * Check-In History: changed for DSO signoff assignments.
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.10  2009/10/09 08:28:42  f599895
 * Check-In History: Shrinking down the DSO SignOff Assignments
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.9  2009/10/08 06:25:15  a363026
 * Check-In History: correct LazyInitializationException in getEscalationProperties
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.8  2009/09/29 15:20:34  f542625
 * Check-In History: changed for dso performance assignments.
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.7  2009/09/15 13:19:47  f542625
 * Check-In History: changed task submission logic to one place.
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.6  2009/08/27 08:59:22  f535955
 * Check-In History: moved calculateFromDate() in SystemUtil
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.5  2009/08/18 13:10:12  f542625
 * Check-In History: changed control count.
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.4  2009/08/18 11:56:57  f599895
 * Check-In History: globallyvisible
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.3  2009/08/13 08:56:07  f542625
 * Check-In History: added submittedby and submitted date for task.
 * Check-In History:
 * Check-In History: Revision 1.105.4.32.2.2  2009/08/07 09:57:47  f542625
 * Check-In History: merged from AR 1.4
 * Check-In History:
 * Check-In History: Revision 1.105.4.35  2009/08/04 16:27:09  f542625
 * Check-In History: changed for excluded PID.
 * Check-In History:
 * Check-In History: Revision 1.105.4.34  2009/08/03 10:06:51  f542625
 * Check-In History: changed for multiple assignments.
 * Check-In History:
 * Check-In History: Revision 1.105.4.33  2009/07/31 10:57:02  f542625
 * Check-In History: changed for excluded PID.
 * Check-In History:
 * Check-In History: Revision 1.105.4.32  2009/07/30 16:45:28  f542625
 * Check-In History: Added hierarchy name with execution He role.
 * Check-In History:
 * Check-In History: Revision 1.105.4.31  2009/07/28 14:03:03  f542625
 * Check-In History: changed the query for DSO assignments.
 * Check-In History:
 * Check-In History: Revision 1.105.4.30  2009/07/24 13:26:34  f542625
 * Check-In History: changed for assignments.
 * Check-In History:
 * Check-In History: Revision 1.105.4.29  2009/07/23 13:34:48  f542625
 * Check-In History: changed for dso signoff assignments
 * Check-In History:
 * Check-In History: Revision 1.105.4.28  2009/07/21 15:44:50  f599895
 * Check-In History: Fix Defect # 809
 * Check-In History:
 * Check-In History: Revision 1.105.4.27  2009/07/21 12:07:49  f542625
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.105.4.26  2009/07/21 09:25:39  f542625
 * Check-In History: changed for assignments.
 * Check-In History:
 * Check-In History: Revision 1.105.4.24  2009/07/17 09:33:47  f542625
 * Check-In History: changed sorting
 * Check-In History:
 * Check-In History: Revision 1.105.4.23  2009/07/16 13:40:56  f542625
 * Check-In History: changed for assignments
 * Check-In History:
 * Check-In History: Revision 1.105.4.22  2009/07/08 07:49:25  f542625
 * Check-In History: added sorting order
 * Check-In History:
 * Check-In History: Revision 1.105.4.21  2009/07/03 16:10:03  f542625
 * Check-In History: changed for myassignments
 * Check-In History:
 * Check-In History: Revision 1.105.4.20  2009/07/03 07:30:58  f542625
 * Check-In History: changed for assignments.
 * Check-In History:
 * Check-In History: Revision 1.105.4.19  2009/07/01 15:12:19  f542625
 * Check-In History: Changed for my assignments
 * Check-In History:
 * Check-In History: Revision 1.105.4.18  2009/06/30 09:47:25  f544490
 * Check-In History: Added changes for My Assignments for MICOS AR 1.4
 * Check-In History:
 * Check-In History: Revision 1.105.4.17  2009/06/26 15:19:16  f542625
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.105.4.16  2009/06/25 09:36:16  f542625
 * Check-In History: added execution HE for display.
 * Check-In History:
 * Check-In History: Revision 1.105.4.15  2009/06/24 16:08:46  f542625
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.105.4.14  2009/06/23 09:54:53  f542625
 * Check-In History: changed for checklist bug
 * Check-In History: changed gto rating in
 * performance task. Check-In History: Check-In History: Revision 1.105.4.10
 * 2009/06/17 07:40:12 f542625 Check-In History: changed for control performance
 * navigation problem Check-In History: Check-In History: Revision 1.105.4.9
 * 2009/06/12 13:11:57 f542625 Check-In History: changed for control scheduling
 * change for executoraor. Check-In History: Check-In History: Revision
 * 1.105.4.8 2009/06/02 09:25:59 f542625 Check-In History: Changed for
 * executorAoR. Check-In History: Check-In History: Revision 1.105.4.7
 * 2009/05/27 15:04:54 f542625 Check-In History: Changed for performance task
 * rating. Check-In History: Check-In History: Revision 1.105.4.6 2009/05/05
 * 12:23:49 f259648 Check-In History: Checkstyle and Enhancement 118 changes
 * Check-In History: Check-In History: Revision 1.105.4.5 2009/04/27 09:16:06
 * g703890 Check-In History: MICIS 1.4 : Deliverables for 27th April Check-In
 * History: Check-In History: Revision 1.25 2009/04/27 08:00:20 155695 Check-In
 * History: *** empty log message *** Check-In History: Check-In History:
 * Revision 1.24 2009/04/27 07:41:08 155695 Check-In History: *** empty log
 * message *** Check-In History: Check-In History: Revision 1.23 2009/04/27
 * 05:52:34 151206 Check-In History: *** empty log message *** Check-In History:
 * Check-In History: Revision 1.105.4.4 2009/04/25 13:07:44 g703890 Check-In
 * History: MICIS 1.4 : Deliverables for 25th April Check-In History: Check-In
 * History: Revision 1.22 2009/04/25 08:45:39 155695 Check-In History:
 * Micos1.4-- Changes for Submitting the Task Check-In History: Check-In
 */