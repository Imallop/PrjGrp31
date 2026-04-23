package es.usc.enso.mvprjgrp31;

import java.util.ArrayList;

public class Memoria {
	ArrayList<Maquina> maquinas;

	public Memoria () {
		maquinas = new ArrayList<>();
	}

	public void addMaquina(Maquina m) {
		maquinas.add(m);
	}

	public Maquina getMaquina(int id) throws MachineNotFoundException {
		for (Maquina m : maquinas) {
			if (m.getId() == id) {
				return m;
			}
		}

		throw new MachineNotFoundException(id);
	}

	public Maquina getMaquinaCercana(Coordenadas c) throws MachineNotFoundException {
		if (maquinas.size() <= 0) {
			throw new MachineNotFoundException(c);
		}
		Maquina mCercana = null;
		double d = Double.POSITIVE_INFINITY;
		for (Maquina m : maquinas) {
			double dAux = Coordenadas.distancia(c, m.getCoordenadas());
			if (dAux < d) {
				mCercana = m;
				d = dAux;
			}
		}

		return mCercana;
	}

	public ArrayList<Maquina> getMaquinas() {
		return new ArrayList<>(maquinas);
	}
}
