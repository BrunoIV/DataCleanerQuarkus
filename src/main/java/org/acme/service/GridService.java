package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.rest.GridRest;

@ApplicationScoped
public class GridService {
	private GridRest grid = new GridRest();
	private int idFile = 0;

	public synchronized GridRest getGrid() {
		return grid;
	}

	public synchronized void setGrid(GridRest grid) {
		this.grid = grid;
	}

	public synchronized int getIdFile() {
		return idFile;
	}

	public synchronized void setIdFile(int idFile) {
		this.idFile = idFile;
	}

}
