drop table if exists oai_id_sequence;

create table oai_id_sequence (
	id                    int        NOT NULL,
	PRIMARY KEY (id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

insert into oai_id_sequence values (0);

drop function if exists get_next_oai_id;

delimiter //
create function get_next_oai_id(incr int)
returns int
begin
	declare oai_id int;
	select id into oai_id from oai_id_sequence;
	update oai_id_sequence set id=id+incr;
	return oai_Id;
end
//

delimiter ;

select get_next_oai_id(50);

select get_next_oai_id(50);

select get_next_oai_id(50);
/*
*/
