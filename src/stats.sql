select * from svn_commits;

select proy,login,count(*) from svn_commits
group by proy,login
order by count(*) desc;

select login,min(TIMESTAMP),max(timestamp), count(*),sum(A),sum(M),sum(R),sum(D) from svn_commits
group by login
order by count(*) desc;


select proy,rev,count(*) from svn_commits group by proy,rev
having count(*) > 1