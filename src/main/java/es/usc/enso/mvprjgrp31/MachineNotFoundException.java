package es.usc.enso.mvprjgrp31;

public class MachineNotFoundException extends Exception {

	public MachineNotFoundException(Coordenadas coordenadas) {
		super("Machine not found near coordinates: lat=" + coordenadas.getLatitud()
				+ ", lon=" + coordenadas.getLongitud()
				+ ", alt=" + coordenadas.getAltitud());
	}

	public MachineNotFoundException(int id) {
		super("Machine not found: " + id);
	}

}
