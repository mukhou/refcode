JPA flow:-

In persistence.xml we need to mention --  persistence-unit name="MICOSJPA", this for using JPA
				      --  Also mention the fully qualified class name of the entitybean as below (e.g for issueType.java)
					<class>com.csg.cs.micos.entitybean.Issuetype</class>

The entitybean (e.g IssueType.java)  -- This basically the ORM with the database table.


Now create your own dao(IssueTypeDaoImpl.java)  ----   Here  getEntityManager() method will return the instance of entity maneger, which is being used for 
						       JPA.  Work on this instance for any database operation.



NOTE: PLEASE CORRECT ME IF I AM WRONG. HOPE THIS WILL HELP YOU. :)