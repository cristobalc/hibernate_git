package es.cristobal;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import es.cristobal.entities.ReportStatus;
import es.cristobal.entities.Vinculado;
import es.cristobal.entities.Estandar;

/*
Pasos para crear una PERSISTENCE-UNIT con Hibernate Entity Manager en ENTORNOS NO GESTIONADOS
(En entornos GESTIONADOS, el encargado es el contenedor. La ventaja que da esto es que el contenedor se encarga de cosas adicionales, como POOL DE CONEXIONES y CONTROL DE SEGURIDAD mejorado)

1. Crear "persistence.xml" en "src/main/resources/META-INF" > ver ejemplo en este proyecto
2. Crear la tabla y la base de datos, en este caso, en mysql:
    CREATE DATABASE test;   

	CREATE TABLE IF NOT EXISTS `estandar` (
		`est_id` bigint(20) NOT NULL AUTO_INCREMENT,
		`est_name` varchar(255) NOT NULL,
		PRIMARY KEY (`est_id`)
	) ENGINE=MyIsam DEFAULT CHARSET=utf8; // En Mysql, el soporte de datos espaciales sólo viene con MyIsam
	
	CREATE TABLE IF NOT EXISTS `vinculado` (
	  `vinc_id` bigint(20) NOT NULL AUTO_INCREMENT,
	  `est_id` bigint(20) NOT NULL,
	  `num_personas` int(11) NOT NULL,
	  `report_time` datetime NOT NULL,
	  `reporte` varchar(255) NOT NULL,
	  `checked` char(1) DEFAULT NULL,
	  `report_location` point NOT NULL,
	  `report_status` int(1) NOT NULL,
	  `report_status_desc` VARCHAR(15) NOT NULL,
	  `miblob` blob NOT NULL,
	  PRIMARY KEY (`vinc_id`),
	  FOREIGN KEY(est_id) REFERENCES estandar(est_id)
	) ENGINE=MyIsam DEFAULT CHARSET=utf8;

3. Crear las clases de Java (preferiblemente a partir de un diagrama UML)
4. Mapeo de entidades. En este caso usamos anotaciones

*/

public class EjemploHibernate {

  private static final Random random = new Random();   
  EntityManagerFactory emf;
  EntityManager em;
  
  public EjemploHibernate() {
	  super();
	  emf = Persistence.createEntityManagerFactory("test");
	  em = emf.createEntityManager();
  }
  
  public EjemploHibernate(EntityManagerFactory emf, EntityManager em) {
	super();
	this.emf = emf;
	this.em = em;
  }

  public java.lang.Long test_insert() // Este test genera un estándar con cien indicadores viculados y persistirlo
  {
	  // INICIALIZAR LA TRANSACCIÓN
      GeometryFactory geometryFactory = new GeometryFactory(); // Para generar un valor de tipo "punto", usamos el módulo "hibernate-spatial"
      EntityTransaction et= em.getTransaction();
      et.begin();
      
      // 0. INICIALIZAR ESTÁNDAR
      Estandar estandar = new Estandar();
      Long randomLong = random.nextLong();
      //estandar.setEst_id(0); // Este id lo genera la base de datos
      estandar.setEst_name("Estandar ".concat(randomLong.toString()));
      estandar.setVinculados(new LinkedList<Vinculado>());
            
      for(int i=0;i<100;i++){
    	  
    	  // 1. INICIALIZAR VINCULADO   	  
          Vinculado vinculado = new Vinculado();
          
          //vinculado.setVinc_id(i); // Este id lo genera la base de datos
          //vinculado.setEst_id(estandar.getEst_id()); // Esto no vale la pena hacerlo, porque el id
          											   // todavía no ha sido asignado por la base de 
          											   // datos y por tanto siempre es 0
          vinculado.setChecked('1');
          
          Byte [] Miblob= new Byte[100];
          for (int n=0; n<100; n++) Miblob[n]=(byte) n;
          vinculado.setMiblob(Miblob);
          
          vinculado.setNum_personas(random.nextInt(3000)); // Un valor aleatorio entre 0 y 3000
          
          vinculado.setReport_status(ReportStatus.BIEN);
          vinculado.setReport_status_desc(ReportStatus.MAL);
          vinculado.setReport_time(new Date(random.nextInt((int)System.currentTimeMillis())));
          vinculado.setReporte("Este es el valor "+ Double.valueOf(random.nextDouble()).toString() );
          
          vinculado.setEstandar(estandar);//importante la asociacion ha de establecerse en los dos sentidos

          Point punto = geometryFactory.createPoint(new Coordinate(random.nextDouble(),random.nextDouble()));
          vinculado.setReport_location(punto);
          
          // 2. INSERTAR VINCULADO EN EL ESTÁNDAR
          estandar.getVinculados().add(vinculado);   
      }
     
	  try {
	      em.persist(estandar);//¿qué pasará aquí?  
	      et.commit();    // EJECUTAR LA TRANSACCIÓN      
	      return estandar.getEst_id();
	  } catch(Exception e){
	  	  et.rollback(); 	  
	  	  return (long)0;
	  }
  }
  
