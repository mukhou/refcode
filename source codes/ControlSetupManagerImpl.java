/*******************************************************************************
 * Copyright(c) 2003-2007 CREDIT SUISSE Financial Services. All Rights Reserved.
 * 
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/
package com.csg.cs.micos.comp.impl;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import com.csg.cs.core.base.exception.ExceptionContext;
import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.bean.Accessaor;
import com.csg.cs.micos.bean.Aorowner;
import com.csg.cs.micos.bean.Attachment;
import com.csg.cs.micos.bean.Control;
import com.csg.cs.micos.bean.ControlRemWarEsc;
import com.csg.cs.micos.bean.Controller;
import com.csg.cs.micos.bean.Controlreference;
import com.csg.cs.micos.bean.Controlrfc;
import com.csg.cs.micos.bean.Controltype;
import com.csg.cs.micos.bean.Employee;
import com.csg.cs.micos.bean.Executionschema;
import com.csg.cs.micos.bean.Executor;
import com.csg.cs.micos.bean.Executoraor;
import com.csg.cs.micos.bean.Executorpid;
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
import com.csg.cs.micos.bean.Poschedule;
import com.csg.cs.micos.bean.Poweroption;
import com.csg.cs.micos.bean.Qaschema;
import com.csg.cs.micos.bean.Ratinggroup;
import com.csg.cs.micos.bean.Referencecode;
import com.csg.cs.micos.bean.RemWarEscOtherRecipient;
import com.csg.cs.micos.bean.Rfc;
import com.csg.cs.micos.bean.Schedule;
import com.csg.cs.micos.bean.Scheduleadhocdate;
import com.csg.cs.micos.bean.Schedulewhen;
import com.csg.cs.micos.bean.Setup;
import com.csg.cs.micos.bean.Sharedcontrol;
import com.csg.cs.micos.bean.Signoffschema;
import com.csg.cs.micos.bean.Status;
import com.csg.cs.micos.bean.Step;
import com.csg.cs.micos.bean.Supportedlanguage;
import com.csg.cs.micos.bean.Supportschema;
import com.csg.cs.micos.bean.Translation;
import com.csg.cs.micos.bp.RollingDates;
import com.csg.cs.micos.bp.SchedulerUtils;
import com.csg.cs.micos.comp.ControlSetupManager;
import com.csg.cs.micos.dao.ControlSetupDao;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.util.Constants;
import com.csg.cs.micos.util.DateUtil;
import com.csg.cs.micos.util.IssuesConstants;
import com.csg.cs.micos.util.MicosConstantValues;
import com.csg.cs.micos.util.SystemUtil;
import com.csg.cs.micos.util.IssuesConstants.ReferenceCodeUtil;
import com.csg.cs.micos.util.MicosConstantValues.AttributeOptions;
import com.csg.cs.micos.util.MicosConstantValues.ScheduleType;
import com.csg.cs.micos.util.comparator.ControlComparator;
import com.csg.cs.micos.util.comparator.MicosComparator;
import com.csg.cs.micos.view.AoRView;
import com.csg.cs.micos.view.AttachmentView;
import com.csg.cs.micos.view.BaseView;
import com.csg.cs.micos.view.ControlStepView;
import com.csg.cs.micos.view.ControlView;
import com.csg.cs.micos.view.ControllerView;
import com.csg.cs.micos.view.EmployeeView;
import com.csg.cs.micos.view.HierarchyElementView;
import com.csg.cs.micos.view.LanguageCodeView;
import com.csg.cs.micos.view.LinkView;
import com.csg.cs.micos.view.NotificationView;
import com.csg.cs.micos.view.PowerOptionView;
import com.csg.cs.micos.view.RFCView;
import com.csg.cs.micos.view.ScheduleAdhocDateView;
import com.csg.cs.micos.view.SchedulingView;
import com.csg.cs.micos.view.SchemaView;
import com.csg.jsf.bean.UserBean;

/**
 * Used as a EJB layer for accessing the data for control.
 * 
 * @author Cognizant Technology Solutions
 * @author Last change by $Author: 163376 $
 * @version $Id: ControlSetupManagerImpl.java,v 1.17 2007/11/20 11:39:19 j014664
 *          Exp $
 */
