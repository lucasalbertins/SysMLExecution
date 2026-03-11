package interfaces.behavior.actions;

import interfaces.behavior.actions.nodes.IFlow;
import interfaces.behavior.actions.nodes.INode;
import interfaces.utils.INamedElement;
import interfaces.utils.IParameter;


public interface IActionDefinition extends INamedElement {

	public INode[] getNodes();

	// public IPartUsage[] getPartUsages();

    // Retorna os nomes dos parâmetros de entrada e saída da ação
	public IParameter[] getParameters();
    
    //parameter - getDirection
    //getInputs
    //getOutpus

    // Retorna os nomes das Flows internas da ação
	public IFlow[] getFlows(); //Owned

    // Retorna os nomes das Successions internas da ação
	// public ISuccession[] getSuccessions();

}
