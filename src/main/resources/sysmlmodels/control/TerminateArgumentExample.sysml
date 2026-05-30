package TerminateActionsExample {
	private import ScalarValues::Boolean;
	
	action def InternalProcess;
	action def WorkflowProcess;
	
	part def Processor {
		ref action workflowProcess : WorkflowProcess;
		action internalProcess : InternalProcess {
			// ...
		}
	}

	action def TerminateProcessing {
		first start;
		in processor : Processor;

		then terminate processor.workflowProcess;
		then terminate processor;
	}
	
	action terminateProcessing : TerminateProcessing;
}