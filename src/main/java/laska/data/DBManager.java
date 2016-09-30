package laska.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Менеджер бази даних. Керує доступом до БД, 
 * і у випадку її відсутності створює нову.
 * @author laska
 */
public class DBManager extends DBLabels{
	
	private static DBManager DB = null;
	
	public static DBManager getDBManager(){
		if (DB == null)
			try {
				DB = new DBManager();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return DB;
	}
	
	private  Connection _con = null;
	
	private DBManager() throws ClassNotFoundException, SQLException{
		_con = connect();
	}

	/**
	 * Встановлює з’єднання з файлом БД
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private Connection connect() throws ClassNotFoundException, SQLException{
		Class.forName("org.sqlite.JDBC");
		Connection con = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME);
		File f = new File(DB_NAME);
		//БД пуста. Створюємо потрібні таблиці
		if (f.length() == 0) createTables(con);
		return con;
	}
	
	/**
	 * Закриває з’єднання з БД
	 * @throws SQLException 
	 */
	private void close() throws SQLException{
		_con.close();
	}
	
	/**
	 * Створює всі необхідні таблиці
	 * @param con
	 * @throws SQLException
	 */
	private void createTables(Connection con) throws SQLException{		
		Statement statmt = con.createStatement();
		statmt.execute(getSQL_сreateNewIssuesTable());
		statmt.execute(getSQL_createIssueTable());
		statmt.execute(getSQL_createComTable());
		statmt.execute(getSQL_createWorkLogTable());
		statmt.close();	
	}
	
	public void deleteDB() throws SQLException{
		DB.close();
		DB = null;
		File f = new File(DB_NAME);
		f.delete();
	}

	private void closeStatement(Statement st){
		try {
			st.close();
		} catch (Exception e) {
			//уже закритий
		}
	}
	
	private void closeStatement(PreparedStatement pst){
		try {
			pst.close();
		} catch (Exception e) {
			//уже закритий
		}
	}
	
	/*==================================================================
	 * Методи, що повертають SQL запит для створення конкретної таблиці
	 *==================================================================*/

	private String getSQL_сreateNewIssuesTable(){
		String newIssTableSql = String.format("CREATE TABLE %s ("	//NEW_IS_TABLE
				+ "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " //ID
				+ "%s TEXT, "		//KEY	
				+ "%s TEXT, "		//SUM
				+ "%s TEXT, "		//TYPE
				+ "%s TEXT, "		//PRIOR
				+ "%s TEXT, "		//DESC
				+ "%s INTEGER, "	//DATE
				+ "%s INTEGER );",  //TIME
				NEW_IS_TABLE, ID, KEY, SUM, TYPE, PRIOR, DESC, DATE, TIME);
		return newIssTableSql;
	}
	
	private String getSQL_createIssueTable(){
		String issTableSql = String.format("CREATE TABLE %s ("		//IS_TABLE
				+ "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " //ID
				+ "%s TEXT, "		//KEY	
				+ "%s TEXT, "		//SUM
				+ "%s TEXT, "		//TYPE
				+ "%s TEXT, "		//STAT
				+ "%s INTEGER, "	//IFST
				+ "%s INTEGER, "	//IFCOM
				+ "%s INTEGER, "	//IFWL
				+ "%s INTEGER, "	//DATE
				+ "%s INTEGER );",  //TIME
				IS_TABLE, ID, KEY, SUM, TYPE, STAT, IFST, IFCOM, IFWL, DATE, TIME);
		return issTableSql;
	}
	
	private String getSQL_createWorkLogTable(){
		String worklogTablSql = String.format("CREATE TABLE %s ("		//WL_TABLE
				+ "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " //ID
				+ "%s TEXT, "		//IDJ	
				+ "%s TEXT, "		//KEY
				+ "%s TEXT, "		//AUTOR
				+ "%s TEXT, "		//DESC
				+ "%s INTEGER, "	//DATE
				+ "%s INTEGER );",  //TIME
				WL_TABLE, ID, IDJ, KEY, AUTOR, DESC, DATE, TIME);
		return worklogTablSql;
	}
	
	private String getSQL_createComTable(){
		//Таблиця містить інформацію, про коментарі до завдань
		String commTablSql = String.format("CREATE TABLE %s ("		//COM_TABLE
				+ "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " //ID
				+ "%s TEXT, "		//IDJ	
				+ "%s TEXT, "		//KEY
				+ "%s TEXT, "		//AUTOR
				+ "%s TEXT, "		//DESC
				+ "%s INTEGER, "	//DATE
				+ "%s INTEGER );",  //TIME
				COM_TABLE, ID, IDJ, KEY, AUTOR, DESC, DATE, TIME);
		return commTablSql;
	}

	/*==================================================================
	 * Методи, для додавання нових записів в БД
	 *==================================================================*/

	/**
	 * Записує нові завдання в БД
	 * @param iss
	 * @return
	 * @throws SQLException 
	 */
	public void addNewIssues(List<ModelNewIssue> iss) throws SQLException{
		String psql = getSQL_addNewIssue();
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql);
			for(ModelNewIssue is:iss){
				ps.setString(1, is.key);
				ps.setString(2, is.summary);
				ps.setString(3, is.type);
				ps.setString(4, is.priority);
				ps.setString(5, is.description);
				ps.setInt(6, is.date);
				ps.setInt(7, is.time);
				ps.executeUpdate();
			}	
		} catch (SQLException e){
			throw e;
		}finally{
			closeStatement(ps);
		}
	}
	
	/**
	 * Додає одне завдання в БД. Налаштування, яким саме чином, 
	 * відслідковується завдання вносяться окремо.
	 * @param is
	 * @throws SQLException 
	 * @throws DublicateException - завдання вже знаходиться в БД
	 */
	public boolean addIssue(ModelIssue is) throws SQLException{
		//перевіряємо, чи таке завдання існує
		if (getIssue(is.getKey())!=null) return false;
		
		String psql = getSQL_addIssue();
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql);
			ps.setString(1, is.key);
			ps.setString(2, is.summary);
			ps.setString(3, is.type);
			ps.setString(4, is.status);
			ps.setInt(5, is.date);
			ps.setInt(6, is.time);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally{
			closeStatement(ps);
		}
		return true;
	}
	
	/**
	 * Зберігає нові коментарі
	 * @param com
	 * @throws SQLException 
	 */
	public void addComment(ModelComment com) throws SQLException{
		String psql = getSQL_addComment();
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql);
			ps.setString(1, com.idj);
			ps.setString(2, com.key);
			ps.setString(3, com.author);
			ps.setString(4, com.body);
			ps.setInt(5, com.date);
			ps.setInt(6, com.time);
			ps.executeUpdate();	
		} catch (SQLException e) {
			throw e;
		} finally{
			closeStatement(ps);
		}
	}

	public void addWorkLog(ModelWorkLog com) throws SQLException{
		String psql = getSQL_addWorkLog();
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql);
			ps.setString(1, com.idj);
			ps.setString(2, com.key);
			ps.setString(3, com.author);
			ps.setString(4, com.body);
			ps.setInt(5, com.date);
			ps.setInt(6, com.time);
			ps.executeUpdate();	
		} catch (SQLException e) {
			throw e;
		} finally{
			closeStatement(ps);
		}
	}
	
	private String getSQL_addNewIssue(){
		return String.format(
				"INSERT INTO %s ("	//NEW_IS_TABLE
				+ "%s, "			//KEY
				+ "%s, "			//SUM
				+ "%s, "			//TYPE
				+ "%s, "			//PRIOR
				+ "%s, "			//DESC
				+ "%s, "			//DATE
				+ "%s ) "			//TIME
				+ "VALUES (?, ?, ?, ?, ?, ?, ?);", 
				NEW_IS_TABLE, KEY, SUM, TYPE, PRIOR, DESC, DATE, TIME);
	}
	
	private String getSQL_addIssue(){
		return String.format(
				"INSERT INTO %s ("	//IS_TABLE
				+ "%s, "			//KEY
				+ "%s, "			//SUM
				+ "%s, "			//TYPE
				+ "%s, "			//STAT
				+ "%s, "			//DATE
				+ "%s ) "			//TIME
				+ "VALUES (?, ?, ?, ?, ?, ?);", 
				IS_TABLE, KEY, SUM, TYPE, STAT, DATE, TIME);
		
	}
	
	private String getSQL_addComment(){
		return String.format("INSERT INTO %s ("	//COM_TABLE
				+ "%s, "	//IDJ
				+ "%s, "	//KEY
				+ "%s, "	//AUTOR
				+ "%s, "	//DESC
				+ "%s, "	//DATE
				+ "%s ) "	//TIME
				+ "VALUES (?, ?, ?, ?, ?, ?)", 
				COM_TABLE, IDJ, KEY, AUTOR, DESC, DATE, TIME);
	}
	
	private String getSQL_addWorkLog(){
		return String.format("INSERT INTO %s ("	//COM_TABLE
				+ "%s, "	//IDJ
				+ "%s, "	//KEY
				+ "%s, "	//AUTOR
				+ "%s, "	//DESC
				+ "%s, "	//DATE
				+ "%s ) "	//TIME
				+ "VALUES (?, ?, ?, ?, ?, ?)", 
				WL_TABLE, IDJ, KEY, AUTOR, DESC, DATE, TIME);
	}
	
	/*==================================================================
	 * Методи, для отримання інформації з БД
	 *==================================================================*/

	/**
	 * Повертає список всіх нових задач, що зберігаються в БД
	 * @throws SQLException 
	 */
	public List<ModelNewIssue> getAllNewIssues() throws SQLException{
		ArrayList<ModelNewIssue> nis = new ArrayList<ModelNewIssue>();
		String sql = String.format("SELECT * FROM %s", NEW_IS_TABLE);
		Statement s = null;
		try {
			s = _con.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while(rs.next()){
				nis.add(readNewIssue(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally{
			closeStatement(s);
		}
		return nis;
	}

	/**
	 * Шукає в БД завдання з ключем key.
	 * @param key
	 * @throws SQLException
	 */
	public ModelIssue getIssue(String key) throws SQLException{
		//перевіремо, чи такого завдання нема в списку
		String psql = String.format("SELECT * FROM %s WHERE %s = ?", IS_TABLE, KEY);
		PreparedStatement ps = null;
		ModelIssue is = null;
		try{
			ps = _con.prepareStatement(psql);
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				is = readIssue(rs);
			}
		}catch(SQLException e){
			throw e;
		} finally{
			closeStatement(ps);
		}
		return is;
	}
	
	/**
	 * Повертає список всіх задач за якими слідкує користувач
	 * @return
	 * @throws SQLException 
	 */
	public List<ModelIssue> getAllIssues() throws SQLException{
		ArrayList<ModelIssue> iss = new ArrayList<ModelIssue>();
		String sql = String.format("SELECT * FROM %s;", IS_TABLE);
		Statement s = null;
		try {
			s = _con.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while(rs.next()){
				iss.add(readIssue(rs));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			closeStatement(s);
		}
		return iss;
	}

	public ModelComment getComment(String key) throws SQLException{
		ModelComment com = null;
		String psql = String.format("SELECT * FROM %s WHERE %s = ?;", 
				COM_TABLE, KEY);
		PreparedStatement s = null;
		try {
			s = _con.prepareStatement(psql);
			s.setString(1, key);
			ResultSet rs = s.executeQuery();
			if(rs.next()){
				com = readComment(rs);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				s.close();
			} catch (Exception e) {
				//уже закритий
			}
		}
		return com;
	}
	
	public ModelWorkLog getWorkLog(String key) throws SQLException{
		ModelWorkLog cmms = null;
		String psql = String.format("SELECT * FROM %s WHERE %s = ?;", 
				WL_TABLE, KEY);
		PreparedStatement s = null;
		try {
			s = _con.prepareStatement(psql);
			s.setString(1, key);
			ResultSet rs = s.executeQuery();
			if(rs.next()){
				cmms = readWorkLog(rs);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				s.close();
			} catch (Exception e) {
				//уже закритий
			}
		}
		return cmms;
	}
	
	/*==================================================================
	 * Методи, для оновлення записів в БД
	 *==================================================================*/
	
	/**
	 * Оновлює інформацію про завдання (стан і дата оновлення)
	 * @param is
	 * @throws SQLException 
	 */
	public void updataIssue(ModelIssue is) throws SQLException{
		String psql = getSQL_updateIssue();
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql);
			ps.setString(1, is.status);
			ps.setInt(2, is.date);
			ps.setInt(3, is.time);
			ps.setString(4, is.key);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally{
			closeStatement(ps);
		}
	}

	/**
	 * Оновлює налаштування завдання
	 * @param is
	 * @throws SQLException
	 */
	public void updataIssueConf(ModelIssue is) throws SQLException{
		String psql = getSQL_updateIssueConf();
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql);
			ps.setInt(1, is.ifStatus);
			ps.setInt(2, is.ifComm);
			ps.setInt(3, is.ifWL);
			ps.setString(4, is.key);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally{
			closeStatement(ps);
		}
	}

	public void updataComment(ModelComment com) throws SQLException{
		String psql = getSQL_updataComment();
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql);
			ps.setString(1, com.idj);
			ps.setString(2, com.author);
			ps.setString(3, com.body);
			ps.setInt(4, com.date);
			ps.setInt(5, com.time);
			ps.setString(6, com.key);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally{
			closeStatement(ps);
		}
	}
	
	public void updataWorkLog(ModelWorkLog com) throws SQLException{
		String psql = getSQL_updataWorkLog();
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql);
			ps.setString(1, com.idj);
			ps.setString(2, com.author);
			ps.setString(3, com.body);
			ps.setInt(4, com.date);
			ps.setInt(5, com.time);
			ps.setString(6, com.key);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally{
			closeStatement(ps);
		}
	}
	
	private String getSQL_updateIssue(){
		return String.format("UPDATE %s SET " //IS_TABLE
				+ "%s = ?, "		//STAT
				+ "%s = ?, "		//DATE
				+ "%s = ?  "		//TIME
				+ "WHERE %s = ?;", 	//KEY
				IS_TABLE, STAT, DATE, TIME, KEY);
	}
	
	private String getSQL_updateIssueConf(){
		return String.format("UPDATE %s SET " //IS_TABLE
				+ "%s = ?, "		//IFST
				+ "%s = ?, "		//IFCOM
				+ "%s = ?  "		//IFWL
				+ "WHERE %s = ?;", 	//KEY
				IS_TABLE, IFST, IFCOM, IFWL, KEY);
	}
	
	private String getSQL_updataComment(){
		return String.format("UPDATE %s SET " //COM_TABLE
				+ "%s = ?, "		//IDJ
				+ "%s = ?, "		//AUTOR
				+ "%s = ?,  "		//DESC
				+ "%s = ?,  "		//DATE
				+ "%s = ?  "		//TIME
				+ "WHERE %s = ?;", 	//KEY
				COM_TABLE, IDJ, AUTOR, DESC, DATE, TIME, KEY);
	}
	
	private String getSQL_updataWorkLog(){
		return String.format("UPDATE %s SET " //WL_TABLE
				+ "%s = ?, "		//IDJ
				+ "%s = ?, "		//AUTOR
				+ "%s = ?,  "		//DESC
				+ "%s = ?,  "		//DATE
				+ "%s = ?  "		//TIME
				+ "WHERE %s = ?;", 	//KEY
				WL_TABLE, IDJ, AUTOR, DESC, DATE, TIME, KEY);
	}
	
	/*====================================================================
	 * Методи, для видалення записів із БД
	 *====================================================================*/

	/**
	 * Чистить таблицю із новими завданнями
	 * @throws SQLException 
	 */
	public void removeAllNewIssues() throws SQLException{
		String sql = String.format("DELETE FROM %s", NEW_IS_TABLE);
		Statement s = null;
		try {
			s = _con.createStatement();
			s.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			closeStatement(s);
		}
	}
	
	
	/**
	 * Видаляє записи про задачу із всіх таблиць
	 * @param is
	 * @throws SQLException 
	 */
	public void removeIssue(String key) throws SQLException{
		//Видаляємо завдання з основної таблиці
		String psql1 = String.format("DELETE FROM %s WHERE %s = ?;", 
				IS_TABLE, KEY);
		PreparedStatement ps = null;
		try {
			ps = _con.prepareStatement(psql1);
			ps.setString(1, key);
			ps.executeUpdate();
			removeComment(key);
			removeWorkLog(key);
		} catch (SQLException e) {
			throw e;
		} finally{
			closeStatement(ps);
		}
	}
	
	public void removeComment(String key) throws SQLException{
		String psql = String.format("DELETE FROM %s WHERE %s = ?;", 
				COM_TABLE, KEY);
		PreparedStatement s = null;
		try {
			s = _con.prepareStatement(psql);
			s.setString(1, key);
			s.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			closeStatement(s);
		}
	}
	
	public void removeWorkLog(String key) throws SQLException{
		String psql = String.format("DELETE FROM %s WHERE %s = ?;", 
				WL_TABLE, KEY);
		PreparedStatement s = null;
		try {
			s = _con.prepareStatement(psql);
			s.setString(1, key);
			s.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
			closeStatement(s);
		}
	}

	/*==================================================================
	 * Методи, розшифровки отриманих даних з БД
	 *==================================================================*/
	
	private ModelNewIssue readNewIssue(ResultSet rs) throws SQLException{
		ModelNewIssue ni = new ModelNewIssue();
		ni.key = rs.getString(KEY);
		ni.summary = rs.getString(SUM);
		ni.type = rs.getString(TYPE);
		ni.priority = rs.getString(PRIOR);
		ni.description = rs.getString(DESC);
		ni.date = rs.getInt(DATE);
		ni.time = rs.getInt(TIME);
		return ni;
	}

	private ModelIssue readIssue(ResultSet rs) throws SQLException{
		ModelIssue is = new ModelIssue();
		is.key = rs.getString(KEY);
		is.type = rs.getString(TYPE);
		is.summary = rs.getString(SUM);
		is.status = rs.getString(STAT);
		is.ifStatus = rs.getInt(IFST);
		is.ifComm = rs.getInt(IFCOM);
		is.ifWL = rs.getInt(IFWL);
		is.date = rs.getInt(DATE);
		is.time = rs.getInt(TIME);
		return is;
	}
	
	private ModelComment readComment(ResultSet rs) throws SQLException{
		ModelComment m = new ModelComment();
		m.idj = rs.getString(IDJ);
		m.author = rs.getString(AUTOR);
		m.key = rs.getString(KEY);
		m.body = rs.getString(DESC);
		m.date = rs.getInt(DATE);
		m.time = rs.getInt(TIME);
		return m;
	}

	private ModelWorkLog readWorkLog(ResultSet rs) throws SQLException{
		ModelWorkLog m = new ModelWorkLog();
		m.idj = rs.getString(IDJ);
		m.author = rs.getString(AUTOR);
		m.key = rs.getString(KEY);
		m.body = rs.getString(DESC);
		m.date = rs.getInt(DATE);
		m.time = rs.getInt(TIME);
		return m;
	}

}
