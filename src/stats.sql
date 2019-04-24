select * from svn_commits;

select proy,login,count(*) from svn_commits
group by proy,login
order by count(*) desc;

select login,min(TIMESTAMP),max(timestamp), count(*),sum(A),sum(M),sum(R),sum(D) from svn_commits
group by login
order by count(*) desc;


select proy,rev,count(*) from svn_commits group by proy,rev
having count(*) > 1

    select proy,login,dame_nombre(login),
    count(*) COMMITS,sum(count(*)) over(partition by proy) TOTAL_COMMITS, round(100*count(*)/sum(count(*)) over(partition by proy),2) PER_COMMITS,
    sum(A) A,sum(sum(A)) over(partition by proy) TOTAL_A, round(100*(1+sum(A))/(1+sum(sum(A)) over(partition by proy)),2) PER_A,
    sum(M) M,sum(sum(M)) over(partition by proy) TOTAL_M, round(100*(1+sum(M))/(1+sum(sum(M)) over(partition by proy)),2) PER_M,
    sum(D) D,sum(sum(D)) over(partition by proy) TOTAL_D, round(100*(1+sum(D))/(1+sum(sum(D)) over(partition by proy)),2) PER_D,
    sum(R) R,sum(sum(R)) over(partition by proy) TOTAL_R, round(100*(1+sum(R))/(1+sum(sum(R)) over(partition by proy)),2) PER_R,
    max(timestamp) TIMESTAMP
    from svn_commits
    where login is not null
    and timestamp>to_date('01/01/2019','dd/mm/yyyy')
    group by proy,login;

    
select proy,count(*) PERSONAS,MIN(LOGIN) P1,dame_nombre(MIN(LOGIN)) N1,MAX(LOGIN) P2,dame_nombre(MAX(LOGIN)) N2,sum(COMMITS) COMMITS,SUM(A) A,SUM(M) M,SUM(D) D,SUM(R) R,MAX(TIMESTAMP) TIMESTAMP from (    
    select proy,login,
    count(*) COMMITS,sum(count(*)) over(partition by proy) TOTAL_COMMITS, round(100*count(*)/sum(count(*)) over(partition by proy),2) PER_COMMITS,
    sum(A) A,sum(sum(A)) over(partition by proy) TOTAL_A, round(100*(1+sum(A))/(1+sum(sum(A)) over(partition by proy)),2) PER_A,
    sum(M) M,sum(sum(M)) over(partition by proy) TOTAL_M, round(100*(1+sum(M))/(1+sum(sum(M)) over(partition by proy)),2) PER_M,
    sum(D) D,sum(sum(D)) over(partition by proy) TOTAL_D, round(100*(1+sum(D))/(1+sum(sum(D)) over(partition by proy)),2) PER_D,
    sum(R) R,sum(sum(R)) over(partition by proy) TOTAL_R, round(100*(1+sum(R))/(1+sum(sum(R)) over(partition by proy)),2) PER_R,
    max(timestamp) TIMESTAMP
    from svn_commits
    where login is not null
    and timestamp>to_date('01/04/2019','dd/mm/yyyy')
    group by proy,login
) group by proy;

 
select login,dame_nombre(MAX(LOGIN)) NOMBRE,count(*) PROYECTOS,MIN(PROY) P1,MAX(PROY) P2,sum(COMMITS) COMMITS,SUM(A) A,SUM(M) M,SUM(D) D,SUM(R) R,MAX(TIMESTAMP) TIMESTAMP from (    
    select proy,login,
    count(*) COMMITS,sum(count(*)) over(partition by proy) TOTAL_COMMITS, round(100*count(*)/sum(count(*)) over(partition by proy),2) PER_COMMITS,
    sum(A) A,sum(sum(A)) over(partition by proy) TOTAL_A, round(100*(1+sum(A))/(1+sum(sum(A)) over(partition by proy)),2) PER_A,
    sum(M) M,sum(sum(M)) over(partition by proy) TOTAL_M, round(100*(1+sum(M))/(1+sum(sum(M)) over(partition by proy)),2) PER_M,
    sum(D) D,sum(sum(D)) over(partition by proy) TOTAL_D, round(100*(1+sum(D))/(1+sum(sum(D)) over(partition by proy)),2) PER_D,
    sum(R) R,sum(sum(R)) over(partition by proy) TOTAL_R, round(100*(1+sum(R))/(1+sum(sum(R)) over(partition by proy)),2) PER_R,
    max(timestamp) TIMESTAMP
    from svn_commits
    where login is not null
    and timestamp>to_date('01/01/2019','dd/mm/yyyy')
    group by proy,login
) group by login;
                                                                                      
                                                                                      
