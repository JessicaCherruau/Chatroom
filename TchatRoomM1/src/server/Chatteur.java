package server;

import java.io.Serializable;

/**
 * Cette classe repr�sente un client connect� (ayant une session)
 * 
 * @author Jessica CHERRUAU
 *
 */
public class Chatteur implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8285075052578153634L;
	private String name;
	private Session session;
	private static final String DEFAULT_NAME = "invit�";
	
	public Chatteur(){
		this.name = DEFAULT_NAME;
	}
	public Chatteur(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setSession(Session s){
		this.session = s;
	}

	public Session getSession(){
		return session;
	}
	
	@Override
	/**
	 * Red�fnition de la m�thode equals 
	 */
	public boolean equals(Object obj) {
		if (obj.getClass().equals(Chatteur.class)){
			Chatteur c = (Chatteur) obj;
			if(c.getSession().getIdSession() == this.session.getIdSession())
					if(c.name.equals(this.name))
						return true;
					else
						return false;
			else
				return false;
		}
		else
			return false;
	}

}
