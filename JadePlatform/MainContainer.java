package JadePlatform;

import Util.Coordenadas;
import Util.Mapa;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.HashMap;
import java.util.Map;

public class MainContainer {

	Runtime rt;
	ContainerController container;

	public MainContainer(){

		initMainContainerInPlatform("localhost", "9888", "MainContainer");

	}

	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		// create a non-main agent container
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}

	public void initMainContainerInPlatform(String host, String port, String containerName) {

		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "true");

		// create a main agent container
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);

	}

	public void startAgentInPlatform(String name, String classpath, Object[] args) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, args);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startInterface(Mapa mapa) {

		this.startAgentInPlatform("Interface", "Agents.AgenteInterface",new Object[] {mapa});

	}

	//ver este pass[0] = this
	public void startSystemAgent(Mapa mapa){
		try {
			Object[] pass = new Object[2]; // argumentos a passar para o agente a ser criado
			pass[0] = this; // "this" corresponde a esta instância do MainContainer
			pass[1] = mapa;
			AgentController ac = container.createNewAgent("System","Agents.AgenteSystem", pass);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startUtilizador(String nome,Mapa mapa){
		try {
			Object[] pass = new Object[1]; // argumentos a passar para o agente a ser criado
			pass[0] = mapa;
			AgentController ac = container.createNewAgent(nome,"Agents.AgenteUtilizador",pass);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}