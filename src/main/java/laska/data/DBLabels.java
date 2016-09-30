package laska.data;

/**
 * Містить назви полів та таблиць в БД
 * @author laska
 */
public class DBLabels {
	//назва БД
	protected final String DB_NAME = "db.s3db";
	
	//назви таблиць
	protected final String NEW_IS_TABLE = "'main'.'new_issues'";
	protected final String IS_TABLE = "'main'.'issues'";
	protected final String COM_TABLE = "'main'.'comments'";
	protected final String WL_TABLE = "'main'.'worklogs'";
	
	//Назви стовпців в таблицях
	protected final String ID = "id";
	protected final String KEY = "key";
	protected final String SUM = "summary";
	protected final String TYPE = "type";
	protected final String REP = "reporter";
	protected final String PRIOR = "priority";
	protected final String DESC = "description";
	protected final String DATE = "date";
	protected final String TIME = "time";
	protected final String STAT = "status";
	protected final String AUTOR = "author";
	protected final String IDJ = "idj";
	protected final String IFST = "ifst";
	protected final String IFCOM = "ifcom";
	protected final String IFWL = "ifwl";
}