  public Estandar test_read(java.lang.Long id)
  {
	  	// CONSULTAR UNA ENTIDAD, no necesita transacción
	  	Estandar e= em.find(Estandar.class,id);
	  	// Estándar tiene que estar en estado MANAGED 
	  	return e;
  }
  
  public void test_update(java.lang.Long id)
  {
		// ACTUALIZAR UNA ENTIDAD
	    EntityTransaction et= em.getTransaction();
	    
		et.begin();
		
		Estandar e = em.find(Estandar.class,id);	
		if (e!=null)
		{
			// Actualizamos las entidades
			e.setEst_name("UPDATED"); //acabamos de cambiar el nombre!
			List<Vinculado> lv= e.getVinculados();
			for (Vinculado v:lv) {
				v.setReporte("UPDATED");
			}
	
			try {
			    em.persist(e);  
			    et.commit(); //en este punto se actualizan los datos de la entidad
			} catch(Exception ex){
			  	et.rollback();
			} 
		} else et.rollback();
  }
  
  public java.lang.Long test_delete(java.lang.Long id)
  {
		// ELIMINAR UNA ENTIDAD
	    EntityTransaction tx= em.getTransaction();	
	    Long id_retorno= 0L;
	    
		Estandar e = em.find(Estandar.class,id); //e está en estado MANAGED
		if (e!=null) 
		{
			tx.begin();
			try {
				id_retorno= e.getEst_id();
				em.remove(e); //ahora e está en estado REMOVED 
			    tx.commit();
			} catch(Exception ex){
			  	tx.rollback();
			} 
		}
		
		return id_retorno;
  }
	
  
  public List test_query(String q)
  {
	  // REALIZAR UNA QUERY
	  EntityManager em = emf.createEntityManager();
	  Query query = em.createQuery(q);	  
	  
	  return query.getResultList();	
  }
  
  public List test_named_query(String est_name) // La named query se define como una annotation en la entity
  {
	  // REALIZAR UNA NAMED QUERY
	  Query query = em.createNamedQuery("Estandar.findByName");
	  query.setParameter("est_name", "%UPD%");
	  
	  return query.getResultList();
  }
  
  public void test_desconectar_entity_manager()
  {
		// DESCONECTAR UNA ENTIDAD
		try {
			Estandar e = em.find(Estandar.class,1L);
			em.close();
		} catch (Exception e) {
			
		}
		//e está es estado DETACHED 
  } 	
  
  public static void main(String args[])
  {
	  EjemploHibernate rh= new EjemploHibernate();
	  
	  // TESTEAMOS CRUD
	  
	  // Testeamos el CREATE
	  System.out.println("TEST CREATE");
	  Long id=rh.test_insert();
	  
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
	  
	  // Testeamos el DELETE
	  System.out.println("TEST DELETE");
	  rh.test_delete(id);
	  
	  // Testeamos QUERY
	  System.out.println("TEST QUERY");
	  for (Estandar estandar:(List<Estandar>)rh.test_query("SELECT e FROM Estandar e"))
		  System.out.println(estandar.toString());
		   
	  // Testeamos NAMED QUERY
	  System.out.println("TEST NAMED QUERY");
	  for (Estandar estandar:(List<Estandar>)rh.test_named_query("UPDATE") )
		  System.out.println(estandar.toString());
	  
	  // Testeamos REMOVE
	  System.out.println("TEST REMOVE ENTITIES");
	  rh.test_desconectar_entity_manager();
	  
	  return;
  }

}
