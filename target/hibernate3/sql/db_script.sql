
    alter table vinculado 
        drop 
        foreign key FK5D671C0DD8F7B22E;

    drop table if exists estandar;

    drop table if exists vinculado;

    create table estandar (
        est_id bigint not null auto_increment,
        est_name varchar(255) not null,
        primary key (est_id)
    );

    create table vinculado (
        vinc_id bigint not null auto_increment,
        checked char(1) not null,
        est_id bigint,
        miblob tinyblob not null,
        num_personas integer,
        report_location GEOMETRY not null,
        report_status integer not null,
        report_status_desc varchar(255) not null,
        report_time datetime,
        reporte varchar(255) not null,
        primary key (vinc_id)
    );

    alter table vinculado 
        add index FK5D671C0DD8F7B22E (est_id), 
        add constraint FK5D671C0DD8F7B22E 
        foreign key (est_id) 
        references estandar (est_id);
