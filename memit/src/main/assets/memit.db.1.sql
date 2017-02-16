CREATE TABLE "book" (
     "_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
     "sid" INTEGER UNIQUE ,
     "name"          TEXT,
     "lang_question" TEXT,
     "lang_answer"   TEXT,
     "level"         TEXT,
     "changed"       DATETIME DEFAULT (datetime('now')),
     "created"       DATETIME DEFAULT (datetime('now')),
     "published"     BOOL DEFAULT false
);

CREATE  TABLE "lecture" (
	"_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
	"sid" INTEGER UNIQUE ,
	"book_id" INTEGER,
	"name" TEXT,
	"lang_question" TEXT,
	"lang_answer" TEXT,
	"changed" DATETIME NOT NULL  DEFAULT (datetime('now'))
);

CREATE  TABLE "word" (
	"_id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,
	"sid"  UNIQUE ,
	"question" TEXT,
	"answer" TEXT,
	"lecture_id" INTEGER NOT NULL,
	"active" INTEGER NOT NULL DEFAULT (0),
	"changed" DATETIME NOT NULL  DEFAULT (datetime('now'))
);

CREATE TABLE "session" (
	"_id" TEXT PRIMARY KEY NOT NULL ,
	"hits" INTEGER NOT NULL DEFAULT (0),
	"rate_sum" INTEGER  NOT NULL DEFAULT (0),
	"learned" INTEGER NOT NULL DEFAULT (0),
	"changed" DATETIME NOT NULL DEFAULT (datetime('now')),
	"created" DATETIME NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE "session_word" (
	"word_id" INTEGER NOT NULL,
	"session_id" TEXT NOT NULL,
	"hits" INTEGER NOT NULL DEFAULT (0),
	"rate_sum" INTEGER NOT NULL DEFAULT (0),
	"last_rating" INTEGER NOT NULL DEFAULT (0),
	"changed" DATETIME NOT NULL DEFAULT (datetime('now')),
	PRIMARY KEY (word_id, session_id)
);

CREATE INDEX book_fk_idx ON lecture (book_id);
CREATE INDEX lecture_fk_idx ON word (lecture_id);
CREATE INDEX word_fk_idx ON session_word (changed);

CREATE TABLE "deleted_rows" (
        tableName VARCHAR(25) NOT NULL,
 		sid INTEGER NOT NULL,
 		deleted TIMESTAMP NOT NULL DEFAULT (datetime('now'))
);

CREATE TRIGGER _sync_delete_book AFTER DELETE ON book FOR EACH ROW WHEN OLD.sid IS NOT NULL BEGIN INSERT INTO deleted_rows(tableName, sid) VALUES('book', OLD.sid); END;
CREATE TRIGGER _sync_delete_lecture AFTER DELETE ON lecture FOR EACH ROW WHEN OLD.sid IS NOT NULL BEGIN INSERT INTO deleted_rows(tableName, sid) VALUES ('lecture', OLD.sid); END;
CREATE TRIGGER _sync_delete_word AFTER DELETE ON word FOR EACH ROW WHEN OLD.sid IS NOT NULL BEGIN INSERT INTO deleted_rows(tableName, sid) VALUES('word', OLD.sid); END;