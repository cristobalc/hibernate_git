package es.cristobal.entities;

import com.vividsolutions.jts.geom.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import java.util.Arrays;
import java.util.Date;

/**
 * Entidad Vinculado
 */
@Entity
@Table(name = "vinculado")
public class Vinculado {
 	
	public Vinculado() {
		super();
		this.vinc_id = -1;
		this.report_location = null;
		this.num_personas = -1;
		this.report_time = null;
		this.reporte = "";
		this.checked = '-';
		this.miblob= new Byte[100];
		for (int n=0; n<100; n++) miblob[n]=0;
		this.report_status = ReportStatus.NO_INICIALIZADO;
		this.report_status_desc = ReportStatus.NO_INICIALIZADO;
		this.estandar = null;
	}

	// 1. CAMPOS
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO) // En mysql es una columna AUTO_INCREMENT, y en Oracle es una SEQUENCE
    @Column(name="vinc_id")
    private long vinc_id;
 
    @Column(name="est_id", insertable=false, updatable=false)  // insertable y updatable van así porque esta columna es parte del Join
    private long est_id;
    
    @Column(name="report_location",nullable = false)
    @Type(type = "org.hibernatespatial.GeometryUserType") // Esta annotation se utiliza cuando queremos definir tipos especiales. La función de la clase del parámetro "type" es realizar la conversión del tipo
    private Point report_location;
 
    @Column(name = "num_personas")
    private Integer num_personas;
 
    @Column(name = "report_time")
    private Date report_time;
 
    @Column(name = "reporte",length = 255,nullable = false)
    private String reporte;
    
    @Column(name = "checked", length = 1,nullable= false)
    private char checked;

    @Column(name = "miblob", nullable= false)
    private Byte[] miblob;
    
     @Override
	public String toString() {
		return "Vinculado [vinc_id=" + vinc_id + ", est_id=" + estandar.getEst_id()
				+ ", report_location=|" + report_location + "|, num_personas="
				+ num_personas + ", report_time=|" + report_time + "|, reporte=|"
				+ reporte + "|, checked=|" + checked + "|, miblob=|"
				+ Arrays.toString(miblob) + "|, report_status=|" + report_status
				+ "|, report_status_desc=|" + report_status_desc + "|]";
	}

	//  EN este caso en la columna guadaremos un número.
    @Column(name="report_status",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ReportStatus report_status;

    //  EN este caso en la columna guadaremos un número.
    @Column(name="report_status_desc",nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus report_status_desc;
    
    // 2. JOINS
    @ManyToOne(optional = false)
    @JoinColumn(name="est_id")
    private Estandar estandar;
    
    // 3. GETTERS AND SETTERS
    public ReportStatus getReport_status() {
    	return report_status;
    }
    
    public Estandar getEstandar() {
		return estandar;
	}

	public void setEstandar(Estandar estandar) {
		this.estandar = estandar;
	}

	public void setReport_status(ReportStatus report_status){
    	this.report_status= report_status;
    }
    
    public ReportStatus getReport_status_desc() {
    	return report_status_desc;
    }
    
    public void setReport_status_desc(ReportStatus report_status_desc){
    	this.report_status_desc= report_status_desc;
    }
    
	public Byte[] getMiblob() {
		return miblob;
	}

	public void setMiblob(Byte[] miblob) {
		this.miblob = miblob;
	}
	
	public long getChecked() {
		return checked;
	}

	public void setChecked(char checked) {
		this.checked = checked;
	}

	public long getVinc_id() {
		return vinc_id;
	}

	public void setVinc_id(long vinc_id) {
		this.vinc_id = vinc_id;
	}

	public Point getReport_location() {
		return report_location;
	}

	public void setReport_location(Point report_location) {
		this.report_location = report_location;
	}

	public Integer getNum_personas() {
		return num_personas;
	}

	public void setNum_personas(Integer num_personas) {
		this.num_personas = num_personas;
	}

	public Date getReport_time() {
		return report_time;
	}

	public void setReport_time(Date report_time) {
		this.report_time = report_time;
	}

	public String getReporte() {
		return reporte;
	}

	public void setReporte(String reporte) {
		this.reporte = reporte;
	}   
}
