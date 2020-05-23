CREATE TABLE I18N (
    topic                   varchar(255)                    NOT NULL,
    tag                     varchar(255)                    NOT NULL,
    language_code           varchar(100)                    NOT NULL,
    default_translation     varchar(255)                    NOT NULL,
    custom_translation      varchar(255)   DEFAULT NULL             ,
    is_editable             bit(1)                          NOT NULL,

  PRIMARY KEY (topic, tag, language_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
