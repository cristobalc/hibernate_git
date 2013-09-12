package es.cristobal;

import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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

public class Tests_hibernate {

  private static final Random random = new Random();
    
  public void test_insert() // Este test genera un estándar con cien indicadores viculados y persistirlo
  {
	  // INICIALIZAR LA TRANSACCIÓN
      GeometryFactory geometryFactory = new GeometryFactory(); // Para generar un valor de tipo "punto", usamos el módulo "hibernate-spatial"
      EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
      EntityManager em = emf.createEntityManager();
      EntityTransaction et= em.getTransaction();
      et.begin();
      
      // 0. INICIALIZAR ESTÁNDAR
      Estandar estandar = new Estandar();
      Long randomLong = random.nextLong();
      //estandar.setEst_id(0); // Este id lo genera la base de datos
      estandar.setEst_name("Estandar ".concat(randomLong.toString()));
      estandar.setVinculados(new LinkedList<Vinculado>());
      System.out.println(estandar.toString());
            
      for(int i=0;i<100;i++){
    	  
    	  // 1. INICIALIZAR VINCULADO   	  
          Vinculado vinculado = new Vinculado();
          
          //vinculado.setVinc_id(i); // Este id lo genera la base de datos
          vinculado.setEst_id(estandar.getEst_id());
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
          System.out.println(vinculado.toString());
      }
      em.persist(estandar);//¿qué pasará aquí?
      
      // EJECUTAR LA TRANSACCIÓN
      et.commit();
  }
  
  public void test_read()
  {
	  /*
	// Generar un estándar con 100 indicadores vinculados
	Estandar e= new Estandar();
    
    
    
    
    // INICIAR UN ENTITY MANAGER
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test"); // Cada EntityManager puede realizar las operaciones básicas Create, Read, Update y Delete sobre un conjunto de objetos persistentes o entidades
    
    
    // REALIZAR UNA QUERY
    EntityManager em = emf.createEntityManager();
    Query query = em.createQuery("select col from test tabla_db");
    
    // PERSISTIR UNA ENTIDAD
    EntityTransaction tx = em.getTransaction();
    TablaDB tabla_db = new TablaDB();
    try {
      em.persist(tabla_db);
      tx.commit();
    } catch(Exception e){
      tx.rollback();
    }
    
    // PERSISTIR UNA ENTIDAD
    EntityManager miem = emf.createEntityManager();  		
    miem.getTransaction().begin();
    TablaDB tdb = new TablaDB();
    //tdb está en estado NEW
    tdb.setName(“Tabla”);
    em.persist(tdb);
    //tdb está en estado MANAGED
    em.getTransaction().commit();
    
    // ACTUALIZAR UNA ENTIDAD
    EntityManager myem = emf.createEntityManager();
    myem.getTransaction().begin();
    TablaDB tabdb = myem.find(TablaDB.class,17);
    tabdb.setName("un nombre diferente"); //acabamos de cambiar el nombre!
    myem.getTransaction().commit();//en este punto se actualizan los datos de la entidad
    
    // CONSULTAR UNA ENTIDAD
    EntityManager Em = emf.createEntityManager();
    TablaDB tab_db = Em.find(TablaDB.class,17);
    //TablaDB está en estado MANAGED
    
    // ELIMINAR UNA ENTIDAD
    EntityManager MEm = emf.createEntityManager();
    MEm.getTransaction().begin();
    TablaDB tadb = MEm.find(TablaDB.class,17);
    //tadb está en estado MANAGED
    MEm.remove(tadb);
    MEm.getTransaction().commit();
    //ahora tadb está en estado REMOVED
    
    // DESCONECTAR UNA ENTIDAD
    EntityManager mem = emf.createEntityManager();
    TablaDB td = mem.find(TablaDB.class,17);
    mem.close();
    //td está es estado DETACHED
     */	  
  }
  
  public void test_update()
  {
	  
  }
  
  public void test_delete()
  {
	  
  }
	
  public static void main(String args[])
  {
	  Tests_hibernate rh= new Tests_hibernate();
	  
	  // TESTEAMOS CRUD
	  
	  // Testeamos el CREATE
	  rh.test_insert();
	  
	  // Testeamos el READ
	  rh.test_read();
	  
	  // Testeamos el UPDATE
	  rh.test_update();
	  
	  // Testeamos el DELETE
	  rh.test_delete();
 
	  return;
  }

}
