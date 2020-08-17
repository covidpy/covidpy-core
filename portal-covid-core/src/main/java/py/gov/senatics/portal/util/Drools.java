package py.gov.senatics.portal.util;

import java.util.Arrays;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import py.gov.senatics.portal.modelCovid19.ReporteSalud;

public class Drools {
	
	public static void clasificarPaciente(ReporteSalud reporteSalud)
	{
		KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
		StatelessKieSession ksession = kc.newStatelessKieSession( "EvaluacionPacienteKS");
		//now create some test data

        ksession.execute( Arrays.asList( new Object[]{reporteSalud} ) );
        
        //if(reporteSalud.getResultadoRecomendaciones().isEmpty())
        {
        	reporteSalud.getResultadoRecomendaciones().add("Si te cuidas nos cuidamos, lo estamos haciendo bien.");
        }
	}
}
