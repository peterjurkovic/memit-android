CREATE TABLE "book" (
     "_id"           TEXT PRIMARY KEY NOT NULL ,
     "name"          TEXT,
     "lang_question" TEXT,
     "lang_answer"   TEXT,
     "level"         TEXT,
     "changed"       DATETIME DEFAULT (datetime('now')),
     "created"       DATETIME DEFAULT (datetime('now')),
     "deleted"       INTEGER NOT NULL DEFAULT (0),
     "published"     INTEGER NOT NULL DEFAULT (0)
);

CREATE  TABLE "lecture" (
	"_id"           TEXT PRIMARY KEY NOT NULL,
	"book_id"       TEXT,
	"name"          TEXT,
	"lang_question" TEXT,
	"lang_answer"   TEXT,
	"changed"       DATETIME NOT NULL  DEFAULT (datetime('now')),
	"deleted"       INTEGER NOT NULL DEFAULT (0)
);

CREATE  TABLE "word" (
	"_id"           TEXT PRIMARY KEY NOT NULL,
	"question"      TEXT,
	"answer"        TEXT,
	"lecture_id"    TEXT,
	"active"        INTEGER NOT NULL DEFAULT (0),
	"deleted"       INTEGER NOT NULL DEFAULT (0),
	"changed"       DATETIME NOT NULL  DEFAULT (datetime('now'))
);

CREATE TABLE "session" (
	"_id" TEXT      PRIMARY KEY NOT NULL ,
	"hits"          INTEGER NOT NULL DEFAULT (0),
	"rate_sum"      INTEGER NOT NULL DEFAULT (0),
	"learned"       INTEGER NOT NULL DEFAULT (0),
	"changed"       DATETIME NOT NULL DEFAULT (datetime('now')),
	"created"       DATETIME NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE "session_word" (
	"word_id"       TEXT NOT NULL,
	"session_id"    TEXT NOT NULL,
	"hits"          INTEGER NOT NULL DEFAULT (0),
	"rate_sum"      INTEGER NOT NULL DEFAULT (0),
	"last_rating"   INTEGER NOT NULL DEFAULT (0),
	"changed"       DATETIME NOT NULL DEFAULT (datetime('now')),
	PRIMARY KEY (word_id, session_id)
);

CREATE INDEX book_fk_idx ON lecture (book_id);
CREATE INDEX lecture_fk_idx ON word (lecture_id);
CREATE INDEX word_fk_idx ON session_word (changed);
