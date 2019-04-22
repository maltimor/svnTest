package svntest;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SvnTestMapper {
	@Select("SELECT * FROM SVN_COMMITS")
	public List<Map<String,Object>> getCommits();

	@Select("SELECT MIN(REV) FIRST, MAX(REV) LAST FROM SVN_COMMITS WHERE PROY=#{PROY}")
	public Map<String,Object> getCommitsBounds(String PROY);
	
	@Insert("INSERT INTO SVN_COMMITS (PROY,REV,LOGIN,TIMESTAMP,A,M,D,R,LOG) VALUES (#{PROY},#{REV},#{LOGIN},#{TIMESTAMP},#{A},#{M},#{D},#{R},#{LOG})")
	public void insertCommit(Map<String,Object> data);

	@Delete("DELETE SVN_COMMITS WHERE PROY=#{PROY} AND REV=#{REV}")
	public void prepareCommit(Map<String,Object> data);

	
	@Insert("INSERT INTO SVN_COMMITS_STATS (PROY,REV,TYPE,EXT,FILES) VALUES (#{PROY},#{REV},#{TYPE},#{EXT},#{FILES})")
	public void insertCommitStat(Map<String,Object> data);

	@Delete("DELETE SVN_COMMITS_STATS WHERE PROY=#{PROY} AND REV=#{REV}")
	public void prepareCommitStats(Map<String,Object> data);
	
	
	@Insert("INSERT INTO SVN_REPOS (PROPERTIES, CONTACT, CREATION_DATE, LAST_MODIFIED, DESCRIPTION, REPO_NAME, URL, PERMISSIONS, ARCHIVED, REPO_TYPE, REPO_PUBLIC)"
			+ " VALUES (#{PROPERTIES}, #{CONTACT}, #{CREATIONDATE}, #{LASTMODIFIED}, #{DESCRIPTION}, #{NAME}, #{URL}, #{PERMISSIONS}, #{ARCHIVED}, #{TYPE}, #{PUBLIC})")
	public void insertRepo(Map<String,Object> data);
	
	@Select("SELECT * FROM SVN_REPOS WHERE #{NAME}=REPO_NAME")
	public List<Map<String,Object>> getRepo(String NAME);
	@Select("SELECT * FROM SVN_REPOS WHERE #{URL}=URL")
	public List<Map<String,Object>> getRepoByUrl(String URL);

	@Update("UPDATE SVN_REPOS SET PROPERTIES=#{PROPERTIES}, CONTACT=#{CONTACT}, CREATION_DATE=#{CREATIONDATE}, LAST_MODIFIED=#{LASTMODIFIED},"
			+ " DESCRIPTION=#{DESCRIPTION}, REPO_NAME=#{NAME}, URL=#{URL}, PERMISSIONS=#{PERMISSIONS}, ARCHIVED=#{ARCHIVED},"
			+ " REPO_TYPE=#{TYPE}, REPO_PUBLIC=#{PUBLIC} WHERE #{NAME}=REPO_NAME")
	public void updateRepo(Map<String,Object> data);

	
	
	@Update("UPDATE SVN_PROYS SET TOTAL_DIRS=#{TOTAL_DIRS}, TOTAL_FILES=#{TOTAL_FILES}, TOTAL_SIZE=#{TOTAL_SIZE}, TIMESTAMP=#{TIMESTAMP} WHERE PROY=#{PROY}")
	public void updateProyStats(Map<String,Object> data);

	@Insert("INSERT INTO SVN_STATS (PROY,REV,EXT,FILES,SIZES) VALUES (#{PROY},#{REV},#{EXT},#{FILES},#{SIZES})")
	public void insertStats(Map<String,Object> data);

	@Delete("DELETE SVN_STATS WHERE PROY=#{PROY} AND REV=#{REV}")
	public void prepareStats(Map<String,Object> data);
	
	@Select("SELECT MIN(REV) FIRST, MAX(REV) LAST FROM SVN_STATS WHERE PROY=#{PROY}")
	public Map<String,Object> getCommitsBoundsStats(String PROY);

	
	@Insert("INSERT INTO SVN_PROYS (PROY, URL, PATH) VALUES (#{PROY}, #{URL}, #{PATH})")
	public void insertProy(Map<String,Object> data);

	@Select("SELECT * FROM SVN_PROYS")
	public List<Map<String,Object>> getProys();
	
	@Select("SELECT * FROM SVN_PROYS WHERE URL=#{URL}")
	public List<Map<String,Object>> getProysByUrl(String url);

}
