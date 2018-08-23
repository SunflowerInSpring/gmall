package com.atguigu.gmall.bena;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class BaseCatalog1 implements Serializable{
	
	 @Id
	  @Column
	private Integer id;
	  @Column
	private String name;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
