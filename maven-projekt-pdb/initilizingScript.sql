ALTER SESSION SET NLS_DATE_FORMAT='DD-MM-YYYY';

DROP TABLE photos CASCADE CONSTRAINT;
DROP TABLE estates CASCADE CONSTRAINT;
DROP TABLE freeholders CASCADE CONSTRAINT;
DROP TABLE related_spatial_entities CASCADE CONSTRAINT;


CREATE TABLE related_spatial_entities(
id NUMBER NOT NULL,
name VARCHAR2(50), 
description VARCHAR2(255), 
geometry SDO_GEOMETRY NOT NULL, 
layer VARCHAR2(50) NOT NULL, 
entity_type VARCHAR2(50) NOT NULL,
valid_from DATE NOT NULL,
valid_to DATE NOT NULL
);


CREATE TABLE freeholders(
id NUMBER NOT NULL,
first_name VARCHAR2(25) NOT NULL,
surname VARCHAR2(25) NOT NULL,
birth_date DATE NOT NULL
);


CREATE TABLE photos(
id NUMBER NOT NULL,
photo ORDSYS.ORDIMAGE,
photo_si ORDSYS.SI_STILLIMAGE,
photo_ac ORDSYS.SI_AVERAGECOLOR,
photo_ch ORDSYS.SI_COLORHISTOGRAM,
photo_pc ORDSYS.SI_POSITIONALCOLOR,
photo_tx ORDSYS.SI_TEXTURE
);


CREATE TABLE estates(
id NUMBER NOT NULL,
name VARCHAR2(50), 
description VARCHAR2(255), 
geometry SDO_GEOMETRY NOT NULL,
valid_from DATE NOT NULL,
valid_to DATE NOT NULL,
freeholders_id NUMBER, 
photos_id NUMBER 
);

ALTER TABLE related_spatial_entities ADD CONSTRAINT PK_related_spatial_entities PRIMARY KEY (id, valid_from, valid_to);
ALTER TABLE related_spatial_entities ADD CONSTRAINT ENUM_entity_type CHECK ( entity_type IN ('house','path','trees','bushes','water area','river','water connection','connection to electricity','connection to gas','power lines','gas pipes','water pipes') );
ALTER TABLE related_spatial_entities ADD CONSTRAINT ENUM_layer CHECK ( layer IN('underground','ground','overground') );

ALTER TABLE freeholders ADD CONSTRAINT PK_freeholders PRIMARY KEY (id);

ALTER TABLE photos ADD CONSTRAINT PK_photos PRIMARY KEY (id);

ALTER TABLE estates ADD CONSTRAINT PK_estates PRIMARY KEY (id, valid_from, valid_to);
ALTER TABLE estates ADD CONSTRAINT FK_estates_photos FOREIGN KEY (photos_id) REFERENCES photos;
ALTER TABLE estates ADD CONSTRAINT FK_estates_freeholders FOREIGN KEY (freeholders_id) REFERENCES freeholders;


DELETE FROM USER_SDO_GEOM_METADATA WHERE
	TABLE_NAME = 'RELATED_SPATIAL_ENTITIES' AND COLUMN_NAME = 'GEOMETRY';
INSERT INTO USER_SDO_GEOM_METADATA VALUES (
	'related_spatial_entities', 'geometry',
	SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 650, 0.01), SDO_DIM_ELEMENT('Y', 0, 650, 0.01)),
	NULL
);
CREATE INDEX SP_INDEX_entities_geometry ON related_spatial_entities ( geometry ) indextype is MDSYS.SPATIAL_INDEX ;

DELETE FROM USER_SDO_GEOM_METADATA WHERE
	TABLE_NAME = 'ESTATES' AND COLUMN_NAME = 'GEOMETRY';
INSERT INTO USER_SDO_GEOM_METADATA VALUES (
	'estates', 'geometry',
	SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 650, 0.01), SDO_DIM_ELEMENT('Y', 0, 650, 0.01)),
	NULL
);
CREATE INDEX SP_INDEX_estates_geometry ON estates ( geometry ) indextype is MDSYS.SPATIAL_INDEX ;


INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	1, 'house A',
	SDO_GEOMETRY(2003, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		SDO_ORDINATE_ARRAY(75,80, 100,80, 100,100, 75,100, 75, 80)
	),
	'overground',
	'house',
	TO_DATE('27-10-2016', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);


INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	2, 'house D',
	SDO_GEOMETRY(2003, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		SDO_ORDINATE_ARRAY(300,80, 325,80, 325,125, 300,125, 300,80)
	),
	'overground',
	'house',
	TO_DATE('27-10-1996', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	3, 'house C',
	SDO_GEOMETRY(2003, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		SDO_ORDINATE_ARRAY(375,125, 400,125, 400,150, 375,150, 375,125)
	),
	'overground',
	'house',
	TO_DATE('27-10-2016', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	4, 'house B',
	SDO_GEOMETRY(2003, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		SDO_ORDINATE_ARRAY(500,200, 600,300, 550,350, 450,250, 500,200)
	),
	'overground',
	'house',
	TO_DATE('27-10-1980', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	5, 'house E',
	SDO_GEOMETRY(2003, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		SDO_ORDINATE_ARRAY(300,425, 325,425, 325,475, 250,475, 250,450, 300,450, 300, 425)
	),
	'overground',
	'house',
	TO_DATE('27-10-1988', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	
	6, 'water area A',
	SDO_GEOMETRY(2003, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 1003, 4),
		SDO_ORDINATE_ARRAY(150,275, 150,325, 125,300) 
	),
	'overground',
	'water area',
	TO_DATE('27-10-1970', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	7, 'bushes A',
	SDO_GEOMETRY(2005, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1,1,1, 3,1,1, 5,1,1, 7,1,1, 9,1,1, 11,1,1),
		SDO_ORDINATE_ARRAY(150,265, 150,250, 150,235, 165,265, 165,250, 165,235)
	),
	'overground',
	'bushes',
	TO_DATE('27-10-2016', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	8, 'trees A',
	SDO_GEOMETRY(2005, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1,1,1, 3,1,1, 5,1,1, 7,1,1, 9,1,1, 11,1,1, 13,1,1, 15,1,1),
		SDO_ORDINATE_ARRAY(435,450, 435,435, 435,420, 435,405, 465,450, 465,435, 465,420, 465,405)
	),
	'overground',
	'trees',
	TO_DATE('27-10-2000', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	9, 'connection to gas A1',
	SDO_GEOMETRY(2001, NULL,
	    SDO_POINT_TYPE(500, 375, NULL),
		NULL, NULL 
	),
	'underground',
	'connection to gas',
	TO_DATE('27-10-2005', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	10, 'water connection A1',
	SDO_GEOMETRY(2001, NULL,
	    SDO_POINT_TYPE(275, 50, NULL), 
		NULL, NULL 
	),
	'underground',
	'water connection',
	TO_DATE('27-10-1997', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	11, 'water connection A2',
	SDO_GEOMETRY(2001, NULL,
	    SDO_POINT_TYPE(275, 325, NULL),
		NULL, NULL 
	),
	'underground',
	'water connection',
	TO_DATE('27-10-2000', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	12, 'water connection A2',
	SDO_GEOMETRY(2001, NULL,
	    SDO_POINT_TYPE(275, 325, NULL),
		NULL, NULL 
	),
	'underground',
	'water connection',
	TO_DATE('27-10-2000', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	13, 'water connection A3',
	SDO_GEOMETRY(2001, NULL,
	    SDO_POINT_TYPE(275, 425, NULL),
		NULL, NULL 
	),
	'underground',
	'water connection',
	TO_DATE('27-10-2000', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	14, 'water connection B1',
	SDO_GEOMETRY(2001, NULL,
	    SDO_POINT_TYPE(450, 325, NULL), 
		NULL, NULL 
	),
	'underground',
	'water connection',
	TO_DATE('27-10-2000', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	15, 'connection to electricity A1',
	SDO_GEOMETRY(2001, NULL,  
	    SDO_POINT_TYPE(450, 50, NULL),
		NULL, NULL 
	),
	'overground',
	'connection to electricity',
	TO_DATE('27-10-1990', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	16, 'connection to electricity A2',
	SDO_GEOMETRY(2001, NULL, 
	    SDO_POINT_TYPE(450, 200, NULL), 
		NULL, NULL 
	),
	'overground',
	'connection to electricity',
	TO_DATE('27-10-2016', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	17, 'connection to electricity A3',
	SDO_GEOMETRY(2001, NULL, 
	    SDO_POINT_TYPE(150, 500, NULL),
		NULL, NULL 
	),
	'overground',
	'connection to electricity',
	TO_DATE('27-10-2004', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	18, 'gas pipes A',
	SDO_GEOMETRY(2002, NULL, NULL, 
		SDO_ELEM_INFO_ARRAY(1, 2, 1),
		SDO_ORDINATE_ARRAY(0,375, 650,375) 
	),
	'underground',
	'gas pipes',
	TO_DATE('27-10-1989', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	19, 'power lines A',
	SDO_GEOMETRY(2002, NULL, NULL, 
		SDO_ELEM_INFO_ARRAY(1, 2, 1),
		SDO_ORDINATE_ARRAY(0,50, 450,50, 450,200, 150,500, 150,650) 
	),
	'overground',
	'power lines',
	TO_DATE('27-10-1999', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	20, 'water pipes',
	SDO_GEOMETRY(2006, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1,2,1, 5,2,1),
		SDO_ORDINATE_ARRAY(275,0, 275,650, 275,325, 450,325) 
	),
	'underground',
	'water pipes',
	TO_DATE('27-10-1970', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);

INSERT INTO related_spatial_entities(id, name, geometry, layer, entity_type, valid_from, valid_to) VALUES (
	21, 'path A',
	SDO_GEOMETRY(2002, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 2, 1),
		SDO_ORDINATE_ARRAY(0,125, 225,125, 450,350, 450,500, 200,500, 200,650) 
	),
	'overground',
	'path',
	TO_DATE('27-10-2008', 'dd-mm-yyyy'),
	TO_DATE('27-10-2116', 'dd-mm-yyyy')
);



INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(1,'Michal', 'Burgh', TO_DATE('01-02-1988', 'dd-mm-yyyy'));
INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(2,'Jan', 'Abib', TO_DATE('01-03-1998', 'dd-mm-yyyy'));
INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(3,'Marek', 'Somer', TO_DATE('01-07-1918', 'dd-mm-yyyy'));
INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(4,'Filip', 'Kin', TO_DATE('01-06-1968', 'dd-mm-yyyy'));
INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(5,'Honza', 'Noprs', TO_DATE('01-05-1948', 'dd-mm-yyyy'));
INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(6,'Peter', 'Tuvxy', TO_DATE('01-07-1938', 'dd-mm-yyyy'));
INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(7,'Karel', 'Gott', TO_DATE('01-08-1998', 'dd-mm-yyyy'));
INSERT INTO freeholders (id, first_name, surname, birth_date) VALUES(8,'Fiko', 'Pome', TO_DATE('01-09-1978', 'dd-mm-yyyy'));

INSERT INTO photos (id) VALUES(1);
INSERT INTO photos (id) VALUES(2);
INSERT INTO photos (id) VALUES(3);
INSERT INTO photos (id) VALUES(4);
INSERT INTO photos (id) VALUES(5);
INSERT INTO photos (id) VALUES(6);
INSERT INTO photos (id) VALUES(7);
INSERT INTO photos (id) VALUES(8);

INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    1,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(0,0, 650,0, 650,150, 350,150, 350,100, 0,100, 0,0)
	    ),
	    TO_DATE('15-10-2001', 'dd-mm-yyyy'), TO_DATE('25-10-2016', 'dd-mm-yyyy'), 1, 1
);
INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    1,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(0,0, 650,0, 650,150, 350,150, 350,100, 0,100, 0,0)
	    ),
	    TO_DATE('26-10-2016', 'dd-mm-yyyy'), TO_DATE('27-10-2116', 'dd-mm-yyyy'), 2, 1
);

INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    2,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(350,150, 650,150, 650,450, 350,450, 350,150)
	    ),
	    TO_DATE('26-10-1999', 'dd-mm-yyyy'), TO_DATE('27-10-2116', 'dd-mm-yyyy'), 4, 2
);

INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    3,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(0,100, 350,100, 350,400, 200,400, 200, 150, 100,150, 100,300, 0,300, 0,100)
	    ),
	    TO_DATE('05-05-2008', 'dd-mm-yyyy'), TO_DATE('12-09-2010', 'dd-mm-yyyy'), 6, 3
);
INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    3,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(0,100, 350,100, 350,400, 200,400, 200, 150, 100,150, 100,300, 0,300, 0,100)
	    ),
	    TO_DATE('13-09-2010', 'dd-mm-yyyy'),TO_DATE('27-10-2116', 'dd-mm-yyyy'), 7, 3
);

INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    4,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(0,300, 100,300, 100,150, 200,150, 200,400, 75,400, 75,550, 0,550, 0,300)
	    ),
	    TO_DATE('26-10-2013', 'dd-mm-yyyy'), TO_DATE('25-10-2014', 'dd-mm-yyyy'), 1, 4
);
INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    4,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(0,300, 100,300, 100,150, 200,150, 200,400, 75,400, 75,550, 0,550, 0,300)
	    ),
	    TO_DATE('26-10-2014', 'dd-mm-yyyy'), TO_DATE('27-10-2116', 'dd-mm-yyyy'), 5, 4
);

INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    5,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(75,400, 350,400, 350,550, 75,550, 75,400)
	    ),
	    TO_DATE('26-10-2014', 'dd-mm-yyyy'), TO_DATE('27-10-2016', 'dd-mm-yyyy'), 3, 5
);

INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    6,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(0,550, 350,550, 350,650, 0,650, 0,550)
	    ),
	    TO_DATE('16-03-2000', 'dd-mm-yyyy'), TO_DATE('10-07-2003', 'dd-mm-yyyy'), 4, 6
);
INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    6,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(0,550, 350,550, 350,650, 0,650, 0,550)
	    ),
	    TO_DATE('11-07-2003', 'dd-mm-yyyy'), TO_DATE('27-10-2116', 'dd-mm-yyyy'), 6, 6
);

INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    7,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(350,450, 550,450, 550,550, 450,550, 450, 650, 350,650, 350,450)
	    ),
	    TO_DATE('17-05-1993', 'dd-mm-yyyy'), TO_DATE('19-10-1995', 'dd-mm-yyyy'), 1, 7
);
INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    7,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(350,450, 550,450, 550,550, 450,550, 450, 650, 350,650, 350,450)
	    ),
	    TO_DATE('20-10-1995', 'dd-mm-yyyy'), TO_DATE('27-10-2116', 'dd-mm-yyyy'), 6, 7
);

INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    8,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(550,450, 650,450, 650,650, 450,650, 450,550, 550,550, 550,450)
	    ),
	    TO_DATE('17-06-2001', 'dd-mm-yyyy'), TO_DATE('23-12-2006', 'dd-mm-yyyy'),  1, 8
);
INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    8,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(550,450, 650,450, 650,650, 450,650, 450,550, 550,550, 550,450)
	    ),
	    TO_DATE('24-12-2006', 'dd-mm-yyyy'), TO_DATE('20-07-2016', 'dd-mm-yyyy'), 2, 8
);
INSERT INTO estates (id, geometry, valid_from, valid_to, freeholders_id, photos_id) VALUES(
	    8,
	    SDO_GEOMETRY(2003, NULL, NULL,
		    SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		    SDO_ORDINATE_ARRAY(550,450, 650,450, 650,650, 450,650, 450,550, 550,550, 550,450)
	    ),
	    TO_DATE('21-07-2016', 'dd-mm-yyyy'), TO_DATE('20-07-2017', 'dd-mm-yyyy'), 1, 8
);


COMMIT;
