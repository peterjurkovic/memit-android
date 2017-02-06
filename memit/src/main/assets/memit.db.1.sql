CREATE TABLE "book"
  (
     "_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
     "sid" INTEGER UNIQUE ,
     "name"          TEXT,
     "lang_question" TEXT,
     "lang_answer"   TEXT,
     "level"         TEXT,
     "changed"       DATETIME DEFAULT CURRENT_TIMESTAMP,
     "created"       DATETIME DEFAULT CURRENT_TIMESTAMP,
     "published"     BOOL DEFAULT false
  );


CREATE  TABLE "lecture" (
	"_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
	"sid" INTEGER UNIQUE ,
	"book_id" INTEGER,
	"lang_question" TEXT,
	"lang_answer" TEXT,
	"created" DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	"changed" DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP
);

CREATE  TABLE "word" (
	"_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
	"sid"  UNIQUE ,
	"question" TEXT,
	"answer" TEXT,
	"lecture_id" INTEGER NOT NULL ,
	"created" DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	"changed" DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX book_fk_idx ON lecture (book_id);
CREATE INDEX lecture_fk_idx ON word (lecture_id);