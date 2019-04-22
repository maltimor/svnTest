--------------------------------------------------------
-- Archivo creado  - lunes-abril-22-2019   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table SVN_COMMITS
--------------------------------------------------------

  CREATE TABLE "SVN_COMMITS" 
   (	"PROY" VARCHAR2(4000 BYTE), 
	"REV" NUMBER, 
	"LOGIN" VARCHAR2(4000 BYTE), 
	"TIMESTAMP" DATE, 
	"A" NUMBER DEFAULT 0, 
	"M" NUMBER DEFAULT 0, 
	"D" NUMBER DEFAULT 0, 
	"R" NUMBER DEFAULT 0, 
	"LOG" VARCHAR2(4000 BYTE)
   );
--------------------------------------------------------
--  DDL for Table SVN_COMMITS_STATS
--------------------------------------------------------

  CREATE TABLE "SVN_COMMITS_STATS" 
   (	"PROY" VARCHAR2(4000 BYTE), 
	"REV" NUMBER, 
	"TYPE" VARCHAR2(20 BYTE), 
	"EXT" VARCHAR2(2000 BYTE), 
	"FILES" NUMBER
   );
--------------------------------------------------------
--  DDL for Table SVN_PROYS
--------------------------------------------------------

  CREATE TABLE "SVN_PROYS" 
   (	"PROY" VARCHAR2(4000 BYTE), 
	"URL" VARCHAR2(4000 BYTE), 
	"PATH" VARCHAR2(4000 BYTE), 
	"TIMESTAMP" DATE DEFAULT SYSDATE, 
	"STATUS" VARCHAR2(4000 BYTE), 
	"TOTAL_DIRS" NUMBER, 
	"TOTAL_FILES" NUMBER, 
	"TOTAL_SIZE" NUMBER
   );
--------------------------------------------------------
--  DDL for Table SVN_REPOS
--------------------------------------------------------

  CREATE TABLE "SVN_REPOS" 
   (	"PROPERTIES" VARCHAR2(4000 BYTE), 
	"CONTACT" VARCHAR2(4000 BYTE), 
	"CREATION_DATE" DATE, 
	"LAST_MODIFIED" DATE, 
	"DESCRIPTION" VARCHAR2(4000 BYTE), 
	"REPO_NAME" VARCHAR2(4000 BYTE), 
	"URL" VARCHAR2(4000 BYTE), 
	"PERMISSIONS" VARCHAR2(4000 BYTE), 
	"ARCHIVED" VARCHAR2(4000 BYTE), 
	"REPO_TYPE" VARCHAR2(4000 BYTE), 
	"REPO_PUBLIC" VARCHAR2(4000 BYTE), 
	"TIMESTAMP" DATE DEFAULT SYSDATE
   );
--------------------------------------------------------
--  DDL for Table SVN_STATS
--------------------------------------------------------

  CREATE TABLE "SVN_STATS" 
   (	"PROY" VARCHAR2(4000 BYTE), 
	"REV" NUMBER, 
	"EXT" VARCHAR2(2000 BYTE), 
	"FILES" NUMBER DEFAULT 0, 
	"SIZES" NUMBER DEFAULT 0
   );
--------------------------------------------------------
--  DDL for Sequence SEQ_GENERIC
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Index SVN_STATS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SVN_STATS_PK" ON "SVN_STATS" ("PROY", "REV", "EXT") ;
--------------------------------------------------------
--  DDL for Index SVN_REPOS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SVN_REPOS_PK" ON "SVN_REPOS" ("REPO_NAME") ;
--------------------------------------------------------
--  DDL for Index SVN_PROYS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SVN_PROYS_PK" ON "SVN_PROYS" ("PROY") ;
--------------------------------------------------------
--  DDL for Index SVN_COMMITS_STATS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SVN_COMMITS_STATS_PK" ON "SVN_COMMITS_STATS" ("PROY", "REV", "TYPE", "EXT") ;
--------------------------------------------------------
--  DDL for Index SVN_COMMITS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SVN_COMMITS_PK" ON "SVN_COMMITS" ("PROY", "REV") ;

--------------------------------------------------------
--  Constraints for Table SVN_COMMITS
--------------------------------------------------------

  ALTER TABLE "SVN_COMMITS" ADD CONSTRAINT "SVN_COMMITS_PK" PRIMARY KEY ("PROY", "REV") ENABLE;
  ALTER TABLE "SVN_COMMITS" MODIFY ("TIMESTAMP" NOT NULL ENABLE);
  ALTER TABLE "SVN_COMMITS" MODIFY ("REV" NOT NULL ENABLE);
  ALTER TABLE "SVN_COMMITS" MODIFY ("PROY" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table SVN_COMMITS_STATS
--------------------------------------------------------

  ALTER TABLE "SVN_COMMITS_STATS" ADD CONSTRAINT "SVN_COMMITS_STATS_PK" PRIMARY KEY ("PROY", "REV", "TYPE", "EXT") ENABLE;
  ALTER TABLE "SVN_COMMITS_STATS" MODIFY ("FILES" NOT NULL ENABLE);
  ALTER TABLE "SVN_COMMITS_STATS" MODIFY ("EXT" NOT NULL ENABLE);
  ALTER TABLE "SVN_COMMITS_STATS" MODIFY ("TYPE" NOT NULL ENABLE);
  ALTER TABLE "SVN_COMMITS_STATS" MODIFY ("REV" NOT NULL ENABLE);
  ALTER TABLE "SVN_COMMITS_STATS" MODIFY ("PROY" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table SVN_PROYS
--------------------------------------------------------

  ALTER TABLE "SVN_PROYS" MODIFY ("TIMESTAMP" NOT NULL ENABLE);
  ALTER TABLE "SVN_PROYS" MODIFY ("PATH" NOT NULL ENABLE);
  ALTER TABLE "SVN_PROYS" MODIFY ("URL" NOT NULL ENABLE);
  ALTER TABLE "SVN_PROYS" MODIFY ("PROY" NOT NULL ENABLE);
  ALTER TABLE "SVN_PROYS" ADD CONSTRAINT "SVN_PROYS_PK" PRIMARY KEY ("PROY") ENABLE;
--------------------------------------------------------
--  Constraints for Table SVN_REPOS
--------------------------------------------------------

  ALTER TABLE "SVN_REPOS" ADD CONSTRAINT "SVN_REPOS_PK" PRIMARY KEY ("REPO_NAME") ENABLE;
  ALTER TABLE "SVN_REPOS" MODIFY ("REPO_NAME" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table SVN_STATS
--------------------------------------------------------

  ALTER TABLE "SVN_STATS" ADD CONSTRAINT "SVN_STATS_PK" PRIMARY KEY ("PROY", "REV", "EXT") ENABLE;
  ALTER TABLE "SVN_STATS" MODIFY ("SIZES" NOT NULL ENABLE);
  ALTER TABLE "SVN_STATS" MODIFY ("FILES" NOT NULL ENABLE);
  ALTER TABLE "SVN_STATS" MODIFY ("EXT" NOT NULL ENABLE);
  ALTER TABLE "SVN_STATS" MODIFY ("REV" NOT NULL ENABLE);
  ALTER TABLE "SVN_STATS" MODIFY ("PROY" NOT NULL ENABLE);
