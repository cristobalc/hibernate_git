package test.cristobal;

import java.util.List;

import org.junit.Test;

import es.cristobal.EjemploHibernate;
import es.cristobal.entities.Estandar;
import es.cristobal.entities.Vinculado;
import junit.framework.TestCase;

/**

SQL generado con:
$mvn compile hibernate3:hbm2ddl
El resultado, por configuración, se coloca en:
target/hibernate3/sql/db_script.sql

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

*/

public class TestHibernate extends TestCase{
	
	@Test
	public void testDeHibernate() 
	{
		 EjemploHibernate rh= new EjemploHibernate();
		  
		  // TESTEAMOS CRUD
		  
		  // Testeamos el CREATE
		  System.out.println("TEST CREATE");
		  Long id=rh.test_insert();
		  assertNotNull(id);
		  
		  // Testeamos el UPDATE
		  System.out.println("TEST UPDATE");
		  rh.test_update(id);
		  
		  // Testeamos el READ
		  System.out.println("TEST READ");
		  Estandar e=rh.test_read(id);
		  if (e!=null)
		  {
			  System.out.println("El estándar que he extraído es: \n"+e.toString());
			  for (Vinculado v:e.getVinculados())
				  System.out.println(v.toString());
		  }
		  assertNotNull(e);
		  
		  // Testeamos el DELETE
		  System.out.println("TEST DELETE");
		  rh.test_delete(id);
		  
		  // Testeamos QUERY
		  System.out.println("TEST QUERY");
		  for (Estandar estandar:(List<Estandar>)rh.test_query("SELECT e FROM Estandar e"))
		  {
			  System.out.println(estandar.toString());
			  assertNotNull(estandar);
		  }
			   
		  // Testeamos NAMED QUERY
		  System.out.println("TEST NAMED QUERY");
		  for (Estandar estandar:(List<Estandar>)rh.test_named_query("UPDATE") )
		  {
			  System.out.println(estandar.toString());
			  assertNotNull(estandar);
		  }
		  
		  // Testeamos REMOVE
		  System.out.println("TEST REMOVE ENTITIES");
		  rh.test_desconectar_entity_manager();
	}

}