public class ControlSetupManagerImpl extends BaseManagerImpl implements
		ControlSetupManager {

	/**
	 * A logger instance with the fully qualified class name as logger name.
	 */
	private static final Logger LOGGER = LoggerHelper
			.getLogger(ControlSetupManagerImpl.class);

	/**
	 * Default value to display in drop down list of Control Type.
	 */
	private static final String SELECT_ITEM_MSG_FOR_CTRL_TYPE = "Select Control Type";
  
  /**
   * Default value to display in drop down list of Control Status.
   */
  private static final String SELECT_ITEM_MSG_FOR_CTRL_STATUS = "Select Control Status";

	/**
	 * Default value to display in drop down list of AORs.
	 */
	private static final String SELECT_ITEM_MSG_FOR_AOR = "Select AOR";

	/**
   	* Default value to display in drop down list of RFCs.
   	*/
	private static final String SELECT_ITEM_MSG_FOR_RFC = "Select Request for Control";

  	/**
	 * Object of Control Dao.
	 */
	private ControlSetupDao controlSetupDao;

	/**
	 * Holds the Status After Save.
	 */
	String statusAfterSave;

	/**
	 * Holds the Status After Submit.
	 */
	String statusAfterSubmit;
	
	/**
	 * Holds Deactivated ControlId.
	 */
	private Integer deactivatedControlId;

	/**
	 * Holds a reference to the detached Control object.
	 */
	private Control detachedControl;

	/**
	 * Holds a references to the detached PowerOption objects.
	 */
	private List<Poweroption> detachedPowerOptions;

	/**
	 * @return Returns the detachedControl.
	 */
	public Control getDetachedControl() {

		return detachedControl;
	}

	/**
	 * @param detachedControl
	 *            The detachedControl to set.
	 */
	public void setDetachedControl(Control detachedControl) {

		this.detachedControl = detachedControl;
	}

	/**
	 * Set control setup DAO instance
	 * 
	 * @param controlSetupDao
	 *            Control setup DAO
	 */
	public void setControlSetupDao(ControlSetupDao controlSetupDao) {
		this.controlSetupDao = controlSetupDao;
	}

/*	*//**
   * Get all controls for control AOR owner
   * 
   * @param controlAOROwnerId
   *          Control AOR owner ID
   * @param getInactiveControl
   *          check to get all inactive controls
   * @return List of control view
   * @throws MicosException
   *           if failure in Dao
   *//*
	public List<ControlView> getAllControlsForControlAOROwner(
      Integer controlAOROwnerId, boolean getInactiveControl)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"getAllControlsForControlAOROwner");
		List<ControlView> controlList = null;
		try {
			controlList = new ArrayList<ControlView>();
			controlList = getAllControlsForControlViewer(controlAOROwnerId,
          getInactiveControl);
			controlList = ControlComparator.sortControls(controlList);
			
		} catch (MicosException micosException) {
			LOGGER.error("getAllControlsForControlAOROwner",
						micosException);
			throw micosException;
		} finally {
			LOGGER.exiting(getClass().getName(),
						"getAllControlsForControlAOROwner");
			
		}
		return controlList;
	}*/

/*	*//**
	 * Get all controls for control AOR owner and also other controls associated
	 * with the role control-viewer.
	 * 
	 * @param controlAOROwnerId
	 *            Control AOR owner ID
   * @param getInactiveControl
   *          check to get all inactive controls
	 * @return List of control view
	 * @throws MicosException
	 *             if failure in Dao
	 *//*
	private List<ControlView> getAllControlsForControlViewer(
      Integer controlAOROwnerId, boolean getInactiveControl)
      throws MicosException {
		LOGGER.entering(getClass().getName(),"getAllControlsForControlViewer");
		List listOfControlsFromDao = new ArrayList();
		List<ControlView> controlList = null;
		List<Controller> controllersList = null;
		try {
			
			controllersList = controlSetupDao.getControllerList(controlAOROwnerId);
     
			listOfControlsFromDao = controlSetupDao.getAllControlsForControlOverview(
          controlAOROwnerId, getInactiveControl);

			if (!SystemUtil.isNull(listOfControlsFromDao)) {
				controlList = new ArrayList<ControlView>();
				Iterator itControls = listOfControlsFromDao.iterator();
				while (itControls.hasNext()) {
					Control control = (Control) itControls.next();
					if (null != control) {
						ControlView controlView = new ControlView();
						if(!isContainsControl(controlList, control.getControlid())) {
							controlView.setId(control.getControlid());
              				controlView.setMasterControlId(control.getMastercontrolid());
							Status status = control.getStatus();
							if (status != null) {
                controlView.setControlStatus(status.getStatusdescription());
                
                 * Disable Edit button for Controls which are in status of
                 * Waiting for Approval or Waiting for ReApproval
                 
                if (status.getStatuscode().equalsIgnoreCase(
                    MicosConstantValues.Status.WAITING_FOR_APPROVAL)
                    || status.getStatuscode().equalsIgnoreCase(
                        MicosConstantValues.Status.WAITING_FOR_REAPPROVAL)) {
                  controlView.setShowEdit(false);
                } else {
                  controlView.setShowEdit(true);
                }
                
                 * Business requested on 7/10/2008 to disable edit and 
                 * delete for inactive controls
                  
                if (status.getStatuscode().equalsIgnoreCase(
                    MicosConstantValues.Status.INACTIVE)) {
                  controlView.setShowEdit(false);
                  controlView.setShowDelete(false);
                }
                
                 * Business requested on 7/10/2008 to disable delete button for
                 * all statuses except "Setup" and "Modified"
                 
                if (!status.getStatuscode().equalsIgnoreCase(
                    MicosConstantValues.Status.SETUP)
                    && !status.getStatuscode().equalsIgnoreCase(
                        MicosConstantValues.Status.MODIFIED)) {
                  controlView.setShowDelete(false);
                }                                        
							}
	            
               * Disable delete button incase deactivated Control Id exists
               
              if (control.getDeactivatedcontrol() != null) {
                //controlView.setShowDelete(false);
              }
              // If the Control end date is less than today then do not show 
              // the delete button
              Date controlEndDate = control.getEnddate();
              Date today = new Date();
              if(controlEndDate != null && controlEndDate.before(today)) {
                controlView.setShowEdit(false);
              }
							SimpleDateFormat date = new SimpleDateFormat(
									"dd-MMM-yyyy");
							String eDate = date.format(control.getEnddate());
							if (null != eDate) {
								controlView.setControlEndDate(eDate);
							}
							String cDate = date.format(control.getCreatedate());
							if (null != cDate) {
								controlView.setCreateDate(cDate);
							}
							Controltype controltype = control.getControltype();
							if (null != controltype) {
								controlView.setControlType(controltype.getName());
							}
							controlView
									.setControlTitleObj(populateControlTitle(control
											.getControlid()));
							Aorowner aorOwner = control.getAorowner();
							if (null != aorOwner) {
								Employee employeeByAorOwner = aorOwner
										.getEmployeeByAorowner();
								if (null != employeeByAorOwner) {
									controlView
											.setControlAorOwnerName(employeeByAorOwner
													.getEmployeeName());
									controlView.setControlAorOwnerId(aorOwner
											.getAorownerid());
									controlView.setControlAORPid(employeeByAorOwner
											.getPid());
									if (!(employeeByAorOwner.getEmployeeid())
											.equals(controlAOROwnerId)) {
										controlView.setControlOfControlOwner(false);
									}
					        // Check for Controller to view his Controls
                  if (controllersList != null) {
                    Iterator iteratorcontroller = controllersList.iterator();
                    while (iteratorcontroller.hasNext()) {
                      Controller aController = (Controller)iteratorcontroller
                          .next();
                      if (aController.getAorowner().getAorownerid().equals(
                          aorOwner.getAorownerid())) {
                        controlView.setControlOfControlOwner(true);
                        break;
                      }
                    }
                  }   
								}
							}
							controlList.add(controlView);
						}
					}
				}
			}
		} catch (MicosException micosException) {
			LOGGER.error("getAllControlsForControlViewer",
						micosException);
			throw micosException;
		} finally {
			LOGGER.exiting(getClass().getName(),
						"getAllControlsForControlViewer");
			
		}

		return controlList;

	}*/
	
/*	*//**
	 * Used to check the duplicate control in control list.
	 * 
	 * @param controls ControlView
	 * @param controlId Control Id
	 * @return true, if control is already in the contol list.
	 *//*
	private boolean isContainsControl(List<ControlView> controls,Integer controlId){
		LOGGER.entering(getClass().getName(),"isContainsControl");
		boolean result = false;
		for(ControlView control:controls) {
			if(control.getId().intValue() == controlId.intValue()) {
				result = true;
				break;
			}
		}
		return result;
	}*/

	/**
	 * populateGoalTitle
	 * 
	 * @param controlId
	 *            v
	 * @return LanguageCodeView
	 * @throws MicosException
	 *             v
	 */
	private LanguageCodeView[] populateControlTitle(Integer controlId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateControlTitle");
		List langCodes = SystemUtil.getAllLanguageCode();
		LanguageCodeView[] langGoalTitle = new LanguageCodeView[langCodes
				.size()];
		Iterator itr = langCodes.iterator();
		String langCode = "";
		int count = 0;
		while (itr.hasNext()) {
			langCode = (String) itr.next();
			langGoalTitle[count] = new LanguageCodeView();
			langGoalTitle[count].setLanguageType(langCode);
			langGoalTitle[count].setValue(SystemUtil
					.getClobValueAsString(controlSetupDao
							.findControlTitleForControl(langCode, controlId)));
			count++;
		}
		LOGGER.exiting(getClass().getName(),"populateControlTitle");
		return langGoalTitle;
	}

	/**
	 * populateGoalTitle
	 * 
	 * @param controlId
	 *            v
	 * @return LanguageCodeView
	 * @throws MicosException
	 *             v
	 */
	public LanguageCodeView[] populateControlDescription(Integer controlId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateControlDescription");
		List langCodes = SystemUtil.getAllLanguageCode();
		LanguageCodeView[] langGoalTitle = new LanguageCodeView[langCodes
				.size()];
		Iterator itr = langCodes.iterator();
		String langCode = "";
		int count = 0;
		while (itr.hasNext()) {
			langCode = (String) itr.next();
			langGoalTitle[count] = new LanguageCodeView();
			langGoalTitle[count].setLanguageType(langCode);
			langGoalTitle[count].setValue(SystemUtil
					.getClobValueAsString(controlSetupDao
							.findControlDescriptionForControl(langCode,
									controlId)));
			count++;
		}
		LOGGER.exiting(getClass().getName(),"populateControlDescription");
		return langGoalTitle;
	}

	/**
	 * populateGoalTitle
	 * 
	 * @param controlId
	 *            v
	 * @return LanguageCodeView
	 * @throws MicosException
	 *             v
	 */
	public LanguageCodeView[] populateControlDetailDescription(Integer controlId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateControlDetailDescription");
		List langCodes = SystemUtil.getAllLanguageCode();
		LanguageCodeView[] langGoalTitle = new LanguageCodeView[langCodes
				.size()];
		Iterator itr = langCodes.iterator();
		String langCode = "";
		int count = 0;
		while (itr.hasNext()) {
			langCode = (String) itr.next();
			langGoalTitle[count] = new LanguageCodeView();
			langGoalTitle[count].setLanguageType(langCode);
			langGoalTitle[count].setValue(SystemUtil
					.getClobValueAsString(controlSetupDao
							.findControlDetailedDescriptionForControl(langCode,
									controlId)));
			count++;
		}
		LOGGER.exiting(getClass().getName(),"populateControlDetailDescription");
		return langGoalTitle;
	}

	/**
	 * Get all controls for control AOR owner
	 * 
	 * @param controlAOROwnerId
	 *            Control AOR owner ID
	 * @param languageCode
	 *            Language Code
	 * @return List of control view
	 * @throws MicosException
	 *             if failure in Dao
	 */
	public List<ControlView> getAllSharedControlsForControlAOROwner(
			Integer controlAOROwnerId, String languageCode)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"getAllSharedControlsForControlAOROwner");
		List listOfControlsFromDao = null;
		List<ControlView> controlList = null;
		try {
			listOfControlsFromDao = controlSetupDao
					.getAllSharedControlsForControlAOROwner(controlAOROwnerId);
			if (!SystemUtil.isNull(listOfControlsFromDao)) {
				controlList = new ArrayList<ControlView>();
				Iterator itControls = listOfControlsFromDao.iterator();
				while (itControls.hasNext()) {
					Sharedcontrol sharedcontrol = (Sharedcontrol) itControls
							.next();
					if (null != sharedcontrol) {
						ControlView controlView = new ControlView();
						controlView.setId(sharedcontrol.getControlrfc()
								.getControl().getControlid());
            			controlView.setMasterControlId(sharedcontrol.getControlrfc()
                			.getControl().getMastercontrolid());
						controlView.setControlIdForShare(sharedcontrol
								.getSharedcontrolid());
						Status status = sharedcontrol.getStatus();
						if (null != status) {
							controlView.setControlStatus(status
									.getStatusdescription());
						}
						SimpleDateFormat date = new SimpleDateFormat(
								"dd-MMM-yyyy");
						String eDate = date.format(sharedcontrol
								.getControlrfc().getControl().getEnddate());
						if (null != eDate) {
							controlView.setControlEndDate(eDate);
						}

						Controltype controltype = sharedcontrol.getControlrfc()
								.getControl().getControltype();
						if (null != controltype) {
							controlView.setControlType(controltype.getName());
						}
						controlView
								.setControlTitleObj(populateControlTitle(controlView
										.getId()));
						Aorowner aorOwner = sharedcontrol.getAorowner();
						if (null != aorOwner) {
							Employee employeeByAorOwner = aorOwner
									.getEmployeeByAorowner();
							if (null != employeeByAorOwner) {
								controlView
										.setControlAorOwnerName(employeeByAorOwner
												.getEmployeeName());
								controlView.setControlAorOwnerId(aorOwner
										.getAorownerid());
							}
						}
						controlList.add(controlView);
					}
				}
			}
		} catch (MicosException micosException) {
			LOGGER.error("getAllSharedControlsForControlAOROwner",
						micosException);
			throw micosException;
		} finally {
			LOGGER.exiting(getClass().getName(),
						"getAllSharedControlsForControlAOROwner");
			
		}
		return controlList;
	}

	/**
	 * Provides Control Type.
	 * 
	 * @return controltype Controltype object
	 * @throws
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<SelectItem> getControlTypes() throws MicosException {
		LOGGER.entering(getClass().getName(), "getControlTypes");
		List<SelectItem> controlTypes = new ArrayList<SelectItem>();
		controlTypes.add(new SelectItem("-1", SELECT_ITEM_MSG_FOR_CTRL_TYPE));
		List<Controltype> listOfControlType = null;
		Controltype controltype = null;

		try {

			listOfControlType = controlSetupDao.getAllControlTypes();

			if (!SystemUtil.isNull(listOfControlType)) {
				Iterator itRole = listOfControlType.iterator();
				while (itRole.hasNext()) {
					controltype = (Controltype) itRole.next();
					controltype.getControltypeid();
					controltype.getName();

					controlTypes.add(new SelectItem(controltype
							.getControltypeid().toString(), controltype
							.getName()));
				}
			}
		} catch (MicosException micosException) {
			LOGGER.error("getControlTypes", micosException);
			throw micosException;
		} finally {
			LOGGER.exiting(getClass().getName(), "getControlTypes");
			
		}
		return controlTypes;
	}
  
  /**
   * Get all controls for Control
   * 
   * @param controlID
   *            ControlID
   * @param controlAorOwner
   *            ControlAOR Owner emp ID
   * @param controlTitle
   *            Control Title
   * @param languageCode
   *            Language Code
   * @return List List of control view
   * @throws MicosException
   *             Micos Exception
   */

  public List<ControlView> searchControl(Integer masterControlID, Integer controlID, String controlReference,
      Integer controlAorOwner, String controlTitle, String languageCode)
      throws MicosException {
    LOGGER.entering(getClass().getName(),"searchControl");
    List<ControlView> listOfControls = null;
    List<Object[]> controlList = controlSetupDao.searchControl(masterControlID, controlID, controlReference,
        controlAorOwner, controlTitle, languageCode);
    if (!SystemUtil.isNull(controlList)) {
      listOfControls = new ArrayList<ControlView>();
      ControlView controlView = null;

      for (int itr = 0; itr < controlList.size(); itr++) {
        controlView = new ControlView();
        Object[] rowVal = controlList.get(itr);
        BigDecimal bigPk = (BigDecimal)rowVal[0];
        controlView.setId(bigPk.intValue());
        controlView.setMasterControlId(((BigDecimal)rowVal[8]).intValue());
        controlView
            .setControlTitleObj(populateControlTitle(controlView.getId()));
        controlView.setControlType((String)rowVal[2]);
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
        String eDate = date.format(rowVal[3]);
        controlView.setControlEndDate(eDate);
        String createDate = date.format(rowVal[7]);
        controlView.setCreateDate(createDate);
        controlView.setControlStatus((String)rowVal[4]);
        controlView.setControlAorOwnerName((String)rowVal[5]);
        BigDecimal bigdecID = (BigDecimal)rowVal[6];
        controlView.setControlAorOwnerId(bigdecID.intValue());
        listOfControls.add(controlView);
      }
    }
    LOGGER.exiting(getClass().getName(), "searchControl");
    return listOfControls;
  }

  /**
   * Get all controls for Control
   * 
   * @param masterControlID
   *          Integer
   * @param controlID
   *          ControlID
   * @param controlReference
   *          String
   * @param controlAorOwnerPID
   *          ControlAOR Owner Name
   * @param controlTitle
   *          Control Title
   * @param description
   *          Control Description
   * @param detaildDescription
   *          Control Detailed Description
   * @param tools
   *          Tools
   * @param scopeAoR
   *          ScopeAoR
   * @param selectedControlTypeId
   *          boolean
   * @param languageCode
   *          Language Code
   * @param selectSharedControl
   *          control type id
   * @param fromPage
   *          String
   * @param selectedControlStatus
   *          String
   * @return List List of control view
   * @throws MicosException
   *           Micos Exception
   */
  public List<ControlView> searchControlForControlCriteria(
      Integer masterControlID, Integer controlID, String controlReference,
      String controlAorOwnerPID, String controlTitle, String description,
      String detaildDescription, String tools, String scopeAoR,
      Integer selectedControlTypeId, String languageCode,
      boolean selectSharedControl, String fromPage, String selectedControlStatus)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "searchControlForControlCriteria");
    List<ControlView> listOfControls = null;
    Integer controllerEmpId = null;
    MicosUserBean user = (MicosUserBean)UserBean.getUserBean();
    Integer loggedInUser = user.getWorkingAsUser().getEmployeeid();
    List<Object[]> controlList = controlSetupDao
        .searchControlForControlCriteria(masterControlID, controlID,
            controlReference, controlAorOwnerPID, controlTitle, description,
            detaildDescription, tools, scopeAoR, selectedControlTypeId,
            languageCode, selectSharedControl, fromPage, selectedControlStatus);
    if (!SystemUtil.isNull(controlList)) {
      listOfControls = new ArrayList<ControlView>();
      ControlView controlView = null;
      for (int itr = 0; itr < controlList.size(); itr++) {
        controlView = new ControlView();
        Object[] rowVal = controlList.get(itr);
        BigDecimal bigPk = (BigDecimal)rowVal[0];
        controlView.setId(bigPk.intValue());
        BigDecimal masterId = (BigDecimal)rowVal[10];
        controlView.setMasterControlId(masterId.intValue());
        controlView
            .setControlTitleObj(populateControlTitle(controlView.getId()));
        controlView.setControlType((String)rowVal[2]);
        controlView.setControlStatus((String)rowVal[3]);
        if (MicosConstantValues.Status.WAITING_FOR_APPROVAL
            .equalsIgnoreCase((String)rowVal[4])
            || MicosConstantValues.Status.WAITING_FOR_REAPPROVAL
                .equalsIgnoreCase((String)rowVal[4])) {
          controlView.setShowEdit(false);
        } else {
          controlView.setShowEdit(true);
        }
        /*
         * Business requested on 7/10/2008 to disable edit and delete for
         * inactive controls
         */
        if (MicosConstantValues.Status.INACTIVE
            .equalsIgnoreCase((String)rowVal[4])) {
          controlView.setShowEdit(false);
          controlView.setShowDelete(false);
        }
        /*
         * Business requested on 7/10/2008 to disable delete button for all
         * statuses except "Setup" and "Modified"
         */
        if (!MicosConstantValues.Status.SETUP
            .equalsIgnoreCase((String)rowVal[4])
            && !MicosConstantValues.Status.MODIFIED
                .equalsIgnoreCase((String)rowVal[4])) {
          controlView.setShowDelete(false);
        }
        /*
         * Disable delete button incase deactivated Control Id exists
         */
        if (rowVal[9] != null) {
          // controlView.setShowDelete(false);
        }
        controlView.setControlOfControlOwner(false);
        controlView.setControlAorOwnerName((String)rowVal[5]);
        BigDecimal bigPkcnt = (BigDecimal)rowVal[6];
        Employee controlOwner = controlSetupDao.loadEmployee(bigPkcnt
            .intValue());
        controlView.setControlAORPid(controlOwner.getPid());
        if (("launchcontrol").equalsIgnoreCase(fromPage)) {
          controllerEmpId = controlSetupDao.checkValidController(loggedInUser,
              controlView.getId(), fromPage);
          if (controllerEmpId != null && loggedInUser.equals(controllerEmpId)) {
            controlView.setControlOfControlOwner(true);
          }
        } else {
          if (loggedInUser.compareTo(bigPkcnt.intValue()) == 0) {
            controlView.setControlOfControlOwner(true);
          } else {
            controllerEmpId = controlSetupDao.checkValidController(
                loggedInUser, controlView.getId(), fromPage);
            if (controllerEmpId != null && loggedInUser.equals(controllerEmpId)) {
              controlView.setControlOfControlOwner(true);
            }
          }
        }
        // If the Control end date is less than today then do not show
        // the delete button
        Date controlEndDate = (Date)rowVal[8];
        Date today = new Date();
        if (controlEndDate != null && controlEndDate.before(today)) {
          controlView.setShowEdit(false);
        }
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
        String eDate = date.format((Date)rowVal[8]);
        controlView.setControlEndDate(eDate);
        String createDate = date.format((Date)rowVal[7]);
        controlView.setCreateDate(createDate);
        // BigDecimal bigdecID = (BigDecimal)rowVal[6];
        // controlView.setControlAorOwnerId(bigdecID.intValue());
        listOfControls.add(controlView);
      }
    }
    LOGGER.exiting(getClass().getName(), "searchControlForControlCriteria");
    listOfControls = ControlComparator.sortControls(listOfControls);
    return listOfControls;
  }

	/**
   * Provides all ControlAORs for logged in UserId.
   * 
   * @param loggedInUserId
   *          logged in user id
   * @return controlAors list of controlAORs
   * @throws MicosException
   *           Micos Exception
   */
	public List<SelectItem> getAllControlAORs(Integer loggedInUserId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "getAllControlAORs");
		List<Object[]> aorownerList = null;
		List<SelectItem> controlAORs = new ArrayList<SelectItem>();
		controlAORs.add(new SelectItem("-1", SELECT_ITEM_MSG_FOR_AOR));
		aorownerList = controlSetupDao.getAorOwnersList(loggedInUserId);

	    if (!SystemUtil.isNull(aorownerList)) {
	      for (int itr = 0; itr < aorownerList.size(); itr++) {
	        Object[] rowVal = aorownerList.get(itr);
	        BigDecimal bigD = (BigDecimal)rowVal[0];
	        controlAORs.add(new SelectItem(bigD.toString(), (String)rowVal[1]
	            + " - " + (String)rowVal[2] + " - " + (String)rowVal[3] + " "
	            + (String)rowVal[4]));
	      }
	    }
   
	    LOGGER.exiting(getClass().getName(), "getAllControlAORs");
	    return controlAORs;
	}

	/**
	 * Provides all RFCs for AOR.
	 * 
	 * @param loggedInUserId
	 *            logged in usaer id
	 * @param languageCode
	 *            Code of the language that uesr has selected
	 * @return list of RFC views
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<RFCView> getAllRFCForAOR(Integer loggedInUserId,
			String languageCode) throws MicosException {
		LOGGER.entering(getClass().getName(), "getAllRFCForAOR");
		List<RFCView> rfcviewList = new ArrayList<RFCView>();
		RFCView rfcView = null;
		List<BigDecimal> listOfRFCId = null;

    listOfRFCId = controlSetupDao.getRFCsForAOR(loggedInUserId);

    if (!SystemUtil.isNull(listOfRFCId)) {

      for (int i = 0; i < listOfRFCId.size(); i++) {
        rfcView = new RFCView();
        rfcView.setId(new Integer(listOfRFCId.get(i).intValue()));
        rfcView.setControlObjTitleObj(populateControlObjTitle(listOfRFCId
            .get(i).intValue()));
        rfcviewList.add(rfcView);
      }
    }
    LOGGER.exiting(getClass().getName(), "getAllRFCForAOR");
    return rfcviewList;
  }

	/**
	 * CR_8 populate PowerOptions
	 * 
	 * @param controlId  Control Id
	 * @param languageCode User preffered language
	 * @return stepViewList 
	 * @throws MicosException
	 *             the MicosException
	 */
	public List<ControlStepView> populatePowerOptions(Integer controlId,
			String languageCode) throws MicosException {
		LOGGER.entering(getClass().getName(),"populatePowerOptions");
		List<Poweroption> powerOptionList = null;
		List<ControlStepView> stepViewList = new ArrayList<ControlStepView>();
		try {
			powerOptionList = controlSetupDao.getPowerOptionsForControlID(
					controlId, languageCode);

			for (Poweroption poweroption : powerOptionList) {
				if (poweroption == null) {
					continue;
				}
				ControlStepView stepView = new ControlStepView();
				PowerOptionView powerOptionView = new PowerOptionView();
				stepView.setId(poweroption.getPoweroptionid());
				stepView.setStepTitle(poweroption.getTitle());
				stepView.setStepStartDate(poweroption.getCoverageperiodstart());
				stepView.setStepEndDate(poweroption.getCoverageperiodend());
				stepView.setPerformanceRatinggroup(poweroption
						.getExecutionratinggroup().getRatinggroupid()
						.toString());
				stepView.setSignoffRatinggroup(poweroption
						.getSignoffratinggroup().getRatinggroupid().toString());
				if (poweroption.getHierarchyelement() != null) {
					powerOptionView.setPerAoRName(poweroption.getHierarchyelement().getHierarchy().getName()
							+ " - " + poweroption.getHierarchyelement().getName());
					
					powerOptionView.setHierarchyElementId(poweroption.getHierarchyelement()
							.getHierarchyelementid());
				}
				if (poweroption.getPerformancerole() != null) {
					powerOptionView.setRolesForAorName(poweroption
							.getPerformancerole().getHierarchyroleid()
							.toString());
          powerOptionView.setRolesForAor(poweroption.getPerformancerole()
              .getHierarchyroleid());
				}
				if (poweroption.getSignoffrole() != null) {
					powerOptionView.setSignOffRoleName(poweroption
							.getSignoffrole().getHierarchyroleid().toString());          
          powerOptionView.setSignOffRole(poweroption.getSignoffrole()
              .getHierarchyroleid());
				}
				if (poweroption.getLevelsup() != null) {
					powerOptionView.setLevelsUp(poweroption.getLevelsup());
				}
				if (poweroption.getLevelsdown() != null) {
					powerOptionView.setLevelsDown(poweroption.getLevelsdown());
				}
				stepView.setPowerOptionView(powerOptionView);
				stepView
						.setCheckSupport(poweroption
								.getSupport()
								.equalsIgnoreCase(
										MicosConstantValues.CheckSchemaState.ENABLED_STATE));
				stepView.setCheckQA(poweroption.getQa().equalsIgnoreCase(
						MicosConstantValues.CheckSchemaState.ENABLED_STATE));
				if (stepView.getCheckSupport()) {
					SchemaView supportSchemaView = new SchemaView();
					Supportschema supportSchema = poweroption.getSupportschema();
					supportSchemaView.setId(supportSchema.getSupportschemaid());
					if (supportSchema != null) {
						Set<Executor> executorSet = supportSchema.getExecutors();
						if (!SystemUtil.isNull(executorSet)) {
							for (Executor exec : executorSet) {
								supportSchemaView.setSelectionViaPIDView(populateExecutorPIDView(exec.getExecutorpids()));
								supportSchemaView.setSelectionViaHierarchyView(populateExecutorAorView(exec.getExecutoraors()));
							}
						}
					}
					stepView.setSupportSchemaView(supportSchemaView);
				}

				if (stepView.getCheckQA()) {
					SchemaView qaSchemaView = new SchemaView();
					Qaschema qaSchema = poweroption.getQaschema();
					qaSchemaView.setId(qaSchema.getQaschema());
					if (qaSchema != null) {
						Set<Executor> executorSet = qaSchema.getExecutors();
						if (!SystemUtil.isNull(executorSet)) {
							for (Executor exec : executorSet) {
								qaSchemaView.setSelectionViaPIDView(populateExecutorPIDView(exec.getExecutorpids()));
								qaSchemaView.setSelectionViaHierarchyView(populateExecutorAorView(exec.getExecutoraors()));
							}
						}
					}
					stepView.setQaSchemaView(qaSchemaView);
				}

				// population of scheduling view
				if (poweroption.getPoschedules() != null) {
					Set<Poschedule> schedules = poweroption.getPoschedules();
					for (Poschedule schedule : schedules) {
						SchedulingView scheduleView = new SchedulingView();
						scheduleView.setId(schedule.getPoscheduleid());
						scheduleView.setScheduleStartDate(schedule.getStartdate());
						if (schedule.getEnddate() != null) {
							scheduleView.setScheduleEndDate(schedule.getEnddate());
						}
						scheduleView.setSchedulePeriodicAdhoc(schedule
								.getPeriodicadhoc());
						if (schedule.getPeriodicadhoc().equalsIgnoreCase(
								MicosConstantValues.ScheduleType.PERIODIC)) {
							if (schedule.getPatternqty() != null) {
								scheduleView.setSchedulePatternQty(schedule
										.getPatternqty().intValue());
							}
							if (schedule.getPeriodtypeByPatternfrequency() != null) {
								scheduleView.setSchedulePatternFreq(schedule
										.getPeriodtypeByPatternfrequency()
										.getPeriodtypeid().toString());
							}
							
							if(schedule.getMonday() != null){
								scheduleView.setMonday(schedule.getMonday());
							}
							if(schedule.getTuesday()!= null){
								scheduleView.setTuesday(schedule.getTuesday());
							}
							if(schedule.getWednesday()!= null){
								scheduleView.setWednesday(schedule.getWednesday());
							}
							if(schedule.getThursday()!= null){
								scheduleView.setThursday(schedule.getThursday());
							}
							if(schedule.getFriday()!= null){
								scheduleView.setFriday(schedule.getFriday());
							}
							if(schedule.getSaturday()!= null){
								scheduleView.setSaturday(schedule.getSaturday());
							}
							if(schedule.getSunday()!= null){
								scheduleView.setSunday(schedule.getSunday());
							}
						} else if (schedule.getPeriodicadhoc()
								.equalsIgnoreCase(
										MicosConstantValues.ScheduleType.ADHOC)) {
							Set<Scheduleadhocdate> adhocDateSet = schedule
									.getPoscheduleadhocdates();

							if (adhocDateSet != null) {
								List<ScheduleAdhocDateView> adhocDateViewList = new ArrayList<ScheduleAdhocDateView>();
								for (Scheduleadhocdate date : adhocDateSet) {
									ScheduleAdhocDateView adhocDateView = new ScheduleAdhocDateView();
									adhocDateView.setScheduleAdhocDate(DateUtil.getFormattedDate(date.getAdhocdate(), Constants.OUTPUT_DATE_FORMAT));
									adhocDateView.setTempId(date.getScheduleadhocdatesid());
									adhocDateView.setId(date.getScheduleadhocdatesid());
									adhocDateViewList.add(adhocDateView);
								}
								scheduleView
										.setScheduleAdhocDateViewList(adhocDateViewList);
							}
						}

						// fields for Support
						if (schedule.getSupportinitiateqty() != null) {
							scheduleView.setScheduleSupportInitiateQty(schedule
									.getSupportinitiateqty().intValue());
						}
						if (schedule.getPeriodtypeBySupportinitiateperiodtype() != null) {
							scheduleView
									.setScheduleSupportInitiatePeriodType(schedule
											.getPeriodtypeBySupportinitiateperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getSupportdurationqty() != null) {
							scheduleView.setScheduleSupportDurationQty(schedule
									.getSupportdurationqty().intValue());
						}
						if (schedule.getPeriodtypeBySupportdurationperiodtype() != null) {
							scheduleView
									.setScheduleSupportDurationPeriodType(schedule
											.getPeriodtypeBySupportdurationperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getSchedulewhenBySupportschedulewhen() != null) {
							scheduleView
									.setScheduleSupportScheduleWhen(schedule
											.getSchedulewhenBySupportschedulewhen()
											.getSchedulewhenid().toString());
						}
						// fields for Exec
						if (schedule.getExecutioninitiateqty() != null) {
							scheduleView.setScheduleExecInitQty(schedule
									.getExecutioninitiateqty().intValue());
						}
						if (schedule
								.getPeriodtypeByExecutorinitiateperiodtype() != null) {
							scheduleView
									.setScheduleExecInitPeriodType(schedule
											.getPeriodtypeByExecutorinitiateperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getExecutiondurationqty() != null) {
							scheduleView.setScheduleExecDurationQty(schedule
									.getExecutiondurationqty().intValue());
						}
						if (schedule
								.getPeriodtypeByExecutiondurationperiodtype() != null) {
							scheduleView
									.setScheduleExecPeriodType(schedule
											.getPeriodtypeByExecutiondurationperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getSchedulewhenByExecutionschedulewhen() != null) {
							scheduleView.setScheduleExecWhen(schedule
									.getSchedulewhenByExecutionschedulewhen()
									.getSchedulewhenid().toString());
						}
						// fields for QA
						if (schedule.getQainitiateqty() != null) {
							scheduleView.setScheduleQAInitQty(schedule
									.getQainitiateqty().intValue());
						}
						if (schedule.getPeriodtypeByQainitiateperiodtype() != null) {
							scheduleView.setScheduleQAInitPeriodType(schedule
									.getPeriodtypeByQainitiateperiodtype()
									.getPeriodtypeid().toString());
						}
						if (schedule.getQadurationqty() != null) {
							scheduleView.setScheduleQADurationQty(schedule
									.getQadurationqty().intValue());
						}
						if (schedule.getPeriodtypeByQadurationperiodtype() != null) {
							scheduleView.setScheduleQAPeriodType(schedule
									.getPeriodtypeByQadurationperiodtype()
									.getPeriodtypeid().toString());
						}
						if (schedule.getSchedulewhenByQaschedulewhen() != null) {
							scheduleView.setScheduleQAWhen(schedule
									.getSchedulewhenByQaschedulewhen()
									.getSchedulewhenid().toString());
						}
						// fields for Signoff
						if (schedule.getSignoffinitiateqty() != null) {
							scheduleView.setScheduleSignoffInitQty(schedule
									.getSignoffinitiateqty().intValue());
						}
						if (schedule.getPeriodtypeBySignoffinitiateperiodtype() != null) {
							scheduleView
									.setScheduleSignoffInitPeriodType(schedule
											.getPeriodtypeBySignoffinitiateperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getSignoffdurationqty() != null) {
							scheduleView.setScheduleSignoffDurationQty(schedule
									.getSignoffdurationqty().intValue());
						}
						if (schedule.getPeriodtypeBySignoffdurationperiodtype() != null) {
							scheduleView.setScheduleSignoffPeriodType(schedule
									.getPeriodtypeBySignoffdurationperiodtype()
									.getPeriodtypeid().toString());
						}
						if (schedule.getSchedulewhenBySignoffschedulewhen() != null) {
							scheduleView.setScheduleSignoffWhen(schedule
									.getSchedulewhenBySignoffschedulewhen()
									.getSchedulewhenid().toString());
						}
						stepView.setSchedulingView(scheduleView);
						//Populate the power option
						populateNotification(null, poweroption, scheduleView);
					}
				}

				stepViewList.add(stepView);
			}

			// set a references to the powerOptions
			setDetachedPowerOptions(powerOptionList);
		} catch (MicosException micosException) {
      LOGGER.error("populatePowerOptions", micosException);
      throw micosException;
		} finally {
      LOGGER.entering(getClass().getName(), "populatePowerOptions");			
		}
		return stepViewList;
	}

	/**
	 * Control Properties for control Id.
	 * 
	 * @param controlid
	 *            control id
	 * @param copyControlFlag
	 *            the boolean
	 * @param languageCode
	 *            Code of the language that user has selected
	 * @return controlpropertiesview Control Properties View
	 * @throws MicosException
	 *             Micos Exception
	 * 
	 */
	public ControlView getControlPropertiesForControlID(String languageCode,
			Integer controlid, boolean copyControlFlag) throws MicosException {
		LOGGER.entering(getClass().getName(),"getControlPropertiesForControlID");
		ControlView controlpropertiesview = null;
		Control control = null;
		String controlaorname = null;
		try {
			control = controlSetupDao.getControlPropertiesForControlID(
					controlid, languageCode);
			if (control != null) {
        // Set copy flag to check while save
        control.setCopyControl(copyControlFlag);
				controlpropertiesview = new ControlView();
        controlpropertiesview.setId(control.getControlid());
        controlpropertiesview.setControlReference(control.getControlreference());
        controlpropertiesview.setMasterControlId(control.getMastercontrolid());
				controlpropertiesview.setControlType(control.getControltype()
						.getControltypeid().toString());
				if(control.getDeactivatedcontrol()!= null){
					controlpropertiesview.setDeactivatedControlId(control.getDeactivatedcontrol().getControlid());
				}
				controlpropertiesview.setControlAorOwnerId(control
						.getAorowner().getAorownerid());
				// Control Status
        controlpropertiesview.setControlStatus(control.getStatus().getStatusdescription());
        controlpropertiesview.setControlStatusCode(control.getStatus().getStatuscode());
        // Last updated date
        controlpropertiesview.setLastUpdateDate(DateUtil.getFormattedDate(
            control.getLastupdate(), Constants.OUTPUT_TIMESTAMP_FORMAT));
				Set<Controlrfc> controlrfcs = control.getControlrfcs();
				Controlrfc controlrfc = null;
				Integer primaryRfcId = null;
				Rfc rfc = null;
				List<RFCView> listofsharedRFCs = new ArrayList<RFCView>();
				if (!SystemUtil.isNull(controlrfcs)) {
					Iterator itcontrolrfc = controlrfcs.iterator();
					while (itcontrolrfc.hasNext()) {
						controlrfc = (Controlrfc) itcontrolrfc.next();
						rfc = controlrfc.getRfc();
						if (rfc != null) {
							if (controlSetupDao.notInSharedControl(controlrfc
									.getControlrfcid())) {
								primaryRfcId = rfc.getRfcid();
							} else {
								RFCView selectedRfcView = new RFCView();
								selectedRfcView.setTempId(controlrfc
										.getControlrfcid());
								selectedRfcView.setId(rfc.getRfcid());
								selectedRfcView
										.setControlObjTitleObj(populateControlObjTitle(rfc
												.getRfcid()));
								selectedRfcView.setScopeAoR(rfc.getAorowner()
										.getEmployeeByAorowner()
										.getEmployeeName());
								listofsharedRFCs.add(selectedRfcView);
							}
						}
					}
				}

				controlpropertiesview.setControlRFCs(listofsharedRFCs);

				RFCView selectedRfcView = new RFCView();
				String rfcName = SystemUtil
						.getClobValueAsString(controlSetupDao
								.findControlObjTitleForRFC(languageCode,
										primaryRfcId));
        if (primaryRfcId != null) {
          selectedRfcView
              .setControlObjTitleObj(populateControlObjTitle(primaryRfcId
                  .intValue()));
          Rfc mainRfc = controlSetupDao.loadRfc(primaryRfcId);
          selectedRfcView.setScopeAoR(mainRfc.getAorowner()
              .getEmployeeByAorowner().getEmployeeName());
          // Populating AoR Name for RFC
          EmployeeView empView = new EmployeeView();
          empView.setHierarchyElementName(mainRfc.getAorowner()
              .getHierarchyelement().getName());
          selectedRfcView.setEmployeeView(empView);
          // Populating Goal Title
          Integer goalId = controlSetupDao.getGoalId(primaryRfcId);
          if (goalId != null) {
            selectedRfcView.setGoalTitleObj(populateGoalTitle(goalId));
          }
        }
        selectedRfcView.setId(primaryRfcId);
				controlpropertiesview.setRfcId(primaryRfcId);
				controlpropertiesview.setRfcName(rfcName);
				controlpropertiesview.setRfcView(selectedRfcView);

				controlpropertiesview.setValidUntil(control.getEnddate());

				Aorowner aorOwner = control.getAorowner();
				if (null != aorOwner) {
					Employee employeeByAorOwner = aorOwner
							.getEmployeeByAorowner();
					if (null != employeeByAorOwner) {
						controlaorname = employeeByAorOwner.getLastname() + " "
								+ employeeByAorOwner.getFirstname() + "("
								+ employeeByAorOwner.getInstradierung() + ")";
						controlpropertiesview
								.setControlAorOwnerName(controlaorname);

						controlpropertiesview
								.setControlAORPid(employeeByAorOwner.getPid());
					}
				}
				controlpropertiesview
						.setControlTitleObj(populateControlTitle(controlid));
				controlpropertiesview
						.setControlDescriptioneObj(populateControlDescription(controlid));
				controlpropertiesview
						.setControlDetailDescriptionObj(populateControlDetailDescription(controlid));
				// Automation and line of defense
        controlpropertiesview.setAutomation(control.getAutomation());
        controlpropertiesview.setLineofDefense(control.getLineofdefense());
        // Show swiss data check
        if (control.getOverrideswissdatapolicy() != null
            && control.getOverrideswissdatapolicy().equalsIgnoreCase("Y")) {
          controlpropertiesview.setCheckShowSwissData(true);
        } else {
          controlpropertiesview.setCheckShowSwissData(false);
        }                
				controlpropertiesview.setTools(control.getTools());
				List<ControlView> controlrefs = new ArrayList<ControlView>();
				ControlView contRef = null;
				Set<Controlreference> controlreferences = control
						.getControlreferencesByFromcontrol();
				if (!SystemUtil.isNull(controlreferences)) {
					Iterator itcontrolreference = controlreferences.iterator();
					while (itcontrolreference.hasNext()) {
						Controlreference controlReference = (Controlreference) itcontrolreference
								.next();
						if (controlReference != null) {
							contRef = new ControlView();
							contRef.setId(controlReference
									.getControlByTocontrol().getControlid());
							contRef.setMasterControlId(controlReference.getControlByTocontrol().getMastercontrolid());
							contRef
									.setControlTitleObj(populateControlTitle(contRef
											.getId()));
							contRef.setControlType(controlReference
									.getControlByTocontrol().getControltype()
									.getName());						
							contRef.setControlStatus(controlReference.getStatus().getStatusdescription());							 
							controlrefs.add(contRef);
						}

					}
				}

				controlpropertiesview.setControlReferences(controlrefs);

				if (copyControlFlag == false) {
					List<AttachmentView> attachmentViewList = new ArrayList<AttachmentView>();
					AttachmentView attachview = null;
					Set<Multiattachment> multiAttachmentSet = control
							.getMultiattachments();
					if (!SystemUtil.isNull(multiAttachmentSet)) {
						for (Multiattachment multiAttachment : multiAttachmentSet) {
							attachview = new AttachmentView();
							Attachment selectedAttachment = multiAttachment
									.getAttachment();
							attachview.setTempId(SystemUtil.getNextSeqNo());
							attachview.setId(selectedAttachment
									.getAttachmentid());
							attachview.setFileName(selectedAttachment
									.getAttachmentfilename());
							attachview.setUploadDate(DateUtil.getFormattedDate(selectedAttachment
									.getCreatedate(), Constants.OUTPUT_DATE_FORMAT));
							attachview.setSize(selectedAttachment
									.getAttachmentsize());
							attachview.setLastUpdate(selectedAttachment
									.getLastupdate());
              // Attachment Type
              attachview.setAttachmentExtension(selectedAttachment
                  .getAttachmenttype().getExtension());
							attachmentViewList.add(attachview);

						}
					}
					controlpropertiesview
							.setListOfAttachmentsForControl(attachmentViewList);

					List<LinkView> linkViewList = new ArrayList<LinkView>();
					LinkView linkView = null;
					Set<Multilink> multiLiknSet = control.getMultilinks();
					if (!SystemUtil.isNull(multiLiknSet)) {
						for (Multilink multiLink : multiLiknSet) {
							linkView = new LinkView();
							Link selectedLink = multiLink.getLink();
							linkView.setId(selectedLink.getLinkid());
							linkView.setLinkName(selectedLink.getLinkname());
							linkView.setLinkUrl(selectedLink.getLinkurl());
							linkViewList.add(linkView);
						}
					}
					controlpropertiesview
							.setListOfLinksForControl(linkViewList);
				}

				controlpropertiesview
						.setControlStepViewList(populateControlSteps(control,
								copyControlFlag));

			}
		} catch (MicosException micosException) {
      LOGGER.error("getControlPropertiesForControlID", micosException);
      throw micosException;
		} finally {
      LOGGER.exiting(getClass().getName(), "getControlPropertiesForControlID");
		}
		// set a reference to this control
		setDetachedControl(control);
		return controlpropertiesview;
	}

	/**
	 * populate ControlSteps
	 * 
	 * @param copyControlFlag
	 *            thr boolean
	 * @param ctrl
	 *            Control
	 * @return stepViewList
	 * @throws MicosException
	 *             the MicosException
	 */
	private List<ControlStepView> populateControlSteps(Control ctrl,
			boolean copyControlFlag) throws MicosException {
		LOGGER.entering(getClass().getName(),"populateControlSteps");
		List<ControlStepView> stepViewList = new ArrayList<ControlStepView>();
		Set<Setup> setups = ctrl.getSetups();
		if (setups == null) {
			return null;
		}
		for (Setup setup : setups) {
			if (setup.getSteps() == null) {
				continue;
			}
			//Integer tempId = 1;
			for (Step step : setup.getSteps()) {
        /*
         * Ignore Steps generated by Dynamic Signoff incase of
         * copy Control function
         */
        if (copyControlFlag
            && MicosConstantValues.StepType.DYNAMIC_SIGNOFF
                .equalsIgnoreCase(step.getStepType())) {
          continue;
        }        
        ControlStepView stepView = new ControlStepView();
        stepView.setId(step.getStepid());
        stepView.setTempId(SystemUtil.getNextSeqNo());
        stepView.setStepTitle(step.getTitle());
        stepView.setStepType(step.getStepType());
        if(MicosConstantValues.StepType.DYNAMIC_SIGNOFF
                .equalsIgnoreCase(step.getStepType())){
          stepView.setDynamicSignoff(true);
        }
				// CR11: Coverage Period for Controls.
				// set Coverageperiodstartdate & Coverageperiodenddate
				if (step.getCoverageperiodstartdate() != null) {
					stepView.setStepStartDate(step.getCoverageperiodstartdate());
				}
				if (step.getCoverageperiodenddate() != null) {
					stepView.setStepEndDate(step.getCoverageperiodenddate());
				}
				// end

				if (step.getStep() != null) {
					stepView.setDependsOn(step.getStep().getStepid());
					stepView.setDependsOnStepTitle(step.getStep().getTitle());
				}

				stepView
						.setCheckPerformance(step
								.getExecution()
								.equalsIgnoreCase(
										MicosConstantValues.CheckSchemaState.ENABLED_STATE));
				stepView.setCheckSupport(step.getSupport().equalsIgnoreCase(
						MicosConstantValues.CheckSchemaState.ENABLED_STATE));
				stepView.setCheckQA(step.getQa().equalsIgnoreCase(
						MicosConstantValues.CheckSchemaState.ENABLED_STATE));
				stepView.setCheckSignOff(step.getSignoff().equalsIgnoreCase(
						MicosConstantValues.CheckSchemaState.ENABLED_STATE));
				if (stepView.getCheckSupport()) {
					Supportschema supportSchema = step.getSupportschema();
					if (supportSchema != null) {
						stepView.setSupportSchemaView(populateSchemaView(supportSchema.getExecutors()));
						if (!copyControlFlag) {
							stepView.getSupportSchemaView().setId(supportSchema.getSupportschemaid());
						}
					}
				}

				if (stepView.getCheckPerformance()) {
					SchemaView perfSchemaView = new SchemaView();
					Executionschema execSchema = step.getExecutionschema();
					if (!copyControlFlag) {
						perfSchemaView.setId(execSchema.getExecutionschemaid());
					}
					if(step.getExecutionratinggroup()!=null) {
						stepView.setPerformanceRatinggroup(step.getExecutionratinggroup().getRatinggroupid().toString());
					}
					
					if (execSchema != null) {
						Set<Executor> executorSet = execSchema.getExecutors();
						if (!SystemUtil.isNull(executorSet)) {
							for (Executor exec : executorSet) {
								perfSchemaView.setSelectionViaPIDView(populateExecutorPIDView(exec.getExecutorpids()));
								perfSchemaView.setSelectionViaHierarchyView(populateExecutorAorView(exec.getExecutoraors()));
							}
						}
					}
					stepView.setPerformanceSchemaView(perfSchemaView);
				}

				if (stepView.getCheckQA()) {
					Qaschema qaSchema = step.getQaschema();
					if (qaSchema != null) {
						stepView.setQaSchemaView(populateSchemaView(qaSchema.getExecutors()));
						if (!copyControlFlag) {
							stepView.getQaSchemaView().setId(qaSchema.getQaschema());
						}
						
					}
				}

				if (stepView.getCheckSignOff()) {
					SchemaView signoffSchemaView = new SchemaView();
					Signoffschema signoffSchema = step.getSignoffschema();
					if (!copyControlFlag) {
						signoffSchemaView.setId(signoffSchema.getSignoffschemaid());
					}
					
					if(step.getSignoffratinggroup()!=null) {
						stepView.setSignoffRatinggroup(step.getSignoffratinggroup().getRatinggroupid().toString());
					}
					if (signoffSchema != null) {
						Set<Executor> executorSet = signoffSchema.getExecutors();
						if (!SystemUtil.isNull(executorSet)) {
							for (Executor exec : executorSet) {
								signoffSchemaView.setSelectionViaPIDView(populateExecutorPIDView(exec.getExecutorpids()));
								signoffSchemaView.setSelectionViaHierarchyView(populateExecutorAorView(exec.getExecutoraors()));
							}
						}
					}
					stepView.setSignOffSchemaView(signoffSchemaView);
				}

				// population of scheduling view
				if (step.getSchedules() != null) {
					Set<Schedule> schedules = step.getSchedules();
					for (Schedule schedule : schedules) {
						SchedulingView scheduleView = new SchedulingView();
						scheduleView.setId(schedule.getScheduleid());
						scheduleView.setScheduleStartDate(schedule.getStartdate());
						if (schedule.getEnddate() != null) {
							scheduleView.setScheduleEndDate(schedule.getEnddate());
						}
						scheduleView.setSchedulePeriodicAdhoc(schedule
								.getPeriodicadhoc());
						if (schedule.getPeriodicadhoc().equalsIgnoreCase(
								MicosConstantValues.ScheduleType.PERIODIC)) {
							if (schedule.getPatternqty() != null) {
								scheduleView.setSchedulePatternQty(schedule
										.getPatternqty().intValue());
							}
							if (schedule.getPeriodtypeByPatternfrequency() != null) {
								scheduleView.setSchedulePatternFreq(schedule
										.getPeriodtypeByPatternfrequency()
										.getPeriodtypeid().toString());
							}
							
							
							if(schedule.getMonday() != null){
								scheduleView.setMonday(schedule.getMonday());
							}
							if(schedule.getTuesday()!= null){
								scheduleView.setTuesday(schedule.getTuesday());
							}
							if(schedule.getWednesday()!= null){
								scheduleView.setWednesday(schedule.getWednesday());
							}
							if(schedule.getThursday()!= null){
								scheduleView.setThursday(schedule.getThursday());
							}
							if(schedule.getFriday()!= null){
								scheduleView.setFriday(schedule.getFriday());
							}
							if(schedule.getSaturday()!= null){
								scheduleView.setSaturday(schedule.getSaturday());
							}
							if(schedule.getSunday()!= null){
								scheduleView.setSunday(schedule.getSunday());
							}
						} else if (schedule.getPeriodicadhoc()
								.equalsIgnoreCase(
										MicosConstantValues.ScheduleType.ADHOC)) {
							Set<Scheduleadhocdate> adhocDateSet = schedule
									.getScheduleadhocdates();

							if (adhocDateSet != null) {
								List<ScheduleAdhocDateView> adhocDateViewList = new ArrayList<ScheduleAdhocDateView>();
								for (Scheduleadhocdate date : adhocDateSet) {
									ScheduleAdhocDateView adhocDateView = new ScheduleAdhocDateView();
									adhocDateView.setScheduleAdhocDate(DateUtil.getFormattedDate(date.getAdhocdate(), Constants.OUTPUT_DATE_FORMAT));
									adhocDateView.setTempId(date
											.getScheduleadhocdatesid());
					                  if (copyControlFlag) {
					                    adhocDateView.setId(null);
					                  } else {
					                    adhocDateView.setId(date.getScheduleadhocdatesid());
					                  }
									adhocDateViewList.add(adhocDateView);
								}
								scheduleView
										.setScheduleAdhocDateViewList(adhocDateViewList);
							}
						}

						// fields for Support
						if (schedule.getSupportinitiateqty() != null) {
							scheduleView.setScheduleSupportInitiateQty(schedule
									.getSupportinitiateqty().intValue());
						}
						if (schedule.getPeriodtypeBySupportinitiateperiodtype() != null) {
							scheduleView
									.setScheduleSupportInitiatePeriodType(schedule
											.getPeriodtypeBySupportinitiateperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getSupportdurationqty() != null) {
							scheduleView.setScheduleSupportDurationQty(schedule
									.getSupportdurationqty().intValue());
						}
						if (schedule.getPeriodtypeBySupportdurationperiodtype() != null) {
							scheduleView
									.setScheduleSupportDurationPeriodType(schedule
											.getPeriodtypeBySupportdurationperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getSchedulewhenBySupportschedulewhen() != null) {
							scheduleView
									.setScheduleSupportScheduleWhen(schedule
											.getSchedulewhenBySupportschedulewhen()
											.getSchedulewhenid().toString());
							
						}
						// fields for Exec
						if (schedule.getExecutioninitiateqty() != null) {
							scheduleView.setScheduleExecInitQty(schedule
									.getExecutioninitiateqty().intValue());
						}
						if (schedule
								.getPeriodtypeByExecutorinitiateperiodtype() != null) {
							scheduleView
									.setScheduleExecInitPeriodType(schedule
											.getPeriodtypeByExecutorinitiateperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getExecutiondurationqty() != null) {
							scheduleView.setScheduleExecDurationQty(schedule
									.getExecutiondurationqty().intValue());
						}
						if (schedule
								.getPeriodtypeByExecutiondurationperiodtype() != null) {
							scheduleView
									.setScheduleExecPeriodType(schedule
											.getPeriodtypeByExecutiondurationperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getSchedulewhenByExecutionschedulewhen() != null) {
							scheduleView.setScheduleExecWhen(schedule
									.getSchedulewhenByExecutionschedulewhen()
									.getSchedulewhenid().toString());
						}
						// fields for QA
						if (schedule.getQainitiateqty() != null) {
							scheduleView.setScheduleQAInitQty(schedule
									.getQainitiateqty().intValue());
						}
						if (schedule.getPeriodtypeByQainitiateperiodtype() != null) {
							scheduleView.setScheduleQAInitPeriodType(schedule
									.getPeriodtypeByQainitiateperiodtype()
									.getPeriodtypeid().toString());
						}
						if (schedule.getQadurationqty() != null) {
							scheduleView.setScheduleQADurationQty(schedule
									.getQadurationqty().intValue());
						}
						if (schedule.getPeriodtypeByQadurationperiodtype() != null) {
							scheduleView.setScheduleQAPeriodType(schedule
									.getPeriodtypeByQadurationperiodtype()
									.getPeriodtypeid().toString());
						}
						if (schedule.getSchedulewhenByQaschedulewhen() != null) {
							scheduleView.setScheduleQAWhen(schedule
									.getSchedulewhenByQaschedulewhen()
									.getSchedulewhenid().toString());
						}
						// fields for Signoff
						if (schedule.getSignoffinitiateqty() != null) {
							scheduleView.setScheduleSignoffInitQty(schedule
									.getSignoffinitiateqty().intValue());
						}
						if (schedule.getPeriodtypeBySignoffinitiateperiodtype() != null) {
							scheduleView
									.setScheduleSignoffInitPeriodType(schedule
											.getPeriodtypeBySignoffinitiateperiodtype()
											.getPeriodtypeid().toString());
						}
						if (schedule.getSignoffdurationqty() != null) {
							scheduleView.setScheduleSignoffDurationQty(schedule
									.getSignoffdurationqty().intValue());
						}
						if (schedule.getPeriodtypeBySignoffdurationperiodtype() != null) {
							scheduleView.setScheduleSignoffPeriodType(schedule
									.getPeriodtypeBySignoffdurationperiodtype()
									.getPeriodtypeid().toString());
						}
						if (schedule.getSchedulewhenBySignoffschedulewhen() != null) {
							scheduleView.setScheduleSignoffWhen(schedule
									.getSchedulewhenBySignoffschedulewhen()
									.getSchedulewhenid().toString());
						}
						stepView.setSchedulingView(scheduleView);
						//Populated the notification
						populateNotification(step,null, scheduleView);
					}
					
				}
				stepViewList.add(stepView);
				//tempId++;
			}
		}

		return stepViewList;
	}
	
	/**
	 * Populate notification.
	 * @param step Step
	 * @param powerOption Power option
	 * @param scheduleView View
	 * @throws MicosException MicosException	
	 */
	private void populateNotification(Step step, Poweroption powerOption, SchedulingView scheduleView)throws MicosException{
		Set<ControlRemWarEsc> setControlRemWarEsc=null;				
		if(null!=step){
			if(step.getStepType() != null && step.getStepType().equalsIgnoreCase("P")) {
				setControlRemWarEsc=step.getPoweroption().getSetOfControlRemEsc();
			} else {
				setControlRemWarEsc=step.getControlRemWarEscStep();	
			}
			
		}else if (null!=powerOption){
			setControlRemWarEsc=powerOption.getSetOfControlRemEsc();
		}
		if(SystemUtil.isNotNull(setControlRemWarEsc)){			
			for(ControlRemWarEsc controlRemWarEsc: setControlRemWarEsc){				
				NotificationView notView=new NotificationView();
				notView.setId(controlRemWarEsc.getControlRemWarEscId());
				notView.setNotRefID(controlRemWarEsc.getNotType().getReferencecodeid());
				if(null != controlRemWarEsc.getRecipient()){
					notView.setRecipient(true);
					notView.setSelectedHierarchy(controlRemWarEsc.getRecipient().getShortdescription());
				}else{
					notView.setRecipient(false);
					if(notView.getNotRefID().equals(getReferenceCodeByCode(ReferenceCodeUtil.ESCALATIONCODE_ID,ReferenceCodeUtil.NOTTYPE_ID ).getId())){
						notView.setSelectedHierarchy(ReferenceCodeUtil.OEHIERARCHY);
					}
				}
				notView.setSelectedFrequency(controlRemWarEsc.getFrequency().getCode());
				notView.setQuantity(controlRemWarEsc.getQuantity().toString());
				notView.setSelectedEstimation(controlRemWarEsc.getPeriodType().getPeriodtypeid().toString());
				notView.setLastUpdate(controlRemWarEsc.getLastupdate());
				Set<RemWarEscOtherRecipient> otherRecs=controlRemWarEsc.getOtherRecipients();
				if(SystemUtil.isNotNull(otherRecs)){
					List<EmployeeView> listofEmployee=new ArrayList<EmployeeView>();
					notView.setCheckOthers(true);
					for(RemWarEscOtherRecipient otherRec: otherRecs){
						Employee emp=otherRec.getEmployee();
						EmployeeView empView=new EmployeeView();
						empView.setFirstName(emp.getLastname() + " "
								+ emp.getFirstname()+","+" " + emp.getInstradierung() +" "+ "(" + emp.getPid()
								+ ")");
						empView.setSurName(emp.getLastname());
						empView.setPid(emp.getPid());
						empView.setEmpId(emp.getEmployeeid().toString());
						empView.setDepartment(emp.getInstradierung());
						empView.setId(otherRec.getRemWarEscOtherRecipientid());
						listofEmployee.add(empView);
					}
					Collections.sort(listofEmployee, new MicosComparator("firstName", true));
					notView.setListOthers(listofEmployee);
				}else{
					notView.setCheckOthers(false);
				}
				if(IssuesConstants.ReferenceCodeUtil.REMINDERCODE_ID.
						equals(controlRemWarEsc.getNotType().getCode())){
					scheduleView.setReminderView(notView);
					scheduleView.setCheckReminder(true);
				
					
				}else if(IssuesConstants.ReferenceCodeUtil.WARNINGCODE_ID.
						equals(controlRemWarEsc.getNotType().getCode())){
					scheduleView.setWarningView(notView);
					scheduleView.setCheckWarning(true);
				}else if(IssuesConstants.ReferenceCodeUtil.ESCALATIONCODE_ID.
						equals(controlRemWarEsc.getNotType().getCode())){
					scheduleView.setEscalationView(notView);
					scheduleView.setCheckEscalation(true);
				}
			}
		}		
	}
	
	/**
	 * Returns the SchemaView.
	 * 
	 * @param executorSet
	 *            set of Executor
	 * @return the SchemaView
	 */
	private SchemaView populateSchemaView(Set<Executor> executorSet) {
		SchemaView schemaView = new SchemaView();
		if (!SystemUtil.isNull(executorSet)) {
			for (Executor exec : executorSet) {
				schemaView.setSelectionViaPIDView(populateExecutorPIDView(exec
						.getExecutorpids()));
				schemaView
						.setSelectionViaHierarchyView(populateExecutorAorView(exec
								.getExecutoraors()));
			}
		}
		
		return schemaView;
	}
	
	/**
	 * Returns the executor AoR view.
	 * 
	 * @param execAorSet
	 *            set of Executoraor
	 * @return the list of executor AoR view
	 */
	private List<HierarchyElementView> populateExecutorAorView(Set<Executoraor> execAorSet) {
		List<HierarchyElementView> execAorList = new ArrayList<HierarchyElementView>();
		if (execAorSet != null) {
			for (Executoraor execAor : execAorSet) {
				HierarchyElementView hierarchyElement = new HierarchyElementView();
				hierarchyElement.setHierarchyName(execAor.getHierarchyelement()
						.getHierarchy().getName());
				hierarchyElement.setTitle(execAor.getHierarchyelement().getName());
				hierarchyElement.setId(execAor.getHierarchyelement()
						.getHierarchyelementid());
				hierarchyElement.setExecutorAoRId(execAor.getExecutoraorid());
		
				if (execAor.getHierarchyrole() != null) {
					hierarchyElement.setSelectedRole(new BaseView(execAor
							.getHierarchyrole().getHierarchyroleid(), execAor
							.getHierarchyrole().getName()));
				}
				if (execAor.getLevelsdown() != null) {
					hierarchyElement.setLevel(execAor.getLevelsdown().intValue());
				}
				execAorList.add(hierarchyElement);
			}
		}
		return execAorList;
	}
	
	/**
	 * Returns the executor PID view.
	 * 
	 * @param execPidSet
	 *            set of Executorpid
	 * @return the list of executor PID view
	 */
	private List<EmployeeView> populateExecutorPIDView(Set<Executorpid> execPidSet) {
		List<EmployeeView> execPidList = new ArrayList<EmployeeView>();
		if (execPidSet != null) {
			for (Executorpid execPid : execPidSet) {
				Employee emp = execPid.getEmployeeByEmployeeid();
				EmployeeView empView = new EmployeeView();
				empView.setEmpId(String.valueOf(emp.getEmployeeid().intValue()));
				empView.setPid(emp.getPid());
				empView.setEmpDescription(emp.getEmployeeName());
				empView.setTempId(execPid.getExecutorpidid());
				if (execPid.getInclude().equals(
						MicosConstantValues.EmpIncludeExcledeStatus.INCLUDE)) {
					empView.setIncludeExclude(false);
				} else {
					empView.setIncludeExclude(true);
				}
				execPidList.add(empView);
			}
		}
		return execPidList;
	}

	/**
	 * Delete a control based on control id.
	 * 
	 * @param controlid
	 *            control id
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void deleteControl(Integer controlid) throws MicosException {
    LOGGER.entering(getClass().getName(), "deleteAControl");
    controlSetupDao.deleteControl(controlid);
    LOGGER.exiting(getClass().getName(), "deleteAControl");
	}

	/**
	 * Delete a control based on control id.
	 * 
	 * @param accessAorId
	 *            Integer
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void deleteControlFromAccessaor(Integer accessAorId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "deleteControlFromAccessaor");
    controlSetupDao.deleteControlFromAccessaor(accessAorId);
    LOGGER.exiting(getClass().getName(), "deleteControlFromAccessaor");
	}

	/**
	 * Retrieve the list of employees based on search parameter passed.
	 * 
	 * @param searchCriteria
	 *            EmployeeView
	 * @return listOfDesignee EmployeeView list
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

	public List<EmployeeView> getEmployeeList(EmployeeView searchCriteria)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "getEmployeeList");
    List setOfEmployees = null;
    List<EmployeeView> listOfDesignee = new ArrayList<EmployeeView>();
    Employee employee = null;

    setOfEmployees = controlSetupDao.getEmployees(searchCriteria);
    if (setOfEmployees == null) {
      return null;
    }
    if (!SystemUtil.isNull(setOfEmployees)) {
      Iterator itDesignee = setOfEmployees.iterator();
      while (itDesignee.hasNext()) {
        EmployeeView employeeView = new EmployeeView();
        employee = (Employee)itDesignee.next();
        employeeView.setEmpId(employee.getEmployeeid().toString());
        employeeView.setFirstName(employee.getFirstname());
        employeeView.setSurName(employee.getLastname());
        employeeView.setPid(employee.getPid());
        employeeView.setDepartment(employee.getDepartment());
        employeeView.setEmpDescription(employee.getEmployeeName());
        employeeView.setInstradierung("(" + employee.getInstradierung() + ")");
        listOfDesignee.add(employeeView);
      }
    }

    LOGGER.exiting(getClass().getName(), "getEmployeeList");
    return listOfDesignee;
	}

	/**
	 * CR_8
	 * populate Control StepData
	 * 
	 * @param loggedEmployee
	 *            Employee
	 * @param controlStepView
	 *            ControlStepView
	 * @param poweroption
	 *            Step
	 * @throws MicosException
	 *             MicosException
	 */
	private void populateControlPowerOptionData(Employee loggedEmployee,
			ControlStepView controlStepView, Poweroption poweroption, String statusControl)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateControlPowerOptionData");
		poweroption.setTitle(controlStepView.getStepTitle());
		poweroption.setCoverageperiodstart(controlStepView.getStepStartDate());
		poweroption.setCoverageperiodend(controlStepView.getStepEndDate());
		
		poweroption.setExecutionratinggroup(controlSetupDao
				.retrieveRatinggroupForId(Integer.parseInt(controlStepView
						.getPerformanceRatinggroup())));
		poweroption.setSignoffratinggroup(controlSetupDao
				.retrieveRatinggroupForId(Integer.parseInt(controlStepView
						.getSignoffRatinggroup())));
		// Next Schedule date
	    if (controlStepView.getSchedulingView() != null) {
			if (controlStepView.getSchedulingView().getSchedulePeriodicAdhoc()
					.equalsIgnoreCase(ScheduleType.PERIODIC)) {
				Integer patternFreq = Integer.parseInt(controlStepView
						.getSchedulingView().getSchedulePatternFreq());
				if (patternFreq.equals(7)) {
					try {
						poweroption.setNextscheduledate(SchedulerUtils
								.getNextWeeklyScheduleDate(controlStepView
										.getSchedulingView()
										.getScheduleStartDate(),
										controlStepView.getSchedulingView()
												.getScheduleEndDate(),
										createWeekdayArr(controlStepView
												.getSchedulingView()),
										patternFreq));
					} catch (Exception e) {
						LOGGER.error("populateControlStepData", e);
						throw new MicosException(e);
					}
				} else {
					poweroption.setNextscheduledate(controlStepView
							.getSchedulingView().getScheduleStartDate());
				}
			} else {
				poweroption.setNextscheduledate(controlStepView.getSchedulingView()
						.getScheduleStartDate());
			}
		}
	    

		if (controlStepView.getPowerOptionView() != null) {
			if (controlStepView.getPowerOptionView().getHierarchyElementId() != null) {
				poweroption.setHierarchyelement(controlSetupDao
						.loadHierarchyElement(controlStepView.getPowerOptionView()
								.getHierarchyElementId()));
			}
			if (controlStepView.getPowerOptionView().getLevelsUp() != null) {
				poweroption.setLevelsup(controlStepView.getPowerOptionView()
						.getLevelsUp());
			}
			if (controlStepView.getPowerOptionView().getLevelsDown() != null) {
				poweroption.setLevelsdown(controlStepView.getPowerOptionView()
						.getLevelsDown());
			}

			if (controlStepView.getPowerOptionView().getRolesForAor() != null
					&& controlStepView.getPowerOptionView().getSignOffRole() != null
					&& controlStepView.getPowerOptionView().getRolesForAor() == controlStepView
							.getPowerOptionView().getSignOffRole()) {
				Hierarchyrole hierarchyrole = controlSetupDao
						.loadHierarchyRole(controlStepView.getPowerOptionView()
								.getRolesForAor());
				poweroption.setPerformancerole(hierarchyrole);
				poweroption.setSignoffrole(hierarchyrole);
			} else {
				if (controlStepView.getPowerOptionView().getRolesForAor() != null) {
					poweroption.setPerformancerole(controlSetupDao
							.loadHierarchyRole(controlStepView
									.getPowerOptionView().getRolesForAor()));
				}

				if (controlStepView.getPowerOptionView().getSignOffRole() != null) {
					poweroption.setSignoffrole(controlSetupDao
							.loadHierarchyRole(controlStepView
									.getPowerOptionView().getSignOffRole()));
				}

			}

		}

		// update support schema
		if (controlStepView.getCheckSupport()) {
			SchemaView supportSchemaView = controlStepView
					.getSupportSchemaView();
			Supportschema supportschemaData = poweroption.getSupportschema();
			if (supportSchemaView != null) {
				if (supportschemaData != null
						&& poweroption
								.getSupport()
								.equals(
										MicosConstantValues.CheckSchemaState.ENABLED_STATE)) {
					// if already created then modify its data
					updateStepSupportSchema(supportSchemaView,
							supportschemaData, loggedEmployee);
				} else {
					// create new support schema data
					populateSupportSchemaPower(loggedEmployee, controlStepView,
							poweroption);
					poweroption
							.setSupport(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
				}
			}
		} else {
			poweroption
					.setSupport(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		// update QA schema
		if (controlStepView.getCheckQA()) {
			SchemaView qaSchemaView = controlStepView.getQaSchemaView();
			Qaschema qaschemaData = poweroption.getQaschema();
			if (qaSchemaView != null) {
				if (qaschemaData != null
						&& poweroption
								.getQa()
								.equals(
										MicosConstantValues.CheckSchemaState.ENABLED_STATE)) {
					// if already created then modify its data
					updateStepQaSchema(qaSchemaView, qaschemaData,
							loggedEmployee);
				} else {
					// create new qa schema data
					populateQaSchemaPower(loggedEmployee, controlStepView,
							poweroption);
					poweroption
							.setQa(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
				}
			}
		} else {
			poweroption
					.setQa(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		updateStepScheduleDataPower(controlStepView, poweroption,
				loggedEmployee,statusControl);
		/*updateStepEscalationDataPower(controlStepView, poweroption,
				loggedEmployee);*/

		poweroption.setEmployee(loggedEmployee);
	}

	/**
	 * CR_8
	 * Creates new Poweroption.
	 * 
	 * @param controlId Control Id
	 * @param powerStepList
	 *            powerStep List
	 * @param loginUserId
	 *            Integer
	 * @return poweroptionids of newly created poweroptions
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<Integer> savePowerOptionStep(Integer controlId,
			List powerStepList, Integer loginUserId) throws MicosException {
		LOGGER.entering(getClass().getName(),"savePowerOptionStep");
		List<Integer> powerOptionIdList = new ArrayList<Integer>();
		List<Poweroption> poList = new ArrayList<Poweroption>();
    Poweroption poweroption = null;
    Integer powerOptionId = null;
    String statusControl=null;

    Employee loggedEmployee = controlSetupDao.loadEmployee(loginUserId);
    if (null != controlId && loggedEmployee != null && powerStepList != null) {

      for (Iterator iter = powerStepList.iterator(); iter.hasNext();) {
        ControlStepView element = (ControlStepView)iter.next();
        if (null == element.getId()) {
          // if id is null, then must be new Poweroption creation
          poweroption = new Poweroption();
          populateNewPowerOptionData(poweroption, element, loggedEmployee,
              controlId,statusControl);
          poList.add(poweroption);
         } else {
          List<Poweroption> powerOptionList = getDetachedPowerOptions();

          for (Iterator iterator = powerOptionList.iterator(); iterator
              .hasNext();) {
            poweroption = (Poweroption)iterator.next();
            if (element.getId().intValue() == poweroption.getPoweroptionid()
                .intValue()) {
            	Control control = controlSetupDao.loadControl(controlId);
            	statusControl = control.getStatus().getStatusdescription();
              populateControlPowerOptionData(loggedEmployee, element,
                  poweroption,statusControl);
              poList.add(poweroption);
              break;
            }
          }
        }

      }
      controlSetupDao.saveOrUpdatePowerOption(poList);
    }
    LOGGER.exiting(getClass().getName(), "savePowerOptionStep");
    return powerOptionIdList;
	}

	/**
	 * CR_8
	 * populate Power Option Data
	 * 
	 * @param poweroption
	 *            Poweroption
	 * @param controlStepView
	 *            ControlStepView
	 * @param loggedEmployee
	 *            Employee
   * @param controlId Control Id           
	 * @throws MicosException
	 *             MicosException
	 */
	private void populateNewPowerOptionData(Poweroption poweroption,
			ControlStepView controlStepView, Employee loggedEmployee,
			Integer controlId,String statusControl) throws MicosException {
		LOGGER.entering(getClass().getName(),"populateNewPowerOptionData");
		poweroption.setTitle(controlStepView.getStepTitle());
		poweroption.setCoverageperiodstart(controlStepView.getStepStartDate());
		poweroption.setCoverageperiodend(controlStepView.getStepEndDate());
		poweroption.setControl(controlSetupDao.loadControl(controlId));
		
		poweroption.setExecutionratinggroup(controlSetupDao
				.retrieveRatinggroupForId(Integer.parseInt(controlStepView
						.getPerformanceRatinggroup())));
		poweroption.setSignoffratinggroup(controlSetupDao
				.retrieveRatinggroupForId(Integer.parseInt(controlStepView
						.getSignoffRatinggroup())));
		// Next Schedule date
		// Next Schedule date
	    if (controlStepView.getSchedulingView() != null) {
			if (controlStepView.getSchedulingView().getSchedulePeriodicAdhoc()
					.equalsIgnoreCase(ScheduleType.PERIODIC)) {
				Integer patternFreq = Integer.parseInt(controlStepView
						.getSchedulingView().getSchedulePatternFreq());
				if (patternFreq.equals(7)) {
					try {
						poweroption.setNextscheduledate(SchedulerUtils
								.getNextWeeklyScheduleDate(controlStepView
										.getSchedulingView()
										.getScheduleStartDate(),
										controlStepView.getSchedulingView()
												.getScheduleEndDate(),
										createWeekdayArr(controlStepView
												.getSchedulingView()),
										patternFreq));
					} catch (Exception e) {
						LOGGER.error("populateControlStepData", e);
						throw new MicosException(e);
					}
				} else {
					poweroption.setNextscheduledate(controlStepView
							.getSchedulingView().getScheduleStartDate());
				}
			} else {
				poweroption.setNextscheduledate(controlStepView.getSchedulingView()
						.getScheduleStartDate());
			}
		}

		if (controlStepView.getCheckSupport()) {
			poweroption
					.setSupport(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
			populateSupportSchemaPower(loggedEmployee, controlStepView,
					poweroption);
		} else {
			poweroption
					.setSupport(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		if (controlStepView.getCheckQA()) {
			poweroption
					.setQa(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
			populateQaSchemaPower(loggedEmployee, controlStepView, poweroption);
		} else {
			poweroption
					.setQa(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}
		if (controlStepView.getPowerOptionView() != null) {
			if (controlStepView.getPowerOptionView().getHierarchyElementId() != null) {
				poweroption.setHierarchyelement(controlSetupDao
						.loadHierarchyElement(controlStepView.getPowerOptionView()
								.getHierarchyElementId()));
			}
			if (controlStepView.getPowerOptionView().getLevelsUp() != null) {
				poweroption.setLevelsup(controlStepView.getPowerOptionView()
						.getLevelsUp());
			}
			if (controlStepView.getPowerOptionView().getLevelsDown() != null) {
				poweroption.setLevelsdown(controlStepView.getPowerOptionView()
						.getLevelsDown());
			}

			if (controlStepView.getPowerOptionView().getRolesForAor() == controlStepView
					.getPowerOptionView().getSignOffRole()) {
				Hierarchyrole hierarchyrole = controlSetupDao
						.loadHierarchyRole(controlStepView.getPowerOptionView()
								.getRolesForAor());
				poweroption.setPerformancerole(hierarchyrole);
				poweroption.setSignoffrole(hierarchyrole);
			} else {
				poweroption.setPerformancerole(controlSetupDao
						.loadHierarchyRole(controlStepView.getPowerOptionView()
								.getRolesForAor()));
				poweroption.setSignoffrole(controlSetupDao
						.loadHierarchyRole(controlStepView.getPowerOptionView()
								.getSignOffRole()));
			}

		}

		if (controlStepView.getSchedulingView() != null) {
			Poschedule schedule = new Poschedule();
			schedule.setCreatedate(new Timestamp(System.currentTimeMillis()));
			schedule.setLastupdate(new Timestamp(System.currentTimeMillis()));
			copySchedulingViewToDataPower(controlStepView.getSchedulingView(),
					loggedEmployee, schedule);
			schedule.setPoweroption(poweroption);
			Set<Poschedule> scheduleSet = new HashSet<Poschedule>();
			scheduleSet.add(schedule);
			poweroption.setPoschedules(scheduleSet);
		}
		
		Set<ControlRemWarEsc> setControlRemWarEsc=createEditNotification(controlStepView, true ,poweroption, null,statusControl);
		poweroption.setSetOfControlRemEsc(setControlRemWarEsc);

		poweroption.setCreatedate(DateUtil.getCurrentTimestamp());
		poweroption.setLastupdate(DateUtil.getCurrentTimestamp());
		poweroption.setEmployee(loggedEmployee);
		
		//poweroption.
		
		
	}

	/**
	 * CR_8
	 * populate SupportSchema
	 * 
	 * @param loggedEmployee
	 *            Employee
	 * @param controlStepView
	 *            ControlStepView
	 * @param poweroption
	 *            Poweroption
	 * @throws MicosException
	 *             MicosException
	 */
	private void populateSupportSchemaPower(Employee loggedEmployee,
			ControlStepView controlStepView, Poweroption poweroption)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateSupportSchemaPower");
		Supportschema supportSchema = new Supportschema();
		supportSchema.setCreatedate(new Timestamp(System.currentTimeMillis()));
		supportSchema.setLastupdate(new Timestamp(System.currentTimeMillis()));
		supportSchema.setEmployee(loggedEmployee);

		if (controlStepView.getSupportSchemaView() != null) {

			Set<Executor> executorSet = new HashSet<Executor>();
			List<EmployeeView> employeesViaPid = controlStepView
					.getSupportSchemaView().getSelectionViaPIDView();

			List<HierarchyElementView> hierarchyElements = controlStepView
					.getSupportSchemaView().getSelectionViaHierarchyView();

			if (!SystemUtil.isNull(employeesViaPid)
					|| !SystemUtil.isNull(hierarchyElements)) {
				Executor executor = new Executor();
				executor.setSupportschema(supportSchema);
				if (!SystemUtil.isNull(employeesViaPid)) {
					executor.setExecutorpids(updateExecutorPid(employeesViaPid,
							loggedEmployee, executor));
					executorSet.add(executor);
				}
				if (!SystemUtil.isNull(hierarchyElements)) {
					executor.setExecutoraors(updateExecutoraAor(
							hierarchyElements, loggedEmployee, executor));
					executorSet.add(executor);
				}
				executor.setCreatedate(new Timestamp(System.currentTimeMillis()));
				executor.setLastupdate(new Timestamp(System.currentTimeMillis()));
				executor.setEmployee(loggedEmployee);
			}
			supportSchema.setExecutors(executorSet);
		}
		poweroption.setSupportschema(supportSchema);
	}
	
	/**
	 * update Executor Pid
	 * 
	 * @param employeesViaPid
	 *            employees Via Pid
	 * @param loggedEmployee
	 *            logged Employee
	 * @param executorForPid
	 *            executor For Pid
	 * @throws MicosException
	 *             MicosException
	 * @return executorPidSet
	 */
	private Set<Executorpid> updateExecutorPid(
			List<EmployeeView> employeesViaPid, Employee loggedEmployee,
			Executor executorForPid) throws MicosException {
		Set<Executorpid> executorPidSet = new HashSet<Executorpid>();
		for (EmployeeView employee : employeesViaPid) {
			Employee selectedEmployee = controlSetupDao.loadEmployee(Integer
					.parseInt(employee.getEmpId()));

			Executorpid executorpid = new Executorpid();
			executorpid.setEmployeeByEmployeeid(selectedEmployee);
			executorpid
					.setCreatedate(new Timestamp(System.currentTimeMillis()));
			executorpid
					.setLastupdate(new Timestamp(System.currentTimeMillis()));
			executorpid.setEmployeeByLastupdatedby(loggedEmployee);

			if (employee.isIncludeExclude()) {
				executorpid
						.setInclude(MicosConstantValues.EmpIncludeExcledeStatus.EXCLUDE);
			} else {
				executorpid
						.setInclude(MicosConstantValues.EmpIncludeExcledeStatus.INCLUDE);
			}

			executorpid.setExecutor(executorForPid);
			executorPidSet.add(executorpid);
		}
		return executorPidSet;
	}
	
	/**
	 * update Executor Aor
	 * 
	 * @param hierarchyElements
	 *            hierarchy Elements
	 * @param loggedEmployee
	 *            logged Employee
	 * @param executor
	 *            executor For Pid
	 * @throws MicosException
	 *             MicosException
	 * @return executorPidSet
	 */
	private Set<Executoraor> updateExecutoraAor(
			List<HierarchyElementView> hierarchyElements, Employee loggedEmployee,
			Executor executor) throws MicosException {
		Set<Executoraor> executorAorSet = new HashSet<Executoraor>();
		for (HierarchyElementView hierarchyElement : hierarchyElements) {
			Executoraor executoraor = new Executoraor();
			executoraor.setCreatedate(new Timestamp(System
					.currentTimeMillis()));
			executoraor.setLastupdate(new Timestamp(System
					.currentTimeMillis()));
			executoraor.setEmployee(loggedEmployee);
			executoraor
					.setHierarchyelement(controlSetupDao
							.loadHierarchyElement(hierarchyElement
									.getId()));
			if (hierarchyElement.getTempId() != -1) {
				executoraor.setTempId(hierarchyElement.getTempId());
			} else {
				executoraor.setTempId(SystemUtil.getNextSeqNo());
			}
			executoraor.setExecutor(executor);

			Hierarchyrole hiechRole = controlSetupDao
					.loadHierarchyRole(hierarchyElement
							.getSelectedRole().getId());
			executoraor.setHierarchyrole(hiechRole);
			if (hierarchyElement.getLevel() != null) {
				executoraor.setLevelsdown(new BigDecimal(
						hierarchyElement.getLevel()));
			}
			executorAorSet.add(executoraor);
		}
		return executorAorSet;
	}

	/**
	 * CR_8 populate QaSchema
	 * 
	 * @param loggedEmployee
	 *            Employee
	 * @param controlStepView
	 *            ControlStepView
	 * @param poweroption
	 *            Poweroption
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void populateQaSchemaPower(Employee loggedEmployee,
			ControlStepView controlStepView, Poweroption poweroption)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateQaSchemaPower");
		Qaschema qaSchema = new Qaschema();
		qaSchema.setCreatedate(new Timestamp(System.currentTimeMillis()));
		qaSchema.setLastupdate(new Timestamp(System.currentTimeMillis()));
		qaSchema.setEmployee(loggedEmployee);
		if (controlStepView.getQaSchemaView() != null) {
			Set<Executor> executorSet = new HashSet<Executor>();
			List<EmployeeView> employeesViaPid = controlStepView
					.getQaSchemaView().getSelectionViaPIDView();
			List<HierarchyElementView> hierarchyElements = controlStepView
					.getQaSchemaView().getSelectionViaHierarchyView();
			if (!SystemUtil.isNull(employeesViaPid)
					|| !SystemUtil.isNull(hierarchyElements)) {
				Executor executor = new Executor();
				executor.setQaschema(qaSchema);
				if (!SystemUtil.isNull(employeesViaPid)) {
					executor.setExecutorpids(updateExecutorPid(employeesViaPid,loggedEmployee, executor));
					executorSet.add(executor);
				}
				if (!SystemUtil.isNull(hierarchyElements)) {
					executor.setExecutoraors(updateExecutoraAor(hierarchyElements,loggedEmployee,executor));
					executorSet.add(executor);
				}
				executor.setCreatedate(new Timestamp(System.currentTimeMillis()));
				executor.setLastupdate(new Timestamp(System.currentTimeMillis()));
				executor.setEmployee(loggedEmployee);
			}
			qaSchema.setExecutors(executorSet);
		}
		poweroption.setQaschema(qaSchema);
	}

	/**
	 * Copies all Poschedule data from the Scheduling view to the data object
	 * 
	 * @param schedulingView
	 *            the Schedule view object
	 * @param loggedEmployee
	 *            Employee
	 * @param scheduleData
	 *            the Poschedule data object
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void copySchedulingViewToDataPower(SchedulingView schedulingView,
			Employee loggedEmployee, Poschedule scheduleData)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"copySchedulingViewToDataPower");
		scheduleData.setEnddate(schedulingView.getScheduleEndDate());
		scheduleData.setStartdate(schedulingView.getScheduleStartDate());

		if (MicosConstantValues.ScheduleType.PERIODIC
				.equalsIgnoreCase(schedulingView.getSchedulePeriodicAdhoc())) {
			scheduleData.setPeriodicadhoc(schedulingView
					.getSchedulePeriodicAdhoc());
			if (schedulingView.getSchedulePatternQty() != null) {
				scheduleData.setPatternqty(new BigDecimal(schedulingView
						.getSchedulePatternQty()));
			}
			if (schedulingView.getSchedulePatternFreq() != null) {
				Integer schedulePatternFreq = Integer.parseInt(schedulingView
						.getSchedulePatternFreq());
				scheduleData.setPeriodtypeByPatternfrequency(controlSetupDao
						.loadPeriodType(schedulePatternFreq));
				
				if(scheduleData.getPeriodtypeByPatternfrequency().getName().equalsIgnoreCase(MicosConstantValues.PeriodType.WEEKS)){
					scheduleData.setMonday(schedulingView.getMonday());
					scheduleData.setTuesday(schedulingView.getTuesday());
					scheduleData.setWednesday(schedulingView.getWednesday());
					scheduleData.setThursday(schedulingView.getThursday());
					scheduleData.setFriday(schedulingView.getFriday());
					scheduleData.setSaturday(schedulingView.getSaturday());
					scheduleData.setSunday(schedulingView.getSunday());
				}
			}
			scheduleData.setPoscheduleadhocdates(null);
		} else if (MicosConstantValues.ScheduleType.ADHOC
				.equalsIgnoreCase(schedulingView.getSchedulePeriodicAdhoc())) {
			scheduleData.setPeriodicadhoc(schedulingView
					.getSchedulePeriodicAdhoc());
			Set<Scheduleadhocdate> adhocDates = null;
			if (scheduleData.getPoscheduleadhocdates() != null) {
				adhocDates = scheduleData.getPoscheduleadhocdates();
			} else {
				adhocDates = new HashSet<Scheduleadhocdate>();
			}
			List<ScheduleAdhocDateView> adhocDateList = schedulingView
					.getScheduleAdhocDateViewList();
			if (adhocDateList != null) {
				for (ScheduleAdhocDateView adhocDateView : adhocDateList) {
					if (adhocDateView.getId() != null) {
						continue;
					}
					Scheduleadhocdate adhocDate = new Scheduleadhocdate();
					adhocDate.setAdhocdate(DateUtil.getDate(adhocDateView.getScheduleAdhocDate(), Constants.OUTPUT_DATE_FORMAT));
					adhocDate.setEmployee(loggedEmployee);
					adhocDate.setCreatedate(new Timestamp(System
							.currentTimeMillis()));
					adhocDate.setLastupdate(new Timestamp(System
							.currentTimeMillis()));
					adhocDate.setPoschedule(scheduleData);
					adhocDates.add(adhocDate);
				}
			}
			if (scheduleData.getPoscheduleadhocdates() == null) {
				scheduleData.setPoscheduleadhocdates(adhocDates);
			}
		}

		if (schedulingView.getScheduleExecInitPeriodType() != null) {
			if (schedulingView.getScheduleExecDurationQty() != null) {
				scheduleData.setExecutiondurationqty(new BigDecimal(
						schedulingView.getScheduleExecDurationQty()));
			}
			if (schedulingView.getScheduleExecInitQty() != null) {
				scheduleData.setExecutioninitiateqty(new BigDecimal(
						schedulingView.getScheduleExecInitQty()));
			}
			if (schedulingView.getScheduleExecPeriodType() != null) {
				Integer execDurPeriodTypeId = Integer.parseInt(schedulingView
						.getScheduleExecPeriodType());
				scheduleData
						.setPeriodtypeByExecutiondurationperiodtype(controlSetupDao
								.loadPeriodType(execDurPeriodTypeId));
			}
			if (schedulingView.getScheduleExecInitPeriodType() != null) {
				Integer execInitPeriodTypeId = Integer.parseInt(schedulingView
						.getScheduleExecInitPeriodType());
				scheduleData
						.setPeriodtypeByExecutorinitiateperiodtype(controlSetupDao
								.loadPeriodType(execInitPeriodTypeId));
			}
			if (schedulingView.getScheduleExecWhen() != null) {
				Integer execScheduleWhenId = Integer.parseInt(schedulingView
						.getScheduleExecWhen());
				scheduleData
						.setSchedulewhenByExecutionschedulewhen(controlSetupDao
								.loadScheduleWhen(execScheduleWhenId));
			}
		}

		if (schedulingView.getScheduleSupportInitiatePeriodType() != null) {
			if (schedulingView.getScheduleSupportInitiateQty() != null) {
				scheduleData.setSupportinitiateqty(new BigDecimal(
						schedulingView.getScheduleSupportInitiateQty()));
			}
			if (schedulingView.getScheduleSupportDurationQty() != null) {
				scheduleData.setSupportdurationqty(new BigDecimal(
						schedulingView.getScheduleSupportDurationQty()));
			}
			if (schedulingView.getScheduleSupportInitiatePeriodType() != null) {
				Integer supportInitPeriodTypeId = Integer
						.parseInt(schedulingView
								.getScheduleSupportInitiatePeriodType());
				scheduleData
						.setPeriodtypeBySupportinitiateperiodtype(controlSetupDao
								.loadPeriodType(supportInitPeriodTypeId));
			}
			if (schedulingView.getScheduleSupportDurationPeriodType() != null) {
				Integer supportDurationPeriodTypeId = Integer
						.parseInt(schedulingView
								.getScheduleSupportDurationPeriodType());
				scheduleData
						.setPeriodtypeBySupportdurationperiodtype(controlSetupDao
								.loadPeriodType(supportDurationPeriodTypeId));
			}
			if (schedulingView.getScheduleSupportScheduleWhen() != null) {
				Integer supportScheduleWhenId = Integer.parseInt(schedulingView
						.getScheduleSupportScheduleWhen());
				scheduleData
						.setSchedulewhenBySupportschedulewhen(controlSetupDao
								.loadScheduleWhen(supportScheduleWhenId));
			}
		}

		if (schedulingView.getScheduleQAInitPeriodType() != null) {
			if (schedulingView.getScheduleQADurationQty() != null) {
				scheduleData.setQadurationqty(new BigDecimal(schedulingView
						.getScheduleQADurationQty()));
			}
			if (schedulingView.getScheduleQAInitQty() != null) {
				scheduleData.setQainitiateqty(new BigDecimal(schedulingView
						.getScheduleQAInitQty()));
			}
			if (schedulingView.getScheduleQAInitPeriodType() != null) {
				Integer qaInitPeriodTypeId = Integer.parseInt(schedulingView
						.getScheduleQAInitPeriodType());
				scheduleData
						.setPeriodtypeByQainitiateperiodtype(controlSetupDao
								.loadPeriodType(qaInitPeriodTypeId));
			}
			if (schedulingView.getScheduleQAPeriodType() != null) {
				Integer qaDurnPeriodTypeId = Integer.parseInt(schedulingView
						.getScheduleQAPeriodType());
				scheduleData
						.setPeriodtypeByQadurationperiodtype(controlSetupDao
								.loadPeriodType(qaDurnPeriodTypeId));
			}
			if (schedulingView.getScheduleQAWhen() != null) {
				Integer qaScheduleWhenId = Integer.parseInt(schedulingView
						.getScheduleQAWhen());
				scheduleData.setSchedulewhenByQaschedulewhen(controlSetupDao
						.loadScheduleWhen(qaScheduleWhenId));
			}
		}

		if (schedulingView.getScheduleSignoffInitPeriodType() != null) {
			if (schedulingView.getScheduleSignoffInitQty() != null) {
				scheduleData.setSignoffinitiateqty(new BigDecimal(
						schedulingView.getScheduleSignoffInitQty()));
			}
			if (schedulingView.getScheduleSignoffDurationQty() != null) {
				scheduleData.setSignoffdurationqty(new BigDecimal(
						schedulingView.getScheduleSignoffDurationQty()));
			}
			if (schedulingView.getScheduleSignoffInitPeriodType() != null) {
				Integer signoffInitPeriodTypeId = Integer
						.parseInt(schedulingView
								.getScheduleSignoffInitPeriodType());
				scheduleData
						.setPeriodtypeBySignoffinitiateperiodtype(controlSetupDao
								.loadPeriodType(signoffInitPeriodTypeId));
			}
			if (schedulingView.getScheduleSignoffPeriodType() != null) {
				Integer signoffDurnPeriodTypeId = Integer
						.parseInt(schedulingView.getScheduleSignoffPeriodType());
				scheduleData
						.setPeriodtypeBySignoffdurationperiodtype(controlSetupDao
								.loadPeriodType(signoffDurnPeriodTypeId));
			}
			if (schedulingView.getScheduleSignoffWhen() != null) {
				Integer sognoffScheduleWhenId = Integer.parseInt(schedulingView
						.getScheduleSignoffWhen());
				scheduleData
						.setSchedulewhenBySignoffschedulewhen(controlSetupDao
								.loadScheduleWhen(sognoffScheduleWhenId));
			}
		}

		
		scheduleData.setEmployee(loggedEmployee);
	}

	/**
	 * Createsnew control returns controlid of that.
	 * 
	 * @param createControlview
	 *            ControlView
	 * @param loginUserId
	 *            Integer
	 * @return controlid id of newly created control
	 * @throws MicosException
	 *             Micos Exception
	 */
	public Integer saveControl(ControlView createControlview,Integer loginUserId) throws MicosException {
		LOGGER.entering(getClass().getName(),"saveControl");
		Integer controlId = null;
		Control control = null;    
		boolean copyAttachment = false;
		boolean savePoweroption = false;
		List<Poweroption> powerOptionList = null;
		String statusControl=null;
		Employee loggedEmployee = controlSetupDao.loadEmployee(loginUserId);
		if (null != createControlview && loggedEmployee != null) {
			if (null == createControlview.getId()) {
				// if id is null, then must be new Control creation
				statusAfterSave = MicosConstantValues.Status.SETUP;
				statusAfterSubmit = MicosConstantValues.Status.WAITING_FOR_APPROVAL;
				// To save Power option steps - Start
				control = getDetachedControl();
				if(control != null && control.isCopyControl()){
					savePoweroption = control.isCopyControl();
					controlId = control.getControlid();
				}
				statusControl = null;
				control = populateNewControl(createControlview, loggedEmployee,statusControl);
			} else {
				control = getDetachedControl();
				if (control.getStatus().getStatusdescription()
						.equalsIgnoreCase("Active")) {
					deactivatedControlId = createControlview.getId();
					createControlview.setId(null);
					statusAfterSave = MicosConstantValues.Status.MODIFIED;
					statusAfterSubmit = MicosConstantValues.Status.WAITING_FOR_REAPPROVAL;
					// To save Power option steps - Start
					savePoweroption = true; 
					copyAttachment = true;
					controlId = control.getControlid();
					statusControl = control.getStatus().getStatusdescription();
					control = populateNewControl(createControlview, loggedEmployee, statusControl);
					control.setMastercontrolid(createControlview.getMasterControlId());
				} else {
					if (control.getStatus().getStatuscode().equalsIgnoreCase(MicosConstantValues.Status.INACTIVE)) {
						deactivatedControlId = createControlview.getId();
					} else {
						deactivatedControlId = null;
					}
					statusAfterSave = MicosConstantValues.Status.SETUP;
					statusAfterSubmit = MicosConstantValues.Status.WAITING_FOR_APPROVAL;
					statusControl = control.getStatus().getStatusdescription();
					updateControlData(createControlview, control,loggedEmployee,statusControl);
				}
			}
			if (deactivatedControlId != null) {
				List<Integer> deactivatedControlIdList = controlSetupDao.getListofDeactivatedControls();
				if (deactivatedControlIdList != null) {
					Iterator itr = deactivatedControlIdList.iterator();
					for (int i = 0; i < deactivatedControlIdList.size(); i++) {
						if (((Integer)itr.next()).equals(deactivatedControlId)) {
							createControlview.setModifyDeactiveControl(true);
							savePoweroption = false;
						}
					}
				}
			}
			if (createControlview.isModifyDeactiveControl() == false) {
				controlSetupDao.saveOrUpdate(control);
			}
        /*
		 * Save Power option steps first time incase of Copy Control and
		 * Modifying Active Control
		 */
			if (savePoweroption) {
				statusControl="Active";
				powerOptionList = getPowerOptionSteps(control.getControlid(),
						controlId, loggedEmployee, copyAttachment,statusControl);
				if (!SystemUtil.isNull(powerOptionList)) {
					// Update dates to Next Schedule cycle
					for (Poweroption powerOption : powerOptionList) {
						recalculateDatesForEditControl(powerOption);
					}
					controlSetupDao.saveOrUpdatePowerOption(powerOptionList);
				}
			}
			controlId = control.getControlid();
			LOGGER.exiting(getClass().getName(), "saveControl");
		}
		return controlId;
	}

  
	/**
	 * Populate Power option steps for save
	 * 
	 * @param controlId
	 *            Integer
	 * @param oldControlId
	 *            Integer
	 * @param loggedEmployee
	 *            Employee object
	 * @param copyAttachment
	 *            boolean value
	 * @return powerOptionList
	 * @throws MicosException
	 *             exception
	 * 
	 */
	private List<Poweroption> getPowerOptionSteps(Integer controlId,
			Integer oldControlId, Employee loggedEmployee,
			boolean copyAttachment,String statusControl) throws MicosException {
		LOGGER.entering(getClass().getName(), "getPowerOptionSteps");
		List<Poweroption> powerOptionList = new ArrayList<Poweroption>();
		List<ControlStepView> stepview = null;
    Poweroption powerOption = null;
    stepview = populatePowerOptions(oldControlId,
        Constants.DEFAULT_LANGUAGE_CODE);
    if (!SystemUtil.isNull(stepview)) {
      Iterator itrStep = stepview.iterator();
      while (itrStep.hasNext()) {
        ControlStepView stepView = (ControlStepView)itrStep.next();
        if (stepView != null) {
          // nullify Schedule Adhoc dates
          if (stepView.getSchedulingView() != null
              && stepView.getSchedulingView().getScheduleAdhocDateViewList() != null) {
            for (ScheduleAdhocDateView adhocDateView : stepView
                .getSchedulingView().getScheduleAdhocDateViewList()) {
              if (adhocDateView != null) {
                adhocDateView.setId(null);
              }
            }
          }
          powerOption = new Poweroption();
          populateNewPowerOptionData(powerOption, stepView, loggedEmployee,
              controlId,statusControl);
          powerOptionList.add(powerOption);
        }

      }
    }
    LOGGER.exiting(getClass().getName(), "getPowerOptionSteps");
    return powerOptionList;
	}

  /**
	 * Recalculate dates for Running Control
	 * 
	 * @param powerOption
	 *            Poweroption object
   * @throws MicosException Exception
	 */
  private void recalculateDatesForEditControl(Poweroption powerOption)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "recalculateDatesForEditControl");
    try {
      
      Set<Poschedule> poSchedules = powerOption.getPoschedules();
      if(poSchedules == null) {
        LOGGER.exiting(getClass().getName(), "recalculateDatesForEditControl",
            "poSchedules is NULL");
        return;
      }
      Iterator<Poschedule> itr = poSchedules.iterator();
      Poschedule poSchedule = null;
      while(itr.hasNext()) {
        poSchedule = itr.next();
      }
      if(poSchedule == null) {
        LOGGER.exiting(getClass().getName(), "recalculateDatesForEditControl",
            "poSchedule is NULL");
        return;
      }
      Date startDate = poSchedule.getStartdate();
      Date endDate = poSchedule.getEnddate();
      Date cpStart = powerOption.getCoverageperiodstart();
      Date cpEnd = powerOption.getCoverageperiodend();
      int periodicFrequency = 0;
      if(poSchedule.getPeriodtypeByPatternfrequency() != null) {
    	  periodicFrequency = poSchedule.getPeriodtypeByPatternfrequency().getPeriodtypeid();
      }
     
      int periodicQty = 0;
      if(poSchedule.getPatternqty() != null) {
    	  periodicQty = poSchedule.getPatternqty().intValue();
      }
      
      // Rolling dates
      RollingDates rollingDates = new RollingDates();
      long poScheduleId = 0;
      if(poSchedule.getPoscheduleid() != null) 
      {
        poScheduleId = new Long(poSchedule.getPoscheduleid()).longValue();
      }
      // Recalculate new dates
      String[] weekDays = createPOWeekdayArr(poSchedule);
      SchedulerUtils.moveControlStepDates(startDate, endDate, cpStart, cpEnd,
          periodicFrequency, periodicQty, rollingDates, weekDays, 
          poSchedule.getPeriodicadhoc(), 
          poScheduleId,
          new Long(0).longValue());

      /** Setting new dates - START */
      poSchedule.setStartdate(rollingDates.getNextScheduledDate());
      powerOption.setCoverageperiodstart(rollingDates.getCoveragePeriodStartDate());
      powerOption.setCoverageperiodend(rollingDates.getCoveragePeriodEndDate());
      powerOption.setNextscheduledate(rollingDates.getNextScheduledDate());
      /** Setting new dates - END */

    } catch (Exception ex) {
      // handle error while recalculating dates
      LOGGER.error(getClass().getName() + ":recalculateDatesForEditControl:"
          + ex);
      throw new MicosException(ExceptionContext.ERROR, ex, SystemUtil
          .getErrorMessage(ex));
    }
    LOGGER.exiting(getClass().getName(), "recalculateDatesForEditControl");
  }

  /**
   * @param poSchedule schedulingView
   * @return days array
   */
  private String[] createPOWeekdayArr(Poschedule poSchedule){
    String[] daysArr = new String[] {"","","","","","",""};
    if(poSchedule.getSunday() != null && poSchedule.getSunday() == AttributeOptions.Y) {
      daysArr[0] = "X";
    }
    if(poSchedule.getMonday() != null && poSchedule.getMonday() == AttributeOptions.Y) {
      daysArr[1] = "X";
    }
    
    if(poSchedule.getTuesday() != null && poSchedule.getTuesday() == AttributeOptions.Y) {
      daysArr[2] = "X";
    }
    if(poSchedule.getWednesday() != null && poSchedule.getWednesday() == AttributeOptions.Y) {
      daysArr[3] = "X";
    }
    if(poSchedule.getThursday() != null && poSchedule.getThursday() == AttributeOptions.Y) {
      daysArr[4] = "X";
    }
    if(poSchedule.getSaturday() != null && poSchedule.getFriday() == AttributeOptions.Y) {
      daysArr[5] = "X";
    }
    if(poSchedule.getSaturday() != null && poSchedule.getSaturday() == AttributeOptions.Y) {
      daysArr[6] = "X";
    }
    return daysArr;
    
  }
  
	/**
   * populate NewControl
   * 
   * @param createControlview
   *          ControlView
   * @param loggedEmployee
   *          Employee
   * @return newControl
   * @throws MicosException
   *           Micos Exception
   */
	private Control populateNewControl(ControlView createControlview,
			Employee loggedEmployee, String statusControl) throws MicosException {
		LOGGER.entering(getClass().getName(),"populateNewControl");
		Control newControl = new Control();
		try {
			if(deactivatedControlId != null){
				Control con = controlSetupDao.loadControl(deactivatedControlId);
				newControl.setDeactivatedcontrol(con);	
			}else{
				newControl.setDeactivatedcontrol(null);
			}
			// title
			Multilanguagetranslation multiLanguageTranslationByTitle = new Multilanguagetranslation();
			multiLanguageTranslationByTitle
					.setTranslations(populateTranslation(createControlview
							.getControlTitleObj(),
							multiLanguageTranslationByTitle, loggedEmployee));
			newControl
					.setMultilanguagetranslationByTitle(multiLanguageTranslationByTitle);

			Multilanguagetranslation multiLanguageTranslationByDescription = new Multilanguagetranslation();
			multiLanguageTranslationByDescription
					.setTranslations(populateTranslation(createControlview
							.getControlDescriptioneObj(),
							multiLanguageTranslationByDescription,
							loggedEmployee));
			newControl
					.setMultilanguagetranslationByDescription(multiLanguageTranslationByDescription);

			if (checkDetailedDescription(createControlview
					.getControlDetailDescriptionObj())) {
				Multilanguagetranslation multiLanguageTranslationByDetailDescription = new Multilanguagetranslation();
				multiLanguageTranslationByDetailDescription
						.setTranslations(populateTranslation(createControlview
								.getControlDetailDescriptionObj(),
								multiLanguageTranslationByDetailDescription,
								loggedEmployee));
				newControl
						.setMultilanguagetranslationByDetaildescription(multiLanguageTranslationByDetailDescription);
			}

			newControl.setTools(createControlview.getTools());
      
      newControl.setControlreference(createControlview.getControlReference());

			if (createControlview.getControlTypeId() != null) {
				newControl.setControltype(controlSetupDao
						.loadControlType(createControlview.getControlTypeId()));
			}

			if (createControlview.getControlAorOwnerId() != null) {
				newControl
						.setAorowner(controlSetupDao
								.loadAorowner(createControlview
										.getControlAorOwnerId()));
			}

			if (createControlview.getValidUntil() != null) {
				newControl.setEnddate(createControlview.getValidUntil());
			}
      if (createControlview.isCheckShowSwissData()) {
        newControl.setOverrideswissdatapolicy("Y");
      } else {
        newControl.setOverrideswissdatapolicy("N");
      }
			Integer rfcId = createControlview.getRfcId();

			if (rfcId != null) {
				Rfc rfc = controlSetupDao.loadRfc(rfcId);

				Controlrfc controlRfc = new Controlrfc();

				controlRfc.setRfc(rfc);
				controlRfc.setCreatedate(new Timestamp(System
						.currentTimeMillis()));
				controlRfc.setLastupdate(new Timestamp(System
						.currentTimeMillis()));
				controlRfc.setEmployee(loggedEmployee);

				if (createControlview.isSubmitStatus()) {
					controlRfc.setStatus(controlSetupDao
							.getStatusByCode(statusAfterSubmit));

				} else {
					controlRfc.setStatus(controlSetupDao
							.getStatusByCode(statusAfterSave));
				}

				controlRfc.setControl(newControl);

				Set<Controlrfc> contRfcSet = new HashSet<Controlrfc>();
				contRfcSet.add(controlRfc);
				newControl.setControlrfcs(contRfcSet);
			}

			Status status = null;
			if (createControlview.isSubmitStatus()) {
				status = controlSetupDao.getStatusByCode(statusAfterSubmit);

			} else {
				status = controlSetupDao.getStatusByCode(statusAfterSave);
			}

			newControl.setStatus(status);

			List<ControlView> controlReferences = createControlview
					.getControlReferences();
			Set<Controlreference> controlRefSet = new HashSet<Controlreference>();
			if (!SystemUtil.isNull(controlReferences)) {
				for (Iterator iter = controlReferences.iterator(); iter
						.hasNext();) {
					ControlView controlView = (ControlView) iter.next();
					Controlreference controlReference = new Controlreference();
					controlReference.setControlByFromcontrol(newControl);
					controlReference.setControlByTocontrol(controlSetupDao
							.loadControl(controlView.getId()));
					controlReference.setCreatedate(DateUtil
							.getCurrentTimestamp());
					controlReference.setEmployee(loggedEmployee);
					controlReference.setLastupdate(DateUtil
							.getCurrentTimestamp());
					controlReference.setStatus(controlSetupDao
							.getStatusByCode(MicosConstantValues.Status.ACTIVE));
					controlRefSet.add(controlReference);
				}
			}
			newControl.setControlreferencesByTocontrol(controlRefSet);

			List attachmentListForControl = createControlview
					.getListOfAttachmentsForControl();

			if (!SystemUtil.isNull(attachmentListForControl)) {
				Set<Multiattachment> multiattachmentSet = new HashSet<Multiattachment>();
				Iterator itAttachView = attachmentListForControl.iterator();
				while (itAttachView.hasNext()) {
					AttachmentView lAttachView = (AttachmentView) itAttachView
							.next();
					Multiattachment lMultiattachment = new Multiattachment();

					populateAttachmentData(loggedEmployee, lAttachView,
							lMultiattachment);
					lMultiattachment.setControl(newControl);
					multiattachmentSet.add(lMultiattachment);
				}
				newControl.setMultiattachments(multiattachmentSet);
			}

			List linkListForControl = createControlview
					.getListOfLinksForControl();

			if (!SystemUtil.isNull(linkListForControl)) {
				Set<Multilink> multilinkSet = new HashSet<Multilink>();
				Iterator itLinkView = linkListForControl.iterator();
				while (itLinkView.hasNext()) {
					LinkView lLinkView = (LinkView) itLinkView.next();
					// Create a multilink for link
					Multilink lMultilink = new Multilink();

					populateLinkData(loggedEmployee, lLinkView, lMultilink);

					lMultilink.setControl(newControl);
					multilinkSet.add(lMultilink);

				}
				newControl.setMultilinks(multilinkSet);
			}
			newControl.setCreatedate(DateUtil.getCurrentTimestamp());
			newControl.setLastupdate(DateUtil.getCurrentTimestamp());
			newControl.setEmployee(loggedEmployee);
			// Automation and Line of Defense
      newControl.setAutomation(createControlview.getAutomation());
      newControl.setLineofdefense(createControlview.getLineofDefense());
      
			populateSteps(newControl, createControlview, loggedEmployee, statusControl);
			
			

		} catch (MicosException micosException) {
			LOGGER.error("populateNewControl", micosException);
			throw micosException;
		} 
		return newControl;
	}

	/**
	 * @param loggedEmployee
	 *            Employee
	 * @param lAttachmentView
	 *            the Attachment View
	 * @param lMultiattachment
	 *            Multiattachment
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void populateAttachmentData(Employee loggedEmployee,
			AttachmentView lAttachmentView, Multiattachment lMultiattachment)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateAttachmentData");
		Attachment lAttachment = new Attachment();
		lAttachment.setAttachmentfilename(lAttachmentView.getFileName());
		lAttachment.setAttachmentsize(lAttachmentView.getSize());
		lAttachment.setCreatedate(DateUtil.getCurrentTimestamp());
		lAttachment.setLastupdate(DateUtil.getCurrentTimestamp());
		lAttachment.setEmployee(loggedEmployee);
    Blob attachmentContent = lAttachmentView.getAttachment();
    if (attachmentContent == null && lAttachmentView.getId() != null) {
      Attachment attachment = controlSetupDao
          .getSelectedAttachment(lAttachmentView.getId().toString());
      if (attachment != null) {
        attachmentContent = attachment.getAttachment();
      }
    }
    lAttachment.setAttachment(attachmentContent);

    if (null != lAttachmentView.getAttachmentExtension()) {
      lAttachment.setAttachmenttype(controlSetupDao
          .getAttachmentTypeforAttachment(lAttachmentView
              .getAttachmentExtension()));
    }

    lMultiattachment.setAttachment(lAttachment);
    lMultiattachment.setCreatedate(DateUtil.getCurrentTimestamp());
		lMultiattachment.setLastupdate(DateUtil.getCurrentTimestamp());
		lMultiattachment.setEmployee(loggedEmployee);
		Set<Multiattachment> multiattachmentSetForAttachment = new HashSet<Multiattachment>();
		multiattachmentSetForAttachment.add(lMultiattachment);
		lAttachment.setMultiattachments(multiattachmentSetForAttachment);
	}

	/**
	 * @param loggedEmployee
	 *            Employee
	 * @param lLinkView
	 *            LinkView
	 * @param lMultilink
	 *            Multilink
	 */
	private void populateLinkData(Employee loggedEmployee, LinkView lLinkView,
			Multilink lMultilink) {
		LOGGER.entering(getClass().getName(),"populateLinkData");
		Link lLink = new Link();
		lLink.setLinkname(lLinkView.getLinkName());
		lLink.setLinkurl(lLinkView.getLinkUrl());
		lLink.setCreatedate(DateUtil.getCurrentTimestamp());
		lLink.setLastupdate(DateUtil.getCurrentTimestamp());
		lLink.setEmployee(loggedEmployee);

		lMultilink.setLink(lLink);
		lMultilink.setCreatedate(DateUtil.getCurrentTimestamp());
		lMultilink.setLastupdate(DateUtil.getCurrentTimestamp());
		lMultilink.setEmployee(loggedEmployee);
		Set<Multilink> multilinkSetForLink = new HashSet<Multilink>();
		multilinkSetForLink.add(lMultilink);
		lLink.setMultilinks(multilinkSetForLink);
	}

	/**
	 * populate Steps
	 * 
	 * @param newControl
	 *            Control
	 * @param createControlview
	 *            ControlView
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	@SuppressWarnings("unchecked")
  private void populateSteps(Control newControl,
			ControlView createControlview, Employee loggedEmployee, String statusControl)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateSteps");
    Setup setup = new Setup();
    setup.setCreatedate(DateUtil.getCurrentTimestamp());
    setup.setLastupdate(DateUtil.getCurrentTimestamp());
    setup.setEmployee(loggedEmployee);
    setup.setControl(newControl);

    if (createControlview.isSubmitStatus()) {
      setup.setStatus(controlSetupDao
          .getStatusByCode(MicosConstantValues.Status.WAITING_FOR_APPROVAL));

    } else {
      setup.setStatus(controlSetupDao
          .getStatusByCode(MicosConstantValues.Status.SETUP));
    }

    Set<Step> stepSet = new HashSet<Step>();
    HashMap stepDependsOnMap = new HashMap();

    List<ControlStepView> stepViewList = createControlview
        .getControlStepViewList();
    if (!SystemUtil.isNull(stepViewList)) {
      for (Iterator iter = stepViewList.iterator(); iter.hasNext();) {
        ControlStepView controlStepView = (ControlStepView)iter.next();
        Step newStep = new Step();
        populateControlStepData(loggedEmployee, controlStepView, newStep,statusControl);
        newStep.setSetup(setup);
        stepSet.add(newStep);
        stepDependsOnMap.put(controlStepView.getTempId(), newStep);
      }
      for (Iterator iter = stepViewList.iterator(); iter.hasNext();) {
        ControlStepView controlStepView = (ControlStepView)iter.next();
        if (controlStepView.getDependsOn() != null) {

          Step depStep = (Step)stepDependsOnMap.get(controlStepView
              .getDependsOn());
          Step creatingStep = (Step)stepDependsOnMap.get(controlStepView
              .getTempId());
          creatingStep.setStep(depStep);
        }
      }
    }

    setup.setSteps(stepSet);
    Set<Setup> setupSet = new HashSet<Setup>();
    setupSet.add(setup);
    newControl.setSetups(setupSet);

    LOGGER.exiting(getClass().getName(), "populateSteps");
	}

	/**
	 * populate Control StepData
	 * 
	 * @param loggedEmployee
	 *            Employee
	 * @param controlStepView
	 *            ControlStepView
	 * @param newStep
	 *            Step
	 * @throws MicosException
	 *             MicosException
	 */
	private void populateControlStepData(Employee loggedEmployee,
			ControlStepView controlStepView, Step newStep, String statusControl)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateControlStepData");
		newStep.setTitle(controlStepView.getStepTitle());
    /*
     * Using temp Id to set hash code incase of new Step 
     * where Step Id is null
     */ 
    newStep.setTempId(controlStepView.getTempId());
		// CR11: Coverage Period for Controls.
		if (controlStepView.getStepStartDate() != null) {
			newStep.setCoverageperiodstartdate(controlStepView.getStepStartDate());
		}
		if (controlStepView.getStepEndDate() != null) {
			newStep.setCoverageperiodenddate(controlStepView.getStepEndDate());
		}
		// end
		// Next Schedule date
	    if (controlStepView.getSchedulingView() != null) {
			if (controlStepView.getSchedulingView().getSchedulePeriodicAdhoc()
					.equalsIgnoreCase(ScheduleType.PERIODIC)) {
				Integer patternFreq = Integer.parseInt(controlStepView
						.getSchedulingView().getSchedulePatternFreq());
				if (patternFreq.equals(7)) {
					try {
						newStep.setNextscheduledate(SchedulerUtils
								.getNextWeeklyScheduleDate(controlStepView
										.getSchedulingView()
										.getScheduleStartDate(),
										controlStepView.getSchedulingView()
												.getScheduleEndDate(),
										createWeekdayArr(controlStepView
												.getSchedulingView()),
										patternFreq));
					} catch (Exception e) {
						LOGGER.error("populateControlStepData", e);
						throw new MicosException(e);
					}
				} else {
					newStep.setNextscheduledate(controlStepView
							.getSchedulingView().getScheduleStartDate());
				}
			} else {
				newStep.setNextscheduledate(controlStepView.getSchedulingView()
						.getScheduleStartDate());
			}
		}
		if (controlStepView.getCheckSupport()) {
			newStep
					.setSupport(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
			populateSupportSchema(loggedEmployee, controlStepView, newStep);
		} else {
			newStep
					.setSupport(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		if (controlStepView.getCheckPerformance()) {
			newStep
					.setExecution(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
			populateExecutionSchema(loggedEmployee, controlStepView, newStep);
			 if (SystemUtil.isNotNull(controlStepView
						.getPerformanceRatinggroup())) {
			    	newStep.setExecutionratinggroup(controlSetupDao
							.retrieveRatinggroupForId(Integer.parseInt(controlStepView
											.getPerformanceRatinggroup())));
			 }
		} else {
			newStep
					.setExecution(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		if (controlStepView.getCheckQA()) {
			newStep.setQa(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
			populateQaSchema(loggedEmployee, controlStepView, newStep);
		} else {
			newStep.setQa(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		if (controlStepView.getCheckSignOff()) {
			newStep
					.setSignoff(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
			populateSignoffSchema(loggedEmployee, controlStepView, newStep);
			if (SystemUtil.isNotNull(controlStepView
					.getSignoffRatinggroup())) {
				newStep.setSignoffratinggroup(controlSetupDao
						.retrieveRatinggroupForId(Integer.parseInt(controlStepView
										.getSignoffRatinggroup())));
			}
		} else {
			newStep
					.setSignoff(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		if (controlStepView.getSchedulingView() != null) {
			Schedule schedule = new Schedule();
			schedule.setCreatedate(new Timestamp(System.currentTimeMillis()));
			schedule.setLastupdate(new Timestamp(System.currentTimeMillis()));
			copySchedulingViewToData(controlStepView.getSchedulingView(),
					loggedEmployee, schedule);
			schedule.setStep(newStep);
			Set<Schedule> scheduleSet = new HashSet<Schedule>();
			scheduleSet.add(schedule);
			newStep.setSchedules(scheduleSet);
		}

		Set<ControlRemWarEsc> setControlRemWarEsc=createEditNotification(controlStepView, false , null ,newStep,statusControl);
		newStep.setControlRemWarEscStep(setControlRemWarEsc);
		
		newStep.setCreatedate(DateUtil.getCurrentTimestamp());
		newStep.setLastupdate(DateUtil.getCurrentTimestamp());
		newStep.setEmployee(loggedEmployee);
    // Steptype as "S" step setup
    newStep.setStepType(MicosConstantValues.StepType.CONTROL_STEP);
	}

	/**
	 * @param schedulingView schedulingView
	 * @return days array
	 */
	private String[] createWeekdayArr(SchedulingView schedulingView){
		String[] daysArr = new String[] {"","","","","","",""};
		if(schedulingView.getSunday() == AttributeOptions.Y) {
			daysArr[0] = "X";
		}
		if(schedulingView.getMonday() == AttributeOptions.Y) {
			daysArr[1] = "X";
		}
		
		if(schedulingView.getTuesday() == AttributeOptions.Y) {
			daysArr[2] = "X";
		}
		if(schedulingView.getWednesday() == AttributeOptions.Y) {
			daysArr[3] = "X";
		}
		if(schedulingView.getThursday() == AttributeOptions.Y) {
			daysArr[4] = "X";
		}
		if(schedulingView.getFriday() == AttributeOptions.Y) {
			daysArr[5] = "X";
		}
		if(schedulingView.getSaturday() == AttributeOptions.Y) {
			daysArr[6] = "X";
		}
		return daysArr;
		
	}
	
	/**
	 * populate SupportSchema
	 * 
	 * @param loggedEmployee
	 *            Employee
	 * @param controlStepView
	 *            ControlStepView
	 * @param newStep
	 *            Step
	 * @throws MicosException
	 *             MicosException
	 */
	private void populateSupportSchema(Employee loggedEmployee,
			ControlStepView controlStepView, Step newStep)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateSupportSchema");
		Supportschema supportSchema = new Supportschema();
		supportSchema.setCreatedate(new Timestamp(System.currentTimeMillis()));
		supportSchema.setLastupdate(new Timestamp(System.currentTimeMillis()));
		supportSchema.setEmployee(loggedEmployee);

		if (controlStepView.getSupportSchemaView() != null) {

			Set<Executor> executorSet = new HashSet<Executor>();
			List<EmployeeView> employeesViaPid = controlStepView
					.getSupportSchemaView().getSelectionViaPIDView();

			List<HierarchyElementView> hierarchyElements = controlStepView
					.getSupportSchemaView().getSelectionViaHierarchyView();

			if (!SystemUtil.isNull(employeesViaPid)
					|| !SystemUtil.isNull(hierarchyElements)) {
				Executor executor = new Executor();
				executor.setSupportschema(supportSchema);
				if (!SystemUtil.isNull(employeesViaPid)) {
					executor.setExecutorpids(updateExecutorPid(employeesViaPid,
							loggedEmployee, executor));
					executorSet.add(executor);
				}
				if (!SystemUtil.isNull(hierarchyElements)) {
					executor.setExecutoraors(updateExecutoraAor(
							hierarchyElements, loggedEmployee, executor));
					executorSet.add(executor);
				}
				executor.setCreatedate(new Timestamp(System.currentTimeMillis()));
				executor.setLastupdate(new Timestamp(System.currentTimeMillis()));
				executor.setEmployee(loggedEmployee);
			}
			supportSchema.setExecutors(executorSet);
		}
		newStep.setSupportschema(supportSchema);
	}
  
 /**
	 * populate ExecutionSchema
	 * 
	 * @param loggedEmployee
	 *            Employee
	 * @param controlStepView
	 *            ControlStepView
	 * @param newStep
	 *            Step
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void populateExecutionSchema(Employee loggedEmployee,
			ControlStepView controlStepView, Step newStep)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateExecutionSchema");
		Executionschema executionSchema = new Executionschema();
		executionSchema.setCreatedate(new Timestamp(System.currentTimeMillis()));
		executionSchema.setLastupdate(new Timestamp(System.currentTimeMillis()));
		executionSchema.setEmployee(loggedEmployee);

		if (controlStepView.getPerformanceSchemaView() != null) {

			Set<Executor> executorSet = new HashSet<Executor>();
			List<EmployeeView> employeesViaPid = controlStepView
					.getPerformanceSchemaView().getSelectionViaPIDView();

			List<HierarchyElementView> hierarchyElements = controlStepView
					.getPerformanceSchemaView().getSelectionViaHierarchyView();

			if (!SystemUtil.isNull(employeesViaPid)
					|| !SystemUtil.isNull(hierarchyElements)) {
				Executor executor = new Executor();
				executor.setExecutionschema(executionSchema);
				if (!SystemUtil.isNull(employeesViaPid)) {
					executor.setExecutorpids(updateExecutorPid(employeesViaPid,
							loggedEmployee, executor));
					executorSet.add(executor);
				}
				if (!SystemUtil.isNull(hierarchyElements)) {
					executor.setExecutoraors(updateExecutoraAor(
							hierarchyElements, loggedEmployee, executor));
					executorSet.add(executor);
				}
				executor
						.setCreatedate(new Timestamp(System.currentTimeMillis()));
				executor
						.setLastupdate(new Timestamp(System.currentTimeMillis()));
				executor.setEmployee(loggedEmployee);
			}
			executionSchema.setExecutors(executorSet);
		}

		newStep.setExecutionschema(executionSchema);
	}

	/**
   * populate QaSchema
   * 
   * @param loggedEmployee
   *          Employee
   * @param controlStepView
   *          ControlStepView
   * @param newStep
   *          Step
   * @throws MicosException
   *           Micos Exception
   */
  private void populateQaSchema(Employee loggedEmployee,
			ControlStepView controlStepView, Step newStep)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateQaSchema");
		Qaschema qaSchema = new Qaschema();
		qaSchema.setCreatedate(new Timestamp(System.currentTimeMillis()));
		qaSchema.setLastupdate(new Timestamp(System.currentTimeMillis()));
		qaSchema.setEmployee(loggedEmployee);
		if (controlStepView.getQaSchemaView() != null) {
			Set<Executor> executorSet = new HashSet<Executor>();
			List<EmployeeView> employeesViaPid = controlStepView
					.getQaSchemaView().getSelectionViaPIDView();
			List<HierarchyElementView> hierarchyElements = controlStepView
					.getQaSchemaView().getSelectionViaHierarchyView();
			if (!SystemUtil.isNull(employeesViaPid)
					|| !SystemUtil.isNull(hierarchyElements)) {
				Executor executor = new Executor();
				executor.setQaschema(qaSchema);
				if (!SystemUtil.isNull(employeesViaPid)) {
					executor.setExecutorpids(updateExecutorPid(employeesViaPid,
							loggedEmployee, executor));
					executorSet.add(executor);
				}
				if (!SystemUtil.isNull(hierarchyElements)) {
					executor.setExecutoraors(updateExecutoraAor(
							hierarchyElements, loggedEmployee, executor));
					executorSet.add(executor);
				}
				executor.setCreatedate(new Timestamp(System.currentTimeMillis()));
				executor.setLastupdate(new Timestamp(System.currentTimeMillis()));
				executor.setEmployee(loggedEmployee);
			}
			qaSchema.setExecutors(executorSet);
		}
		newStep.setQaschema(qaSchema);
  }

	/**
	 * populate SignoffSchema
	 * 
	 * @param loggedEmployee
	 *            Employee
	 * @param controlStepView
	 *            ControlStepView
	 * @param newStep
	 *            Step
	 * @throws MicosException
	 *             Micos Exception
	 */
  private void populateSignoffSchema(Employee loggedEmployee,
			ControlStepView controlStepView, Step newStep)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateSignoffSchema");
		Signoffschema signoffSchema = new Signoffschema();
		signoffSchema.setCreatedate(new Timestamp(System.currentTimeMillis()));
		signoffSchema.setLastupdate(new Timestamp(System.currentTimeMillis()));
		signoffSchema.setEmployee(loggedEmployee);

		if (controlStepView.getSignOffSchemaView() != null) {

			Set<Executor> executorSet = new HashSet<Executor>();
			List<EmployeeView> employeesViaPid = controlStepView
					.getSignOffSchemaView().getSelectionViaPIDView();
			List<HierarchyElementView> hierarchyElements = controlStepView
					.getSignOffSchemaView().getSelectionViaHierarchyView();

			if (!SystemUtil.isNull(employeesViaPid)
					|| !SystemUtil.isNull(hierarchyElements)) {
				Executor executor = new Executor();
				executor.setSignoffschema(signoffSchema);
				if (!SystemUtil.isNull(employeesViaPid)) {
					executor.setExecutorpids(updateExecutorPid(employeesViaPid,
							loggedEmployee, executor));
					executorSet.add(executor);
				}
				if (!SystemUtil.isNull(hierarchyElements)) {
					executor.setExecutoraors(updateExecutoraAor(
							hierarchyElements, loggedEmployee, executor));
					executorSet.add(executor);
				}
				executor
						.setCreatedate(new Timestamp(System.currentTimeMillis()));
				executor
						.setLastupdate(new Timestamp(System.currentTimeMillis()));
				executor.setEmployee(loggedEmployee);
			}
			signoffSchema.setExecutors(executorSet);
			signoffSchema.setControlgovernancesignoff(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		newStep.setSignoffschema(signoffSchema);
  }

	/**
	 * populate MultiLang
	 * 
	 * @return multiLang
	 * @param languageCodeView
	 *            List
	 * @param mulLangTranslation
	 *            mulLangTranslation
	 * @param employee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	private Set<Translation> populateTranslation(
			LanguageCodeView[] languageCodeView,
			Multilanguagetranslation mulLangTranslation, Employee employee)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateTranslation");
		Set<Translation> translationSet = new HashSet<Translation>();
		Supportedlanguage supportedLanguage = null;
		int langCodeCount = languageCodeView.length;
		for (int count = 0; count < langCodeCount; count++) {
			Translation translation = new Translation();
			translation.setTranslatedtext((SystemUtil
					.getClobFromString(languageCodeView[count].getValue())));
			translation.setMultilanguagetranslation(mulLangTranslation);
			supportedLanguage = controlSetupDao
					.loadSupportedLanguage(languageCodeView[count]
							.getLanguageType());
			translation.setSupportedlanguage(supportedLanguage);
			translation.setCreatedate(DateUtil.getCurrentTimestamp());
			translation.setLastupdate(DateUtil.getCurrentTimestamp());
			translation.setEmployee(employee);
			translationSet.add(translation);
			// hibernateSession.saveOrUpdate(translation);
		}
		return translationSet;
	}

	/**
	 * Copies all control data from the Control view to the data object
	 * 
	 * @param controlView
	 *            the Control view object
	 * @param controlData
	 *            the Control data object
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             MicosException
	 */
	private void updateControlData(ControlView controlView,
			Control controlData, Employee loggedEmployee, String statusControl) throws MicosException {
		LOGGER.entering(getClass().getName(),"updateControlData");
		updateControlProperties(controlView, controlData, loggedEmployee);
		updateControlSetup(controlView, controlData, loggedEmployee,statusControl);

	}

	/**
	 * @param controlView
	 *            ControlView
	 * @param controlData
	 *            Control
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             MicosException
	 */
	private void updateControlProperties(ControlView controlView,
			Control controlData, Employee loggedEmployee) throws MicosException {
		LOGGER.entering(getClass().getName(),"updateControlProperties");
		if (controlView.getControlTypeId() != null) {
			controlData.setControltype(controlSetupDao
					.loadControlType(controlView.getControlTypeId()));
		}

		if (controlView.getControlAorOwnerId() != null) {
			controlData.setAorowner(controlSetupDao.loadAorowner(controlView
					.getControlAorOwnerId()));
		}
		controlData.setEmployee(loggedEmployee);
		Status controlStatus = controlData.getStatus();
		String controlStatusCode = controlStatus.getStatuscode();
		boolean modified = false;
		Status modifiedStatus = null;
		if (!controlStatusCode.equals(MicosConstantValues.Status.SETUP)) {
			// if other than setup status
			modified = true;
			modifiedStatus = controlSetupDao
					.loadStatus(MicosConstantValues.Status.MODIFIED);
			controlData.setStatus(modifiedStatus);
		}

		Integer rfcId = controlView.getRfcId();
		if (rfcId != null) {
			Rfc rfc = controlSetupDao.loadRfc(rfcId);
			Set<Controlrfc> contRfcSet = controlData.getControlrfcs();
			Controlrfc controlRfc = null;
			// as RFC is a required field, while update there must be a not null
			// set
			// so not checking for null, also though it is a set it contains
			// only one entry
			// according to business rule
			for (Iterator iter = contRfcSet.iterator(); iter.hasNext();) {
				controlRfc = (Controlrfc) iter.next();
			}

			controlRfc.setRfc(rfc);
			controlRfc.setEmployee(loggedEmployee);
			if (modified) {
				controlRfc.setStatus(modifiedStatus);
			}
		}

		if (controlView.getValidUntil() != null) {
			controlData.setEnddate(controlView.getValidUntil());
		}

		updateMultilanguageText(controlData
				.getMultilanguagetranslationByTitle(), controlView
				.getControlTitleObj(), loggedEmployee);
		updateMultilanguageText(controlData
				.getMultilanguagetranslationByDescription(), controlView
				.getControlDescriptioneObj(), loggedEmployee);

		if (controlData.getMultilanguagetranslationByDetaildescription() != null
				&& controlData.getMultilanguagetranslationByDetaildescription()
						.getControlsByDetaildescription() != null) {
			updateDetailedDescriptionMultilanguageText(controlData
					.getMultilanguagetranslationByDetaildescription(),
					controlView.getControlDetailDescriptionObj(),
					loggedEmployee);
		} else if (checkDetailedDescription(controlView
				.getControlDetailDescriptionObj())) {
			Multilanguagetranslation multiLanguageTranslationByDetailDescription = new Multilanguagetranslation();
			multiLanguageTranslationByDetailDescription
					.setTranslations(populateTranslation(controlView
							.getControlDetailDescriptionObj(),
							multiLanguageTranslationByDetailDescription,
							loggedEmployee));
			controlData
					.setMultilanguagetranslationByDetaildescription(multiLanguageTranslationByDetailDescription);
		}
		controlData.setTools(controlView.getTools());
		// Automation and Line of defense
    controlData.setAutomation(controlView.getAutomation());
    controlData.setLineofdefense(controlView.getLineofDefense());
    if (controlView.isCheckShowSwissData()) {
      controlData.setOverrideswissdatapolicy("Y");
    } else {
      controlData.setOverrideswissdatapolicy("N");
    }    
		// controls refered from this control
		updateControlReferences(
				controlData.getControlreferencesByFromcontrol(), controlView
						.getControlReferences(), loggedEmployee);
		updateControlAttachment(controlData.getMultiattachments(), controlView
				.getListOfAttachmentsForControl(), loggedEmployee);
		updateControlLinks(controlData.getMultilinks(), controlView
				.getListOfLinksForControl(), loggedEmployee);
	}

	/**
	 * Method to check whether detailed description is entered or not
	 * 
	 * @param multilang
	 *            Multilanguagetranslation
	 * @param langText
	 *            LanguageCodeView
	 * @param loggedEmployee
	 *            Employee
	 * @exception MicosException
	 *                MicosException
	 */
	private void updateDetailedDescriptionMultilanguageText(
			Multilanguagetranslation multilang, LanguageCodeView[] langText,
			Employee loggedEmployee) throws MicosException {
		LOGGER.entering(getClass().getName(),"updateDetailedDescriptionMultilanguageText");
		for (int i = 0; i < langText.length; i++) {
			if (langText[i] != null) {
				String txtValue = langText[i].getValue();
				String langCode = langText[i].getLanguageType();
				Translation translation = findTranslationForCode(multilang,
						langCode);
				if (translation != null) {
					if (txtValue != null && txtValue.length() > 0) {
						translation.setTranslatedtext(SystemUtil
								.getClobFromString(langText[i].getValue()));
						translation.setEmployee(loggedEmployee);
					} else {
						translation.setTranslatedtext(SystemUtil
								.getClobFromString(" "));
						translation.setEmployee(loggedEmployee);
					}
				}
			}
		}
	}

	/**
	 * Method to check whether detailed description is entered or not
	 * 
	 * @param DDText
	 *            Code View
	 * @return boolean True - incase no detailed description
	 */
	private boolean checkDetailedDescription(LanguageCodeView[] DDText) {
		LOGGER.entering(getClass().getName(),"checkDetailedDescription");
		boolean detailedDesc = false;
		if (DDText != null) {
			for (int i = 0; i < DDText.length; i++) {
				if (DDText[i] != null && DDText[i].getValue() != null) {
					if (DDText[i].getValue().length() > 0) {
						detailedDesc = true;
						break;
					}
				}
			}
		}
		return detailedDesc;
	}

	/**
	 * update Control Attachments
	 * 
	 * @param multiattachments
	 *            Set
	 * @param listofAttachmentsForControl
	 *            List
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void updateControlAttachment(Set<Multiattachment> multiattachments,
			List<AttachmentView> listofAttachmentsForControl,
			Employee loggedEmployee) throws MicosException {
		LOGGER.entering(getClass().getName(), "updateControlAttachment");
    if (listofAttachmentsForControl != null) {
      for (Iterator iter = listofAttachmentsForControl.iterator(); iter
          .hasNext();) {
        AttachmentView attachView = (AttachmentView)iter.next();
        if (attachView.getId() == null) {
          // new link
          Multiattachment multiattachment = new Multiattachment();
          populateAttachmentData(loggedEmployee, attachView, multiattachment);
          multiattachment.setControl(this.getDetachedControl());
          multiattachments.add(multiattachment);
        }
      }
    }
	}

	/**
	 * update Control Links
	 * 
	 * @param multilinks
	 *            Set
	 * @param listOfLinksForControl
	 *            List
	 * @param loggedEmployee
	 *            Employee
	 */
	private void updateControlLinks(Set<Multilink> multilinks,
			List<LinkView> listOfLinksForControl, Employee loggedEmployee) {
		LOGGER.entering(getClass().getName(),"updateControlLinks");
		if (listOfLinksForControl != null) {
			for (Iterator iter = listOfLinksForControl.iterator(); iter
					.hasNext();) {
				LinkView linkView = (LinkView) iter.next();
				if (linkView.getId() == null) {
					// new link
					Multilink multilink = new Multilink();
					populateLinkData(loggedEmployee, linkView, multilink);
					multilink.setControl(this.getDetachedControl());
					multilinks.add(multilink);
				}
			}
		}
	}

	/**
	 * @param multilang
	 *            Multilanguagetranslation
	 * @param langText
	 *            LanguageCodeView
	 * @param loggedEmployee
	 *            Employee
	 */
	private void updateMultilanguageText(Multilanguagetranslation multilang,
			LanguageCodeView[] langText, Employee loggedEmployee) {
		LOGGER.entering(getClass().getName(),"updateMultilanguageText");
		for (int i = 0; i < langText.length; i++) {
			if (langText[i] != null) {
				String langCode = langText[i].getLanguageType();
				Translation translation = findTranslationForCode(multilang,
						langCode);
				if (translation != null) {
					String text = langText[i].getValue();
					if (text != null) {
						translation.setTranslatedtext(SystemUtil
								.getClobFromString(langText[i].getValue()));
					}
					translation.setEmployee(loggedEmployee);
				}
			}
		}
	}

	/**
	 * @param multilang
	 *            Multilanguagetranslation
	 * @param langCode
	 *            String
	 * @return null
	 */
	private Translation findTranslationForCode(
			Multilanguagetranslation multilang, String langCode) {
		LOGGER.entering(getClass().getName(),"findTranslationForCode");
		Set<Translation> translations = multilang.getTranslations();
		for (Iterator iter = translations.iterator(); iter.hasNext();) {
			Translation translation = (Translation) iter.next();
			Supportedlanguage suptdLang = translation.getSupportedlanguage();
			if (langCode != null && suptdLang != null) {
				if (langCode.equals(suptdLang.getLanguagecode())) {
					return translation;
				}
			}
		}
		return null;
	}

	/**
	 * Updates controls refered from this control. Basically for update, a user
	 * must have added some new references so we just need to add those
	 * references. References deleted need not be checked here as the delete
	 * operation must have deleted the record from the database in some other
	 * transaction.
	 * 
	 * @param controlreferencesView
	 *            List
	 * @param controlreferencesData
	 *            Set
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void updateControlReferences(
			Set<Controlreference> controlreferencesData,
			List<ControlView> controlreferencesView, Employee loggedEmployee)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"updateControlReferences");
		if (controlreferencesView != null) {
			for (Iterator iter = controlreferencesView.iterator(); iter
					.hasNext();) {
				ControlView controlView = (ControlView) iter.next();
				Integer addedControlId = controlView.getId();

				// if user adds new control reference then only add to the Set
				// of Controlreferences
				if (!isControlAlreadyRefered(addedControlId,
						controlreferencesData)) {
					Controlreference controlreference = new Controlreference();
					populateControlReference(controlView, controlreference,
							loggedEmployee);
					controlreference.setControlByFromcontrol(this
							.getDetachedControl());

					controlreferencesData.add(controlreference);
				}
			}

		}

	}

	/**
	 * Checks if the control being refered-to is already present.
	 * 
	 * @param addedControlId
	 *            Integer
	 * @param controlreferencesData
	 *            Set
	 * @return alreadyPresent
	 */
	private boolean isControlAlreadyRefered(Integer addedControlId,
			Set controlreferencesData) {
		LOGGER.entering(getClass().getName(),"isControlAlreadyRefered");
		boolean alreadyPresent = false;

		for (Iterator iter = controlreferencesData.iterator(); iter.hasNext();) {
			Controlreference controlreference = (Controlreference) iter.next();
			Control toControl = controlreference.getControlByTocontrol();
			if (addedControlId.equals(toControl.getControlid())) {
				alreadyPresent = true;
				break;
			}
		}
		return alreadyPresent;
	}

	/**
	 * @param controlView
	 *            ControlView
	 * @param controlreference
	 *            Controlreference
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void populateControlReference(ControlView controlView,
			Controlreference controlreference, Employee loggedEmployee)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateControlReference");
		controlreference.setControlByTocontrol(controlSetupDao
				.loadControl(controlView.getId()));
		controlreference.setCreatedate(DateUtil.getCurrentTimestamp());
		controlreference.setEmployee(loggedEmployee);
		controlreference.setLastupdate(DateUtil.getCurrentTimestamp());
		controlreference
				.setStatus(controlSetupDao
						.getStatusByCode(MicosConstantValues.Status.ACTIVE));

	}

	/**
	 * update Control Setup
	 * 
	 * @param controlView
	 *            ControlView
	 * @param controlData
	 *            Control
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             MicosException
	 */
	@SuppressWarnings("unchecked")
  private void updateControlSetup(ControlView controlView,
			Control controlData, Employee loggedEmployee, String statusControl) throws MicosException {
		LOGGER.entering(getClass().getName(),"updateControlSetup");
		List<ControlStepView> controlStepsViewList = controlView
				.getControlStepViewList();
		if (!SystemUtil.isNull(controlStepsViewList)) {
			Set<Setup> controlSetups = controlData.getSetups();
			if (!SystemUtil.isNull(controlSetups)) {

				// already some steps were added earlier, check for modification
				// or addition of new steps

				Setup controlSetupData = null;
				for (Iterator iter = controlSetups.iterator(); iter.hasNext();) {
					// the Set contains only one record as per business
					controlSetupData = (Setup) iter.next();
				}
				Set<Step> controlStepsData = null;
				if (controlSetupData != null) {
					controlStepsData = controlSetupData.getSteps();
				}

				// Status of setup
				Status controlStatus = controlData.getStatus();
				String controlStatusCode = controlStatus.getStatuscode();
				Status modifiedStatus = null;
				if (!controlStatusCode.equals(MicosConstantValues.Status.SETUP)) {
					// if other than setup status
					modifiedStatus = controlSetupDao
							.loadStatus(MicosConstantValues.Status.MODIFIED);
					controlSetupData.setStatus(modifiedStatus);
				}

				HashMap stepDependsOnMap = new HashMap();

				for (Iterator iter = controlStepsViewList.iterator(); iter
						.hasNext();) {
					ControlStepView controlStepView = (ControlStepView) iter
							.next();
					Step stepData = null;
					if (controlStepView.getId() == null) {
						// this must be a new step
						stepData = new Step();
						populateControlStepData(loggedEmployee,
								controlStepView, stepData, null);
						stepData.setSetup(controlSetupData);

						// the set "Set<Step> controlStepsData" must not be null
						// at this point as atleast one step must have been
						// created earlier
						controlStepsData.add(stepData);
					} else {
						stepData = findStepWithId(controlStepView.getId(),
								controlStepsData);
						updateControlStep(controlStepView, stepData,
								loggedEmployee,statusControl);
					}
					stepDependsOnMap.put(controlStepView.getTempId(), stepData);
				}

				for (Iterator iter = controlStepsViewList.iterator(); iter
						.hasNext();) {
					ControlStepView controlStepView = (ControlStepView) iter
							.next();
					Step creatingStep = (Step) stepDependsOnMap
							.get(controlStepView.getTempId());
					if (controlStepView.getDependsOn() != null) {

						Step depStep = (Step) stepDependsOnMap
								.get(controlStepView.getDependsOn());

						creatingStep.setStep(depStep);
					} else {
						creatingStep.setStep(null);

					}
				}

			} else {
				// if here then steps are being created for the first time
				populateSteps(controlData, controlView, loggedEmployee, statusControl);
			}
		}
	}

	/**
	 * @param stepViewId
	 *            Integer
	 * @param controlStepsData
	 *            Set
	 * @return stepDataWithId
	 */
	private Step findStepWithId(Integer stepViewId, Set<Step> controlStepsData) {
		LOGGER.entering(getClass().getName(),"findStepWithId");
		Step stepDataWithId = null;
		for (Iterator iter = controlStepsData.iterator(); iter.hasNext();) {
			Step stepData = (Step) iter.next();
			if (stepViewId.equals(stepData.getStepid())) {
				stepDataWithId = stepData;
				break;
			}
		}
		return stepDataWithId;
	}

	/**
	 * Updates an existing step with modified data.
	 * 
	 * @param controlStepView
	 *            controlStepView
	 * @param stepData
	 *            Step
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void updateControlStep(ControlStepView controlStepView,
			Step stepData, Employee loggedEmployee,String statusControl) throws MicosException {
		LOGGER.entering(getClass().getName(),"updateControlStep");
		stepData.setTitle(controlStepView.getStepTitle());
		// CR11: Coverage Period for Controls.
		// update Coverage period
		if (controlStepView.getStepStartDate() != null) {
			stepData.setCoverageperiodstartdate(controlStepView.getStepStartDate());
		}
		if (controlStepView.getStepEndDate() != null) {
			stepData.setCoverageperiodenddate(controlStepView.getStepEndDate());
		}
		// end
	    // Next Schedule date
	    if (controlStepView.getSchedulingView() != null) {
	    	 if (controlStepView.getSchedulingView() != null) {
	 			if (controlStepView.getSchedulingView().getSchedulePeriodicAdhoc()
	 					.equalsIgnoreCase(ScheduleType.PERIODIC)) {
	 				Integer patternFreq = Integer.parseInt(controlStepView
	 						.getSchedulingView().getSchedulePatternFreq());
	 				if (patternFreq.equals(7)) {
	 					try {
	 						stepData.setNextscheduledate(SchedulerUtils
	 								.getNextWeeklyScheduleDate(controlStepView
	 										.getSchedulingView()
	 										.getScheduleStartDate(),
	 										controlStepView.getSchedulingView()
	 												.getScheduleEndDate(),
	 										createWeekdayArr(controlStepView
	 												.getSchedulingView()),
	 										patternFreq));
	 					} catch (Exception e) {
	 						LOGGER.error("populateControlStepData", e);
	 						throw new MicosException(e);
	 					}
	 				} else {
	 					stepData.setNextscheduledate(controlStepView
	 							.getSchedulingView().getScheduleStartDate());
	 				}
	 			} else {
	 				stepData.setNextscheduledate(controlStepView.getSchedulingView()
	 						.getScheduleStartDate());
	 			}
	 		}
	    }

		// update support schema
		if (controlStepView.getCheckSupport()) {
			SchemaView supportSchemaView = controlStepView
					.getSupportSchemaView();
			Supportschema supportschemaData = stepData.getSupportschema();
			if (supportSchemaView != null) {
				if (supportschemaData != null
						&& stepData
								.getSupport()
								.equals(
										MicosConstantValues.CheckSchemaState.ENABLED_STATE)) {
					// if already created then modify its data
					updateStepSupportSchema(supportSchemaView,
							supportschemaData, loggedEmployee);
				} else {
					// create new support schema data
					populateSupportSchema(loggedEmployee, controlStepView,
							stepData);
					stepData
							.setSupport(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
				}
			}
		} else {
			stepData
					.setSupport(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		// update execution schema
		if (controlStepView.getCheckPerformance()) {
			SchemaView performanceSchemaView = controlStepView
					.getPerformanceSchemaView();
			Executionschema executionschemaData = stepData.getExecutionschema();
			if (performanceSchemaView != null) {
				if (executionschemaData != null
						&& stepData
								.getExecution()
								.equals(
										MicosConstantValues.CheckSchemaState.ENABLED_STATE)) {
					// if already created then modify its data
					updateStepExecutionSchema(performanceSchemaView,
							executionschemaData, loggedEmployee);
				} else {
					// create new execution schema data
					populateExecutionSchema(loggedEmployee, controlStepView,
							stepData);
					stepData
							.setExecution(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
				}
				if (SystemUtil.isNotNull(controlStepView
						.getPerformanceRatinggroup())) {
					stepData.setExecutionratinggroup(controlSetupDao
							.retrieveRatinggroupForId(Integer.parseInt(controlStepView
											.getPerformanceRatinggroup())));
				}
			}
		} else {
			stepData
					.setExecution(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		// update QA schema
		if (controlStepView.getCheckQA()) {
			SchemaView qaSchemaView = controlStepView.getQaSchemaView();
			Qaschema qaschemaData = stepData.getQaschema();
			if (qaSchemaView != null) {
				if (qaschemaData != null
						&& stepData
								.getQa()
								.equals(
										MicosConstantValues.CheckSchemaState.ENABLED_STATE)) {
					// if already created then modify its data
					updateStepQaSchema(qaSchemaView, qaschemaData,
							loggedEmployee);
				} else {
					// create new qa schema data
					populateQaSchema(loggedEmployee, controlStepView, stepData);
					stepData
							.setQa(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
				}
			}
		} else {
			stepData.setQa(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		// update Signoff schema
		if (controlStepView.getCheckSignOff()) {
			SchemaView signoffSchemaView = controlStepView
					.getSignOffSchemaView();
			Signoffschema signoffschemaData = stepData.getSignoffschema();
			if (signoffSchemaView != null) {
				if (signoffschemaData != null
						&& stepData
								.getSignoff()
								.equals(
										MicosConstantValues.CheckSchemaState.ENABLED_STATE)) {
					// if already created then modify its data
					updateStepSignoffSchema(signoffSchemaView,
							signoffschemaData, loggedEmployee);
				} else {
					// create new signoff schema data
					populateSignoffSchema(loggedEmployee, controlStepView,
							stepData);
					stepData
							.setSignoff(MicosConstantValues.CheckSchemaState.ENABLED_STATE);
				}
				if (SystemUtil.isNotNull(controlStepView
						.getSignoffRatinggroup())) {
					stepData.setSignoffratinggroup(controlSetupDao
							.retrieveRatinggroupForId(Integer.parseInt(controlStepView
											.getSignoffRatinggroup())));
				}
			}
		} else {
			stepData
					.setSignoff(MicosConstantValues.CheckSchemaState.DISABLED_STATE);
		}

		updateStepScheduleData(controlStepView, stepData, loggedEmployee,statusControl);
		//updateStepEscalationData(controlStepView, stepData, loggedEmployee);
	}

	/**
	 * @param supportSchemaView
	 *            SchemaView
	 * @param supportschemaData
	 *            Supportschema
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void updateStepSupportSchema(SchemaView supportSchemaView,
			Supportschema supportschemaData, Employee loggedEmployee)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"updateStepSupportSchema");
		supportschemaData.setEmployee(loggedEmployee);
		Set<Executor> executorsOfSchema = supportschemaData.getExecutors();
		List<EmployeeView> selectedPidList = supportSchemaView
				.getSelectionViaPIDView();
		Executor executorData = null;
		if (executorsOfSchema != null) {
			for (Iterator iter = executorsOfSchema.iterator(); iter.hasNext();) {
				executorData = (Executor) iter.next();
			}
		} else {
			executorsOfSchema = new HashSet<Executor>();
			supportschemaData.setExecutors(executorsOfSchema);
		}

		if (executorData == null) {
			executorData = new Executor();
			executorData
					.setCreatedate(new Timestamp(System.currentTimeMillis()));
			executorData
					.setLastupdate(new Timestamp(System.currentTimeMillis()));
			executorData.setEmployee(loggedEmployee);
			executorData.setSupportschema(supportschemaData);
			executorsOfSchema.add(executorData);
		}

		updateSchemaExecutors(loggedEmployee, executorsOfSchema,
				selectedPidList, supportSchemaView
				.getSelectionViaHierarchyView());
	}

	/**
	 * update Step Execution Schema
	 * 
	 * @param performanceSchemaView
	 *            SchemaView
	 * @param executionschemaData
	 *            Executionschema
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             MicosException
	 */
	private void updateStepExecutionSchema(SchemaView performanceSchemaView,
			Executionschema executionschemaData, Employee loggedEmployee)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"updateStepExecutionSchema");
		executionschemaData.setEmployee(loggedEmployee);
		Set<Executor> executorsOfSchema = executionschemaData.getExecutors();
		List<EmployeeView> selectedPidList = performanceSchemaView
				.getSelectionViaPIDView();

		Executor executorData = null;
		if (executorsOfSchema != null) {
			for (Iterator iter = executorsOfSchema.iterator(); iter.hasNext();) {
				executorData = (Executor) iter.next();
			}
		} else {
			executorsOfSchema = new HashSet<Executor>();
			executionschemaData.setExecutors(executorsOfSchema);
		}

		if (executorData == null) {
			executorData = new Executor();
			executorData
					.setCreatedate(new Timestamp(System.currentTimeMillis()));
			executorData
					.setLastupdate(new Timestamp(System.currentTimeMillis()));
			executorData.setEmployee(loggedEmployee);
			executorData.setExecutionschema(executionschemaData);
			executorsOfSchema.add(executorData);
		}

		updateSchemaExecutors(loggedEmployee, executorsOfSchema,
				selectedPidList, performanceSchemaView.getSelectionViaHierarchyView());
	}

	/**
	 * update Step Qa Schema
	 * 
	 * @param qaSchemaView
	 *            SchemaView
	 * @param qaschemaData
	 *            Qaschema
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             MicosException
	 */
	private void updateStepQaSchema(SchemaView qaSchemaView,
			Qaschema qaschemaData, Employee loggedEmployee)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "updateStepQaSchema");
		qaschemaData.setEmployee(loggedEmployee);
		Set<Executor> executorsOfSchema = qaschemaData.getExecutors();
		List<EmployeeView> selectedPidList = qaSchemaView
				.getSelectionViaPIDView();

		Executor executorData = null;
		if (executorsOfSchema != null) {
			for (Iterator iter = executorsOfSchema.iterator(); iter.hasNext();) {
				executorData = (Executor) iter.next();
			}
		} else {
			executorsOfSchema = new HashSet<Executor>();
			qaschemaData.setExecutors(executorsOfSchema);
		}

		if (executorData == null) {
			executorData = new Executor();
			executorData
					.setCreatedate(new Timestamp(System.currentTimeMillis()));
			executorData
					.setLastupdate(new Timestamp(System.currentTimeMillis()));
			executorData.setEmployee(loggedEmployee);
			executorData.setQaschema(qaschemaData);
			executorsOfSchema.add(executorData);
		}

		updateSchemaExecutors(loggedEmployee, executorsOfSchema,
				selectedPidList, qaSchemaView.getSelectionViaHierarchyView());
	}

	/**
	 * update Step Signoff Schema
	 * 
	 * @param signoffSchemaView
	 *            SchemaView
	 * @param signoffschemaData
	 *            Signoffschema
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             MicosException
	 */
	private void updateStepSignoffSchema(SchemaView signoffSchemaView,
			Signoffschema signoffschemaData, Employee loggedEmployee)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "updateStepSignoffSchema");
		signoffschemaData.setEmployee(loggedEmployee);
		Set<Executor> executorsOfSchema = signoffschemaData.getExecutors();
		List<EmployeeView> selectedPidList = signoffSchemaView
				.getSelectionViaPIDView();

		Executor executorData = null;
		if (executorsOfSchema != null) {
			for (Iterator iter = executorsOfSchema.iterator(); iter.hasNext();) {
				executorData = (Executor) iter.next();
			}
		} else {
			executorsOfSchema = new HashSet<Executor>();
			signoffschemaData.setExecutors(executorsOfSchema);
		}

		if (executorData == null) {
			executorData = new Executor();
			executorData
					.setCreatedate(new Timestamp(System.currentTimeMillis()));
			executorData
					.setLastupdate(new Timestamp(System.currentTimeMillis()));
			executorData.setEmployee(loggedEmployee);
			executorData.setSignoffschema(signoffschemaData);
			executorsOfSchema.add(executorData);
		}

		updateSchemaExecutors(loggedEmployee, executorsOfSchema,
				selectedPidList, signoffSchemaView
						.getSelectionViaHierarchyView());

	}

	/**
	 * @param loggedEmployee
	 *            Employee
	 * @param executorsOfSchema
	 *            Set
	 * @param selectedPidList
	 *            List
	 * @param selectedHierarchyList
	 *            List
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void updateSchemaExecutors(Employee loggedEmployee,
			Set<Executor> executorsOfSchema,
			List<EmployeeView> selectedPidList,
			List<HierarchyElementView> selectedHierarchyList) throws MicosException {
		LOGGER.entering(getClass().getName(), "updateSchemaExecutors");
		Set<Executorpid> executorpids = null;
		Set<Executoraor> executoraors = null;
		Executor executorData = null;
		if (executorsOfSchema != null) {
			// according to business the set "Set<Executor> executorsOfSupport"
			// should contain only one entry
			for (Iterator iter = executorsOfSchema.iterator(); iter.hasNext();) {
				executorData = (Executor) iter.next();
				if (executorData != null) {
					executorpids = executorData.getExecutorpids();
					executoraors = executorData.getExecutoraors();
				}
			}
		}

		if (executorData != null) {
			if (selectedPidList != null) {
				for (Iterator iter = selectedPidList.iterator(); iter.hasNext();) {
					EmployeeView selectedPid = (EmployeeView) iter.next();
					if (selectedPid != null) {
						if (!isExecutorpidAlreadyPresent(selectedPid,
								executorpids)) {
							// if new pid added
							Executorpid executorpidData = new Executorpid();
							populateExecutorpid(selectedPid, executorpidData,
									loggedEmployee);
							executorpidData.setExecutor(executorData);

							if (executorpids == null) {
								executorpids = new HashSet<Executorpid>();
								executorData.setExecutorpids(executorpids);
							}

							executorpids.add(executorpidData);
						}
					}
				}
			}

			if (selectedHierarchyList != null) {
				for (HierarchyElementView hierarchyEmployeeView : selectedHierarchyList) {
						Integer hierarchyelementid = hierarchyEmployeeView.getId();
						if (!isExecutoraorAlreadyPresent(executoraors,hierarchyelementid)) {
							// if new hierarchy employee added
							populateHierarchyEmployeeAorowners(loggedEmployee,
									executorData, hierarchyEmployeeView);
						} 
				}
			}
		}
	}

	/**
	 * @param executorpids
	 *            Set
	 * @param selectedPid
	 *            EmployeeView
	 * @return present
	 */
	private boolean isExecutorpidAlreadyPresent(EmployeeView selectedPid,
			Set<Executorpid> executorpids) {
		LOGGER.entering(getClass().getName(),"isExecutorpidAlreadyPresent");
		boolean present = false;
		Integer pidEmpId = Integer.parseInt(selectedPid.getEmpId());
		if (executorpids != null) {
			for (Iterator iter = executorpids.iterator(); iter.hasNext();) {
				Executorpid executorpidData = (Executorpid) iter.next();
				if (pidEmpId.equals(executorpidData.getEmployeeByEmployeeid()
						.getEmployeeid())) {

					if (selectedPid.isIncludeExclude()) {
						executorpidData
								.setInclude(MicosConstantValues.EmpIncludeExcledeStatus.EXCLUDE);
					} else {
						executorpidData
								.setInclude(MicosConstantValues.EmpIncludeExcledeStatus.INCLUDE);
					}
					present = true;
					break;
				}
			}
		}
		return present;
	}

	/**
	 * @param selectedPid
	 *            EmployeeView
	 * @param executorpidData
	 *            Executorpid
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void populateExecutorpid(EmployeeView selectedPid,
			Executorpid executorpidData, Employee loggedEmployee)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateExecutorpid");
		Employee selectedEmployee = controlSetupDao.loadEmployee(Integer
				.parseInt(selectedPid.getEmpId()));
		executorpidData.setEmployeeByEmployeeid(selectedEmployee);
		executorpidData
				.setCreatedate(new Timestamp(System.currentTimeMillis()));
		executorpidData
				.setLastupdate(new Timestamp(System.currentTimeMillis()));
		executorpidData.setEmployeeByLastupdatedby(loggedEmployee);
		if (selectedPid.isIncludeExclude()) {
			executorpidData
					.setInclude(MicosConstantValues.EmpIncludeExcledeStatus.EXCLUDE);
		} else {
			executorpidData
					.setInclude(MicosConstantValues.EmpIncludeExcledeStatus.INCLUDE);
		}
	}

	/**
	 * @param hierarchyelementid
	 *            the Integer
	 * @param executoraors
	 *            Set
	 * @return present
	 */
	private boolean isExecutoraorAlreadyPresent(Set<Executoraor> executoraors, Integer hierarchyelementid) {
		LOGGER.entering(getClass().getName(),"isExecutoraorAlreadyPresent");
		boolean present = false;
		if (executoraors != null) {
			for (Iterator iter = executoraors.iterator(); iter.hasNext();) {
				Executoraor executoraorData = (Executoraor) iter.next();
				Integer heid = executoraorData.getHierarchyelement().getHierarchyelementid();
				if ((hierarchyelementid.equals(heid))) {
					present = true;
					break;
				}
			}
		}
		return present;
	}

   /**
	 * @param loggedEmployee
	 *            Employee
	 * @param executorData
	 *            Executor
	 * @param hierarchyEmployeeView
	 *            EmployeeView
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void populateHierarchyEmployeeAorowners(Employee loggedEmployee,
			Executor executorData, HierarchyElementView hierarchyEmployeeView)
			throws MicosException {
		LOGGER.entering(getClass().getName(),
				"populateHierarchyEmployeeAorowners");

		Set<Executoraor> executoraors = executorData.getExecutoraors();
		Executoraor executoraorData = new Executoraor();
		executoraorData
				.setCreatedate(new Timestamp(System.currentTimeMillis()));
		executoraorData
				.setLastupdate(new Timestamp(System.currentTimeMillis()));
		executoraorData.setEmployee(loggedEmployee);
		executoraorData.setHierarchyelement(controlSetupDao
				.loadHierarchyElement(hierarchyEmployeeView.getId()));
		executoraorData.setTempId(hierarchyEmployeeView.getTempId());
		executoraorData.setExecutor(executorData);
		if (hierarchyEmployeeView.getSelectedRole().getId() != null) {
			Hierarchyrole hiechRole = controlSetupDao
					.loadHierarchyRole(hierarchyEmployeeView.getSelectedRole()
							.getId());
			executoraorData.setHierarchyrole(hiechRole);
		}
		if (hierarchyEmployeeView.getLevel() != null) {
			executoraorData.setLevelsdown(new BigDecimal(hierarchyEmployeeView
					.getLevel()));
		}
		if (executoraors == null) {
			executoraors = new HashSet<Executoraor>();
			executorData.setExecutoraors(executoraors);
		}

		executoraors.add(executoraorData);
	}
	
	/**
	 * update Step Schedule Data
	 * 
	 * @param controlStepView
	 *            ControlStepView
	 * @param stepData
	 *            Step
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             MicosException
	 */
	private void updateStepScheduleData(ControlStepView controlStepView,
			Step stepData, Employee loggedEmployee,String statusControl) throws MicosException {
		LOGGER.entering(getClass().getName(),"updateStepScheduleData");
		SchedulingView schedulingView = controlStepView.getSchedulingView();
		if (schedulingView != null) {
			Set<Schedule> stepSchedules = stepData.getSchedules();
			Schedule scheduleData = null;
			if (stepSchedules != null) {

				for (Iterator iter = stepSchedules.iterator(); iter.hasNext();) {
					scheduleData = (Schedule) iter.next();
				}
			}

			if (scheduleData == null) {
				stepSchedules = new HashSet<Schedule>();
				scheduleData = new Schedule();
				scheduleData.setCreatedate(new Timestamp(System
						.currentTimeMillis()));
				scheduleData.setLastupdate(new Timestamp(System
						.currentTimeMillis()));
				copySchedulingViewToData(schedulingView, loggedEmployee,
						scheduleData);
				scheduleData.setStep(stepData);
				stepSchedules.add(scheduleData);
				stepData.setSchedules(stepSchedules);
			} else {
				copySchedulingViewToData(schedulingView, loggedEmployee,
						scheduleData);
			}
			
			Set<ControlRemWarEsc> setControlRemWarEsc=createEditNotification(controlStepView, false, null, stepData,statusControl);
			stepData.setControlRemWarEscStep(setControlRemWarEsc);

		}
	}

	/**
	 * CR_8 update Step Schedule Data
	 * 
	 * @param controlStepView ControlStepView
	 * @param poweroption Poweroption
	 * @param loggedEmployee Employee
	 * @throws MicosException MicosException
	 */
	private void updateStepScheduleDataPower(ControlStepView controlStepView,
			Poweroption poweroption, Employee loggedEmployee, String statusControl)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"updateStepScheduleDataPower");
		SchedulingView schedulingView = controlStepView.getSchedulingView();
		if (schedulingView != null) {
			Set<Poschedule> stepSchedules = poweroption.getPoschedules();
			Poschedule scheduleData = null;
			if (stepSchedules != null) {

				for (Iterator iter = stepSchedules.iterator(); iter.hasNext();) {
					scheduleData = (Poschedule) iter.next();
				}
			}

			if (scheduleData == null) {
				stepSchedules = new HashSet<Poschedule>();
				scheduleData = new Poschedule();
				scheduleData.setCreatedate(new Timestamp(System
						.currentTimeMillis()));
				scheduleData.setLastupdate(new Timestamp(System
						.currentTimeMillis()));
				copySchedulingViewToDataPower(schedulingView, loggedEmployee,
						scheduleData);
				scheduleData.setPoweroption(poweroption);
				stepSchedules.add(scheduleData);
				poweroption.setPoschedules(stepSchedules);
			} else {
				copySchedulingViewToDataPower(schedulingView, loggedEmployee,
						scheduleData);
			}
			
			Set<ControlRemWarEsc> setControlRemWarEsc=createEditNotification(controlStepView, true, poweroption,null,statusControl);
			poweroption.setSetOfControlRemEsc(setControlRemWarEsc);

		}
	}

	/**
	 * Copies all schedule data from the Scheduling view to the data object
	 * 
	 * @param schedulingView
	 *            the Schedule view object
	 * @param scheduleData
	 *            the Schedule data object
	 * @param loggedEmployee
	 *            Employee
	 * @throws MicosException
	 *             Micos Exception
	 */
	private void copySchedulingViewToData(SchedulingView schedulingView,
			Employee loggedEmployee, Schedule scheduleData)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"copySchedulingViewToData");
		scheduleData.setEnddate(schedulingView.getScheduleEndDate());
		scheduleData.setStartdate(schedulingView.getScheduleStartDate());

		if (MicosConstantValues.ScheduleType.PERIODIC
				.equalsIgnoreCase(schedulingView.getSchedulePeriodicAdhoc())) {
			scheduleData.setPeriodicadhoc(schedulingView
					.getSchedulePeriodicAdhoc());
			if (schedulingView.getSchedulePatternQty() != null) {
				scheduleData.setPatternqty(new BigDecimal(schedulingView
						.getSchedulePatternQty()));
			}
			if (schedulingView.getSchedulePatternFreq() != null) {
				Integer schedulePatternFreq = Integer.parseInt(schedulingView
						.getSchedulePatternFreq());
				scheduleData.setPeriodtypeByPatternfrequency(controlSetupDao
						.loadPeriodType(schedulePatternFreq));
				
				if(scheduleData.getPeriodtypeByPatternfrequency().getName().equalsIgnoreCase(MicosConstantValues.PeriodType.WEEKS)){
					scheduleData.setMonday(schedulingView.getMonday());
					scheduleData.setTuesday(schedulingView.getTuesday());
					scheduleData.setWednesday(schedulingView.getWednesday());
					scheduleData.setThursday(schedulingView.getThursday());
					scheduleData.setFriday(schedulingView.getFriday());
					scheduleData.setSaturday(schedulingView.getSaturday());
					scheduleData.setSunday(schedulingView.getSunday());
				}
				
			}
			scheduleData.setScheduleadhocdates(null);
		} else if (MicosConstantValues.ScheduleType.ADHOC
				.equalsIgnoreCase(schedulingView.getSchedulePeriodicAdhoc())) {
			scheduleData.setPeriodicadhoc(schedulingView
					.getSchedulePeriodicAdhoc());
			Set<Scheduleadhocdate> adhocDates = null;
			if (scheduleData.getScheduleadhocdates() != null) {
				adhocDates = scheduleData.getScheduleadhocdates();
			} else {
				adhocDates = new HashSet<Scheduleadhocdate>();
			}
			List<ScheduleAdhocDateView> adhocDateList = schedulingView
					.getScheduleAdhocDateViewList();
			if (adhocDateList != null) {
				for (ScheduleAdhocDateView adhocDateView : adhocDateList) {
					if (adhocDateView.getId() != null) {
						continue;
					}
					Scheduleadhocdate adhocDate = new Scheduleadhocdate();
					adhocDate.setAdhocdate(DateUtil.getDate(adhocDateView.getScheduleAdhocDate(), Constants.OUTPUT_DATE_FORMAT));
					adhocDate.setEmployee(loggedEmployee);
					adhocDate.setCreatedate(new Timestamp(System
							.currentTimeMillis()));
					adhocDate.setLastupdate(new Timestamp(System
							.currentTimeMillis()));
					adhocDate.setSchedule(scheduleData);
					adhocDates.add(adhocDate);
				}
			}
			if (scheduleData.getScheduleadhocdates() == null) {
				scheduleData.setScheduleadhocdates(adhocDates);
			}
		}

		if (schedulingView.getScheduleExecInitPeriodType() != null) {
			if (schedulingView.getScheduleExecDurationQty() != null) {
				scheduleData.setExecutiondurationqty(new BigDecimal(
						schedulingView.getScheduleExecDurationQty()));
			}
			if (schedulingView.getScheduleExecInitQty() != null) {
				scheduleData.setExecutioninitiateqty(new BigDecimal(
						schedulingView.getScheduleExecInitQty()));
			}
			if (schedulingView.getScheduleExecPeriodType() != null) {
				Integer execDurPeriodTypeId = Integer.parseInt(schedulingView
						.getScheduleExecPeriodType());
				scheduleData
						.setPeriodtypeByExecutiondurationperiodtype(controlSetupDao
								.loadPeriodType(execDurPeriodTypeId));
			}
			if (schedulingView.getScheduleExecInitPeriodType() != null) {
				Integer execInitPeriodTypeId = Integer.parseInt(schedulingView
						.getScheduleExecInitPeriodType());
				scheduleData
						.setPeriodtypeByExecutorinitiateperiodtype(controlSetupDao
								.loadPeriodType(execInitPeriodTypeId));
			}
			if (schedulingView.getScheduleExecWhen() != null) {
				Integer execScheduleWhenId = Integer.parseInt(schedulingView
						.getScheduleExecWhen());
				scheduleData
						.setSchedulewhenByExecutionschedulewhen(controlSetupDao
								.loadScheduleWhen(execScheduleWhenId));
			}
		}

		if (schedulingView.getScheduleSupportInitiatePeriodType() != null) {
			if (schedulingView.getScheduleSupportInitiateQty() != null) {
				scheduleData.setSupportinitiateqty(new BigDecimal(
						schedulingView.getScheduleSupportInitiateQty()));
			}
			if (schedulingView.getScheduleSupportDurationQty() != null) {
				scheduleData.setSupportdurationqty(new BigDecimal(
						schedulingView.getScheduleSupportDurationQty()));
			}
			if (schedulingView.getScheduleSupportInitiatePeriodType() != null) {
				Integer supportInitPeriodTypeId = Integer
						.parseInt(schedulingView
								.getScheduleSupportInitiatePeriodType());
				scheduleData
						.setPeriodtypeBySupportinitiateperiodtype(controlSetupDao
								.loadPeriodType(supportInitPeriodTypeId));
			}
			if (schedulingView.getScheduleSupportDurationPeriodType() != null) {
				Integer supportDurationPeriodTypeId = Integer
						.parseInt(schedulingView
								.getScheduleSupportDurationPeriodType());
				scheduleData
						.setPeriodtypeBySupportdurationperiodtype(controlSetupDao
								.loadPeriodType(supportDurationPeriodTypeId));
			}
			if (schedulingView.getScheduleSupportScheduleWhen() != null) {
				Integer supportScheduleWhenId = Integer.parseInt(schedulingView
						.getScheduleSupportScheduleWhen());
				scheduleData
						.setSchedulewhenBySupportschedulewhen(controlSetupDao
								.loadScheduleWhen(supportScheduleWhenId));
			}
		}

		if (schedulingView.getScheduleQAInitPeriodType() != null) {
			if (schedulingView.getScheduleQADurationQty() != null) {
				scheduleData.setQadurationqty(new BigDecimal(schedulingView
						.getScheduleQADurationQty()));
			}
			if (schedulingView.getScheduleQAInitQty() != null) {
				scheduleData.setQainitiateqty(new BigDecimal(schedulingView
						.getScheduleQAInitQty()));
			}
			if (schedulingView.getScheduleQAInitPeriodType() != null) {
				Integer qaInitPeriodTypeId = Integer.parseInt(schedulingView
						.getScheduleQAInitPeriodType());
				scheduleData
						.setPeriodtypeByQainitiateperiodtype(controlSetupDao
								.loadPeriodType(qaInitPeriodTypeId));
			}
			if (schedulingView.getScheduleQAPeriodType() != null) {
				Integer qaDurnPeriodTypeId = Integer.parseInt(schedulingView
						.getScheduleQAPeriodType());
				scheduleData
						.setPeriodtypeByQadurationperiodtype(controlSetupDao
								.loadPeriodType(qaDurnPeriodTypeId));
			}
			if (schedulingView.getScheduleQAWhen() != null) {
				Integer qaScheduleWhenId = Integer.parseInt(schedulingView
						.getScheduleQAWhen());
				scheduleData.setSchedulewhenByQaschedulewhen(controlSetupDao
						.loadScheduleWhen(qaScheduleWhenId));
			}
		}

		if (schedulingView.getScheduleSignoffInitPeriodType() != null) {
			if (schedulingView.getScheduleSignoffInitQty() != null) {
				scheduleData.setSignoffinitiateqty(new BigDecimal(
						schedulingView.getScheduleSignoffInitQty()));
			}
			if (schedulingView.getScheduleSignoffDurationQty() != null) {
				scheduleData.setSignoffdurationqty(new BigDecimal(
						schedulingView.getScheduleSignoffDurationQty()));
			}
			if (schedulingView.getScheduleSignoffInitPeriodType() != null) {
				Integer signoffInitPeriodTypeId = Integer
						.parseInt(schedulingView
								.getScheduleSignoffInitPeriodType());
				scheduleData
						.setPeriodtypeBySignoffinitiateperiodtype(controlSetupDao
								.loadPeriodType(signoffInitPeriodTypeId));
			}
			if (schedulingView.getScheduleSignoffPeriodType() != null) {
				Integer signoffDurnPeriodTypeId = Integer
						.parseInt(schedulingView.getScheduleSignoffPeriodType());
				scheduleData
						.setPeriodtypeBySignoffdurationperiodtype(controlSetupDao
								.loadPeriodType(signoffDurnPeriodTypeId));
			}
			if (schedulingView.getScheduleSignoffWhen() != null) {
				Integer sognoffScheduleWhenId = Integer.parseInt(schedulingView
						.getScheduleSignoffWhen());
				scheduleData
						.setSchedulewhenBySignoffschedulewhen(controlSetupDao
								.loadScheduleWhen(sognoffScheduleWhenId));
			}
		}

		scheduleData.setEmployee(loggedEmployee);
		
	}

	/**
	 * Get all the AORs.
	 * 
	 * @return AOR List
	 * @param goalOwnerId
	 * @exception MicosException
	 *                Micos Exception
	 */
	public List<EmployeeView> getListOfAORForControl() throws MicosException {
		LOGGER.entering(getClass().getName(), "getListOfAORForControl");
		List<EmployeeView> employeeList = new ArrayList<EmployeeView>();
    List setOfHierrarchyElements = controlSetupDao.getAllHierarchyelements();
    if (!SystemUtil.isNull(setOfHierrarchyElements)) {
      Iterator itHierrarchyElements = setOfHierrarchyElements.iterator();
      while (itHierrarchyElements.hasNext()) {
        Hierarchyelement lHierarchyelement = (Hierarchyelement)itHierrarchyElements
            .next();
        if (null != lHierarchyelement) {
          Aorowner aorOwner = lHierarchyelement.getAorowner();
          if (null != aorOwner) {
            Employee employee = aorOwner.getEmployeeByAorowner();
            EmployeeView empView = new EmployeeView();
            empView.setId(employee.getEmployeeid());
            empView.setLastUpdate(employee.getLastupdate());
            empView.setHierarchyElementName(lHierarchyelement.getName());
            empView.setHierarchyElementDesc(lHierarchyelement.getDescription());
            empView.setHierrarchyName(lHierarchyelement.getHierarchy()
                .getName());
            empView.setAorOwner(employee.getPid() + "-"
                + employee.getLastname() + " " + employee.getFirstname());
            empView.setAorOwnerId(aorOwner.getAorownerid());
            empView.setEmpPid(employee.getPid());
            employeeList.add(empView);
          }
        }
      }
    }
    LOGGER.exiting(getClass().getName(), "getListOfAORForControl");
    return employeeList;
	}

	/**
	 * search AoR
	 * 
	 * @param searchCriteria
	 *            AoRView
	 * @return employeeList
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<EmployeeView> searchAoR(AoRView searchCriteria)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "searchAoR");
		List<EmployeeView> employeeList = new ArrayList<EmployeeView>();
		employeeList = controlSetupDao.searchAoR(searchCriteria);
		LOGGER.exiting(getClass().getName(), "searchAoR");
		return employeeList;
	}

	/**
	 * Method to populate roles based on selected hierarchy element
	 * 
	 * @param hierarchyElementid
	 *            Hierarchy Element ID
	 * @return role list
	 * @exception MicosException
	 *                MicosException
	 */
  public List<SelectItem> populateRole(int hierarchyElementid)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "populateRole");
    List<SelectItem> role = controlSetupDao.populateRole(hierarchyElementid);
    LOGGER.exiting(getClass().getName(), "populateRole");
    return role;
  }

	/**
	 * search AoR
	 * 
	 * @param searchCriteria
	 *            HierarchyElementView 
	 * @return employeeList
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<HierarchyElementView> searchHierarchyElementForExecutorAoR(
			HierarchyElementView searchCriteria) throws MicosException {
		LOGGER.entering(getClass().getName(),
				"searchHierarchyElementForExecutorAoR");
		List<HierarchyElementView> heList =controlSetupDao.searchHierarchyElementForExecutorAoR(searchCriteria);
		LOGGER.exiting(getClass().getName(),
				"searchHierarchyElementForExecutorAoR");
		return heList;
	}

	/**
	 * Requests for control returns controlid of that.
	 * 
	 * @param controlShareRequesterId
	 *            Integer
	 * @param controlView
	 *            ControlView
	 * @param loggedInUserId
	 *            Integer
	 * @param rfcId
	 *            Integer
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void requestForShare(ControlView controlView,
			Integer controlShareRequesterId, Integer rfcId,
			Integer loggedInUserId) throws MicosException {
		LOGGER.entering(getClass().getName(),"requestForShare");
		Employee lastUpdatedByEmployee = null;
		Controlrfc controlrfc = null;
		Sharedcontrol sharedcontrol = null;
		if (loggedInUserId != null) {
			lastUpdatedByEmployee = controlSetupDao
					.loadEmployee(loggedInUserId);

			if (controlView != null && controlShareRequesterId != null) {
				controlrfc = new Controlrfc();
				controlrfc.setControl(controlSetupDao.loadControl(controlView
						.getId()));
				controlrfc.setCreatedate(new Timestamp(System
						.currentTimeMillis()));
				controlrfc.setStatus(controlSetupDao
						.loadStatus(MicosConstantValues.Status.SETUP));
				controlrfc.setEmployee(lastUpdatedByEmployee);
				controlrfc.setRfc(controlSetupDao.loadRfc(rfcId));

				sharedcontrol = new Sharedcontrol();
				sharedcontrol.setAorowner(controlSetupDao
						.loadAorowner(controlShareRequesterId));
				sharedcontrol.setControlrfc(controlrfc);
				sharedcontrol
						.setStatus(controlSetupDao
								.loadStatus(MicosConstantValues.Status.WAITING_FOR_APPROVAL));
				sharedcontrol.setCreatedate(new Timestamp(System
						.currentTimeMillis()));
				sharedcontrol.setEmployee(lastUpdatedByEmployee);

				Set<Sharedcontrol> sharedControls = new HashSet<Sharedcontrol>();
				sharedControls.add(sharedcontrol);

				controlrfc.setSharedcontrols(sharedControls);
			}
		}

		try {
			controlSetupDao.saveSharedControl(controlrfc);
			Integer scopeAorEmpId = null;
			Rfc selectedRfc = controlSetupDao.loadRfc(rfcId);
			Aorowner scopeAor = selectedRfc.getAorowner();
			if (scopeAor != null) {
				Employee scopeAorEmp = scopeAor.getEmployeeByAorowner();
				if (scopeAorEmp != null) {
					scopeAorEmpId = scopeAorEmp.getEmployeeid();

				}
			}
			if (scopeAorEmpId != null) {
				Notificationtype notificationType = controlSetupDao
						.loadNotificationType(MicosConstantValues.MailNotificationTypeCode.SHAREDCONTROL);
				String notificationTypeText = notificationType
						.getNotificationtypetext();
				String newnotificationTypeText = notificationTypeText
						.replaceAll("mailShareControlId", sharedcontrol
								.getSharedcontrolid().toString());

				Notification notification = new Notification();
				notification.setNotifyuser(controlSetupDao
						.loadEmployee(scopeAorEmpId));
				notification.setNotificationlinetext(newnotificationTypeText);
				notification.setNotificationtype(notificationType);
				notification.setEscalated("N");
				notification.setCreatedate(new Timestamp(System
						.currentTimeMillis()));
				notification.setLastupdate(new Timestamp(System
						.currentTimeMillis()));
				notification.setLastupdatedby(controlSetupDao
						.loadEmployee(loggedInUserId));
				controlSetupDao.saveNotification(notification);
			}
		} catch (MicosException micosException) {
      LOGGER.error("requestForShare", micosException);
      throw micosException;
    } finally {
      LOGGER.exiting(getClass().getName(), "requestForShare");
    }

	}

	/**
	 * {@inheritDoc}
	 */
	public List<SelectItem> getScheduleWhenList() throws MicosException {
		LOGGER.entering(getClass().getName(),"getScheduleWhenList");
		List<SelectItem> scheduleWhenList = new ArrayList<SelectItem>();
		List<Schedulewhen> scheduleWhenListFromDB = controlSetupDao
				.getScheduleWhenList();

		for (Schedulewhen scheduleWhen : scheduleWhenListFromDB) {
			scheduleWhenList.add(new SelectItem(scheduleWhen
					.getSchedulewhenid().toString(), scheduleWhen.getName()));
		}
		LOGGER.exiting(getClass().getName(),"getScheduleWhenList");
		return scheduleWhenList;
	}

	/**
	 * get ControllersForSelectedAor
	 * 
	 * @param aorId
	 *            Integer
	 * @return employeeList
	 * @throws MicosException
	 *             MicosException
	 */
	public List<ControllerView> getControllersForSelectedAor(Integer aorId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"getControllersForSelectedAor");
		List<ControllerView> employeeList = new ArrayList<ControllerView>();
		List<Controller> controllerList = null;
		ControllerView anEmployeeView = null;
    Controller countController = null;
    controllerList = controlSetupDao.searchControllerForAor(aorId);
    Iterator itrController = controllerList.iterator();
    while (itrController.hasNext()) {
      countController = (Controller)itrController.next();
      anEmployeeView = new ControllerView();
      anEmployeeView.setId(countController.getControllerid());
      anEmployeeView.setEmpId(countController.getController().getEmployeeid()
          .toString());
      anEmployeeView.setFirstName(countController.getController()
          .getFirstname());
      anEmployeeView.setSurName(countController.getController().getLastname());
      anEmployeeView.setPid(countController.getController().getPid());
      anEmployeeView.setDepartment(countController.getController()
          .getDepartment());
      employeeList.add(anEmployeeView);
    }
    LOGGER.exiting(getClass().getName(), "getControllersForSelectedAor");

    return employeeList;
  }

	/**
	 * get AorForSelectedEmployee
	 * 
	 * @param loggedInUserId
	 *            Integer
	 * @return aorViewList
	 * @throws MicosException
	 *             Micos Exception
	 * 
	 */
	public List<AoRView> getAorForSelectedEmployee(Integer loggedInUserId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"getAorForSelectedEmployee");
		List<AoRView> aorViewList = new ArrayList<AoRView>();
		AoRView aoRView = null;
		Aorowner anAorowner = null;
    Hierarchy hierarchy = null;
    Set<Aorowner> aorownerList = controlSetupDao
        .getAorForEmployee(loggedInUserId);
    if (!SystemUtil.isNull(aorownerList)) {

      Iterator iteratoraor = aorownerList.iterator();
      while (iteratoraor.hasNext()) {
        anAorowner = (Aorowner)iteratoraor.next();
        aoRView = new AoRView();
        aoRView.setId(anAorowner.getAorownerid());
        hierarchy = anAorowner.getHierarchyelement().getHierarchy();
        if (hierarchy != null) {
          aoRView.setHiearchyName(hierarchy.getName());
        }
        aoRView.setHiearchyElementName(anAorowner.getHierarchyelement()
            .getName());
        aoRView.setScopeAoROwnerName(anAorowner.getEmployeeByAorowner()
            .getFirstname()
            + " "
            + anAorowner.getEmployeeByAorowner().getLastname()
            + " ("
            + anAorowner.getEmployeeByAorowner().getInstradierung() + ")");
        aoRView.setScopeAoRPID(anAorowner.getEmployeeByAorowner().getPid());
        aorViewList.add(aoRView);
      }
    }
    LOGGER.exiting(getClass().getName(), "getAorForSelectedEmployee");
    return aorViewList;
	}

	/**
	 * delete ControllerFromAor
	 * 
	 * @param deletedControllerId
	 *            Integer
	 * @return result
	 * @throws MicosException
	 *             MicosException
	 */
	public boolean deleteControllerFromAor(Integer deletedControllerId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "deleteControllerFromAor");
    boolean result = false;
    result = controlSetupDao.deleteController(deletedControllerId);
    LOGGER.exiting(getClass().getName(), "deleteControllerFromAor");
    return result;
	}

	/**
	 * save ControllerToAor
	 * 
	 * @param aorId
	 *            Integer
	 * @param loggedInUserId
	 *            Integer
	 * @param selectedControllerList
	 *            List
	 * @throws MicosException
	 *             MicosException
	 */
	public void saveControllerToAor(
			List<ControllerView> selectedControllerList, Integer aorId,
			Integer loggedInUserId) throws MicosException {
		LOGGER.entering(getClass().getName(),"saveControllerToAor");
		List<Controller> controllerList = new ArrayList<Controller>();
		Controller aController = null;
		ControllerView anEmployee = null;
		if (selectedControllerList != null) {
			Iterator itrcontrollers = selectedControllerList.iterator();
			Aorowner selectedAorowner = controlSetupDao.loadAorowner(aorId);

			Employee lastUpdatedByEmployee = controlSetupDao
					.loadEmployee(loggedInUserId);
			while (itrcontrollers.hasNext()) {
				anEmployee = (ControllerView) itrcontrollers.next();
				aController = new Controller();

				Integer controllerID = anEmployee.getId();

				if (controllerID != null) {
					continue;
				}

				aController.setAorowner(selectedAorowner);

				Employee selectedEmployee = controlSetupDao
						.loadEmployee(Integer.parseInt(anEmployee.getEmpId()));
				aController.setController(selectedEmployee);

				aController.setCreatedate(new Timestamp(System
						.currentTimeMillis()));
				aController.setLastupdate(new Timestamp(System
						.currentTimeMillis()));

				aController.setEmployee(lastUpdatedByEmployee);

				controllerList.add(aController);
			}
		}
    controlSetupDao.addControllers(controllerList);
    LOGGER.exiting(getClass().getName(), "saveControllerToAor");
	}

  /**
   * Provides all ControlAORs for logged in UserId.
   * 
   * @param languageCode
   *            String
   * @return controlAors list of controlAORs
   * @throws MicosException
   *             Micos Exception
   */
  public List<SelectItem> getAllRFCs(String languageCode) throws MicosException {
    LOGGER.entering(getClass().getName(), "getAllRFCs");
    MicosUserBean user = (MicosUserBean) UserBean.getUserBean();
    List<SelectItem> rfcviewList = new ArrayList<SelectItem>();
    rfcviewList.add(new SelectItem("-1", SELECT_ITEM_MSG_FOR_RFC));
    List<BigDecimal> listOfRFCId = controlSetupDao
        .getRFCsForAOR(user.getWorkingAsUser().getEmployeeid());

    if (!SystemUtil.isNull(listOfRFCId)) {
      for (int i = 0; i < listOfRFCId.size(); i++) {
        String rfc = null;
        rfc = SystemUtil.getClobValueAsString(controlSetupDao
            .findControlObjTitleForRFC(languageCode, listOfRFCId.get(i)
                .intValue()));
        rfcviewList.add(new SelectItem(listOfRFCId.get(i).toString(), rfc));
      }
    }
    LOGGER.exiting(getClass().getName(), "getAllRFCs");
    return rfcviewList;
  }

	/**
	 * {@inheritDoc}
	 */
	public List<SelectItem> getPeriodTypeList() throws MicosException {
		LOGGER.entering(getClass().getName(),"getPeriodTypeList");
		List<SelectItem> periodTypeList = new ArrayList<SelectItem>();
		List<Periodtype> periodTypeListFromDB = controlSetupDao
				.getPeriodTypeList();

		for (Periodtype periodtype : periodTypeListFromDB) {
			periodTypeList.add(new SelectItem(periodtype.getPeriodtypeid()
					.toString(), periodtype.getName()));
		}
		LOGGER.exiting(getClass().getName(),"getPeriodTypeList");
		return periodTypeList;

	}

	/**
	 * Changes the status based on accept or reject
	 * 
	 * @param controlid
	 *            Integer
	 * @param loggedInUserId
	 *            Integer
	 * @param isAccepted
	 *            boolean
	 * @param comments
	 *            String
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void changeStatus(Integer controlid, Integer loggedInUserId,
			boolean isAccepted, String comments) throws MicosException {
		  LOGGER.entering(getClass().getName(),"changeStatus");
			controlSetupDao.changeStatus(controlid, loggedInUserId, isAccepted,
					comments);

//			if (isAccepted) {
//				Set<EmployeeView> employeeSet = new HashSet<EmployeeView>();
//
//				Control selectedControl = controlSetupDao
//						.getControlPropertiesForControlID(controlid,
//								MicosConstantValues.LanguageCode.EN);
//				List<ControlStepView> controlStepViewList = populateControlSteps(
//						selectedControl, true);
//				employeeSet = populateEmpList(controlStepViewList);
//				Map empMap = new HashMap();
//
//				Notificationtype notificationType = controlSetupDao
//						.loadNotificationType(MicosConstantValues.MailNotificationTypeCode.CONTROLLAUNCH);
//				String notificationTypeText = notificationType
//						.getNotificationtypetext();
//
//				for (Iterator iteremp = employeeSet.iterator(); iteremp
//						.hasNext();) {
//					EmployeeView employeeView = (EmployeeView) iteremp.next();
//					if (employeeView != null) {
//						if (empMap.get(employeeView.getEmpId()) == null) {
//
//							Notification notification = new Notification();
//							notification.setNotifyuser(controlSetupDao
//									.loadEmployee(Integer.parseInt(employeeView
//											.getEmpId())));
//							String newNotificationTypeText = notificationTypeText
//									.replaceAll("mailControlId", controlid
//											.toString());
//							notification
//									.setNotificationlinetext(newNotificationTypeText);
//							notification.setNotificationtype(notificationType);
//							notification
//									.setEscalated(MicosConstantValues.MailNotificationEscalated.MAILNOTIFICATIONNOTESCALATED);
//							notification.setCreatedate(new Timestamp(System
//									.currentTimeMillis()));
//							notification.setLastupdate(new Timestamp(System
//									.currentTimeMillis()));
//							notification.setLastupdatedby(controlSetupDao
//									.loadEmployee(loggedInUserId));
//							controlSetupDao.saveNotification(notification);
//							empMap.put(employeeView.getEmpId(), employeeView);
//						}
//
//					}
//				}
//			} else {
      if (!isAccepted) {
      Control control = controlSetupDao.loadControl(controlid);
      Integer controlAorId = null;
      List<Controller> controllerList = null;
      Controller controller = null;
      if (control != null && control.getAorowner() != null
          && control.getAorowner().getEmployeeByAorowner() != null) {
        controlAorId = control.getAorowner().getEmployeeByAorowner()
            .getEmployeeid();
        controllerList = controlSetupDao.searchControllerForAor(control
            .getAorowner().getAorownerid());
        if (controllerList != null) {
          Iterator itrController = controllerList.iterator();
          while (itrController.hasNext()) {
            controller = (Controller)itrController.next();
            if (controller != null) {
              notifyControlLaunchRejection(controlid, controller
                  .getController().getEmployeeid(), loggedInUserId);
            }
          }
        }
      }
      if (controlAorId != null) {
        notifyControlLaunchRejection(controlid, controlAorId, loggedInUserId);
      }
    }
    LOGGER.exiting(getClass().getName(), "changeStatus");
	}
	
	/**
   * CR_8 Changes the status based on accept or reject
   * 
   * @param controlid Integer
   * @param loggedInUserId Integer
   * @param isAccepted boolean
   * @param comments String
   * @param deactivatedControlId Control Id
   * @throws MicosException Micos Exception
   */
	public void changeStatus(Integer controlid, Integer loggedInUserId,
      boolean isAccepted, String comments, Integer deactivatedControlId)
      throws MicosException {
		  LOGGER.entering(getClass().getName(),"changeStatus");
			controlSetupDao.changeStatus(controlid, loggedInUserId, isAccepted,
					comments,deactivatedControlId);
			
			if(isAccepted && deactivatedControlId != null) {
				controlSetupDao.addCCElementForDeactivatedControl(deactivatedControlId, controlid, getLoggedinUser());
			}

//			if (isAccepted) {
//				Set<EmployeeView> employeeSet = new HashSet<EmployeeView>();
//
//				Control selectedControl = controlSetupDao
//						.getControlPropertiesForControlID(controlid,
//								MicosConstantValues.LanguageCode.EN);
//				List<ControlStepView> controlStepViewList = populateControlSteps(
//						selectedControl, true);
//				employeeSet = populateEmpList(controlStepViewList);
//				Map empMap = new HashMap();
//
//				Notificationtype notificationType = controlSetupDao
//						.loadNotificationType(MicosConstantValues.MailNotificationTypeCode.CONTROLLAUNCH);
//				String notificationTypeText = notificationType
//						.getNotificationtypetext();
//
//				for (Iterator iteremp = employeeSet.iterator(); iteremp
//						.hasNext();) {
//					EmployeeView employeeView = (EmployeeView) iteremp.next();
//					if (employeeView != null) {
//						if (empMap.get(employeeView.getEmpId()) == null) {
//
//							Notification notification = new Notification();
//							notification.setNotifyuser(controlSetupDao
//									.loadEmployee(Integer.parseInt(employeeView
//											.getEmpId())));
//							String newNotificationTypeText = notificationTypeText
//									.replaceAll("mailControlId", controlid
//											.toString());
//							notification
//									.setNotificationlinetext(newNotificationTypeText);
//							notification.setNotificationtype(notificationType);
//							notification
//									.setEscalated(MicosConstantValues.MailNotificationEscalated.MAILNOTIFICATIONNOTESCALATED);
//							notification.setCreatedate(new Timestamp(System
//									.currentTimeMillis()));
//							notification.setLastupdate(new Timestamp(System
//									.currentTimeMillis()));
//							notification.setLastupdatedby(controlSetupDao
//									.loadEmployee(loggedInUserId));
//							controlSetupDao.saveNotification(notification);
//							empMap.put(employeeView.getEmpId(), employeeView);
//						}
//
//					}
//				}
//			} else {
      if (!isAccepted) {
        Control control = controlSetupDao.loadControl(controlid);
        Integer controlAorId = null;
        List<Controller> controllerList = null;
        Controller controller = null;
        if (control != null && control.getAorowner() != null
            && control.getAorowner().getEmployeeByAorowner() != null) {
          controlAorId = control.getAorowner().getEmployeeByAorowner()
              .getEmployeeid();
          controllerList = controlSetupDao.searchControllerForAor(control
              .getAorowner().getAorownerid());
          if (controllerList != null) {
            Iterator itrController = controllerList.iterator();
            while (itrController.hasNext()) {
              controller = (Controller)itrController.next();
              if (controller != null) {
                notifyControlLaunchRejection(controlid, controller
                    .getController().getEmployeeid(), loggedInUserId);
              }
            }
          }
        }
        if (controlAorId != null) {
          notifyControlLaunchRejection(controlid, controlAorId, loggedInUserId);
        }
      }
    LOGGER.exiting(getClass().getName(), "changeStatus");			
	}

  /**
   * Notify ControlAoR Owner or Controllers incase of rejections
   * @param controlid Control Id
   * @param controlAorId Control AoR Id
   * @param loggedInUserId Logged in employee
   * @throws MicosException Exception
   */
  private void notifyControlLaunchRejection(Integer controlid,
      Integer controlAorId, Integer loggedInUserId) throws MicosException {
    Notificationtype notificationType = controlSetupDao
        .loadNotificationType(MicosConstantValues.MailNotificationTypeCode.CONTROLLAUNCHREJECT);
    String notificationTypeText = notificationType.getNotificationtypetext();

    Notification notification = new Notification();
    notification.setNotifyuser(controlSetupDao.loadEmployee(controlAorId));
    String newNotificationTypeText = notificationTypeText.replaceAll(
        "mailControlId", controlid.toString());
    notification.setNotificationlinetext(newNotificationTypeText);
    notification.setNotificationtype(notificationType);
    notification
        .setEscalated(MicosConstantValues.MailNotificationEscalated.MAILNOTIFICATIONNOTESCALATED);
    notification.setCreatedate(new Timestamp(System.currentTimeMillis()));
    notification.setLastupdate(new Timestamp(System.currentTimeMillis()));
    notification.setLastupdatedby(controlSetupDao.loadEmployee(loggedInUserId));
    controlSetupDao.saveNotification(notification);
    LOGGER.exiting(getClass().getName(), "changeStatus");
  }
  
	/**
   * populate Employee List
   * 
   * @param controlStepViewList
   *          the List
   * @return employeeSet
   */
//	private Set<EmployeeView> populateEmpList(
//			List<ControlStepView> controlStepViewList) {
//		LOGGER.entering(getClass().getName(),"populateEmpList");
//		Set<EmployeeView> employeeSet = new HashSet<EmployeeView>();
//		for (Iterator iter = controlStepViewList.iterator(); iter.hasNext();) {
//			ControlStepView controlStepView = (ControlStepView) iter.next();
//			if (controlStepView != null) {
//				if (controlStepView.getCheckSupport()
//						&& controlStepView.getSupportSchemaView() != null) {
//					if (controlStepView.getSupportSchemaView()
//							.getSelectionViaHierarchyView() != null) {
//
//						employeeSet.addAll(controlStepView
//								.getSupportSchemaView()
//								.getSelectionViaHierarchyView());
//					}
//					if (controlStepView.getSupportSchemaView()
//							.getSelectionViaPIDView() != null) {
//						employeeSet.addAll(controlStepView
//								.getSupportSchemaView()
//								.getSelectionViaPIDView());
//					}
//
//				}
//				if (controlStepView.getCheckPerformance()
//						&& controlStepView.getPerformanceSchemaView() != null) {
//					if (controlStepView.getPerformanceSchemaView()
//							.getSelectionViaHierarchyView() != null) {
//						employeeSet.addAll(controlStepView
//								.getPerformanceSchemaView()
//								.getSelectionViaHierarchyView());
//					}
//					if (controlStepView.getPerformanceSchemaView()
//							.getSelectionViaPIDView() != null) {
//						employeeSet.addAll(controlStepView
//								.getPerformanceSchemaView()
//								.getSelectionViaPIDView());
//					}
//
//				}
//				if (controlStepView.getCheckQA()
//						&& controlStepView.getQaSchemaView() != null) {
//					if (controlStepView.getQaSchemaView()
//							.getSelectionViaHierarchyView() != null) {
//						employeeSet.addAll(controlStepView.getQaSchemaView()
//								.getSelectionViaHierarchyView());
//					}
//					if (controlStepView.getQaSchemaView()
//							.getSelectionViaPIDView() != null) {
//						employeeSet.addAll(controlStepView.getQaSchemaView()
//								.getSelectionViaPIDView());
//					}
//
//				}
//				if (controlStepView.getCheckSignOff()
//						&& controlStepView.getSignOffSchemaView() != null) {
//					if (controlStepView.getSignOffSchemaView()
//							.getSelectionViaHierarchyView() != null) {
//						employeeSet.addAll(controlStepView
//								.getSignOffSchemaView()
//								.getSelectionViaHierarchyView());
//					}
//					if (controlStepView.getSignOffSchemaView()
//							.getSelectionViaPIDView() != null) {
//						employeeSet.addAll(controlStepView
//								.getSignOffSchemaView()
//								.getSelectionViaPIDView());
//					}
//				}
//			}
//		}
//		return employeeSet;
//	}

	/**
	 * Changes the status based on accept or reject
	 * 
	 * @param controlid
	 *            Integer
	 * @param loggedInUserId
	 *            Integer
	 * @param isAccepted
	 *            boolean
	 * @param comments
	 *            String
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void changeStatusofSharedControl(Integer controlid,
      Integer loggedInUserId, boolean isAccepted, String comments)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "changeStatusofSharedControl");
    Sharedcontrol sharedcontrol = controlSetupDao.loadSharedControl(controlid);
    Controlrfc controlrfc = controlSetupDao.loadControlRfcbyRfcId(sharedcontrol
        .getControlrfc().getControlrfcid());
    Employee employee = controlSetupDao.loadEmployee(loggedInUserId);

    controlrfc.setEmployee(employee);
    if (isAccepted) {
      controlrfc.setStatus(controlSetupDao
          .loadStatus(MicosConstantValues.Status.WAITING_FOR_APPROVAL));
    } else {
      controlrfc.setStatus(controlSetupDao
          .loadStatus(MicosConstantValues.Status.REJECTED));
    }

    controlSetupDao.updateControlRfc(controlrfc);

    sharedcontrol.setEmployee(employee);
    if (isAccepted) {
      sharedcontrol.setStatus(controlSetupDao
          .loadStatus(MicosConstantValues.Status.ACCEPTED));
    } else {
      sharedcontrol.setStatus(controlSetupDao
          .loadStatus(MicosConstantValues.Status.REJECTED));
    }
    if (comments != null) {
      sharedcontrol.setSharecomment(comments);
    }

    controlSetupDao.updateSharedControl(sharedcontrol);

    LOGGER.exiting(getClass().getName(), "changeStatusofSharedControl");
  }

	/**
	 * Finds the informations of scopeAoR based on the control id
	 * 
	 * @param controlId
	 *            Integer
	 * @return the list containing the scopeAoR information
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<AoRView> findScopeAorforControlid(Integer controlId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "findScopeAorforControlid");
    List<AoRView> listOfScopeAorDetails = null;
    List<Object[]> controlList = controlSetupDao
        .findScopeAorforControlid(controlId);
    if (!SystemUtil.isNull(controlList)) {
      listOfScopeAorDetails = new ArrayList<AoRView>();
      AoRView aoRView = null;

      for (int itr = 0; itr < controlList.size(); itr++) {
        aoRView = new AoRView();
        Object[] rowVal = controlList.get(itr);
        BigDecimal bigPk = (BigDecimal)rowVal[0];
        aoRView.setId(bigPk.intValue());
        aoRView.setHiearchyElementName((String)rowVal[1]);
        aoRView.setScopeAoROwnerName((String)rowVal[2]);
        aoRView.setScopeAoRPID((String)rowVal[3]);
        listOfScopeAorDetails.add(aoRView);
      }
    }
    LOGGER.exiting(getClass().getName(), "findScopeAorforControlid");
    return listOfScopeAorDetails;
  }

	/**
	 * Provides all ControlAORs for logged in UserId.
	 * 
	 * @param controlid
	 *            logged in Control Id
	 * @param loggedInUserId
	 *            logged in user id
	 * @param languageCode
	 *            Language Code
	 * @return list of controlAOROwner Details
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<ControlView> approvalrequests(Integer controlid,
      Integer loggedInUserId, String languageCode) throws MicosException {
    LOGGER.entering(getClass().getName(), "approvalrequests");
    List<ControlView> aorownerdetails = null;
    Control cntrl = controlSetupDao.approvalrequests(controlid, loggedInUserId,
        languageCode);
    aorownerdetails = new ArrayList<ControlView>();
    if (null != cntrl) {
      ControlView controlView = new ControlView();
      Aorowner aorOwner = cntrl.getAorowner();
      if (null != aorOwner) {
        Employee employeeByAorOwner = aorOwner.getEmployeeByAorowner();
        if (null != employeeByAorOwner) {
          controlView.setControlAorOwnerName(employeeByAorOwner
              .getEmployeeName());
          controlView.setControlAorOwnerId(employeeByAorOwner.getEmployeeid());
        }
        Hierarchyelement hrchyelement = aorOwner.getHierarchyelement();
        if (null != hrchyelement) {
          LanguageCodeView controlDescriptioneObj = new LanguageCodeView();
          controlDescriptioneObj.setValue(hrchyelement.getName());
          controlView
              .setControlDescriptioneObj(new LanguageCodeView[] {controlDescriptioneObj});
        }
      }
      aorownerdetails.add(controlView);
    }
    LOGGER.exiting(getClass().getName(), "approvalrequests");
    return aorownerdetails;
	}

	/**
	 * delete AControlReference
	 * @param intControlRefId Control Reference Id
	 * @param controlId Integer
	 * @throws MicosException Micos Exception
	 */
	public void deleteControlReference(Integer intControlRefId, Integer controlId) throws MicosException {
    LOGGER.entering(getClass().getName(), "deleteAControlReference");
    Control control = controlSetupDao.loadControl(intControlRefId);
    Integer controlAorId = null;
    if (control != null && control.getAorowner() != null
        && control.getAorowner().getEmployeeByAorowner() != null) {
      controlAorId = control.getAorowner().getEmployeeByAorowner()
          .getEmployeeid();
    }
    controlSetupDao.deleteControlReference(intControlRefId, controlId);
    if (intControlRefId != null) {
      Notificationtype notificationType = controlSetupDao
          .loadNotificationType(MicosConstantValues.MailNotificationTypeCode.REMOVECONTROLREF);
      String notificationTypeText = notificationType.getNotificationtypetext();
      notificationTypeText = notificationTypeText.replace("referredControl",
          intControlRefId.toString());
      notificationTypeText = notificationTypeText.replace("referringControl",
          controlId.toString());
      Notification notification = new Notification();
      notification.setNotifyuser(controlSetupDao.loadEmployee(controlAorId));
      notification.setNotificationlinetext(notificationTypeText);
      notification.setNotificationtype(notificationType);
      notification
          .setEscalated(MicosConstantValues.MailNotificationEscalated.MAILNOTIFICATIONNOTESCALATED);
      notification.setCreatedate(new Timestamp(System.currentTimeMillis()));
      notification.setLastupdate(new Timestamp(System.currentTimeMillis()));
      notification.setLastupdatedby(controlSetupDao
          .loadEmployee(getLoggedinUser()));
      controlSetupDao.saveNotification(notification);
    }

    LOGGER.exiting(getClass().getName(), "deleteAControlReference");
  }

	/**
   * Provides all ControlAORs for logged in UserId.
   * 
   * @param shareControlid
   *          Integer
   * @param loggedInUserId
   *          logged in user id
   * @param languageCode
   *          Language Code
   * @return list of controlAOROwner Details
   * @throws MicosException
   *           Micos Exception
   */
	public List<EmployeeView> approvalrequestsForShare(Integer shareControlid,
      Integer loggedInUserId, String languageCode) throws MicosException {
    LOGGER.entering(getClass().getName(), "approvalrequestsForShare");
    List<EmployeeView> aorownerdetails = null;
    Sharedcontrol cntrl = controlSetupDao.approvalrequestsForShare(
        shareControlid, loggedInUserId, languageCode);
    aorownerdetails = new ArrayList<EmployeeView>();
    if (null != cntrl) {
      EmployeeView aorView = new EmployeeView();
      Aorowner aorOwner = cntrl.getAorowner();
      if (null != aorOwner) {
        Employee employeeByAorOwner = aorOwner.getEmployeeByAorowner();
        if (null != employeeByAorOwner) {
          aorView.setAorOwner(employeeByAorOwner.getEmployeeName());
          aorView.setAorOwnerId(employeeByAorOwner.getEmployeeid());
          aorView.setAorOwnerPid(employeeByAorOwner.getPid());
        }
        Hierarchyelement hrchyelement = aorOwner.getHierarchyelement();
        if (null != hrchyelement) {
          aorView.setHierarchyElementDesc(hrchyelement.getName());
        }
        // Setting Status code in Employee Description setter method
        // as no other method exists
        aorView.setEmpDescription(cntrl.getStatus().getStatuscode());
      }
      aorownerdetails.add(aorView);
    }
    LOGGER.exiting(getClass().getName(), "approvalrequestsForShare");
    return aorownerdetails;
	}

	/**
	 * submit the control operation
	 * 
	 * @param controlId
	 *            Integer
	 * @param loggedinUserId
	 *            the Integer
	 * @param rfcId
	 *            Integer
	 * @throws MicosException
	 *             MicosException
	 */
	public void submitControl(Integer controlId, Integer rfcId,
      Integer loggedinUserId) throws MicosException {
    LOGGER.entering(getClass().getName(), "submitControl");
    controlSetupDao.submitControl(controlId, rfcId);

    Integer scopeAorEmpId = null;
    Rfc selectedRfc = controlSetupDao.loadRfc(rfcId);
    Aorowner scopeAor = selectedRfc.getAorowner();
    if (scopeAor != null) {
      Employee scopeAorEmp = scopeAor.getEmployeeByAorowner();
      if (scopeAorEmp != null) {
        scopeAorEmpId = scopeAorEmp.getEmployeeid();

      }
    }
    if (scopeAorEmpId != null) {
      Notificationtype notificationType = controlSetupDao
          .loadNotificationType(MicosConstantValues.MailNotificationTypeCode.CONTROLTOSCOPEAOR);
      String notificationTypeText = notificationType.getNotificationtypetext();
      String newNotificationTypeText = notificationTypeText.replaceAll(
          "mailControlId", controlId.toString());
      Notification notification = new Notification();
      notification.setNotifyuser(controlSetupDao.loadEmployee(scopeAorEmpId));
      notification.setNotificationlinetext(newNotificationTypeText);
      notification.setNotificationtype(notificationType);
      notification
          .setEscalated(MicosConstantValues.MailNotificationEscalated.MAILNOTIFICATIONNOTESCALATED);
      notification.setCreatedate(new Timestamp(System.currentTimeMillis()));
      notification.setLastupdate(new Timestamp(System.currentTimeMillis()));
      notification.setLastupdatedby(controlSetupDao
          .loadEmployee(loggedinUserId));
      controlSetupDao.saveNotification(notification);
    }
    LOGGER.exiting(getClass().getName(), "submitControl");
	}

	/**
	 * Gets all Controls from ControlRfc for logged in User.
	 * 
	 * @return List of Controls
	 * @throws MicosException MicosException
	 * 
	 */
	public List<ControlView> getAllControlsForLaunchControl()
			throws MicosException {
		LOGGER.entering(getClass().getName(),"getAllControlsForLaunchControl");
		List listOfControlsFromDao = null;
		List<ControlView> launchControlList = null;
		try {
			listOfControlsFromDao = controlSetupDao
					.getAllLaunchControlsForControlAOROwner();
			if (!SystemUtil.isNull(listOfControlsFromDao)) {
				launchControlList = new ArrayList<ControlView>();
				Iterator itControls = listOfControlsFromDao.iterator();
				while (itControls.hasNext()) {
					Controlrfc controlrfc = (Controlrfc) itControls.next();
					if (null != controlrfc) {
						ControlView controlView = new ControlView();
						controlView.setId(controlrfc.getControl()
								.getControlid());
            			controlView.setMasterControlId(controlrfc.getControl().getMastercontrolid());
            			controlView.setControlOfControlOwner(true);
						Status status = controlrfc.getStatus();
						if (null != status) {
							controlView.setControlStatus(status
									.getStatusdescription());
						}
						SimpleDateFormat date = new SimpleDateFormat(
								"dd-MMM-yyyy");
						String eDate = date.format(controlrfc.getControl()
								.getEnddate());
						if (null != eDate) {
							controlView.setControlEndDate(eDate);
						}
						String cDate = date.format(controlrfc.getControl()
								.getCreatedate());
						if (null != cDate) {
							controlView.setCreateDate(cDate);
						}
						Controltype controltype = controlrfc.getControl()
								.getControltype();
						if (null != controltype) {
							controlView.setControlType(controltype.getName());
						}
						controlView
								.setControlTitleObj(populateControlTitle(controlView
										.getId()));
						Aorowner aorOwner = controlrfc.getControl()
								.getAorowner();
						if (null != aorOwner) {
							Employee employeeByAorOwner = aorOwner
									.getEmployeeByAorowner();
							if (null != employeeByAorOwner) {
								controlView
										.setControlAorOwnerName(employeeByAorOwner
												.getEmployeeName());
								controlView.setControlAorOwnerId(aorOwner
										.getAorownerid());
								controlView.setControlAORPid(employeeByAorOwner
										.getPid());
							}
						}
						launchControlList.add(controlView);
					}
				}
			}
		} catch (MicosException micosException) {
			LOGGER.error("getAllControlsForLaunchControl", micosException);
			throw micosException;
		} finally {
			LOGGER.exiting(getClass().getName(),"getAllControlsForLaunchControl");
			
		}
		return launchControlList;

	}

	/**
	 * delete A Step
	 * 
	 * @return dflag
	 * @param intSelectedStepId
	 *            Integer
	 * @throws MicosException
	 *             MicosException
	 * 
	 */
	public boolean deleteStep(Integer intSelectedStepId) throws MicosException {
		LOGGER.entering(getClass().getName(), "deleteAStep");
		boolean dflag = false;
		dflag = controlSetupDao.deleteStep(intSelectedStepId);
		LOGGER.exiting(getClass().getName(), "deleteAStep");
		return dflag;
	}

	/**
	 * CR_8 Deletes A Power Option
	 * 
	 * @param intSelectedPowerOptionId
	 *            Poweroption Id
	 * @return delete status
	 * @throws MicosException
	 *             Micos Exception
	 */
	public boolean deletePowerOption(Integer intSelectedPowerOptionId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "deleteAPowerOption");
		boolean dflag = false;
		dflag = controlSetupDao.deletePowerOption(intSelectedPowerOptionId);
		LOGGER.exiting(getClass().getName(), "deleteAPowerOption");
		return dflag;
	}

	/**
	 * delete A Link
	 * 
	 * @param linkId
	 *            Integer
	 * @throws MicosException
	 *             MicosException
	 * 
	 */
	public void deleteLink(Integer linkId) throws MicosException {
	    LOGGER.entering(getClass().getName(), "deleteALink");
	    controlSetupDao.deleteLink(linkId);
	    LOGGER.exiting(getClass().getName(), "deleteALink");
    }

	/**
	 * Provides RFCs.
	 * 
	 * @param controlAoRId
	 *            selected Control AoR Owner
	 * @param rfcId
	 *            RFC ID
	 * @param rfcName
	 *            ControlObjective Title
	 * @param scopeAoR
	 *            Scope AoR
	 * @param languageCode
	 *            language Code
	 * @return List of RFCview
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<RFCView> getRFCsForSelectedControlAoR(Integer controlAoRId,
      Integer rfcId, String rfcName, String scopeAoR, String languageCode)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "getRFCsForSelectedControlAoR");
    List<RFCView> rfcviewList = new ArrayList<RFCView>();
    RFCView rfcView = null;
    List<Object[]> listOfRFCs = null;
    listOfRFCs = controlSetupDao.getRFCsForSelectedControlAoR(controlAoRId,
        rfcId, rfcName, scopeAoR, languageCode);

    if (!SystemUtil.isNull(listOfRFCs)) {

      for (int i = 0; i < listOfRFCs.size(); i++) {
        rfcView = new RFCView();
        Object[] rowVal = listOfRFCs.get(i);
        BigDecimal bigPk = (BigDecimal)rowVal[0];
        rfcView.setId(bigPk.intValue());

        // rfcView.setId(new Integer(listOfRFCs.get(i).intValue()));
        rfcView
            .setControlObjTitleObj(populateControlObjTitle(bigPk.intValue()));
        /*
         * LanguageCodeView controlTitleObj = new LanguageCodeView();
         * controlTitleObj.setLanguageType(languageCode);
         * controlTitleObj.setValue(SystemUtil .getClobValueAsString((Clob)
         * rowVal[1]));
         */
        EmployeeView employeeView = new EmployeeView();
        employeeView.setHierrarchyName((String)rowVal[3]);
        employeeView.setHierarchyElementName((String)rowVal[4]);
        rfcView.setEmployeeView(employeeView);
        rfcView.setScopeAoR((String)rowVal[1]);
        // Goal title
        BigDecimal bigGoalPk = (BigDecimal)rowVal[5];
        rfcView.setGoalTitleObj(populateGoalTitle(bigGoalPk.intValue()));
        rfcviewList.add(rfcView);

      }
    }
    LOGGER.exiting(getClass().getName(), "getRFCsForSelectedControlAoR");
    return rfcviewList;
	}

	/**
	 * Gets Requested Access Controls to approve or Request.
	 * 
	 * @param loggedInUserId
	 *            Id of Loggedin User
	 * @param languageCode
	 *            Code of selected language
	 * @return List
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<ControlView> getAllRequestedAccessControls(
			Integer loggedInUserId, String languageCode) throws MicosException {
		LOGGER.entering(getClass().getName(),"getAllRequestedAccessControls");
		List listOfControlsFromDao = null;
		List<ControlView> accessControlList = null;
		try {
			listOfControlsFromDao = controlSetupDao
					.getAllRequestAccessControls(loggedInUserId);
			if (!SystemUtil.isNull(listOfControlsFromDao)) {
				accessControlList = new ArrayList<ControlView>();
				Iterator itControls = listOfControlsFromDao.iterator();
				while (itControls.hasNext()) {
					Accessaor accessaor = (Accessaor) itControls.next();
					if (null != accessaor) {
						boolean aorBool = false;
						boolean accBool = false;
						ControlView controlView = new ControlView();
						controlView.setAccessAorId(accessaor.getAccessaorid());
						controlView.setId(accessaor.getControl().getControlid());
            controlView.setMasterControlId(accessaor.getControl()
                .getMastercontrolid());
						Status status = accessaor.getStatus();
						if (null != status) {
							controlView.setControlStatus(status
									.getStatusdescription());
						}
						Controltype controltype = accessaor.getControl()
								.getControltype();
						if (null != controltype) {
							controlView.setControlType(controltype.getName());
						}
						controlView
								.setControlTitleObj(populateControlTitle(controlView
										.getId()));
						controlView.setControlAorOwnerName(accessaor
								.getAorowner().getEmployeeByAorowner()
								.getEmployeeName());
						controlView.setControlAORPid(accessaor.getAorowner()
								.getEmployeeByAorowner().getPid());
						controlView.setControlAorOwnerId(accessaor
								.getAorowner().getEmployeeByAorowner()
								.getEmployeeid());
						controlView.setRequesterId(accessaor.getRequester()
								.getEmployeeByAorowner().getEmployeeid());
						if (!controlView.getControlAorOwnerId().equals(
								loggedInUserId)) {
							aorBool = true;
						}
						if (!MicosConstantValues.Status.WAITING_FOR_APPROVAL
                .equalsIgnoreCase(status.getStatuscode())) {
              accBool = true;
						}

						if (aorBool || accBool) {
							controlView.setShowApprove(false);
						}
						if (!controlView.getRequesterId()
								.equals(loggedInUserId)) {
							controlView.setShowDelete(false);
						}
						accessControlList.add(controlView);
					}
				}
			}

		} catch (MicosException micosException) {
			LOGGER.error("getAllRequestedAccessControls", micosException);
			throw micosException;
		} finally {
			LOGGER.exiting(getClass().getName(),"getAllRequestedAccessControls");
			
		}
		return accessControlList;

	}

	/**
	 * Changes the status based on accept or reject
	 * 
	 * @param accessAorId
	 *            Integer
	 * @param loggedInUserId
	 *            Integer
	 * @param isAccepted
	 *            boolean
	 * @param comments
	 *            String
	 * 
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void changeStatusofAccessAor(Integer accessAorId,
      Integer loggedInUserId, boolean isAccepted, String comments)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "changeStatusofAccessAor");
    controlSetupDao.changeStatusofAccessAor(accessAorId, loggedInUserId,
        isAccepted, comments);
    LOGGER.exiting(getClass().getName(), "changeStatusofAccessAor");
	}

	/**
	 * Gets AOROwner to approve or Request.
	 * 
	 * @param accessaorid
	 *            Integer
	 * @param loggedInUserId
	 *            Id of Loggedin User
	 * @param languageCode
	 *            Code of selected language
	 * @return List
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<EmployeeView> approvalrequestsForAccess(Integer accessaorid,
      Integer loggedInUserId, String languageCode) throws MicosException {
    LOGGER.entering(getClass().getName(), "approvalrequestsForAccess");
    Accessaor accessaor = null;
    List<EmployeeView> aorownerdetails = null;
    accessaor = controlSetupDao.approvalrequestsForAccessControl(accessaorid,
        loggedInUserId, languageCode);
    aorownerdetails = new ArrayList<EmployeeView>();
    if (null != accessaor) {
      EmployeeView employeeView = new EmployeeView();
      employeeView.setAorOwner(accessaor.getRequester().getEmployeeByAorowner()
          .getEmployeeName());
      if (accessaor.getRequester().getEmployeeByAorowner().getEmployeeid() != null) {
        employeeView.setEmpId(accessaor.getRequester().getEmployeeByAorowner()
            .getEmployeeid().toString());
      }
      Hierarchyelement hrchyelement = accessaor.getRequester()
          .getHierarchyelement();
      if (hrchyelement != null) {
        employeeView.setHierarchyElementName(hrchyelement.getName());
      }
      aorownerdetails.add(employeeView);
    }
    LOGGER.exiting(getClass().getName(), "approvalrequestsForAccess");
    return aorownerdetails;
	}

	/**
   * Requests for control returns controlid of that.
   * 
   * @return ownControls
   * @param loggedInUserId
   *          Id of Loggedin User
   * @param listControlView
   *          List
   * @param requester
   *          the Aorowner
   * @throws MicosException
   *           Micos Exception
   */
	public List<Integer> requestForAccess(List<ControlView> listControlView,
			Aorowner requester, Integer loggedInUserId) throws MicosException {
		LOGGER.entering(getClass().getName(),"requestForAccess");
		MicosUserBean user = (MicosUserBean) UserBean.getUserBean();
		Integer lastUpdatedUserId = null;
		List<Integer> ownControls = new ArrayList<Integer>();
		boolean success = true;
		try {
			Accessaor accessaor = null;
			Iterator iterator = listControlView.iterator();
			while (iterator.hasNext()) {
				ControlView controlView = (ControlView) iterator.next();
				accessaor = new Accessaor();

				Control control = controlSetupDao.loadControl(controlView
						.getId());
				accessaor.setControl(control);

				Aorowner selectedAorowner = controlSetupDao
						.loadAorowner(controlView.getControlAorOwnerId());
				accessaor.setAorowner(selectedAorowner);
				Integer aor = selectedAorowner.getEmployeeByAorowner()
						.getEmployeeid();

				accessaor.setRequester(requester);

				accessaor.setCreatedate(new Timestamp(System
						.currentTimeMillis()));

				String statusCode = MicosConstantValues.Status.WAITING_FOR_APPROVAL;
				Status status = controlSetupDao.loadStatus(statusCode);
				accessaor.setStatus(status);
				if (null != user.getLoginUser()) {
					lastUpdatedUserId = user.getLoginUser().getEmployeeid();
				}
				Employee lastUpdatedByEmployee = controlSetupDao
						.loadEmployee(lastUpdatedUserId);
				accessaor.setEmployee(lastUpdatedByEmployee);
				if (!aor.equals(loggedInUserId)) {
					if (!controlSetupDao.saveAccessAor(accessaor)) {
						success = false;
					}

					Notificationtype notificationType = controlSetupDao
							.loadNotificationType(MicosConstantValues.MailNotificationTypeCode.ACCESSAOR);
					String notificationTypeText = notificationType
							.getNotificationtypetext();
					String newnotificationTypeText = notificationTypeText
							.replaceAll("mailControlId", controlView.getId()
									.toString());

					Notification notification = new Notification();
					notification.setNotifyuser(controlSetupDao
							.loadEmployee(aor));
					notification
							.setNotificationlinetext(newnotificationTypeText);
					notification.setNotificationtype(notificationType);
					notification.setEscalated("N");
					notification.setCreatedate(new Timestamp(System
							.currentTimeMillis()));
					notification.setLastupdate(new Timestamp(System
							.currentTimeMillis()));
					notification.setLastupdatedby(controlSetupDao
							.loadEmployee(lastUpdatedUserId));
					controlSetupDao.saveNotification(notification);
				} else {
					ownControls.add(controlView.getId());
				}

			}
			if (!success) {
				throw new MicosException("MICOS.Controlsetupandissuance.error.DuplicateRequest");
			}
		} catch (MicosException micosException) {
			LOGGER.error("requestForAccess", micosException);
			throw micosException;
		} finally {
			LOGGER.exiting(getClass().getName(), "requestForAccess");
			
		}
		return ownControls;
	}

	/**
	 * Returns all the parents of the AOR specified.
	 * 
	 * @param aorId
	 *            Integer
	 * @return result
	 * @throws MicosException
	 *             MicosException
	 */
	public List<EmployeeView> getAoRParentList(Integer aorId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "getAoRParentList");
    List<EmployeeView> result = new ArrayList<EmployeeView>();
    result = controlSetupDao.getAoRParentList(aorId);
    LOGGER.exiting(getClass().getName(), "getAoRParentList");

    return result;
	}

	/**
	 * Searches Controls based on search criteria.
	 * 
	 * @return List of controls based on search
	 * @param controlId
	 *            Integer
	 * @param controlTitle
	 *            String
	 * @param languageCode
	 *            String
	 * @param selectedAor
	 *            String
	 * @throws MicosException
	 *             Micos Exception
	 */
	public List<ControlView> getCriteriaRequestedAccessControls(
      String selectedAor, Integer controlId, String controlTitle,
      String languageCode) throws MicosException {
    LOGGER.entering(getClass().getName(), "getCriteriaRequestedAccessControls");
    List<ControlView> listAccessAor = null;
    List<Object[]> controlList = controlSetupDao
        .getCriteriaRequestedAccessControls(selectedAor, controlId,
            controlTitle, languageCode);
    if (!SystemUtil.isNull(controlList)) {
      listAccessAor = new ArrayList<ControlView>();
      ControlView controlView = null;
      for (int itr = 0; itr < controlList.size(); itr++) {
        controlView = new ControlView();
        Object[] rowVal = controlList.get(itr);
        BigDecimal bigPk = (BigDecimal)rowVal[0];
        controlView.setId(bigPk.intValue());
        LanguageCodeView controlTitleObj = new LanguageCodeView();
        controlTitleObj.setLanguageType(languageCode);
        controlTitleObj.setValue(SystemUtil
            .getClobValueAsString((Clob)rowVal[1]));
        controlView
            .setControlTitleObj(populateControlObjTitle(bigPk.intValue()));
        controlView.setControlType((String)rowVal[2]);
        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
        String eDate = date.format(rowVal[3]);
        controlView.setControlEndDate(eDate);
        String createDate = date.format(rowVal[7]);
        controlView.setCreateDate(createDate);
        controlView.setControlStatus((String)rowVal[4]);
        controlView.setControlAorOwnerName((String)rowVal[5]);
        BigDecimal bigdecID = (BigDecimal)rowVal[6];
        controlView.setControlAorOwnerId(bigdecID.intValue());
        BigDecimal bigdecAccessAorId = (BigDecimal)rowVal[8];
        controlView.setAccessAorId(bigdecAccessAorId.intValue());
        if (controlView.getControlStatus().equalsIgnoreCase("Accepted")) {
          controlView.setShowApprove(false);
        }
        listAccessAor.add(controlView);
      }
    }
    LOGGER.exiting(getClass().getName(), "getCriteriaRequestedAccessControls");
    return listAccessAor;
	}

	/**
	 * load Aor owner
	 * 
	 * @return result
	 * @param aorID
	 *            Integer
	 * @throws MicosException
	 *             Micos Exception
	 */
	public Aorowner loadAorowner(Integer aorID) throws MicosException {
    LOGGER.entering(getClass().getName(), "loadAorowner");
    Aorowner aorOwner = null;
    aorOwner = controlSetupDao.loadAorowner(aorID);
    LOGGER.exiting(getClass().getName(), "loadAorowner");
    return aorOwner;
	}

	/**
	 * Fetches {@code loadAorowner} the AoR owner.
	 * 
	 * @param aorID
	 *            Integer
	 * @param loggedinUserID
	 *            the Integer
	 * @return Aorowner
	 * @throws MicosException
	 *             Micos Exception
	 */
	public Aorowner loadAorownerForRequester(Integer loggedinUserID, Integer aorID)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "loadAorownerForRequester");
    Aorowner aorOwner = null;
    aorOwner = controlSetupDao.loadAorownerForRequester(loggedinUserID, aorID);
    LOGGER.exiting(getClass().getName(), "loadAorownerForRequester");
    return aorOwner;
	}

	/**
	 * gets SelectedAttachment
	 * 
	 * @return attachView
	 * @param str
	 *            String
	 * @throws MicosException
	 *             Micos Exception
	 */
	public AttachmentView getSelectedAttachment(String str) throws MicosException {
    LOGGER.entering(getClass().getName(), "getSelectedAttachment");
    AttachmentView attachView = new AttachmentView();
    Attachment attachment = new Attachment();
    attachment = controlSetupDao.getSelectedAttachment(str);
    if (null != attachment) {
      attachView = populateAttachmentView(attachment, attachView);
    }
    LOGGER.exiting(getClass().getName(), "getSelectedAttachment");
    return attachView;
	}

	/**
	 * delete Attachments
	 * 
	 * @param selectedAttachmentId
	 *            Integer
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void deleteAttachments(Integer selectedAttachmentId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "deleteAttachments");
    controlSetupDao.deleteAttachments(selectedAttachmentId);
    LOGGER.exiting(getClass().getName(), "deleteAttachments");
	}

	/**
	 * Deleted Adhoc Date.
	 * 
	 * @param adhocDateIdList
	 *            the List
	 * @param adhocDateId
	 * @throws MicosException
	 *             the MicosException
	 */
	public void deleteAdhocDate(List<Integer> adhocDateIdList)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"deleteAdhocDate");
		controlSetupDao.deleteAdhocDate(adhocDateIdList);
		LOGGER.exiting(getClass().getName(),"deleteAdhocDate");
	}

	/**
	 * Reload Detached Control
	 * 
	 * @param controlId
	 *            Integer
	 * @throws MicosException
	 *             Micos Exception
	 * 
	 */
	public void reloadDetachedControl(Integer controlId) throws MicosException {
		LOGGER.entering(getClass().getName(),"reloadDetachedControl");
		Control control = controlSetupDao.getControlPropertiesForControlID(
				controlId, null);
		setDetachedControl(control);
		LOGGER.exiting(getClass().getName(),"reloadDetachedControl");
	}
	
	/**
   * CR_8 Reload Detached PowerOptions
   * 
   * @param controlId Integer
   * @param languageCode User preferred language
   * @throws MicosException Micos Exception
   * 
   */
	public void reloadDetachedPowerOptions(Integer controlId, String languageCode)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "reloadDetachedPowerOptions");
    List<Poweroption> powerOptionList = controlSetupDao
        .getPowerOptionsForControlID(controlId, languageCode);
    setDetachedPowerOptions(powerOptionList);
    LOGGER.exiting(getClass().getName(), "reloadDetachedPowerOptions");
	}

	/**
	 * populateControlObjTitle
	 * 
	 * @param controlObjId
	 *            v
	 * @return v
	 * @throws MicosException
	 *             ex
	 */
	public LanguageCodeView[] populateControlObjTitle(Integer controlObjId)
			throws MicosException {
		LOGGER.entering(getClass().getName(),"populateControlObjTitle");
		List langCodes = SystemUtil.getAllLanguageCode();
		LanguageCodeView[] langualgeControlObjTitle = new LanguageCodeView[langCodes
				.size()];
		Iterator itr = langCodes.iterator();
		String langCode = "";
		int count = 0;
		while (itr.hasNext()) {
			langCode = (String) itr.next();
			langualgeControlObjTitle[count] = new LanguageCodeView();
			langualgeControlObjTitle[count].setLanguageType(langCode);
			langualgeControlObjTitle[count]
					.setValue(SystemUtil.getClobValueAsString(controlSetupDao
							.findControlObjTitleForRFC(langCode, controlObjId)));
			count++;
		}
		LOGGER.exiting(getClass().getName(),"populateControlObjTitle");
		return langualgeControlObjTitle;
	}

	/**
	 * Checks if startAOR and lastAOR are valid.
	 * 
	 * @param startAorOwnerId
	 *            Integer
	 * @param lastAorOwnerId
	 *            Integer
	 * @return boolean
	 * @throws MicosException
	 *             the MicosException
	 */
	public boolean validateAORs(Integer startAorOwnerId, Integer lastAorOwnerId)
			throws MicosException {
		return controlSetupDao.validateAORS(startAorOwnerId, lastAorOwnerId);
	}

	/**
	 * delete ExecutorAoR
	 * 
	 * @param executorAoRId
	 *            Integer
	 * @throws MicosException
	 *             the MicosException
	 */
	public void deleteExecutorAoR(Integer executorAoRId) throws MicosException {
    LOGGER.entering(getClass().getName(), "deleteExecutorAoR");
    controlSetupDao.deleteExecutorAoR(executorAoRId);
    LOGGER.exiting(getClass().getName(), "deleteExecutorAoR");
	}

	/**
	 * delete ExecutorPID
	 * 
	 * @param executorAoRId
	 *            Integer
	 * @throws MicosException
	 *             the MicosException
	 */
	public void deleteSelectedExecutorPID(Integer executorAoRId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "deleteSelectedExecutorPID");
    controlSetupDao.deleteSelectedExecutorPID(executorAoRId);
    LOGGER.exiting(getClass().getName(), "deleteSelectedExecutorPID");
	}
  
	/**
	 * @param detachedPowerOptions
	 *            The detachedPowerOptions to set.
	 */
	public void setDetachedPowerOptions(List<Poweroption> detachedPowerOptions) {
		this.detachedPowerOptions = detachedPowerOptions;
	}

	/**
	 * @return Returns the detachedPowerOptions.
	 */
	public List<Poweroption> getDetachedPowerOptions() {
		return detachedPowerOptions;
	}

	/**
	 * delete Selected control rfc.
	 * 
	 * @param controlRFCId
	 *            Integer
	 * @throws MicosException
	 *             exception
	 */
	public void deleteControlRfc(Integer controlRFCId) throws MicosException {
    LOGGER.entering(getClass().getName(), "deleteControlRfc");
    controlSetupDao.deleteControlRfc(controlRFCId);
    LOGGER.exiting(getClass().getName(), "deleteControlRfc");
	}
  
  /**
   * Show the Setup new Control button only when the logged in user is AoR Owner
   * or Controller
   * 
   * @param loggedInUserId
   *          User Id
   * @return boolean
   * @throws MicosException
   *           Micos Exception
   */
  public boolean validateUserToCreateControl(Integer loggedInUserId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "validateUserToCreateControl");
    boolean canCreateControl = true;
    canCreateControl = controlSetupDao
        .validateUserToCreateControl(loggedInUserId);
    LOGGER.exiting(getClass().getName(), "validateUserToCreateControl");
    return canCreateControl;
  }
  
  /**
	 * CR_23
	 * @param hierarchyElementId	This is the hierarchy element that the search will start from
	 * @param role					This is the role that the we wish to collect all of the employee from
	 * @param levelsDown			This is the number of levels down that should be search for the role
	 * @return List<Employee>		Return List of Employees
	 * @throws Exception			Exception
	 */

	public List<EmployeeView> getAllEmployeesForHE(long hierarchyElementId, Long role, long levelsDown) throws Exception {
	    LOGGER.entering(getClass().getName(), "getAllEmployeesForHE");
	    
	    List<EmployeeView>  empViewList = controlSetupDao.getAllEmployeesForHE(hierarchyElementId, role, levelsDown);
	    
	    LOGGER.exiting(getClass().getName(), "getAllEmployeesForHE");
	    return empViewList;
	}

  /**
   * Get modified control details
   * 
   * @param controlId Control ID
   * @return controlView Control View
   * @throws MicosException Micos Exception
   */
  public ControlView getModifiedControlProperties(Integer controlId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "getModifiedControlProperties");
    ControlView controlView = null;
    Control con = controlSetupDao.getModifiedControlProperties(controlId);
    controlView = new ControlView();
    if (con != null) {
      controlView.setId(con.getControlid());
      controlView.setControlAorOwnerName(con.getAorowner()
          .getEmployeeByAorowner().getEmployeeName());
    }

    LOGGER.exiting(getClass().getName(), "getModifiedControlProperties");
    return controlView;
  }

   /**
     * Gets the Employee id of the control owner.
     * @param intSelectedControlId Integer
     * @return controlOwnerid Integer
     * @throws MicosException exception
     */
  public Integer getControlOwner(Integer intSelectedControlId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "getControlOwner");
    return controlSetupDao.getControlOwner(intSelectedControlId);
  }
  
  /**
   * Checks whether the result exceeds the maximum search limit or not.
   * 
   * @return true, If results are more than maximum specified limit.
   */
  public boolean hasMoreResults() {
	  return controlSetupDao.hasMoreResults();
  }
  
  
  /**
   * Calculates the deadlines for each schema
   * @param scheduleStartDate the start date from where schema start date will be calculated
   * @param scheduleInitPeriodType schema initiation period type
   * @param scheduleInitQty schema initiation duration
   * @param schedulePeriodType schema duration period type
   * @param scheduleDurationQty schema duration
   * @return deadline date
   * @throws MicosException exception
   */
  public Date calculateSchemaDate(Date scheduleStartDate,String scheduleInitPeriodType,
			Integer scheduleInitQty, String schedulePeriodType, Integer scheduleDurationQty) throws MicosException{
	  LOGGER.entering(getClass().getName(),"calculateSchemaDate");
		  return controlSetupDao.calculateSchemaDate(scheduleStartDate,scheduleInitPeriodType,
				scheduleInitQty,schedulePeriodType,scheduleDurationQty);
  }
  
  /**
   * Calculates the start date for schema
   * @param scheduleStartDate the start date from where schema start date will be calculated
   * @param scheduleInitPeriodType schema initiation period type
   * @param scheduleInitQty schema initiation duration
   * @return schema start date
   * @throws MicosException exception
   */
  public Date calculateSchemaStartDate(Date scheduleStartDate,
      String scheduleInitPeriodType, Integer scheduleInitQty)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "calculateSchemaStartDate");
    return controlSetupDao.calculateSchemaStartDate(scheduleStartDate,
        scheduleInitPeriodType, scheduleInitQty);
  }
  
  /**
   * Get the Period type id by passing Period type name
   * 
   * @param periodTypeName period type
   * @return Periodtype
   * @throws MicosException Micos Exception
   */  
  public String getPeriodTypeId(String periodTypeName) throws MicosException {
    LOGGER.entering(getClass().getName(), "getPeriodTypeId");
    String periodTypeId = "";
    Periodtype periodType = null;
    periodType = controlSetupDao.getPeriodTypeId(periodTypeName);
    if (periodType != null && periodType.getPeriodtypeid() != null) {
      periodTypeId = periodType.getPeriodtypeid().toString();
    }
    LOGGER.exiting(getClass().getName(), "getPeriodTypeId");
    return periodTypeId;
  }  
  
  /**
	 * Returns the array which contains the list of rating groups and default
	 * selected value.
	 * 
	 * @param ratinggrouptype
	 *            int
	 * @return Array which contains the list of rating groups and default
	 *         selected value.
	 * @throws MicosException
	 *             Micos Exception
	 */
	public Object[] fetchAllRatinggroups(int ratinggrouptype)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "fetchAllRatinggroups");
    Object[] ratinggroupObj = new Object[2];
    List<Ratinggroup> ratinggroupList = controlSetupDao
        .retrieveRatinggroups(ratinggrouptype);
    if (SystemUtil.isNotNull(ratinggroupList)) {
      List<SelectItem> allRatinggroups = new ArrayList<SelectItem>();
      String defaultRatinggroup = "";
      for (Ratinggroup ratinggroup : ratinggroupList) {
        SelectItem selectitem = new SelectItem(ratinggroup.getRatinggroupid(),
            ratinggroup.getRatinggrouptitle().trim());
        allRatinggroups.add(selectitem);
        if ((ratinggrouptype == MicosConstantValues.Rating.PERFORMANCE_RATING_GROUP)
            && (SystemUtil.isNull(defaultRatinggroup))) {
          if (SystemUtil.isNotNull(ratinggroup.getGlobaldefaultexecution())
              && (ratinggroup.getGlobaldefaultexecution().equalsIgnoreCase("Y"))) {
            defaultRatinggroup = ratinggroup.getRatinggroupid().toString();
          }
        } else if ((ratinggrouptype == MicosConstantValues.Rating.SIGNOFF_RATING_GROUP)
            && (SystemUtil.isNull(defaultRatinggroup))) {
          if (SystemUtil.isNotNull(ratinggroup.getGlobaldefaultsignoff())
              && (ratinggroup.getGlobaldefaultsignoff().equalsIgnoreCase("Y"))) {
            defaultRatinggroup = ratinggroup.getRatinggroupid().toString();
          }
        }
      }
      ratinggroupObj[0] = allRatinggroups;
      if (SystemUtil.isNotNull(defaultRatinggroup)) {
        ratinggroupObj[1] = defaultRatinggroup;
      } else {
        if (ratinggroupList.get(0) != null) {
          ratinggroupObj[1] = ratinggroupList.get(0).getRatinggroupid()
              .toString();
        }
      }
    }
    LOGGER.exiting(getClass().getName(), "fetchAllRatinggroups");
    return ratinggroupObj;
	}

  /**
   * Gets the list of control statuses for drop down list
   * 
   * @param frompage String
   * @return List
   * @throws MicosException
   *           Micos Exception
   */
  public List<SelectItem> getControlStatuses(String frompage) throws MicosException {
    LOGGER.entering(getClass().getName(), "getControlStatuses");
    List<SelectItem> controlStatuses = new ArrayList<SelectItem>();
    controlStatuses.add(new SelectItem("-1", SELECT_ITEM_MSG_FOR_CTRL_STATUS));
    List<Status> listOfControlStatus = null;
    try {
      listOfControlStatus = controlSetupDao.getAllControlStatuses(frompage);
      if (!SystemUtil.isNull(listOfControlStatus)) {
        for (Status controlStatus : listOfControlStatus) {
          controlStatuses.add(new SelectItem(controlStatus.getStatusid(),
              controlStatus.getStatusdescription()));
        }
      }
    } finally {
      LOGGER.exiting(getClass().getName(), "getControlStatuses");
    }
    return controlStatuses;
  }
  
  /**
   * Get the power option step count
   * @param controlId Control Id
   * @return int poweroption count
   * @throws MicosException micosException
   */
  public int getPoweroptionStepCount(Integer controlId) throws MicosException {
    LOGGER.entering(getClass().getName(), "getPoweroptionStepCount");
    return controlSetupDao.getPoweroptionStepCount(controlId);
  }

  /**
   * Check the schedule date is today for power option steps
   * @param controlId Control Id
   * @return boolean true/false
   * @throws MicosException micosException
   */
  public boolean isPoweroptionSchedileToday(Integer controlId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "isPoweroptionSchedileToday");
    return controlSetupDao.isPoweroptionSchedileToday(controlId);
  }
  
  /**
   * Method to populate Goal title
   * 
   * @param goalId Goal id
   * @return LanguageCodeView object
   * @throws MicosException
   *           micosException
   */
  public LanguageCodeView[] populateGoalTitle(Integer goalId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "populateGoalTitle");
    List langCodes = SystemUtil.getAllLanguageCode();
    LanguageCodeView[] langualgeGoalTitle = new LanguageCodeView[langCodes
        .size()];
    Iterator itr = langCodes.iterator();
    String langCode = "";
    int count = 0;
    while (itr.hasNext()) {
      langCode = (String)itr.next();
      langualgeGoalTitle[count] = new LanguageCodeView();
      langualgeGoalTitle[count].setLanguageType(langCode);
      langualgeGoalTitle[count]
          .setValue(SystemUtil.getClobValueAsString(controlSetupDao
              .findGoalTitle(langCode, goalId)));
      count++;
    }
    LOGGER.exiting(getClass().getName(), "populateGoalTitle");
    return langualgeGoalTitle;
  }
  
  /**
   * Method to inactivate Control
   * 
   * @param controlId Control ID
   * @param loginUserId Logged Employee ID
   * @throws MicosException micosException
   */
  public void inactivateControl(Integer controlId, Integer loginUserId)
      throws MicosException {
    LOGGER.entering(getClass().getName(), "inactivateControl");
    controlSetupDao.inactivateControl(controlId, loginUserId);
    LOGGER.exiting(getClass().getName(), "inactivateControl");
  }  
  
	/**
	 * delete schema
	 * 
	 * @param schemaid
	 *            Integer
	 * @param schematype
	 * 			schematype
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void deleteStepSchema(Integer schemaid, String schematype)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "deleteStepSchema");
	    controlSetupDao.deleteStepSchema(schemaid, schematype, getLoggedinUser());
	    reloadDetachedControl(detachedControl.getControlid());
	    LOGGER.exiting(getClass().getName(), "deleteStepSchema");
	}
	
	/**
	 * delete schema
	 * 
	 * @param schemaid
	 *            Integer
	 * @param schematype
	 * 			schematype
	 * @throws MicosException
	 *             Micos Exception
	 */
	public void deleteDsoSchema(Integer schemaid, String schematype)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "deleteDsoSchema");
	    controlSetupDao.deleteDsoSchema(schemaid, schematype, getLoggedinUser());
	    LOGGER.exiting(getClass().getName(), "deleteDsoSchema");
	}
	
	/**
	 * used to fecth all the controls for control overview page.
	 * @param withInactive true if user also wants inactive controls
	 * @param languageCode languageCode
	 * @return list of all controls
	 * @throws MicosException MicosException
	 */
	public List<ControlView> getControlsforOverview(boolean withInactive, String languageCode) throws MicosException {
		List<ControlView> listOfControls = new ArrayList<ControlView>();
		
		List<Object[]> listOfControlsObj = controlSetupDao.getControlsforOverview(withInactive, getWorkingAsUser(),languageCode);
		if(listOfControlsObj != null) {
			for(Object[] controlobj : listOfControlsObj) {
				ControlView controlView = new ControlView();
				controlView.setMasterControlId((Integer) controlobj[0]);
				controlView.setId((Integer) controlobj[1]);
				controlView.setControlType((String) controlobj[2]);
				controlView.setControlTitle((String) controlobj[3]);
				String statusCode = (String) controlobj[4];
				controlView.setControlStatus((String) controlobj[5]);
				controlView.setControlAORPid((String) controlobj[6]);
				controlView.setControlAorOwnerName((String) controlobj[7]);
        controlView
        .setControlTitleObj(populateControlTitle(controlView.getId()));
				Date controlEndDate = (Date) controlobj[8];
				String editable = (String) controlobj[9];
				
				Date today = new Date();
				
				if (editable.equals("true")) {
					controlView.setControlOfControlOwner(true);
					if(statusCode.equalsIgnoreCase(MicosConstantValues.Status.WAITING_FOR_APPROVAL) ||
							statusCode.equalsIgnoreCase(MicosConstantValues.Status.WAITING_FOR_REAPPROVAL) ||
							statusCode.equalsIgnoreCase(MicosConstantValues.Status.INACTIVE) ) {
						controlView.setShowEdit(false);
					} else if(controlEndDate != null && controlEndDate.before(today)) {
						controlView.setShowEdit(false);
					} else {
						controlView.setShowEdit(true);
					}
					
					if(statusCode.equalsIgnoreCase(MicosConstantValues.Status.SETUP) ||
							statusCode.equalsIgnoreCase(MicosConstantValues.Status.MODIFIED) ) {
						controlView.setShowDelete(true);
					} else {
						controlView.setShowDelete(false);
					}
				} else {
					controlView.setShowEdit(false);
					controlView.setShowDelete(false);
				}
				
				listOfControls.add(controlView);
			}
			
			listOfControls = ControlComparator.sortControls(listOfControls);
		}
		
		return listOfControls;
	}
	/**
	 * Fetch list from reference code table based on reference code group.
	 * 
	 * @param refCodeGroupName
	 *            Ref code group name
	 * @return Object array pupolated by
	 * @throws MicosException exception
	 */
  public List<SelectItem> getReferenceCodes(String refCodeGroupName) throws MicosException {
		List<SelectItem>  referenceCodes = new ArrayList<SelectItem>();
			List<Object[]> listOfRefCode=controlSetupDao.getReferenceCodes(refCodeGroupName);	
			if(SystemUtil.isNotNull(listOfRefCode)) {
				for(Object[] refCode : listOfRefCode) {
					SelectItem selectitem = new SelectItem();
					selectitem.setValue((String)refCode[0]);
					selectitem.setLabel((String)refCode[1]);
					referenceCodes.add(selectitem);
				}
			}
		
		return referenceCodes;
	}
  
  /**
   * Create/Edit notification 
   * @param controlStepView Control setup view
   * @param isForDSO Indicated which Tab
   * @param step Step
   * @param poweroption Poweroption
   * @param statusControl Control Status
   * @return Set<ControlRemWarEsc> Set<ControlRemWarEsc>
   * @throws MicosException MicosException
   */
  private Set<ControlRemWarEsc> createEditNotification(ControlStepView controlStepView, boolean 
		  isForDSO,Poweroption poweroption,Step step, String statusControl)throws MicosException{	 
	  Set<ControlRemWarEsc> setControlRemWarEsc = null;
		if(null!=controlStepView){	
			setControlRemWarEsc = new HashSet<ControlRemWarEsc>();
			ControlRemWarEsc controlRWE=null;
			NotificationView notView=null;
			SchedulingView schedulingView=controlStepView.getSchedulingView();
			if(schedulingView.isCheckWarning()){
				notView=schedulingView.getWarningView();
				controlRWE=modifyNotification(notView, isForDSO, controlStepView, poweroption, step, statusControl);	
				//Referance code of for group name 'ControlNotType'
				Referencecode nottType=controlSetupDao.loadReferenceCode(notView.getNotRefID());	
				controlRWE.setNotType(nottType);
				setControlRemWarEsc.add(controlRWE);				
			}
			if(schedulingView.isCheckReminder()){
				notView=schedulingView.getReminderView();
				controlRWE=modifyNotification(notView, isForDSO, controlStepView, poweroption, step, statusControl);	
				//Referance code of for group name 'ControlNotType'
				Referencecode nottType=controlSetupDao.loadReferenceCode(notView.getNotRefID());	
				controlRWE.setNotType(nottType);
				setControlRemWarEsc.add(controlRWE);				
			}
			if(schedulingView.isCheckEscalation()){
				notView=schedulingView.getEscalationView();
				controlRWE=modifyNotification(notView, isForDSO, controlStepView, poweroption, step, statusControl);	
				//Referance code of for group name 'ControlNotType'
				Referencecode nottType=controlSetupDao.loadReferenceCode(notView.getNotRefID());	
				controlRWE.setNotType(nottType);
				setControlRemWarEsc.add(controlRWE);				
			}
		}
		return setControlRemWarEsc;
	}
  
  /**
   * Modify Notification.
   * @param notView Not View 
   * @param isForDSO Denots the tab
   * @param step Step
   * @param poweroption Poweroption
   * @param controlStepView Control setup view
   * @param statusControl Ststus of Control
   * @throws MicosException MicosException
   * @return controlRWE ControlRemWarEsc
   */
  private ControlRemWarEsc modifyNotification(NotificationView notView,  
  			boolean isForDSO, ControlStepView controlStepView ,Poweroption poweroption,Step step, 
  			String statusControl)throws MicosException{
	  ControlRemWarEsc controlRWE=null;
  		if(null!=notView){
			if(null!=notView.getId() && null!=statusControl && !"Active".equalsIgnoreCase(statusControl)){
				controlRWE=controlSetupDao.loadControlWRE(notView.getId());				
			}else{
				controlRWE=new ControlRemWarEsc();	
				controlRWE.setCreatedate(DateUtil.getCurrentTimestamp());
				controlRWE.setLastupdate(DateUtil.getCurrentTimestamp());
				if(!isForDSO){
					controlRWE.setStepId(step);
					Set<ControlRemWarEsc> setOfControlRemWarEsc=null;
					if(SystemUtil.isNotNull(step.getControlRemWarEscStep())){
						setOfControlRemWarEsc=step.getControlRemWarEscStep();
						setOfControlRemWarEsc.add(controlRWE);
					}else{
						setOfControlRemWarEsc=new HashSet<ControlRemWarEsc>();
						setOfControlRemWarEsc.add(controlRWE);
					}
					step.setControlRemWarEscStep(setOfControlRemWarEsc);
				}else{
					if(null != poweroption.getPoweroptionid()){
						controlRWE.setPowerOption(controlSetupDao.loadPowerOption(poweroption.getPoweroptionid()));
					}else{
						controlRWE.setPowerOption(poweroption);
					}
					Set<ControlRemWarEsc> setOfControlRemWarEsc=null;
					if(SystemUtil.isNotNull(poweroption.getSetOfControlRemEsc())){
						setOfControlRemWarEsc=poweroption.getSetOfControlRemEsc();
						setOfControlRemWarEsc.add(controlRWE);
					}else{
						setOfControlRemWarEsc=new HashSet<ControlRemWarEsc>();
						setOfControlRemWarEsc.add(controlRWE);
					}
					poweroption.setSetOfControlRemEsc(setOfControlRemWarEsc);
				}
			}	
			//Setting employee
			controlRWE.setLastupdatedby(controlSetupDao.loadEmployee(getWorkingAsUser()));
			//Referance code for recipient
			if(notView.isRecipient()){
				if(SystemUtil.isNotNull(notView.getSelectedHierarchy()) && notView.getSelectedHierarchy().equalsIgnoreCase(ReferenceCodeUtil.OEHIERARCHY)){
					Referencecode recipient=controlSetupDao.getReferenceCodeByCode(IssuesConstants.ReferenceCodeUtil.OEHIERARCHY_CODE, 
							IssuesConstants.ReferenceCodeUtil.RECIPIENT);
					controlRWE.setRecipient(recipient);
				}else{

					if(notView.getSelectedHierarchy().equalsIgnoreCase(IssuesConstants.ReferenceCodeUtil.TASK_OWNER)){
											notView.setSelectedHierarchy("1"); // ref code for short Description of "Task-Owner"
										}
					Referencecode recipient=controlSetupDao.getReferenceCodeByCode(notView.getSelectedHierarchy(), 
							IssuesConstants.ReferenceCodeUtil.RECIPIENT);
					controlRWE.setRecipient(recipient);
				}
			}else{
				controlRWE.setRecipient(null);
			}
			//Setting start date
			if(SystemUtil.isNotNull(notView.getQuantity())){
				controlRWE.setQuantity(Integer.parseInt(notView.getQuantity()));
			}
			//Setting start quentity type
			if(SystemUtil.isNotNull(notView.getSelectedEstimation())){
				Periodtype periodType=controlSetupDao.loadPeriodType(Integer.parseInt(notView.getSelectedEstimation()));
				controlRWE.setPeriodType(periodType);
			}
			//Referance code for 'Frequency'
			if(SystemUtil.isNotNull(notView.getSelectedFrequency())){
				Referencecode frequency=controlSetupDao.getReferenceCodeByCode(notView.getSelectedFrequency(), 
						ReferenceCodeUtil.FREQUENCY);
				controlRWE.setFrequency(frequency);
			}
			//Setting employee as othere ricipient
			if(notView.isCheckOthers()){
				if(SystemUtil.isNotNull(notView.getListOthers())){
					List<EmployeeView> assoEmp=notView.getListOthers();
					Set<RemWarEscOtherRecipient> others = new HashSet<RemWarEscOtherRecipient>();
					/*if(SystemUtil.isNull(controlRWE.getOtherRecipients())){
						others=new HashSet<RemWarEscOtherRecipient>();
					}else{
						others = controlRWE.getOtherRecipients();
					}*/
					for(EmployeeView empV: assoEmp){
						if(empV.getId() == null){
							RemWarEscOtherRecipient other=new RemWarEscOtherRecipient();
							other.setControlRemWarEsc(controlRWE);
							Employee emp=controlSetupDao.loadEmployee(Integer.parseInt(empV.getEmpId()));
							other.setEmployee(emp);
							other.setCreatedate(DateUtil.getCurrentTimestamp());
							other.setLastupdatedby(controlSetupDao.loadEmployee(getWorkingAsUser()));
							other.setLastupdate(DateUtil.getCurrentTimestamp());
							others.add(other);
						}else{
							if("Active".equalsIgnoreCase(statusControl)){
								RemWarEscOtherRecipient other=new RemWarEscOtherRecipient();
								other.setControlRemWarEsc(controlRWE);
								Employee emp=controlSetupDao.loadEmployee(Integer.parseInt(empV.getEmpId()));
								other.setEmployee(emp);
								other.setCreatedate(DateUtil.getCurrentTimestamp());
								other.setLastupdatedby(controlSetupDao.loadEmployee(getWorkingAsUser()));
								other.setLastupdate(DateUtil.getCurrentTimestamp());
								others.add(other);
							}else if(null == statusControl){
								RemWarEscOtherRecipient other=new RemWarEscOtherRecipient();
								other.setControlRemWarEsc(controlRWE);
								Employee emp=controlSetupDao.loadEmployee(Integer.parseInt(empV.getEmpId()));
								other.setEmployee(emp);
								other.setCreatedate(DateUtil.getCurrentTimestamp());
								other.setLastupdatedby(controlSetupDao.loadEmployee(getWorkingAsUser()));
								other.setLastupdate(DateUtil.getCurrentTimestamp());
								others.add(other);
							}
						}
					}	
					if(SystemUtil.isNotNull(others)){
						controlRWE.setOtherRecipients(others);
					}
				}
			}else{
				List<Integer> listDelete = new ArrayList<Integer>();
				if(SystemUtil.isNotNull(notView.getListOthers())){
					List<EmployeeView> assoEmp=notView.getListOthers();
					for(EmployeeView empV: assoEmp){
						if(empV.getId() != null){
							listDelete.add(empV.getId());
						}
					}
					if(SystemUtil.isNotNull(listDelete)){
						controlSetupDao.deleteListOfOtherReceipient(listDelete);
					}
				}
			}
		}
  		return controlRWE;
  	}
  
  /**
	 * Delete control rem esc
	 * @param remwarescotherrecId PK
	 * @throws MicosException MicosException
	 */
	public void deleteOtherRec(Integer remwarescotherrecId)throws MicosException{
		controlSetupDao.deleteOtherReceipient(remwarescotherrecId);
	}
	
	/**
	 * Delete Reminder, Warning or Escelation for a Step or Poweroption
	 * @param controlremId PK
	 * @throws MicosException MicosException
	 */
	public void deleteNotificationType(List<Integer> controlremId)throws MicosException{
		controlSetupDao.deleteControlRemWarEsc(controlremId);
	}
  /**
	 * fetch corresponding reference code
	 * 
	 * @param refcode
	 *            String
	 * @param refCodegroup
	 *            String
	 * @return String
	 * @throws MicosException exception
	 */
	public BaseView getReferenceCodeByCode(String refcode, String refCodegroup)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "getReferenceCodeByCode");
		Referencecode refCode = null;
		BaseView baseView = new BaseView();
		refCode = controlSetupDao.getReferenceCodeByCode(refcode, refCodegroup);
		if (null != refCode && null !=refCode.getReferencecodeid()&& null != refCode.getShortdescription()) {
			baseView.setId(refCode.getReferencecodeid());
			baseView.setDescription(refCode.getShortdescription());
		}
		return baseView;

	}
}

