package es.cristobal.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import static javax.persistence.FetchType.*;

/**
 * Entidad Estándar
 */
@NamedQuery(name="Estandar.findByName", query="SELECT e FROM Estandar e WHERE e.est_name LIKE :est_name ")
@Entity
@Table(name = "estandar")
public class Estandar {
 
	// 1. CAMPOS
    @Id
    @Column(name="est_id")
    @GeneratedValue(strategy=GenerationType.AUTO) // En mysql es una columna AUTO_INCREMENT, y en Oracle es una SEQUENCE
    private long est_id;
    
    @Column(name = "est_name",length = 255,nullable = false)
    private String est_name;
    
    
    @Override
	public String toString() {
		return "Estandar [est_id=" + est_id + ", est_name=|" + est_name + "|]";
	}

	// 2. JOINS
    @OneToMany(mappedBy="est_id", cascade={CascadeType.ALL}, fetch=LAZY)
    private List<Vinculado> vinculados;

    // 3. GETTERS AND SETTERS
	public long getEst_id() {
		return est_id;
	}

	public void setEst_id(long est_id) {
		this.est_id = est_id;
	}

	public List<Vinculado> getVinculados() {
		return vinculados;
	}

	public void setVinculados(List<Vinculado> vinculados) {
		this.vinculados = vinculados;
	}

	public String getEst_name() {
		return est_name;
	}

	public void setEst_name(String est_name) {
		this.est_name = est_name;
	}
}
