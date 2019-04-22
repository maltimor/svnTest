package svntest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.ISVNPropertyHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Test implements ISVNLogEntryHandler,ISVNDirEntryHandler,ISVNPropertyHandler {
	private DataSource datasource;
	private SqlSession session;
	private SvnTestMapper mapper;
	private SVNLogClient log;
	private SVNWCClient wc;
	private Map<String,String> proys;
	private String actualProy;
	private boolean test;
	private long maxRev;
	private Map<String,Long> extFiles;
	private Map<String,Long> extSizes;
	private long totalDirs;
	private long totalFiles;
	private long totalSize;
	
	
	public boolean isTest() {
		return test;
	}
	public void setTest(boolean test) {
		this.test = test;
	}
	public void setActualProy(String proy) {
		this.actualProy = proy;
		this.extFiles=new HashMap<String,Long>();
		this.extSizes=new HashMap<String,Long>();
		this.totalDirs=0;
		this.totalFiles=0;
		this.totalSize=0;
	}
	public String getActualProy() {
		return this.actualProy;
	}
	
	public void addProy(String name,String url,String path) {
		if (proys==null) proys = new HashMap<String,String>();
		proys.put(name, url+"|"+path);
		
		if (mapper!=null) {
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("PROY", name);
			data.put("URL", url);
			data.put("PATH", path);
			mapper.insertProy(data);
		}
	}
	
	/*
	 * Si existe un proyecto en la tabla de proyectos identificado por la url no hace nada, si no lo inserta
	 */
	public void testProy(String name,String url,String path) {
		if (mapper!=null) {
			List<Map<String,Object>> lst = mapper.getProysByUrl(url);
			if (lst!=null && lst.size()==0) addProy(name,url,path);
		}
	}
	
	
	public void loadProys() {
		if (proys==null) proys = new HashMap<String,String>();
		if (mapper!=null) {
			List<Map<String,Object>> lst = mapper.getProys();
			for(Map<String,Object> p:lst) {
				proys.put(""+p.get("PROY"), p.get("URL")+"|"+p.get("PATH"));
			}
		}
	}

	
	public String getHttpsResponse(String method, String url,String data,List<String> cookiesIn,List<String> cookiesOut) throws IOException {
		URL Url = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection)Url.openConnection();
		conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(10000);
		conn.setDoInput(true);
		conn.setRequestMethod(method);
		conn.addRequestProperty("Accept", "application/json");
		if (cookiesIn!=null && cookiesIn.size()>0) {
			for(String cook:cookiesIn) {
				conn.addRequestProperty("Cookie", cook);
				//System.out.println("+++"+cook);;
			}
		}
		
		if (data!=null) {
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(data);
			writer.flush();
			writer.close();
			os.close();
		}

		conn.connect();
		
		Map<String,List<String>> map = conn.getHeaderFields();
		for(String key:map.keySet()) {
			//System.out.println(key+"="+map.get(key));
			if (key!=null && key.toLowerCase().equals("set-cookie")) {
				//if (cookiesOut!=null) cookiesOut.addAll(map.get(key));
				for(String cook:map.get(key)) {
					//System.out.println("--"+cook);
					if (cookiesOut!=null && cook.startsWith("JSESSIONID")) cookiesOut.add(cook);
				}
			}
		}
		if (conn.getResponseCode()==200) {
			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(),Charset.forName("UTF-8")));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) sb.append(line);
			
			//System.out.println(sb.toString());
			return sb.toString();
		} else {
			System.out.println("**** "+conn.getResponseCode()+" "+conn.getResponseMessage());
			return null;
		}
	}
	
	public void getAllRepoList(String urlBase,String user,String password) throws IOException {
		List<String> cookies = new ArrayList<String>();
		String response_1 = getHttpsResponse("POST", urlBase+"/api/rest/authentication/login", "username="+user+"&password="+password+"&rememberMe=true", null, cookies);
		String response_2 = getHttpsResponse("GET", urlBase+"/api/rest/repositories", null, cookies,null);
		
		if (response_2!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			ObjectMapper omapper = new ObjectMapper();
			List<Map<String,Object>> lst=new ArrayList<Map<String,Object>>();
			lst = omapper.readValue(response_2, lst.getClass());
			for(Map<String,Object> map:lst) {
				//System.out.println(map);
				Map<String,Object> data = new HashMap<String,Object>();
				for(String key:map.keySet()) {
					if (key.equals("creationDate")||key.equalsIgnoreCase("lastModified")) {
						Object o = map.get(key);
						if (o!=null) data.put(key.toUpperCase(), new java.sql.Date(Long.parseLong(""+o)));
						else data.put(key.toUpperCase(), null);
					} else data.put(key.toUpperCase(), map.get(key)==null?"":map.get(key).toString());
				}
				
				if (mapper!=null) {
					//veo si hacer un insert o un update
					List<Map<String,Object>> r = mapper.getRepoByUrl(""+data.get("URL"));
					if (r==null || r.size()==0) mapper.insertRepo(data);
					else mapper.updateRepo(data);
					testProy(""+data.get("NAME"),""+data.get("URL"),"/");
				}
			}
		}
		
	}
	
	public void initSVN(String user,String pass) {
		BasicAuthenticationManager auth = new BasicAuthenticationManager(user,pass);
		ISVNOptions myOptions = SVNWCUtil.createDefaultOptions(true) ;
		SVNClientManager clientManager = SVNClientManager.newInstance(myOptions, auth);
		this.log = clientManager.getLogClient();
		this.wc=clientManager.getWCClient();
	}
	
	public void initDB(String url,String user,String password) throws SQLException {
		BasicDataSource db = new BasicDataSource();
		db.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		db.setUrl(url);
		db.setUsername(user);
		db.setPassword(password);
		this.datasource = db;
		
/*		Configuration configuration = new Configuration();
		configuration.setJdbcTypeForNull(JdbcType.NULL);
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(db);
		sqlSessionFactory.setConfiguration(configuration);*/
		
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, db);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(SvnTestMapper.class);
		configuration.setJdbcTypeForNull(JdbcType.NULL);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		session = sqlSessionFactory.openSession(true);
		mapper = session.getMapper(SvnTestMapper.class);
	}
	
	private void endDB() {
		session.close();
	}
	
	public void doAllList() throws Exception {
		int i=0;
		int total=proys.size();
		for(String name:proys.keySet()) {
			System.out.println(i+++"/"+total+" ---------------------"+name);
			//obtengo la version mas alta en la bbdd y actualizo desde ahi
			int start=0;
			int end = 0;
			
			if (mapper!=null) {
				Map<String,Object> bounds = mapper.getCommitsBoundsStats(name);
				//System.out.println(bounds);
				if (bounds!=null) start = Integer.parseInt(""+bounds.get("LAST"))+1; 
			}
			doList(name,start,end);
			//if (i>20) return;
			//TODO
		}
	}

	public void doList(String proy,int start,int end) throws Exception {
		//System.out.println(proy+" "+start+" "+end);
		String prj=proys.get(proy);
		if (prj==null) throw new Exception("No existe el proyecto "+proy);
		int i = prj.indexOf("|");
		String path = prj.substring(i+1);
		String url = prj.substring(0, i);
		
		String[] paths = {path};
		SVNRevision send = SVNRevision.HEAD;
		if (end>0) send = SVNRevision.create(end);
		this.setActualProy(proy);
		
		//este mecanismo obtiene el mayor n rev y si este es menor que start sale fuera
		maxRev=-1;
		this.setTest(true);
		log.doLog(SVNURL.parseURIDecoded(url),paths,null,SVNRevision.HEAD,SVNRevision.HEAD,true,true,0,this);
		
		if (maxRev<start) return;
		
		this.setTest(false);
		log.doList(SVNURL.parseURIDecoded(url), null, send, true, true, this);
		//wc.doGetProperty(SVNURL.parseURIDecoded(url), null, null, send, SVNDepth.INFINITY, this);
		System.out.println("-----------------");
		System.out.println("Numero de directorios="+totalDirs);
		System.out.println("Numero de archivos="+totalFiles);
		System.out.println("Tamaño Total="+totalSize);
		System.out.println("Ficheros="+extFiles);
		System.out.println("Tamaños="+extSizes);
		System.out.println("-----------------");
		
		if (mapper!=null) {
			Date f1 = new Date();
			java.sql.Date f = new java.sql.Date(f1.getTime());
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("PROY", this.getActualProy());
			data.put("TOTAL_DIRS", totalDirs);
			data.put("TOTAL_FILES", totalFiles);
			data.put("TOTAL_SIZE", totalSize);
			data.put("TIMESTAMP", f);
			mapper.updateProyStats(data);
			data = new HashMap<String,Object>();
			data.put("PROY", this.getActualProy());
			data.put("REV", maxRev);
			mapper.prepareStats(data);
			for(String key:extFiles.keySet()) {
				data = new HashMap<String,Object>();
				data.put("PROY", this.getActualProy());
				data.put("REV", maxRev);
				data.put("EXT", key);
				data.put("FILES", extFiles.get(key));
				data.put("SIZES", extSizes.get(key));
				mapper.insertStats(data);
			}
		}
	}
	
	public void doAllLog() throws Exception {
		int i=0;
		int total=proys.size();
		for(String name:proys.keySet()) {
			System.out.println(i+++"/"+total+" ---------------------"+name);
			//obtengo la version mas alta en la bbdd y actualizo desde ahi
			int start=0;
			int end = 0;
			
			if (mapper!=null) {
				Map<String,Object> bounds = mapper.getCommitsBounds(name);
				//System.out.println(bounds);
				if (bounds!=null) start = Integer.parseInt(""+bounds.get("LAST"))+1; 
			}
			doLog(name,start,end);
		}
	}
	
	public void doLog(String proy,int start,int end) throws Exception {
		//System.out.println(proy+" "+start+" "+end);
		String prj=proys.get(proy);
		if (prj==null) throw new Exception("No existe el proyecto "+proy);
		int i = prj.indexOf("|");
		String path = prj.substring(i+1);
		String url = prj.substring(0, i);
		
		String[] paths = {path};
		SVNRevision send = SVNRevision.HEAD;
		if (end>0) send = SVNRevision.create(end);
		this.setActualProy(proy);
		
		//este mecanismo obtiene el mayor n rev y si este es menor que start sale fuera
		maxRev=-1;
		this.setTest(true);
		log.doLog(SVNURL.parseURIDecoded(url),paths,null,SVNRevision.HEAD,SVNRevision.HEAD,true,true,0,this);
		
		if (maxRev<start) return;
		
		this.setTest(false);
		log.doLog(SVNURL.parseURIDecoded(url),paths,null,SVNRevision.create(start),send,true,true,0,this);
	}

	public void handleLogEntry(SVNLogEntry entry) throws SVNException {
		//optimizacion si solo estoy haciedno test no hago nada mas y salgo
		if (test) {
			//no inserto esto es simplemente para saber cual es el numero de revision mas alto y poder evitar error de PROPFIND
			maxRev = entry.getRevision();
			return;
		}
		
		//System.out.println("--------");
		//System.out.println(entry);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String res = this.getActualProy();
		res+=" Rev: "+entry.getRevision();
		res+=" Autor:"+entry.getAuthor();
		res+=" Fecha:"+sdf.format(entry.getDate());
		
		int A=0;
		int M=0;
		int D=0;
		int R=0;
		int N=0;
		int numFiles=0;
		Map<String,Long> extF = new HashMap<String,Long>();

		Map<String,SVNLogEntryPath> map = entry.getChangedPaths();
		if (map!=null) {
			for(String key:map.keySet()) {
				SVNLogEntryPath sp = map.get(key);
				
				System.out.println(sp.toString());
				switch(sp.getType()) {
				case 'A': A++; break;
				case 'D': D++; break;
				case 'M': M++; break;
				case 'R': R++; break;
				default: N++;
				}
				//determino la cantidad de ficheros por su tipo
				String file = sp.getPath();
				String ext = "---";
				
				//caso particular de la estructura de SVN y de la migracion de CVS a SVN
				//no contemplo tags, branch ni trunk/CVSROOT
				if (!file.startsWith("/tags") && !file.startsWith("/branch") && !file.startsWith("/trunk/CVSROOT")) {
					numFiles++;
					int i1=file.lastIndexOf(".");
					int i2=file.lastIndexOf("/");
					if (i1>0 && i1>i2) ext=file.substring(i1+1);
					//por defecto he asociado al key la concatenacion de TYPO y EXTENSION
					String keyF = sp.getType()+ext;
					if (extF.containsKey(keyF)) extF.put(keyF, extF.get(keyF)+1);
					else extF.put(keyF, Long.valueOf(1));
				} 
			}
		}
		res+=" A="+A+" M="+M+" D="+D+" R="+R+" N="+N;
		res+=" Message:"+entry.getMessage();
		//res+=" Properties:"+entry.getRevisionProperties().asMap();
		
		System.out.println(res);
		//System.out.println(numFiles+" | "+extF);
		
		if (mapper!=null) {
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("PROY", this.getActualProy());
			data.put("REV", entry.getRevision());
			mapper.prepareCommit(data);
			data = new HashMap<String,Object>();
			data.put("PROY", this.getActualProy());
			data.put("REV", entry.getRevision());
			data.put("LOGIN", entry.getAuthor());
			data.put("TIMESTAMP", new java.sql.Date(entry.getDate().getTime()));
			data.put("A", A);
			data.put("M", M);
			data.put("D", D);
			data.put("R", R);
			data.put("LOG", entry.getMessage()==null?null:entry.getMessage().substring(0, Math.min(4000, entry.getMessage().length())));
			mapper.insertCommit(data);
			//inserto los datos estadisticos asociados al commit
			data = new HashMap<String,Object>();
			data.put("PROY", this.getActualProy());
			data.put("REV", entry.getRevision());
			mapper.prepareCommitStats(data);
			for(String key:extF.keySet()) {
				//por defecto he asociado al key la concatenacion de TYPO y EXTENSION
				String ext = key.substring(1);
				String type = key.substring(0,1);
				data = new HashMap<String,Object>();
				data.put("PROY", this.getActualProy());
				data.put("REV", entry.getRevision());
				data.put("EXT", ext);
				data.put("TYPE", type);
				data.put("FILES", extF.get(key));
				mapper.insertCommitStat(data);
			}
		}
	}
	
	private String printPrp(SVNPropertyData prp) {
		String res="";
		res+=prp.getName()+" = "+prp.getValue().getString();
		return res;
	}
	
	@SuppressWarnings("deprecation")
	public void handleDirEntry(SVNDirEntry entry) throws SVNException {
		//System.out.println(entry);
		//System.out.println(entry.getDate()+" "+entry.getAuthor()+" "+entry.getSize()+" "+entry.getPath());
		//System.out.println(entry.getSize()+" "+entry.getPath());
		String file=entry.getPath();
		long size=entry.getSize();
		
		//caso particular de la estructura de SVN y de la migracion de CVS a SVN
		//no contemplo tags, branch ni trunk/CVSROOT
		if (file.startsWith("tags") || file.startsWith("branch") || file.startsWith("trunk/CVSROOT")) return;
		
		//System.out.println(entry.getSize()+" "+entry.getPath());

		
		//caso especial de directorios que tienen size=0
		if (size==0) totalDirs++;
		else {
			String ext = "---";
			totalFiles++;
			totalSize+=size;
			int i1=file.lastIndexOf(".");
			int i2=file.lastIndexOf("/");
			if (i1>0 && i1>i2) ext=file.substring(i1+1);
			if (extFiles.containsKey(ext)) extFiles.put(ext, extFiles.get(ext)+1);
			else extFiles.put(ext, Long.valueOf(1));
			if (extSizes.containsKey(ext)) extSizes.put(ext, extSizes.get(ext)+size);
			else extSizes.put(ext, Long.valueOf(size));
		}
		
		System.out.print(".");
		if ((totalFiles+totalDirs)%80==0) System.out.println();
	}
	
	public void handleProperty(File path, SVNPropertyData prp) throws SVNException {
		System.out.println("FILE:"+path+" prp="+printPrp(prp));
	}
	public void handleProperty(SVNURL url, SVNPropertyData prp) throws SVNException {
		System.out.println("URL:"+url+" prp="+printPrp(prp));
	}
	public void handleProperty(long rev, SVNPropertyData prp) throws SVNException {
		System.out.println("REV:"+rev+" prp="+printPrp(prp));
	}
	
	
	public static void main(String[] args) throws Exception {
		if (args.length<7) {
			System.out.println("Uso: svntext.Test user pass dbUser dbPass dbUrl repoUri");
			System.out.println(args);
			System.exit(-1);
		}
		Test test = new Test();
		String user = args[1];
		String pass=args[2];
		String dbUser = args[3];
		String dbPass = args[4];
		String dbUrl = args[5];
		String repoUrl = args[6];
		
		test.initSVN(user, pass);
		test.initDB(dbUrl,dbUser,dbPass);
		
		test.loadProys();		
		test.getAllRepoList(repoUrl, user,pass);

		test.doAllLog();
		test.doAllList();
		
		test.endDB();
		
	}
}