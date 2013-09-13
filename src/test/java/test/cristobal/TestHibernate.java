package test.cristobal;

import java.util.List;

import org.junit.Test;

import es.cristobal.EjemploHibernate;
import es.cristobal.entities.Estandar;
import es.cristobal.entities.Vinculado;
import junit.framework.TestCase;


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