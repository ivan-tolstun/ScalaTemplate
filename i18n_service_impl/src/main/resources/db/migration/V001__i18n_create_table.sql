CREATE TABLE I18N (
    language_code           varchar(100)    NOT NULL,
    page                    varchar(255)    NOT NULL,
    section                 varchar(255)    NOT NULL,
    tag                     varchar(255)    NOT NULL,
    default_translation     varchar(255)    NOT NULL,
    custom_translation      varchar(255)    DEFAULT NULL,
    is_editable             bit(1)          NOT NULL,

  PRIMARY KEY (language_code, page, section, tag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
